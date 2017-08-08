package de.digitalcollections.iiif.presentation.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import de.digitalcollections.iiif.presentation.model.sharedcanvas.Content;

@JsonTypeInfo(use = Id.NAME,
    property = "@type",
    include = As.EXISTING_PROPERTY)
@JsonSubTypes({
    @JsonSubTypes.Type(value = GenericContent.class),
})
public class GenericContent extends Content {
  @JsonCreator
  public GenericContent(@JsonProperty("@id") String identifier,
                        @JsonProperty("format") String format) {
    super(identifier);
    this.setFormat(format);
  }
}
