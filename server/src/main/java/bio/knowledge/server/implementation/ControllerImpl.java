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
import bio.knowledge.database.repository.StatementRepository;
import bio.knowledge.model.Annotation;
import bio.knowledge.model.Concept;
import bio.knowledge.model.Predicate;
import bio.knowledge.model.Statement;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.server.model.InlineResponse200;
import bio.knowledge.server.model.InlineResponse2001;
import bio.knowledge.server.model.InlineResponse2002;
import bio.knowledge.server.model.InlineResponse2003;
import bio.knowledge.server.model.InlineResponse2004;
import bio.knowledge.server.model.StatementsObject;
import bio.knowledge.server.model.StatementsPredicate;
import bio.knowledge.server.model.StatementsSubject;
import bio.knowledge.server.utilities.Utilities;

@Controller
public class ControllerImpl {
	
	@Autowired ConceptRepository conceptRepository;
	@Autowired StatementRepository statementRepository;
	@Autowired EvidenceRepository evidenceRepository;
	
	public ResponseEntity<List<InlineResponse2002>> getConcepts(
			String keywords,
			String semgroups,
			Integer pageNumber,
			Integer pageSize
	) {
		pageNumber = Utilities.fixPageNumber(pageNumber);
		pageSize = Utilities.fixPageSize(pageSize);
		keywords = Utilities.urlDecode(keywords);
		semgroups = Utilities.urlDecode(semgroups);

		String[] filter = Utilities.buildArray(keywords);
		String[] semanticGroups = Utilities.buildArray(semgroups);

		List<Neo4jConcept> concepts = conceptRepository.apiGetConcepts(filter, semanticGroups, pageNumber, pageSize);
		List<InlineResponse2002> responses = new ArrayList<InlineResponse2002>();
		
		for (Concept concept : concepts) {
			InlineResponse2002 response = new InlineResponse2002();
			
			response.setId(concept.getId());
			response.setName(concept.getName());
			response.setSemanticGroup(concept.getSemanticGroup().name());
			response.setDefinition(concept.getDescription());
			response.setSynonyms(Arrays.asList(concept.getSynonyms().split("\\|")));

			responses.add(response);
		}

		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<InlineResponse2001>> getConceptDetails(String conceptId) {
		conceptId = Utilities.urlDecode(conceptId);
		
		List<InlineResponse2001> responses = new ArrayList<InlineResponse2001>();
		Concept concept = conceptRepository.apiGetConceptById(conceptId);
		
		if (concept != null) {
			InlineResponse2001 response = new InlineResponse2001();
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
	
	public ResponseEntity<List<InlineResponse2004>> getEvidence(String statementId,
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
		
		List<InlineResponse2004> responses = new ArrayList<InlineResponse2004>();
		
		for (Map<String, Object> entry : data) {
			String year = String.valueOf((Integer) entry.get("year"));
			String month = String.valueOf((Integer) entry.get("month"));
			String day = String.valueOf((Integer) entry.get("day"));
			Annotation annotation = (Annotation) entry.get("annotation");
			
			InlineResponse2004 response = new InlineResponse2004();
			response.setId(annotation.getId());
			response.setLabel(annotation.getName());
			response.setDate(year + "-" + month + "-" + day);
			
			responses.add(response);
		}
		
		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<InlineResponse2003>> getStatements(
			List<String> c,
			Integer pageNumber,
			Integer pageSize,
			String keywords,
			String semgroups
	) {
		pageNumber = Utilities.fixPageNumber(pageNumber);
		pageSize = Utilities.fixPageSize(pageSize);
		c = Utilities.urlDecode(c);
		keywords = Utilities.urlDecode(keywords);
		semgroups = Utilities.urlDecode(semgroups);

		String[] curies = c.toArray(new String[c.size()]);
		String[] filter = Utilities.buildArray(keywords);
		String[] semanticGroups = Utilities.buildArray(semgroups);

		List<InlineResponse2003> responses = new ArrayList<InlineResponse2003>();

		List<Map<String, Object>> data = statementRepository.apiFindById(curies, filter, semanticGroups, pageNumber, pageSize);

		for (Map<String, Object> entry : data) {
			InlineResponse2003 response = new InlineResponse2003();

			StatementsObject statementsObject = new StatementsObject();
			StatementsSubject statementsSubject = new StatementsSubject();
			StatementsPredicate statementsPredicate = new StatementsPredicate();

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

			if (object != null) {
				statementsObject.setId(object.getId());
				statementsObject.setName(object.getName());
			}

			if (subject != null) {
				statementsSubject.setId(subject.getId());
				statementsSubject.setName(subject.getName());
			}

			if (relation != null) {
				statementsPredicate.setId(relation.getId());
				statementsPredicate.setName(relation.getName());
			}

			response.setObject(statementsObject);
			response.setSubject(statementsSubject);
			response.setPredicate(statementsPredicate);

			responses.add(response);
		}

		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<InlineResponse200>> linkedTypes() {
		List<InlineResponse200> responses = new ArrayList<InlineResponse200>();
		
		List<Map<String, Object>> counts = conceptRepository.countAllGroupBySemanticGroup();
		
		for (Map<String, Object> map : counts) {
			InlineResponse200 response = new InlineResponse200();
			response.setId((String) map.get("type"));
			response.setFrequency((Integer) map.get("frequency"));
			
			responses.add(response);
		}
		
        return ResponseEntity.ok(responses);
    }
	
}