package de.digitalcollections.iiif.presentation.model.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import de.digitalcollections.iiif.presentation.model.PropertyValue;
import java.net.URI;

@JsonPropertyOrder({"@context", "@id", "@type"})
/*
@JsonTypeInfo(use = Id.NAME,
    property = "@context",
    include = As.EXISTING_PROPERTY,
    defaultImpl = GenericService.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ImageService.class, name=ImageService.CONTEXT),
    @JsonSubTypes.Type(value = ContentSearchService.class, name=ContentSearchService.CONTEXT),
})
  */
@JsonTypeInfo(use = Id.NAME,
    include = As.EXTERNAL_PROPERTY,
    defaultImpl = GenericService.class)
public abstract class Service {
  @JsonProperty("@context")
  private String context;

  @JsonProperty("@id")
  private URI identifier;

  private URI profile;

  private PropertyValue label;

  @JsonCreator
  public Service(@JsonProperty("@context") String context) {
    this.context = context;
  }

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public URI getIdentifier() {
    return identifier;
  }

  public void setIdentifier(URI identifier) {
    this.identifier = identifier;
  }

  public URI getProfile() {
    return profile;
  }

  public void setProfile(URI profile) {
    this.profile = profile;
  }

  public PropertyValue getLabel() {
    return label;
  }

  public void setLabel(PropertyValue label) {
    this.label = label;
  }

}
