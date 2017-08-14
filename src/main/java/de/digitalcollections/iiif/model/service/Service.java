package de.digitalcollections.iiif.model.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.digitalcollections.iiif.model.PropertyValue;
import java.net.URI;

@JsonPropertyOrder({"@context", "@id", "@type"})
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

  public Service(String context, String identifier) {
    this(context);
    this.identifier = URI.create(identifier);
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

  public void setProfile(String profile) {
    setProfile(URI.create(profile));
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
