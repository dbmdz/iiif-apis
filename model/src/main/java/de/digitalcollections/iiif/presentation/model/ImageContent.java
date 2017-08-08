package de.digitalcollections.iiif.presentation.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.digitalcollections.core.model.api.MimeType;
import de.digitalcollections.iiif.presentation.model.service.ImageService;
import de.digitalcollections.iiif.presentation.model.sharedcanvas.Content;

@JsonTypeInfo(use = Id.NAME,
    property = "@type",
    include = As.EXISTING_PROPERTY)
@JsonSubTypes({
    @JsonSubTypes.Type(value= ImageContent.class, name="dctypes:Image"),
})
@JsonTypeName(ImageContent.TYPE)
public class ImageContent extends Content {
  public final static String TYPE = "dctypes:Image";

  // We sometimes want to set this to null during serialization, hence the copy in an instance variable
  public String _type = TYPE;

  @JsonCreator
  public ImageContent(@JsonProperty("@id") String identifier) {
    super(identifier);
    // Since the image ID is supposed to resolve to a real image, we can try guessing the format from it
    this.setFormat(MimeType.fromURI(this.getIdentifier()));
  }

  public ImageContent(ImageService service) {
    this(String.format("%s/full/full/0/default.jpg", service.getIdentifier()));
    this.addService(service);
  }

  @Override
  public String getType() {
    return _type;
  }

}
