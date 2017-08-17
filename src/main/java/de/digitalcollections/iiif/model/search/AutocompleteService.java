package de.digitalcollections.iiif.model.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.digitalcollections.iiif.model.service.Service;
import java.net.URI;

public class AutocompleteService extends Service {
  public final static String PROFILE = "http://iiif.io/api/search/1/autocomplete";

  @JsonCreator
  public AutocompleteService(@JsonProperty("@id") URI identifier) {
    // Doesn't need a context, since it is always a child of ContentSearchService
    super(null);
    this.addProfile(PROFILE);
    this.setIdentifier(identifier);
  }
}
