package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * Statement
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-11-04T11:27:59.578-07:00")

public class ServerStatement   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("subject")
  private ServerStatementSubject subject = null;

  @JsonProperty("predicate")
  private ServerStatementPredicate predicate = null;

  @JsonProperty("object")
  private ServerStatementObject object = null;

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

  public ServerStatement subject(ServerStatementSubject subject) {
    this.subject = subject;
    return this;
  }

   /**
   * Get subject
   * @return subject
  **/
  @ApiModelProperty(value = "")
  public ServerStatementSubject getSubject() {
    return subject;
  }

  public void setSubject(ServerStatementSubject subject) {
    this.subject = subject;
  }

  public ServerStatement predicate(ServerStatementPredicate predicate) {
    this.predicate = predicate;
    return this;
  }

   /**
   * Get predicate
   * @return predicate
  **/
  @ApiModelProperty(value = "")
  public ServerStatementPredicate getPredicate() {
    return predicate;
  }

  public void setPredicate(ServerStatementPredicate predicate) {
    this.predicate = predicate;
  }

  public ServerStatement object(ServerStatementObject object) {
    this.object = object;
    return this;
  }

   /**
   * Get object
   * @return object
  **/
  @ApiModelProperty(value = "")
  public ServerStatementObject getObject() {
    return object;
  }

  public void setObject(ServerStatementObject object) {
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
    ServerStatement statement = (ServerStatement) o;
    return Objects.equals(this.id, statement.id) &&
        Objects.equals(this.subject, statement.subject) &&
        Objects.equals(this.predicate, statement.predicate) &&
        Objects.equals(this.object, statement.object);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, subject, predicate, object);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Statement {\n");
    
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

