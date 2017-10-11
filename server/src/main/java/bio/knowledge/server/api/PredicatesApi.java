package bio.knowledge.server.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import bio.knowledge.server.model.ServerPredicate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-11T11:29:02.272-07:00")

@Api(value = "predicates", description = "the predicates API")
public interface PredicatesApi {

    @ApiOperation(value = "", notes = "Get a list of predicates used in statements issued by the knowledge source ", response = ServerPredicate.class, responseContainer = "List", tags={ "predicates", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with types and frequency returned ", response = ServerPredicate.class) })
    @RequestMapping(value = "/predicates",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<ServerPredicate>> getPredicates();

}
