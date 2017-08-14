package de.digitalcollections.iiif.model.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GenericService extends Service {
  @JsonCreator
  public GenericService(@JsonProperty("@context") String context,
                        @JsonProperty("@id") String identifier) {
    super(context, identifier);
  }

  public GenericService(String context, String identifier, String profile) {
    super(context, identifier);
    super.setProfile(profile);
  }
}
