package de.digitalcollections.iiif.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import java.net.URI;

/**
 * Type for Content resources such as images or texts that are associated with a canvas.
 *
 * <p>Used in the "related", "renderings" and "otherContent" fields of IIIF resources.
 */
@JsonPropertyOrder({"@id", "@type"})
public class OtherContent extends Resource<OtherContent> {

  private MimeType format;
  private Profile profile;
  private Integer width;
  private Integer height;

  @JsonProperty("@type")
  private String type;

  @JsonCreator
  public OtherContent(@JsonProperty("@id") String identifier) {
    super(identifier);
  }

  public OtherContent(String identifier, String format) {
    super(identifier);
    this.setFormat(format);
  }

  public OtherContent(String identifier, String format, String profile) {
    this(identifier);
    this.setFormat(format);
    this.setProfile(URI.create(profile));
  }

  @Override
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public MimeType getFormat() {
    if (format != null) {
      return format;
    } else {
      // Try to guess the format from the identifier
      return MimeType.fromURI(this.getIdentifier());
    }
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

  public Profile getProfile() {
    return profile;
  }

  public void setProfile(URI uri) {
    this.profile = new Profile(uri);
  }
}
