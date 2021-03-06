package bio.knowledge.server.implementation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.database.repository.EvidenceRepository;
import bio.knowledge.database.repository.PredicateRepository;
import bio.knowledge.database.repository.StatementRepository;
import bio.knowledge.model.Annotation;
import bio.knowledge.model.Concept;
import bio.knowledge.model.Predicate;
import bio.knowledge.model.Statement;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jPredicate;
import bio.knowledge.ontology.mapping.NameSpace;
import bio.knowledge.ontology.mapping.umls.UMLSBiolinkMapping;
import bio.knowledge.server.model.BeaconAnnotation;
import bio.knowledge.server.model.BeaconConcept;
import bio.knowledge.server.model.BeaconConceptCategory;
import bio.knowledge.server.model.BeaconConceptWithDetails;
import bio.knowledge.server.model.BeaconKnowledgeMapObject;
import bio.knowledge.server.model.BeaconKnowledgeMapPredicate;
import bio.knowledge.server.model.BeaconKnowledgeMapStatement;
import bio.knowledge.server.model.BeaconKnowledgeMapSubject;
import bio.knowledge.server.model.BeaconPredicate;
import bio.knowledge.server.model.BeaconStatement;
import bio.knowledge.server.model.BeaconStatementObject;
import bio.knowledge.server.model.BeaconStatementPredicate;
import bio.knowledge.server.model.BeaconStatementSubject;
import bio.knowledge.server.model.ExactMatchResponse;
import bio.knowledge.server.utilities.Utilities;

@Controller
public class ControllerImpl {
	
	private static Logger _logger = LoggerFactory.getLogger(ControllerImpl.class);
	
	@Autowired private ConceptRepository conceptRepository;
	@Autowired private PredicateRepository predicateRepository;
	@Autowired private StatementRepository statementRepository;
	@Autowired private EvidenceRepository evidenceRepository;
	@Autowired private UMLSBiolinkMapping mapping;
	
	@Value("kmap.path")
	private String KMAP_PATH;
	
	/**
	 * 
	 * @param keywords
	 * @param types
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public ResponseEntity<List<BeaconConcept>> getConcepts(
			List<String> keywords,
			List<String> categories,
			Integer size
	) {
		size = Utilities.fixPageSize(size);
		keywords = Utilities.urlDecode(keywords);
		categories = Utilities.urlDecode(categories);
		
		categories = mapping.biolinkToUmls(categories);

		List<Neo4jConcept> concepts = conceptRepository.apiGetConcepts(keywords, categories, size);
		List<BeaconConcept> responses = new ArrayList<BeaconConcept>();
		
		for (Concept concept : concepts) {
			BeaconConcept response = new BeaconConcept();
			
			String category = mapping.umlsToBiolinkLabel(concept.getSemanticGroup().name());
			
			response.setId(concept.getId());
			response.setName(concept.getName());
			response.setCategory(category);
			response.setDefinition(concept.getDescription());
			response.setSynonyms(Arrays.asList(concept.getSynonyms().split("\\|")));

			responses.add(response);
		}

		return ResponseEntity.ok(responses);
	}

	public ResponseEntity<List<BeaconPredicate>> getPredicates() {
		
		List<Map<String, Object>> data = predicateRepository.findAllPredicates();
				
		List<BeaconPredicate> responses = new ArrayList<>();

		for (Map<String, Object> m : data) {
			Neo4jPredicate predicate = (Neo4jPredicate) m.get("predicate");
			Long frequency = (Long) m.get("frequency");
			
			String local_id = predicate.getId();
			String local_uri = predicate.getUri();
			String local_name = predicate.getName();
			String description = predicate.getDescription();
			
			BeaconPredicate response = new BeaconPredicate();
			
			String edgeLabel = String.join("_", local_name.split(" "));
			
			// TODO: eventually need to remap these id's to their Biolink Minimal predicate id's
			response.setId(local_id);
			response.setUri(local_uri);
			response.setEdgeLabel(edgeLabel);
			response.setFrequency(frequency.intValue());
			
			// Here, the maximal == minimal predicate
			response.setRelation(edgeLabel);
			
			response.setLocalId(local_id);
			response.setLocalUri(local_uri);
			response.setLocalRelation(edgeLabel);
			
			response.setDescription(description);
			
			responses.add(response);
		}
		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<BeaconConceptWithDetails>> getConceptDetails(String conceptId) {
		conceptId = Utilities.urlDecode(conceptId);
		
		List<BeaconConceptWithDetails> responses = new ArrayList<BeaconConceptWithDetails>();
		Concept concept = conceptRepository.apiGetConceptById(conceptId);
		
		if (concept != null) {
			BeaconConceptWithDetails response = new BeaconConceptWithDetails();
			response.setDefinition(concept.getDescription());
			response.setId(concept.getId());
			response.setName(concept.getName());

			String category = mapping.umlsToBiolinkLabel(concept.getSemanticGroup().name());
			response.category(category);
			
			response.setSynonyms(Arrays.asList(concept.getSynonyms().split("\\|")));

			responses.add(response);
		}

		return ResponseEntity.ok(responses);
    }
	
	//private static final String EXACT_MATCH_RELATION = "wd:P2888"; // "exact match" predicate used for equivalent concept identification?
	
	private List<ExactMatchResponse> getExactMatches(List<String> c) {
		
		/*
		 * RMB, October 11, 2017
		 * I thought that perhaps the 'exact match' property would be semantically accurate
		 *  for equivalent concepts in SemMedDb, however, direct manual querying on this
		 *  relation in the database suggests otherwise: the property is very poorly curated
		 *  curated with respect to the concept of "equivalent concepts"; so, I ignore this 
		 *  relation for now; however, the code here would work great guide for predicate-constrained
		 *  searching of statements, so I should keep it in mind.
		 *  
		String[] curies = c.toArray(new String[c.size()]);
		List<Map<String, Object>> data = statementRepository.findConceptsMatchedByConceptsAndRelation(curies,EXACT_MATCH_RELATION);
		List<String> responses = new ArrayList<String>();
		for (Map<String, Object> entry : data) {
			Concept concept = (Concept) entry.get("concept");
			responses.add(concept.getId());
		}
		*/
		
		List<String> m = conceptRepository.getKnownConceptIds(c);
		
		List<ExactMatchResponse> response = new ArrayList<ExactMatchResponse>();
		
		for (String conceptId : c) {
			ExactMatchResponse e = new ExactMatchResponse();
			e.setId(conceptId);
			e.setWithinDomain(m.contains(conceptId));
			//TODO: Find exact matches?
//			e.setHasExactMatches(hasExactMatches);
			response.add(e);
		}
		
		return response;
	}

	public ResponseEntity<List<ExactMatchResponse>> getExactMatchesToConceptList(List<String> c) {
		List<ExactMatchResponse> responses = getExactMatches(c);
		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<BeaconAnnotation>> getEvidence(String statementId,
	        List<String> keywords,
	        Integer size
	) {
		// RMB: May 5, 2017 - Statement ID hack here to fix ID truncation problem
		statementId = statementId.replaceAll("_",".");
		
		size = Utilities.fixPageSize(size);
		keywords = Utilities.urlDecode(keywords);
		
		List<Map<String, Object>> data = evidenceRepository.apiGetEvidence(statementId, keywords, size);
		
		List<BeaconAnnotation> responses = new ArrayList<BeaconAnnotation>();
		
		for (Map<String, Object> entry : data) {
			String year = String.valueOf((Long) entry.get("year"));
			String month = String.valueOf((Long) entry.get("month"));
			String day = String.valueOf((Long) entry.get("day"));
			Annotation annotation = (Annotation) entry.get("annotation");
			
			BeaconAnnotation response = new BeaconAnnotation();
			response.setId(annotation.getId());
			response.setLabel(annotation.getName());
			response.setDate(year + "-" + month + "-" + day);
			
			response.setType("TAS");
			
			responses.add(response);
		}
		
		return ResponseEntity.ok(responses);
	}
	
	/**
	 * 
	 * @param s
	 * @param relations
	 * @param t
	 * @param keywords
	 * @param categories
	 * @param pageNumber
	 * @param size
	 * @return
	 */
	public ResponseEntity<List<BeaconStatement>> getStatements(
			List<String> s,
			List<String> relations,
			List<String> t,
			List<String> keywords,
			List<String> categories, 
			Integer size
	) {
		s = Utilities.urlDecode(s);
		
		if(s == null) {
			ResponseEntity<List<BeaconStatement>> response =
					new ResponseEntity<List<BeaconStatement>>(HttpStatus.BAD_REQUEST);
			return response;
		}
		
		relations = Utilities.urlDecode(relations);
		t = Utilities.urlDecode(t);
		
		keywords = Utilities.urlDecode(keywords);
		categories = Utilities.urlDecode(categories);
		size = Utilities.fixPageSize(size);

		String[] sourceCuries = s.toArray(new String[s.size()]);
		
		String[] targetCuries = null;
		if( t != null)
			targetCuries = t.toArray(new String[t.size()]);
		
		categories = mapping.biolinkToUmls(categories);

		List<BeaconStatement> responses = new ArrayList<BeaconStatement>();

		List<Map<String, Object>> data = statementRepository.findStatements(
				sourceCuries,
				relations,
				targetCuries,
				keywords,
				categories,
				size
		);

		for (Map<String, Object> entry : data) {
			BeaconStatement response = new BeaconStatement();

			BeaconStatementObject statementsObject = new BeaconStatementObject();
			BeaconStatementPredicate statementsPredicate = new BeaconStatementPredicate();
			BeaconStatementSubject statementsSubject = new BeaconStatementSubject();

			Statement statement = (Statement) entry.get("statement");
			Concept object = (Concept) entry.get("object");
			Concept subject = (Concept) entry.get("subject");
			Predicate relation = (Predicate) entry.get("relation");

			if (statement != null) {
				// RMB: May 5, 2017 - Statement ID hack here to fix ID
				// truncation problem
				String statementId = statement.getId().replaceAll("\\.", "_");

				response.setId(statementId);
			}

			if (subject != null) {
				statementsSubject.setId(subject.getId());
				statementsSubject.setName(subject.getName());
				statementsSubject.category(mapping.umlsToBiolinkLabel(subject.getSemanticGroup().name()));
			}

			if (relation != null) {
				String edgeLabel = String.join("_", relation.getName().split(" "));
				statementsPredicate.setRelation(relation.getId());
				statementsPredicate.setEdgeLabel(edgeLabel);
			}

			if (object != null) {
				statementsObject.setId(object.getId());
				statementsObject.setName(object.getName());
				statementsObject.setCategory(mapping.umlsToBiolinkLabel(object.getSemanticGroup().name()));
			}

			response.setObject(statementsObject);
			response.setSubject(statementsSubject);
			response.setPredicate(statementsPredicate);

			responses.add(response);
		}

		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<BeaconConceptCategory>> getConceptCategories() {
		
		List<BeaconConceptCategory> responses = new ArrayList<BeaconConceptCategory>();
		
		List<Map<String, Object>> counts = conceptRepository.countAllGroupBySemanticGroup();
		
		for (Map<String, Object> map : counts) {
			
			BeaconConceptCategory response = new BeaconConceptCategory();
			
			String local_id = (String) map.get("semanticGroup");

			String biolinkTerm = mapping.umlsToBiolinkLabel(local_id);

			if( biolinkTerm != null ) {
				
				response.setId(NameSpace.BIOLINK.getCurie(biolinkTerm));
				response.setCategory(biolinkTerm);
				response.setUri(NameSpace.BIOLINK.getUri(biolinkTerm));
				
				response.setLocalId(NameSpace.UMLSSG.getCurie(local_id));
				response.setLocalCategory(local_id);
				response.setLocalUri(NameSpace.UMLSSG.getUri(local_id));
				
				//response.setDescription(description);
				
				Long frequency = (Long) map.get("frequency");
				response.setFrequency(frequency != null ? frequency.intValue() : null);
				
				responses.add(response);
				
			} else
				_logger.debug("ControllerImpl.getConceptTypes(): Warning: local id '"+local_id+"' has no Biolink Model mapping? Ignored!");
		}
		
        return ResponseEntity.ok(responses);
    }

	@SuppressWarnings("unchecked")
	public ResponseEntity<List<BeaconKnowledgeMapStatement>> getKnowledgeMap() {
		
		List<Map<String, Object>> kmaps = loadKmap();
		
		if (kmaps == null) {
			kmaps = statementRepository.getKmap();
			saveKmap(kmaps);
		}
		
		List<BeaconKnowledgeMapStatement> knowledgeMapStatements = new ArrayList<BeaconKnowledgeMapStatement>();
		
		for (Map<String, Object> kmap : kmaps) {
			
			Map<String, Object> triple = (Map<String, Object>) kmap.get("row");
			
			String subjectType = (String) triple.get("subjectType");
			String subjectBiolinkTerm = mapping.umlsToBiolinkLabel(subjectType);
			if(subjectBiolinkTerm==null) continue;
			
			String objectType = (String) triple.get("objectType");
			String objectBiolinkTerm = mapping.umlsToBiolinkLabel(objectType);
			if(objectBiolinkTerm==null) continue;
			
			BeaconKnowledgeMapStatement knowledgeMapStatement = new BeaconKnowledgeMapStatement();
			
			BeaconKnowledgeMapSubject subject = new BeaconKnowledgeMapSubject();
			subject.setCategory(subjectBiolinkTerm);
			knowledgeMapStatement.setSubject(subject);

			BeaconKnowledgeMapObject object = new BeaconKnowledgeMapObject();
			object.setCategory(objectBiolinkTerm);
			knowledgeMapStatement.setObject(object);
			
			BeaconKnowledgeMapPredicate predicate = new BeaconKnowledgeMapPredicate();
			
			String relationName = (String) triple.get("relationName");
			
			// Translator Knowledge Graph standard prescribes 'snake_case'
			String relation = String.join("_", relationName.split(" "));
			
			predicate.setRelation(relation);

			knowledgeMapStatement.setPredicate(predicate);
			
			// Just using the predicate description here?
			knowledgeMapStatement.setDescription(
					subjectBiolinkTerm+" "+
					relationName+" "+
					objectBiolinkTerm
			);
			
			Long frequency = (Long) kmap.get("frequency");
			knowledgeMapStatement.setFrequency(frequency != null ? frequency.intValue() : null);
			
			knowledgeMapStatements.add(knowledgeMapStatement);
		}
		
		return ResponseEntity.ok(knowledgeMapStatements);
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> loadKmap() {
		List<Map<String, Object>> kmap = null;
		ObjectInputStream in = null;
		try {
			FileInputStream fileIn = new FileInputStream(KMAP_PATH);
			in = new ObjectInputStream(fileIn);
			Object object = in.readObject();
			in.close();
			kmap = (List<Map<String, Object>>) object;
			
		} catch (IOException | ClassNotFoundException e) {
			_logger.error(e.getMessage());
		} finally {
			if(in!=null)
				try {
					in.close();
				} catch (IOException e) {
					// ignore?
				}
		}
		
		return kmap;
	}
	
	private void saveKmap(List<Map<String, Object>> kmap) {
		ObjectOutputStream out = null;
		try {
			FileOutputStream fileOut = new FileOutputStream(KMAP_PATH);
			out = new ObjectOutputStream(fileOut);
			out.writeObject(kmap);
		} catch (IOException e) {
			_logger.error(e.getMessage());
		} finally {
			if(out!=null)
				try {
					out.close();
				} catch (IOException e) {
					// ignore?
				}
		}
	}
	
}