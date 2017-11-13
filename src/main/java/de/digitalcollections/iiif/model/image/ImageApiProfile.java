package de.digitalcollections.iiif.model.image;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import de.digitalcollections.core.model.api.MimeType;
import de.digitalcollections.iiif.model.Profile;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An Image API profile.
 *
 * Can be either a simple pre-defined profile (e.g. {@link ImageApiProfile#LEVEL_ZERO}) or a complex profile
 * describing the available features of a given IIIF image.
 *
 * See http://iiif.io/api/image/2.1/#profile-description for an overview of all available properties and features
 * and http://iiif.io/api/image/2.1/compliance/ for an overview of the available compliance levels.
 */
public class ImageApiProfile extends Profile {
  public enum Format {
    JPG(MimeType.fromTypename("image/jpeg")),
    TIF(MimeType.fromTypename("image/tif")),
    PNG(MimeType.fromTypename("image/png")),
    GIF(MimeType.fromTypename("image/gif")),
    JP2(MimeType.fromTypename("image/jp2")),
    PDF(MimeType.fromTypename("application/pdf")),
    WEBP(MimeType.fromTypename("image/webp"));

    private final MimeType mimeType;

    Format(MimeType mimeType) {
      this.mimeType = mimeType;
    }

    public MimeType getMimeType() {
      return mimeType;
    }

    @JsonValue
    @Override
    public String toString() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
    }
  }

  public enum Quality {
    COLOR, GRAY, BITONAL, DEFAULT;

    @JsonValue
    @Override
    public String toString() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
    }
  }

  public static class Feature {
    private enum ImageApiFeature {
      BASE_URI_REDIRECT,  CANONICAL_LINK_HEADER,  CORS,  JSONLD_MEDIA_TYPE,  MIRRORING,  PROFILE_LINK_HEADER,
      REGION_BY_PCT,  REGION_BY_PX,  REGION_SQUARE, ROTATION_ARBITRARY,  ROTATION_BY_90S,  SIZE_ABOVE_FULL,
      SIZE_BY_WH_LISTED, SIZE_BY_FORCED_WH,  SIZE_BY_H,  SIZE_BY_PCT, SIZE_BY_W,  SIZE_BY_WH, SIZE_BY_CONFINED_WH,
      SIZE_BY_DISTORTED_WH, OTHER
    }

    public static final Feature BASE_URI_REDIRECT = new Feature(ImageApiFeature.BASE_URI_REDIRECT);
    public static final Feature CANONICAL_LINK_HEADER = new Feature(ImageApiFeature.CANONICAL_LINK_HEADER);
    public static final Feature CORS = new Feature(ImageApiFeature.CORS);
    public static final Feature JSONLD_MEDIA_TYPE = new Feature(ImageApiFeature.JSONLD_MEDIA_TYPE);
    public static final Feature MIRRORING = new Feature(ImageApiFeature.MIRRORING);
    public static final Feature PROFILE_LINK_HEADER = new Feature(ImageApiFeature.PROFILE_LINK_HEADER);
    public static final Feature REGION_BY_PCT = new Feature(ImageApiFeature.REGION_BY_PCT);
    public static final Feature REGION_BY_PX = new Feature(ImageApiFeature.REGION_BY_PX);
    public static final Feature REGION_SQUARE = new Feature(ImageApiFeature.REGION_SQUARE);
    public static final Feature ROTATION_ARBITRARY = new Feature(ImageApiFeature.ROTATION_ARBITRARY);
    public static final Feature ROTATION_BY_90S = new Feature(ImageApiFeature.ROTATION_BY_90S);
    public static final Feature SIZE_ABOVE_FULL = new Feature(ImageApiFeature.SIZE_ABOVE_FULL);
    public static final Feature SIZE_BY_WH_LISTED = new Feature(ImageApiFeature.SIZE_BY_WH_LISTED);
    public static final Feature SIZE_BY_FORCED_WH = new Feature(ImageApiFeature.SIZE_BY_FORCED_WH);
    public static final Feature SIZE_BY_H = new Feature(ImageApiFeature.SIZE_BY_H);
    public static final Feature SIZE_BY_PCT = new Feature(ImageApiFeature.SIZE_BY_PCT);
    public static final Feature SIZE_BY_W = new Feature(ImageApiFeature.SIZE_BY_W);
    public static final Feature SIZE_BY_WH = new Feature(ImageApiFeature.SIZE_BY_WH);
    public static final Feature SIZE_BY_CONFINED_WH = new Feature(ImageApiFeature.SIZE_BY_CONFINED_WH);
    public static final Feature SIZE_BY_DISTORTED_WH = new Feature(ImageApiFeature.SIZE_BY_DISTORTED_WH);

    private final ImageApiFeature imageApiFeature;
    private final URI customFeature;

    @JsonCreator
    public Feature(String featureName) {
      if (featureName.startsWith("http://") || featureName.startsWith("https://")) {
        this.imageApiFeature = ImageApiFeature.OTHER;
        this.customFeature = URI.create(featureName);
      } else {
        this.imageApiFeature = ImageApiFeature.valueOf(
            CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, featureName));
        this.customFeature = null;
      }
    }

    private Feature(ImageApiFeature feature) {
      this.imageApiFeature = feature;
      this.customFeature = null;
    }

    @JsonValue
    @Override
    public String toString() {
      if (this.imageApiFeature == ImageApiFeature.OTHER) {
        return this.customFeature.toString();
      } else {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.imageApiFeature.name());
      }
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Feature)) {
        return false;
      }
      Feature other = (Feature) obj;
      return Objects.equals(this.imageApiFeature, other.imageApiFeature) &&
            Objects.equals(this.customFeature, other.customFeature);
    }
  }



  public static final ImageApiProfile LEVEL_ZERO = new ImageApiProfile("http://iiif.io/api/image/2/level0.json");
  public static final ImageApiProfile LEVEL_ONE = new ImageApiProfile("http://iiif.io/api/image/2/level1.json");
  public static final ImageApiProfile LEVEL_TWO = new ImageApiProfile("http://iiif.io/api/image/2/level2.json");

  @JsonProperty("@context")
  public static final String CONTEXT = "http://iiif.io/api/image/2/context.json";
  @JsonProperty("@type")
  public static final String TYPE = "iiif:ImageProfile";

  Set<Format> formats;
  Set<Quality> qualities;

  @JsonProperty("supports")
  Set<Feature> features;

  Integer maxArea;
  Integer maxHeight;
  Integer maxWidth;

  public ImageApiProfile() {
    super(null);
  }

  public ImageApiProfile(String url) {
    super(URI.create(url));
  }

  public Set<Format> getFormats() {
    return formats;
  }

  public void setFormats(Set<Format> formats) {
    this.formats = formats;
  }

  public ImageApiProfile addFormat(Format first, Format... rest) {
    if (this.formats == null) {
      this.formats = new LinkedHashSet<>();
    }
    this.formats.addAll(Lists.asList(first, rest));
    return this;
  }

  public Set<Quality> getQualities() {
    return qualities;
  }

  public void setQualities(Set<Quality> qualities) {
    this.qualities = qualities;
  }

  public ImageApiProfile addQuality(Quality first, Quality... rest) {
    if (this.qualities == null) {
      this.qualities = new LinkedHashSet<>();
    }
    this.qualities.addAll(Lists.asList(first, rest));
    return this;
  }

  public Set<Feature> getFeatures() {
    return features;
  }

  public void setFeatures(Set<Feature> features) {
    this.features = features;
  }

  public ImageApiProfile addFeature(Feature first, Feature... rest) {
    if (this.features == null) {
      this.features = new LinkedHashSet<>();
    }
    this.features.addAll(Lists.asList(first, rest));
    return this;
  }

  public Integer getMaxArea() {
    return maxArea;
  }

  public void setMaxArea(Integer maxArea) {
    this.maxArea = maxArea;
  }

  public Integer getMaxHeight() {
    return maxHeight;
  }

  public void setMaxHeight(Integer maxHeight) {
    this.maxHeight = maxHeight;
  }

  public Integer getMaxWidth() {
    return maxWidth;
  }

  public void setMaxWidth(Integer maxWidth) {
    this.maxWidth = maxWidth;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ImageApiProfile that = (ImageApiProfile) o;

    if (formats != null ? !formats.equals(that.formats) : that.formats != null) {
      return false;
    }
    if (qualities != null ? !qualities.equals(that.qualities) : that.qualities != null) {
      return false;
    }
    return features != null ? features.equals(that.features) : that.features == null;
  }

  @Override
  public int hashCode() {
    int result = formats != null ? formats.hashCode() : 0;
    result = 31 * result + (qualities != null ? qualities.hashCode() : 0);
    result = 31 * result + (features != null ? features.hashCode() : 0);
    return result;
  }
}
