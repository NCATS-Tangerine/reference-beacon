package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.StatementsObject;
import bio.knowledge.server.model.StatementsPredicate;
import bio.knowledge.server.model.StatementsSubject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * InlineResponse2004
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-11T11:29:02.272-07:00")

public class InlineResponse2004   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("subject")
  private StatementsSubject subject = null;

  @JsonProperty("predicate")
  private StatementsPredicate predicate = null;

  @JsonProperty("object")
  private StatementsObject object = null;

  public InlineResponse2004 id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier for statement (can be used to retrieve associated evidence)
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier for statement (can be used to retrieve associated evidence)")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public InlineResponse2004 subject(StatementsSubject subject) {
    this.subject = subject;
    return this;
  }

   /**
   * Get subject
   * @return subject
  **/
  @ApiModelProperty(value = "")
  public StatementsSubject getSubject() {
    return subject;
  }

  public void setSubject(StatementsSubject subject) {
    this.subject = subject;
  }

  public InlineResponse2004 predicate(StatementsPredicate predicate) {
    this.predicate = predicate;
    return this;
  }

   /**
   * Get predicate
   * @return predicate
  **/
  @ApiModelProperty(value = "")
  public StatementsPredicate getPredicate() {
    return predicate;
  }

  public void setPredicate(StatementsPredicate predicate) {
    this.predicate = predicate;
  }

  public InlineResponse2004 object(StatementsObject object) {
    this.object = object;
    return this;
  }

   /**
   * Get object
   * @return object
  **/
  @ApiModelProperty(value = "")
  public StatementsObject getObject() {
    return object;
  }

  public void setObject(StatementsObject object) {
    this.object = object;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InlineResponse2004 inlineResponse2004 = (InlineResponse2004) o;
    return Objects.equals(this.id, inlineResponse2004.id) &&
        Objects.equals(this.subject, inlineResponse2004.subject) &&
        Objects.equals(this.predicate, inlineResponse2004.predicate) &&
        Objects.equals(this.object, inlineResponse2004.object);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, subject, predicate, object);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineResponse2004 {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    subject: ").append(toIndentedString(subject)).append("\n");
    sb.append("    predicate: ").append(toIndentedString(predicate)).append("\n");
    sb.append("    object: ").append(toIndentedString(object)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

