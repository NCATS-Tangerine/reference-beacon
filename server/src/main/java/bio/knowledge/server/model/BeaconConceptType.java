package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * BeaconConceptType
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-01T11:12:55.456-08:00")

public class BeaconConceptType   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("idmap")
  private String idmap = null;

  @JsonProperty("frequency")
  private Integer frequency = null;

  public BeaconConceptType id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the type  
   * @return id
  **/
  @ApiModelProperty(value = "the type  ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BeaconConceptType idmap(String idmap) {
    this.idmap = idmap;
    return this;
  }

   /**
   * The URL to execute the exactmatches API call on the id
   * @return idmap
  **/
  @ApiModelProperty(value = "The URL to execute the exactmatches API call on the id")
  public String getIdmap() {
    return idmap;
  }

  public void setIdmap(String idmap) {
    this.idmap = idmap;
  }

  public BeaconConceptType frequency(Integer frequency) {
    this.frequency = frequency;
    return this;
  }

   /**
   * the number of instances of the specified type 
   * @return frequency
  **/
  @ApiModelProperty(value = "the number of instances of the specified type ")
  public Integer getFrequency() {
    return frequency;
  }

  public void setFrequency(Integer frequency) {
    this.frequency = frequency;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeaconConceptType beaconConceptType = (BeaconConceptType) o;
    return Objects.equals(this.id, beaconConceptType.id) &&
        Objects.equals(this.idmap, beaconConceptType.idmap) &&
        Objects.equals(this.frequency, beaconConceptType.frequency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, idmap, frequency);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconConceptType {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    idmap: ").append(toIndentedString(idmap)).append("\n");
    sb.append("    frequency: ").append(toIndentedString(frequency)).append("\n");
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

