package bio.knowledge.server.implementation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

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
import bio.knowledge.ontology.BiolinkClass;
import bio.knowledge.ontology.BiolinkModel;
import bio.knowledge.ontology.BiolinkSlot;
import bio.knowledge.ontology.mapping.InheritanceLookup;
import bio.knowledge.ontology.mapping.ModelLookup;
import bio.knowledge.ontology.mapping.NameSpace;
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
	
	@Autowired ConceptRepository conceptRepository;
	@Autowired PredicateRepository predicateRepository;
	@Autowired StatementRepository statementRepository;
	@Autowired EvidenceRepository evidenceRepository;
	
	BiolinkModel biolinkModel;
	
	InheritanceLookup<BiolinkClass> classInheritanceLookup;
	ModelLookup<BiolinkClass> classModelLookup;
	
	InheritanceLookup<BiolinkSlot> slotInheritanceLookup;
	ModelLookup<BiolinkSlot> slotModelLookup;
	
	@PostConstruct
	public void init() {
		biolinkModel = BiolinkModel.get();
		
		classInheritanceLookup = new InheritanceLookup<BiolinkClass>(biolinkModel.getClasses());
		classModelLookup = new ModelLookup<BiolinkClass>(biolinkModel.getClasses(), classInheritanceLookup);
		
		slotInheritanceLookup = new InheritanceLookup<BiolinkSlot>(biolinkModel.getSlots());
		slotModelLookup = new ModelLookup<BiolinkSlot>(biolinkModel.getSlots(), slotInheritanceLookup);
	}
	
	@Value("kmap.path")
	private String KMAP_PATH;
	
	private String umlsToBiolinkLabel(String semGroup) {
		return classModelLookup.lookupName(NameSpace.UMLSSG.getPrefix() + semGroup);
	}
	
	private String wikidataToBiolinkDescription(String curie) {
		return classModelLookup.lookupDescription(curie);
	}
	
	private String biolinkToUmls(String biolinkClassName) {
		BiolinkClass biolinkClass = classModelLookup.getClassByName(biolinkClassName);
		
		for (String curie : biolinkClass.getMappings()) {
			if (curie.startsWith(NameSpace.UMLSSG.getPrefix())) {
				return curie;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a list of UMLS categories that map onto the given
	 * biolink classes. If no mapping exists for some biolink class
	 * then a NO_MATCH string is added to the list to ensure that
	 * filter is active (resulting in no matches for that filter).
	 */
	private List<String> biolinkToUmls(List<String> biolinkClasses) {
		if (biolinkClasses == null) {
			return null;
		}
		
		Set<BiolinkClass> classes = new HashSet<BiolinkClass>();
		
		for (String name : biolinkClasses) {
			BiolinkClass c = classModelLookup.getClassByName(name);
			classes.add(c);
			classes.addAll(classInheritanceLookup.getDescendants(c));
		}
		
		biolinkClasses = classes.stream().map(c -> c.getName()).collect(Collectors.toList());
		
		Set<String> curies = new HashSet<String>();
		
		for (String biolinkClass : biolinkClasses) {
			String curie = biolinkToUmls(biolinkClass);
			if (curie != null) {
				curie = curie.replace(NameSpace.UMLSSG.getPrefix(), "");
				curies.add(curie);
			} else {
				curies.add("NO_MATCH[" + biolinkClass + "]");
			}
		}
		
		return new ArrayList<String>(curies);
	}
	
	public ResponseEntity<List<BeaconConcept>> getConcepts(
			List<String> keywords,
			List<String> categories,
			Integer size
	) {
		size = Utilities.fixPageSize(size);
		keywords = Utilities.urlDecode(keywords);
		categories = Utilities.urlDecode(categories);
		
		categories = biolinkToUmls(categories);

		List<Neo4jConcept> concepts = conceptRepository.apiGetConcepts(keywords, categories, size);
		List<BeaconConcept> responses = new ArrayList<BeaconConcept>();
		
		for (Concept concept : concepts) {
			BeaconConcept response = new BeaconConcept();
			
			String category = umlsToBiolinkLabel(concept.getSemanticGroup().name());
			
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
		
		List<Neo4jPredicate> data = predicateRepository.findAllPredicates();
				
		List<BeaconPredicate> responses = new ArrayList<>();
		for (Predicate predicate : data) {
			
			if(predicate.getName().isEmpty()) continue;
			
			String curie = predicate.getId();
			String name = predicate.getName();
			String description = predicate.getDescription();
			
			BeaconPredicate response = new BeaconPredicate();
			
			String edgeLabel = String.join("_", name.split(" "));
			
			response.setId(curie);
			response.setEdgeLabel(edgeLabel);
			
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
			String category = umlsToBiolinkLabel(concept.getSemanticGroup().name());
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
		
		List<ExactMatchResponse> response = new ArrayList<ExactMatchResponse>();
		
		for (String conceptId : c) {
			boolean isAvailable = conceptRepository.isConceptAvailable(conceptId);
			
			ExactMatchResponse e = new ExactMatchResponse();
			
			e.setWithinDomain(isAvailable);
			e.setId(conceptId);
			
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
			String year = String.valueOf((Integer) entry.get("year"));
			String month = String.valueOf((Integer) entry.get("month"));
			String day = String.valueOf((Integer) entry.get("day"));
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
		
		categories = biolinkToUmls(categories);

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
				statementsSubject.category(umlsToBiolinkLabel(subject.getSemanticGroup().name()));
			}

			if (relation != null) {
				String edgeLabel = String.join("_", relation.getName().split(" "));
				statementsPredicate.setRelation(relation.getId());
				statementsPredicate.setEdgeLabel(edgeLabel);
			}

			if (object != null) {
				statementsObject.setId(object.getId());
				statementsObject.setName(object.getName());
				statementsObject.setCategory(umlsToBiolinkLabel(object.getSemanticGroup().name()));
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
			
			String local_id = (String) map.get("type");

			String biolinkTerm = umlsToBiolinkLabel(local_id);

			if(biolinkTerm!=null) {
				
				response.setId(NameSpace.BIOLINK.getCurie(biolinkTerm));
				response.setCategory(biolinkTerm);
				response.setUri(NameSpace.BIOLINK.getIri(biolinkTerm));
				
				// TODO: add in local id metadata, etc.
				//response.setId(local_id);
				//response.setXref(NameSpace.UMLS.getIri(local_id));
				//String description = umlsToBiolinkDescription(local_id);
				//response.setDescription(description);
				
				response.setFrequency((Integer) map.get("frequency"));
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
			String subjectBiolinkTerm = umlsToBiolinkLabel(subjectType);
			if(subjectBiolinkTerm==null) continue;
			
			String objectType = (String) triple.get("objectType");
			String objectBiolinkTerm = umlsToBiolinkLabel(objectType);
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
//			predicate.setId(NameSpace.BIOLINK.getCurie(relationName));
			predicate.setRelation(relationName);
			knowledgeMapStatement.setPredicate(predicate);
			
			// Just using the predicate description here?
			knowledgeMapStatement.setDescription(
					subjectBiolinkTerm+" "+
					relationName+" "+
					objectBiolinkTerm
			);
			
			Integer frequency = (Integer) kmap.get("frequency");
			knowledgeMapStatement.setFrequency(frequency);
			
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