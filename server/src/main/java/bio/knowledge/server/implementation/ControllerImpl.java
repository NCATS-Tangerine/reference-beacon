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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import bio.knowledge.server.model.BeaconAnnotation;
import bio.knowledge.server.model.BeaconConcept;
import bio.knowledge.server.model.BeaconConceptType;
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
import bio.knowledge.server.utilities.Utilities;

@Controller
public class ControllerImpl {
	
	@Autowired ConceptRepository conceptRepository;
	@Autowired PredicateRepository predicateRepository;
	@Autowired StatementRepository statementRepository;
	@Autowired EvidenceRepository evidenceRepository;
	
	@Value("kmap.path")
	private String KMAP_PATH;
	
	public ResponseEntity<List<BeaconConcept>> getConcepts(
			String keywords,
			String types,
			Integer pageNumber,
			Integer pageSize
	) {
		pageNumber = Utilities.fixPageNumber(pageNumber);
		pageSize = Utilities.fixPageSize(pageSize);
		keywords = Utilities.urlDecode(keywords);
		types = Utilities.urlDecode(types);

		String[] filter = Utilities.buildArray(keywords);
		String[] semanticGroupFilter = Utilities.buildArray(types);

		List<Neo4jConcept> concepts = conceptRepository.apiGetConcepts(filter, semanticGroupFilter, pageNumber, pageSize);
		List<BeaconConcept> responses = new ArrayList<BeaconConcept>();
		
		for (Concept concept : concepts) {
			BeaconConcept response = new BeaconConcept();
			
			response.setId(concept.getId());
			response.setName(concept.getName());
			response.setType(concept.getSemanticGroup().name());
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
			
			BeaconPredicate response = new BeaconPredicate();
			response.setId(predicate.getId());
			response.setName(predicate.getName());
			response.setDefinition(predicate.getDescription());
			
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
			String semanticType = concept.getSemanticGroup().name();
			response.setType(semanticType);
			response.setSynonyms(Arrays.asList(concept.getSynonyms().split("\\|")));

			responses.add(response);
		}

		return ResponseEntity.ok(responses);
    }
	
	//private static final String EXACT_MATCH_RELATION = "wd:P2888"; // "exact match" predicate used for equivalent concept identification?
	
	private List<String> getExactMatches(List<String> c) {
		
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
		
		return new ArrayList<String>();
	}
	
	public ResponseEntity<List<String>> getExactMatchesToConcept(String conceptId) {
		List<String> c = new ArrayList<String>();
		c.add(conceptId) ;
		List<String> responses = getExactMatches(c);
		// also return back the original conceptId
		responses.add(conceptId); 
		return ResponseEntity.ok(responses);
	}

	public ResponseEntity<List<String>> getExactMatchesToConceptList(List<String> c) {
		List<String> responses = getExactMatches(c);
		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<BeaconAnnotation>> getEvidence(String statementId,
	        String keywords,
	        Integer pageNumber,
	        Integer pageSize
	) {
		// RMB: May 5, 2017 - Statement ID hack here to fix ID truncation problem
		statementId = statementId.replaceAll("_",".");
		
		pageNumber = Utilities.fixPageNumber(pageNumber);
		pageSize = Utilities.fixPageSize(pageSize);
		keywords = Utilities.urlDecode(keywords);
		
		String[] filter = Utilities.buildArray(keywords);
		
		List<Map<String, Object>> data = evidenceRepository.apiGetEvidence(statementId, filter, pageNumber, pageSize);
		
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
			
			responses.add(response);
		}
		
		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<BeaconStatement>> getStatements(
			List<String> s,
			String relations,
			List<String> t,
			String keywords,
			String types, 
			Integer pageNumber,
			Integer pageSize
	) {
		s = Utilities.urlDecode(s);
		relations = Utilities.urlDecode(relations);
		t = Utilities.urlDecode(t);
		
		keywords = Utilities.urlDecode(keywords);
		types = Utilities.urlDecode(types);
		pageNumber = Utilities.fixPageNumber(pageNumber);
		pageSize = Utilities.fixPageSize(pageSize);

		String[] sourceCuries = s.toArray(new String[s.size()]);
		
		String[] targetCuries = null;
		if( t != null)
			targetCuries = t.toArray(new String[t.size()]);
		
		String[] filter = Utilities.buildArray(keywords);
		String[] semanticFilter = Utilities.buildArray(types);
		String[] predicateFilter = Utilities.buildArray(relations);

		List<BeaconStatement> responses = new ArrayList<BeaconStatement>();

		List<Map<String, Object>> data = statementRepository.findStatements(sourceCuries, predicateFilter, targetCuries, filter, semanticFilter, pageNumber, pageSize);

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
				statementsSubject.setType(subject.getSemanticGroup().name());
			}

			if (relation != null) {
				statementsPredicate.setId(relation.getId());
				statementsPredicate.setName(relation.getName());
			}

			if (object != null) {
				statementsObject.setId(object.getId());
				statementsObject.setName(object.getName());
				statementsObject.setType(object.getSemanticGroup().name());
			}

			response.setObject(statementsObject);
			response.setSubject(statementsSubject);
			response.setPredicate(statementsPredicate);

			responses.add(response);
		}

		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<BeaconConceptType>> getConceptTypes() {
		List<BeaconConceptType> responses = new ArrayList<BeaconConceptType>();
		
		List<Map<String, Object>> counts = conceptRepository.countAllGroupBySemanticGroup();
		
		for (Map<String, Object> map : counts) {
			BeaconConceptType response = new BeaconConceptType();
			response.setId((String) map.get("type"));
			response.setFrequency((Integer) map.get("frequency"));
			
			responses.add(response);
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
			
			Integer frequency = (Integer) kmap.get("frequency");
			String objectType = (String) triple.get("objectType");
			String relationName = (String) triple.get("relationName");
			String subjectType = (String) triple.get("subjectType");
			
			
			BeaconKnowledgeMapStatement knowledgeMapStatement = new BeaconKnowledgeMapStatement();
			BeaconKnowledgeMapPredicate predicate = new BeaconKnowledgeMapPredicate();
			BeaconKnowledgeMapObject object = new BeaconKnowledgeMapObject();
			BeaconKnowledgeMapSubject subject = new BeaconKnowledgeMapSubject();
			
			object.setType(objectType);
			subject.setType(subjectType);
			predicate.setName(relationName);
			
			knowledgeMapStatement.setFrequency(frequency);
			knowledgeMapStatement.setObject(object);
			knowledgeMapStatement.setSubject(subject);
			knowledgeMapStatement.setPredicate(predicate);
			
			knowledgeMapStatements.add(knowledgeMapStatement);
		}
		
		return ResponseEntity.ok(knowledgeMapStatements);
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> loadKmap() {
		try {
			FileInputStream fileIn = new FileInputStream(KMAP_PATH);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Object object = in.readObject();
			List<Map<String, Object>> kmap = (List<Map<String, Object>>) object;
			
			return kmap;
			
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}
	}
	
	private void saveKmap(List<Map<String, Object>> kmap) {
		try {
			FileOutputStream fileOut = new FileOutputStream(KMAP_PATH);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(kmap);
		} catch (IOException e) {
			
		}
	}
	
}