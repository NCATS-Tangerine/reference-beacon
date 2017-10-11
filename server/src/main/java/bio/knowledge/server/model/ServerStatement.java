package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * InlineResponse2004
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-11T11:29:02.272-07:00")

public class ServerStatement   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("subject")
  private ServerStatementsSubject subject = null;

  @JsonProperty("predicate")
  private ServerStatementsPredicate predicate = null;

  @JsonProperty("object")
  private ServerStatementsObject object = null;

  public ServerStatement id(String id) {
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

  public ServerStatement subject(ServerStatementsSubject subject) {
    this.subject = subject;
    return this;
  }

   /**
   * Get subject
   * @return subject
  **/
  @ApiModelProperty(value = "")
  public ServerStatementsSubject getSubject() {
    return subject;
  }

  public void setSubject(ServerStatementsSubject subject) {
    this.subject = subject;
  }

  public ServerStatement predicate(ServerStatementsPredicate predicate) {
    this.predicate = predicate;
    return this;
  }

   /**
   * Get predicate
   * @return predicate
  **/
  @ApiModelProperty(value = "")
  public ServerStatementsPredicate getPredicate() {
    return predicate;
  }

  public void setPredicate(ServerStatementsPredicate predicate) {
    this.predicate = predicate;
  }

  public ServerStatement object(ServerStatementsObject object) {
    this.object = object;
    return this;
  }

   /**
   * Get object
   * @return object
  **/
  @ApiModelProperty(value = "")
  public ServerStatementsObject getObject() {
    return object;
  }

  public void setObject(ServerStatementsObject object) {
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
    ServerStatement inlineResponse2004 = (ServerStatement) o;
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

