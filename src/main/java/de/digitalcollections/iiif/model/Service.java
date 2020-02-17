package de.digitalcollections.iiif.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.Lists;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Abstract base type for services. */
@JsonPropertyOrder({"@context", "@id", "@type"})
public abstract class Service {

  @JsonProperty("@context")
  private URI context;

  @JsonProperty("@id")
  private URI identifier;

  @JsonProperty("profile")
  private List<Profile> profiles;

  private PropertyValue label;

  @JsonCreator
  public Service(@JsonProperty("@context") URI context) {
    this.context = context;
  }

  public Service(URI context, String identifier) {
    this(context);
    this.identifier = URI.create(identifier);
  }

  public URI getContext() {
    return context;
  }

  public void setContext(URI context) {
    this.context = context;
  }

  public URI getIdentifier() {
    return identifier;
  }

  public void setIdentifier(URI identifier) {
    this.identifier = identifier;
  }

  public List<Profile> getProfiles() {
    return profiles;
  }

  public void setProfiles(List<Profile> profile) {
    this.profiles = profile;
  }

  public Service addProfile(Profile first, Profile... rest) {
    if (this.profiles == null) {
      this.profiles = new ArrayList<>();
    }
    this.profiles.addAll(Lists.asList(first, rest));
    return this;
  }

  public Service addProfile(String first, String... rest) {
    return this.addProfile(
        new Profile(URI.create(first)),
        Arrays.stream(rest).map(p -> new Profile(URI.create(p))).toArray(Profile[]::new));
  }

  public PropertyValue getLabel() {
    return label;
  }

  @JsonIgnore
  public String getLabelString() {
    return label.getFirstValue();
  }

  public void setLabel(PropertyValue label) {
    this.label = label;
  }

  public void setLabel(String label) {
    setLabel(new PropertyValue(label));
  }
}
