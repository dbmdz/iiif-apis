package de.digitalcollections.iiif.presentation.model.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.digitalcollections.iiif.presentation.model.enums.ImageAPIProfile;
import java.net.URI;

@JsonTypeInfo(use = Id.NAME,
    property = "@context",
    include = As.EXISTING_PROPERTY)
@JsonTypeName(ImageService.CONTEXT)
public class ImageService extends Service {
  public static final String CONTEXT = "http://iiif.io/api/image/2/context.json";


  @JsonCreator
  public ImageService(@JsonProperty("@id") String identifier,
                      @JsonProperty("profile") ImageAPIProfile profile) {
    super(CONTEXT);
    this.setIdentifier(URI.create(identifier));
    this.setProfile(profile.getUri());
  }

  // TODO: Additional properties
}
