package bio.knowledge.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * BeaconKnowledgeMapObject
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-09T10:28:16.800-08:00")

public class BeaconKnowledgeMapObject   {
  @JsonProperty("type")
  private String type = null;

  @JsonProperty("prefixes")
  private List<String> prefixes = new ArrayList<String>();

  public BeaconKnowledgeMapObject type(String type) {
    this.type = type;
    return this;
  }

   /**
   * the concept semantic type of a statement object 
   * @return type
  **/
  @ApiModelProperty(value = "the concept semantic type of a statement object ")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public BeaconKnowledgeMapObject prefixes(List<String> prefixes) {
    this.prefixes = prefixes;
    return this;
  }

  public BeaconKnowledgeMapObject addPrefixesItem(String prefixesItem) {
    this.prefixes.add(prefixesItem);
    return this;
  }

   /**
   * Get prefixes
   * @return prefixes
  **/
  @ApiModelProperty(value = "")
  public List<String> getPrefixes() {
    return prefixes;
  }

  public void setPrefixes(List<String> prefixes) {
    this.prefixes = prefixes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeaconKnowledgeMapObject beaconKnowledgeMapObject = (BeaconKnowledgeMapObject) o;
    return Objects.equals(this.type, beaconKnowledgeMapObject.type) &&
        Objects.equals(this.prefixes, beaconKnowledgeMapObject.prefixes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, prefixes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconKnowledgeMapObject {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    prefixes: ").append(toIndentedString(prefixes)).append("\n");
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

