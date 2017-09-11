package de.digitalcollections.iiif.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * An entry in a IIIF resource's "metadata" list.
 *
 * Consists of a label and a value, both of which can be multi-valued and multi-lingual (see {@link PropertyValue}.
 * The value should be either simple HTML, including links and text markup, or plain text, and the label should be plain text.
 */
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

  @Override
  public String toString() {
    return String.format("MetadataEntry(label=[%s],value=[%s]",
                         getLabel().toString(), getValue().toString());
  }
}
