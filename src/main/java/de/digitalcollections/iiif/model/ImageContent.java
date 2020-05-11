package de.digitalcollections.iiif.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import java.net.URI;

/**
 * An image resource.
 *
 * <p>This entity is what is contained in the "resource" field of annotations with motivation
 * "sc:painting": http://iiif.io/api/presentation/2.1/#image-resources
 */
public class ImageContent extends Resource<ImageContent> {

  public static final String TYPE = "dctypes:Image";

  // We sometimes want to set this to null during serialization, hence the copy in an instance
  // variable
  @SuppressWarnings("checkstyle:membername")
  @JsonIgnore
  public String _type = TYPE;

  private MimeType format;
  private Integer width;
  private Integer height;
  private URI profile;

  @JsonCreator
  public ImageContent(@JsonProperty("@id") String identifier) {
    super(identifier);
    // Since the image ID is supposed to resolve to a real image, we can try guessing the format
    // from it
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

  public MimeType getFormat() {
    return format;
  }

  public void setFormat(MimeType format) {
    this.format = format;
  }

  public void setFormat(String format) {
    this.format = MimeType.fromTypename(format);
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public URI getProfile() {
    return profile;
  }

  public void setProfile(URI profile) {
    this.profile = profile;
  }
}
