package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * BeaconStatementObject
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-08T20:39:58.606-07:00")

public class BeaconStatementObject   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("category")
  private String category = null;

  public BeaconStatementObject id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier of object concept 
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier of object concept ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BeaconStatementObject name(String name) {
    this.name = name;
    return this;
  }

   /**
   * human readable label of object concept
   * @return name
  **/
  @ApiModelProperty(value = "human readable label of object concept")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BeaconStatementObject category(String category) {
    this.category = category;
    return this;
  }

   /**
   * a semantic group for the object concept (specified as a code gene, pathway, disease, etc. - see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of categories) 
   * @return category
  **/
  @ApiModelProperty(value = "a semantic group for the object concept (specified as a code gene, pathway, disease, etc. - see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of categories) ")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeaconStatementObject beaconStatementObject = (BeaconStatementObject) o;
    return Objects.equals(this.id, beaconStatementObject.id) &&
        Objects.equals(this.name, beaconStatementObject.name) &&
        Objects.equals(this.category, beaconStatementObject.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, category);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconStatementObject {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
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

