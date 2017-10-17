package bio.knowledge.server.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import bio.knowledge.server.model.ServerAnnotation;
import bio.knowledge.server.model.ServerConcept;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerPredicate;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementObject;
import bio.knowledge.server.model.ServerStatementPredicate;
import bio.knowledge.server.model.ServerStatementSubject;
import bio.knowledge.server.model.ServerSummary;
import bio.knowledge.server.utilities.Utilities;

@Controller
public class ControllerImpl {
	
	@Autowired ConceptRepository conceptRepository;
	@Autowired PredicateRepository predicateRepository;
	@Autowired StatementRepository statementRepository;
	@Autowired EvidenceRepository evidenceRepository;
	
	public ResponseEntity<List<ServerConcept>> getConcepts(
			String keywords,
			String semanticGroups,
			Integer pageNumber,
			Integer pageSize
	) {
		pageNumber = Utilities.fixPageNumber(pageNumber);
		pageSize = Utilities.fixPageSize(pageSize);
		keywords = Utilities.urlDecode(keywords);
		semanticGroups = Utilities.urlDecode(semanticGroups);

		String[] filter = Utilities.buildArray(keywords);
		String[] semanticGroupFilter = Utilities.buildArray(semanticGroups);

		List<Neo4jConcept> concepts = conceptRepository.apiGetConcepts(filter, semanticGroupFilter, pageNumber, pageSize);
		List<ServerConcept> responses = new ArrayList<ServerConcept>();
		
		for (Concept concept : concepts) {
			ServerConcept response = new ServerConcept();
			
			response.setId(concept.getId());
			response.setName(concept.getName());
			response.setSemanticGroup(concept.getSemanticGroup().name());
			response.setDefinition(concept.getDescription());
			response.setSynonyms(Arrays.asList(concept.getSynonyms().split("\\|")));

			responses.add(response);
		}

		return ResponseEntity.ok(responses);
	}

	public ResponseEntity<List<ServerPredicate>> getPredicates() {
		
		List<Neo4jPredicate> data = predicateRepository.findAllPredicates();
				
		List<ServerPredicate> responses = new ArrayList<>();
		for (Predicate predicate : data) {
			
			ServerPredicate response = new ServerPredicate();
			response.setId(predicate.getId());
			response.setName(predicate.getName());
			response.setDefinition(predicate.getDescription());
			
			responses.add(response);
		}
		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<ServerConceptWithDetails>> getConceptDetails(String conceptId) {
		conceptId = Utilities.urlDecode(conceptId);
		
		List<ServerConceptWithDetails> responses = new ArrayList<ServerConceptWithDetails>();
		Concept concept = conceptRepository.apiGetConceptById(conceptId);
		
		if (concept != null) {
			ServerConceptWithDetails response = new ServerConceptWithDetails();
			response.setDefinition(concept.getDescription());
			response.setId(concept.getId());
			response.setName(concept.getName());
			String semanticType = concept.getSemanticGroup().name();
			response.setSemanticGroup(semanticType);
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
	
	public ResponseEntity<List<ServerAnnotation>> getEvidence(String statementId,
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
		
		List<ServerAnnotation> responses = new ArrayList<ServerAnnotation>();
		
		for (Map<String, Object> entry : data) {
			String year = String.valueOf((Integer) entry.get("year"));
			String month = String.valueOf((Integer) entry.get("month"));
			String day = String.valueOf((Integer) entry.get("day"));
			Annotation annotation = (Annotation) entry.get("annotation");
			
			ServerAnnotation response = new ServerAnnotation();
			response.setId(annotation.getId());
			response.setLabel(annotation.getName());
			response.setDate(year + "-" + month + "-" + day);
			
			responses.add(response);
		}
		
		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<ServerStatement>> getStatements(
			List<String> c,
			Integer pageNumber,
			Integer pageSize,
			String keywords,
			String semanticGroups, 
			String relations
	) {
		pageNumber = Utilities.fixPageNumber(pageNumber);
		pageSize = Utilities.fixPageSize(pageSize);
		c = Utilities.urlDecode(c);
		keywords = Utilities.urlDecode(keywords);
		semanticGroups = Utilities.urlDecode(semanticGroups);
		relations = Utilities.urlDecode(relations);

		String[] curies = c.toArray(new String[c.size()]);
		String[] filter = Utilities.buildArray(keywords);
		String[] semanticFilter = Utilities.buildArray(semanticGroups);
		String[] predicateFilter = Utilities.buildArray(relations);

		List<ServerStatement> responses = new ArrayList<ServerStatement>();

		List<Map<String, Object>> data = statementRepository.apiFindById(curies, filter, semanticFilter, predicateFilter, pageNumber, pageSize);

		for (Map<String, Object> entry : data) {
			ServerStatement response = new ServerStatement();

			ServerStatementObject statementsObject = new ServerStatementObject();
			ServerStatementPredicate statementsPredicate = new ServerStatementPredicate();
			ServerStatementSubject statementsSubject = new ServerStatementSubject();

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
				statementsSubject.setSemanticGroup(subject.getSemanticGroup().name());
			}

			if (relation != null) {
				statementsPredicate.setId(relation.getId());
				statementsPredicate.setName(relation.getName());
			}

			if (object != null) {
				statementsObject.setId(object.getId());
				statementsObject.setName(object.getName());
				statementsObject.setSemanticGroup(object.getSemanticGroup().name());
			}

			response.setObject(statementsObject);
			response.setSubject(statementsSubject);
			response.setPredicate(statementsPredicate);

			responses.add(response);
		}

		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<ServerSummary>> linkedTypes() {
		List<ServerSummary> responses = new ArrayList<ServerSummary>();
		
		List<Map<String, Object>> counts = conceptRepository.countAllGroupBySemanticGroup();
		
		for (Map<String, Object> map : counts) {
			ServerSummary response = new ServerSummary();
			response.setId((String) map.get("type"));
			response.setFrequency((Integer) map.get("frequency"));
			
			responses.add(response);
		}
		
        return ResponseEntity.ok(responses);
    }
	
}