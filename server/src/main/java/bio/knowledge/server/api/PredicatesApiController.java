package bio.knowledge.server.api;

import bio.knowledge.server.model.InlineResponse2003;

import io.swagger.annotations.*;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-11T11:29:02.272-07:00")

@Controller
public class PredicatesApiController implements PredicatesApi {



    public ResponseEntity<List<InlineResponse2003>> getPredicates() {
        // do some magic!
        return new ResponseEntity<List<InlineResponse2003>>(HttpStatus.OK);
    }

}
