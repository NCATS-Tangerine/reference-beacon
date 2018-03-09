package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import bio.knowledge.server.implementation.ControllerImpl;
import bio.knowledge.server.model.BeaconPredicate;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-09T10:28:16.800-08:00")

@Controller
public class PredicatesApiController implements PredicatesApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<BeaconPredicate>> getPredicates() {
         return ctrl.getPredicates();
    }

}
