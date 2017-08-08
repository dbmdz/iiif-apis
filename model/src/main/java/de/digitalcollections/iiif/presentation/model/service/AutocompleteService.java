package de.digitalcollections.iiif.presentation.model.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.net.URI;

@JsonTypeInfo(use = Id.NAME,
    property = "profile",
    include = As.EXISTING_PROPERTY)
@JsonTypeName(AutocompleteService.PROFILE)
public class AutocompleteService extends Service {
  public final static String PROFILE = "http://iiif.io/api/search/1/autocomplete";

  @JsonCreator
  public AutocompleteService(@JsonProperty("@id") URI identifier) {
    // Doesn't need a context, since it is always a child of ContentSearchService
    super(null);
    this.setProfile(URI.create(PROFILE));
    this.setIdentifier(identifier);
  }
}
