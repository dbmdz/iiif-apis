package de.digitalcollections.iiif.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;

public class GenericService extends Service {
  @JsonCreator
  public GenericService(@JsonProperty("@context") URI context,
                        @JsonProperty("@id") String identifier) {
    super(context, identifier);
  }

  public GenericService(String context, String identifier, String profile) {
    super(URI.create(context), identifier);
    super.addProfile(profile);
  }
}
