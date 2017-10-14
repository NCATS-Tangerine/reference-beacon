package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.implementation.ControllerImpl;
import bio.knowledge.server.model.ServerAnnotation;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-13T16:45:32.456-07:00")

@Controller
public class EvidenceApiController implements EvidenceApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ServerAnnotation>> getEvidence(@ApiParam(value = "(url-encoded) CURIE identifier of the concept-relationship statement (\"assertion\", \"claim\") for which associated evidence is sought ",required=true ) @PathVariable("statementId") String statementId,
         @ApiParam(value = "(url-encoded, space delimited) keyword filter to apply against the label field of the annotation ") @RequestParam(value = "keywords", required = false) String keywords,
         @ApiParam(value = "(1-based) number of the page to be returned in a paged set of query results ") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
         @ApiParam(value = "number of cited references per page to be returned in a paged set of query results ") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
         return ctrl.getEvidence(statementId, keywords, pageNumber, pageSize);
    }

}
