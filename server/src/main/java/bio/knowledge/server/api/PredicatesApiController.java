package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import bio.knowledge.server.implementation.ControllerImpl;
import bio.knowledge.server.model.ServerPredicate;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-11-02T20:37:45.588-07:00")

@Controller
public class PredicatesApiController implements PredicatesApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ServerPredicate>> getPredicates() {
         return ctrl.getPredicates();
    }

}
