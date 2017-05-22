package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import bio.knowledge.server.implementation.ControllerImpl;
import bio.knowledge.server.model.InlineResponse200;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-15T14:54:43.522-07:00")

@Controller
public class TypesApiController implements TypesApi {

	@Autowired
	ControllerImpl ctrl;

    public ResponseEntity<List<InlineResponse200>> linkedTypes() {
    	return ctrl.linkedTypes();
    }

}
