package de.digitalcollections.iiif.presentation.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"label", "value"})
public class MetadataEntry {
  private PropertyValue label;
  private PropertyValue value;

  @JsonCreator
  public MetadataEntry(@JsonProperty("label") PropertyValue label,
                       @JsonProperty("value") PropertyValue value) {
    this.label = label;
    this.value = value;
  }

  public MetadataEntry(String label, String value) {
    this.label = new PropertyValue(label);
    this.value = new PropertyValue(value);
  }

  public PropertyValue getLabel() {
    return label;
  }

  public PropertyValue getValue() {
    return value;
  }

  @JsonIgnore
  public String getLabelString() {
    return label.getFirstValue();
  }

  @JsonIgnore
  public String getValueString() {
    return value.getFirstValue();
  }
}
