package de.digitalcollections.iiif.model.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.digitalcollections.iiif.model.interfaces.Selector;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Format;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Quality;
import java.net.URI;
import java.util.Objects;

// TODO: This should perform validation of the Image API parameters
@JsonTypeName(ImageApiSelector.TYPE)
public class ImageApiSelector implements Selector {
  public static String CONTEXT = "http://iiif.io/api/annex/openannotation/context.json";
  public static final String TYPE = "iiif:ImageApiSelector";

  private String region;
  private String size;
  private String rotation;
  private Quality quality;
  private Format format;

  @JsonProperty("@context")
  public String getContext() {
    return CONTEXT;
  }

  @JsonProperty("@type")
  public String getType() {
    return TYPE;
  }

  public URI asImageApiUri(URI baseUri) {
    String baseUriString = baseUri.toString();
    if (!baseUriString.endsWith("/")) {
      baseUriString += "/";
    }
    return baseUri.resolve(String.format(
        "%s%s/%s/%s/%s.%s",
        baseUriString,
        Objects.toString(region, "full"),
        Objects.toString(size, "full"),
        Objects.toString(rotation, "0"),
        Objects.toString(quality, "default"),
        Objects.toString(format, "jpg")));
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getRotation() {
    return rotation;
  }

  public void setRotation(String rotation) {
    this.rotation = rotation;
  }

  public Quality getQuality() {
    return quality;
  }

  public void setQuality(Quality quality) {
    this.quality = quality;
  }

  public Format getFormat() {
    return format;
  }

  public void setFormat(Format format) {
    this.format = format;
  }
}
