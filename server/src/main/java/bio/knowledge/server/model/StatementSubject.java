package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * StatementSubject
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-13T16:45:32.456-07:00")

public class StatementSubject   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("semgroup")
  private String semgroup = null;

  public StatementSubject id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier of concept 
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier of concept ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public StatementSubject name(String name) {
    this.name = name;
    return this;
  }

   /**
   * human readable label of subject concept
   * @return name
  **/
  @ApiModelProperty(value = "human readable label of subject concept")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public StatementSubject semgroup(String semgroup) {
    this.semgroup = semgroup;
    return this;
  }

   /**
   * a semantic group for the subject concept (specified as a code CHEM, GENE, etc. - see [SemGroups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes) 
   * @return semgroup
  **/
  @ApiModelProperty(value = "a semantic group for the subject concept (specified as a code CHEM, GENE, etc. - see [SemGroups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes) ")
  public String getSemgroup() {
    return semgroup;
  }

  public void setSemgroup(String semgroup) {
    this.semgroup = semgroup;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StatementSubject statementSubject = (StatementSubject) o;
    return Objects.equals(this.id, statementSubject.id) &&
        Objects.equals(this.name, statementSubject.name) &&
        Objects.equals(this.semgroup, statementSubject.semgroup);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, semgroup);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StatementSubject {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    semgroup: ").append(toIndentedString(semgroup)).append("\n");
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

