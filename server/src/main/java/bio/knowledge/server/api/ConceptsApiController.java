package bio.knowledge.server.api;

import bio.knowledge.server.implementation.ControllerImpl;
import bio.knowledge.server.model.BeaconConcept;
import bio.knowledge.server.model.BeaconConceptWithDetails;

import io.swagger.annotations.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-08T20:39:58.606-07:00")

@Controller
public class ConceptsApiController implements ConceptsApi {
	
	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<BeaconConceptWithDetails>> getConceptDetails(@ApiParam(value = "(url-encoded) CURIE identifier of concept of interest",required=true ) @PathVariable("conceptId") String conceptId) {
    	return ctrl.getConceptDetails(conceptId);
    }

    public ResponseEntity<List<BeaconConcept>> getConcepts( @NotNull @ApiParam(value = "an array of keywords or substrings against which to match concept names and synonyms", required = true) @RequestParam(value = "keywords", required = true) List<String> keywords,
         @ApiParam(value = "an array set of concept categories - specified as Biolink name labels codes gene, pathway, etc. - to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of terms) ") @RequestParam(value = "categories", required = false) List<String> categories,
         @ApiParam(value = "maximum number of concept entries requested by the query (default 100) ") @RequestParam(value = "size", required = false) Integer size) {
    	return ctrl.getConcepts(keywords, categories, size);
    }

}
