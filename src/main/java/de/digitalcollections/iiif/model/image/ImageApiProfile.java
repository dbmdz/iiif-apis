package de.digitalcollections.iiif.model.image;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.MimeType;
import de.digitalcollections.iiif.model.Profile;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * An Image API profile.
 *
 * <p>Can be either a simple pre-defined profile (e.g. {@link ImageApiProfile#LEVEL_ZERO}) or a
 * complex profile describing the available features of a given IIIF image.
 *
 * <p>See http://iiif.io/api/image/2.1/#profile-description for an overview of all available
 * properties and features and http://iiif.io/api/image/2.1/compliance/ for an overview of the
 * available compliance levels.
 */
public class ImageApiProfile extends Profile {

  public enum Format {
    JPG(MimeType.fromTypename("image/jpeg")),
    TIF(MimeType.fromTypename("image/tiff")),
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
    COLOR,
    GRAY,
    BITONAL,
    DEFAULT;

    @JsonValue
    @Override
    public String toString() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
    }
  }

  public static class Feature {

    private enum ImageApiFeature {
      BASE_URI_REDIRECT,
      CANONICAL_LINK_HEADER,
      CORS,
      JSONLD_MEDIA_TYPE,
      MIRRORING,
      PROFILE_LINK_HEADER,
      REGION_BY_PCT,
      REGION_BY_PX,
      REGION_SQUARE,
      ROTATION_ARBITRARY,
      ROTATION_BY_90S,
      SIZE_ABOVE_FULL,
      SIZE_BY_WH_LISTED,
      SIZE_BY_FORCED_WH,
      SIZE_BY_H,
      SIZE_BY_PCT,
      SIZE_BY_W,
      SIZE_BY_WH,
      SIZE_BY_CONFINED_WH,
      SIZE_BY_DISTORTED_WH,
      OTHER
    }

    /** The base URI of the service will redirect to the image information document. * */
    public static final Feature BASE_URI_REDIRECT = new Feature(ImageApiFeature.BASE_URI_REDIRECT);

    /** The canonical image URI HTTP link header is provided on image responses. * */
    public static final Feature CANONICAL_LINK_HEADER =
        new Feature(ImageApiFeature.CANONICAL_LINK_HEADER);

    /** The CORS HTTP header is provided on all responses. * */
    public static final Feature CORS = new Feature(ImageApiFeature.CORS);

    /** The JSON-LD media type is provided when JSON-LD is requested. * */
    public static final Feature JSONLD_MEDIA_TYPE = new Feature(ImageApiFeature.JSONLD_MEDIA_TYPE);

    /**
     * The image may be rotated around the vertical axis, resulting in a left-to-right mirroring of
     * the content. *
     */
    public static final Feature MIRRORING = new Feature(ImageApiFeature.MIRRORING);

    /** The profile HTTP link header is provided on image responses. * */
    public static final Feature PROFILE_LINK_HEADER =
        new Feature(ImageApiFeature.PROFILE_LINK_HEADER);

    /** Regions of images may be requested by percentage. * */
    public static final Feature REGION_BY_PCT = new Feature(ImageApiFeature.REGION_BY_PCT);

    /** Regions of images may be requested by pixel dimensions. * */
    public static final Feature REGION_BY_PX = new Feature(ImageApiFeature.REGION_BY_PX);

    /**
     * A square region where the width and height are equal to the shorter dimension of the complete
     * image content. *
     */
    public static final Feature REGION_SQUARE = new Feature(ImageApiFeature.REGION_SQUARE);

    /** Rotation of images may be requested by degrees other than multiples of 90. * */
    public static final Feature ROTATION_ARBITRARY =
        new Feature(ImageApiFeature.ROTATION_ARBITRARY);

    /** Rotation of images may be requested by degrees in multiples of 90. * */
    public static final Feature ROTATION_BY_90S = new Feature(ImageApiFeature.ROTATION_BY_90S);

    /** Size of images may be requested larger than the “full” size. See warning. * */
    public static final Feature SIZE_ABOVE_FULL = new Feature(ImageApiFeature.SIZE_ABOVE_FULL);

    /** Size of images may be requested in the form “!w,h”. * */
    public static final Feature SIZE_BY_CONFINED_WH =
        new Feature(ImageApiFeature.SIZE_BY_CONFINED_WH);

    /**
     * Size of images may be requested in the form “w,h”, including sizes that would distort the
     * image. *
     */
    public static final Feature SIZE_BY_DISTORTED_WH =
        new Feature(ImageApiFeature.SIZE_BY_DISTORTED_WH);

    /** Size of images may be requested in the form “,h”. * */
    public static final Feature SIZE_BY_H = new Feature(ImageApiFeature.SIZE_BY_H);

    /** Size of images may be requested in the form “pct:n”.* */
    public static final Feature SIZE_BY_PCT = new Feature(ImageApiFeature.SIZE_BY_PCT);

    /** Size of images may be requested in the form “w,”. * */
    public static final Feature SIZE_BY_W = new Feature(ImageApiFeature.SIZE_BY_W);

    /**
     * Size of images may be requested in the form “w,h” where the supplied w and h preserve the
     * aspect ratio. *
     */
    public static final Feature SIZE_BY_WH = new Feature(ImageApiFeature.SIZE_BY_WH);

    @Deprecated
    public static final Feature SIZE_BY_WH_LISTED = new Feature(ImageApiFeature.SIZE_BY_WH_LISTED);

    @Deprecated
    public static final Feature SIZE_BY_FORCED_WH = new Feature(ImageApiFeature.SIZE_BY_FORCED_WH);

    private final ImageApiFeature imageApiFeature;
    private final URI customFeature;

    @JsonCreator
    public Feature(String featureName) {
      if (featureName.startsWith("http://") || featureName.startsWith("https://")) {
        this.imageApiFeature = ImageApiFeature.OTHER;
        this.customFeature = URI.create(featureName);
      } else {
        String name = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, featureName);
        name = name.replaceAll("([A-Z])(\\d)", "$1_$2");
        this.imageApiFeature = ImageApiFeature.valueOf(name);
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
      return Objects.equals(this.imageApiFeature, other.imageApiFeature)
          && Objects.equals(this.customFeature, other.customFeature);
    }
  }

  public static final ImageApiProfile LEVEL_ZERO =
      new ImageApiProfile("http://iiif.io/api/image/2/level0.json");

  static {
    LEVEL_ZERO.addFormat(Format.JPG);
    LEVEL_ZERO.addQuality(Quality.DEFAULT);
  }

  public static final ImageApiProfile LEVEL_ONE =
      new ImageApiProfile("http://iiif.io/api/image/2/level1.json");

  static {
    LEVEL_ONE.addFeature(
        Feature.REGION_BY_PX,
        Feature.SIZE_BY_W,
        Feature.SIZE_BY_H,
        Feature.SIZE_BY_PCT,
        Feature.BASE_URI_REDIRECT,
        Feature.CORS,
        Feature.JSONLD_MEDIA_TYPE);
    LEVEL_ONE.addFormat(Format.JPG);
    LEVEL_ONE.addQuality(Quality.DEFAULT);
  }

  public static final ImageApiProfile LEVEL_TWO =
      new ImageApiProfile("http://iiif.io/api/image/2/level2.json");

  static {
    LEVEL_TWO.addFeature(
        Feature.REGION_BY_PX,
        Feature.REGION_BY_PCT,
        Feature.SIZE_BY_W,
        Feature.SIZE_BY_H,
        Feature.SIZE_BY_PCT,
        Feature.SIZE_BY_CONFINED_WH,
        Feature.SIZE_BY_DISTORTED_WH,
        Feature.SIZE_BY_WH,
        Feature.ROTATION_BY_90S,
        Feature.BASE_URI_REDIRECT,
        Feature.CORS,
        Feature.JSONLD_MEDIA_TYPE);
    LEVEL_TWO.addFormat(Format.JPG, Format.PNG);
    LEVEL_TWO.addQuality(Quality.DEFAULT, Quality.COLOR, Quality.GRAY, Quality.BITONAL);
  }

  public static final Set<String> V1_PROFILES =
      ImmutableSet.of(
          "http://library.stanford.edu/iiif/image-api/compliance.html#level0",
          "http://library.stanford.edu/iiif/image-api/1.1/compliance.html#level0",
          "http://library.stanford.edu/iiif/image-api/1.1/conformance.html#level0",
          "http://library.stanford.edu/iiif/image-api/compliance.html#level1",
          "http://library.stanford.edu/iiif/image-api/1.1/compliance.html#level1",
          "http://library.stanford.edu/iiif/image-api/1.1/conformance.html#level1",
          "http://library.stanford.edu/iiif/image-api/compliance.html#level2",
          "http://library.stanford.edu/iiif/image-api/1.1/compliance.html#level2",
          "http://library.stanford.edu/iiif/image-api/1.1/conformance.html#level2");

  @JsonProperty("@context")
  public static final String CONTEXT = "http://iiif.io/api/image/2/context.json";

  @JsonProperty("@type")
  public static final String TYPE = "iiif:ImageProfile";

  private Set<Format> formats;
  private Set<Quality> qualities;

  @JsonProperty("supports")
  Set<Feature> features;

  Long maxArea;
  Integer maxHeight;
  Integer maxWidth;

  public static ImageApiProfile fromUrl(String url) {
    URI uri = URI.create(url);
    if (uri.equals(LEVEL_ZERO.getIdentifier())) {
      return LEVEL_ZERO;
    } else if (uri.equals(LEVEL_ONE.getIdentifier())) {
      return LEVEL_ONE;
    } else if (uri.equals(LEVEL_TWO.getIdentifier())) {
      return LEVEL_TWO;
    } else if (V1_PROFILES.contains(url)) {
      String lvl = url.split("#level")[1];
      switch (lvl) {
        case "0":
          return new ImageApiProfile(url, LEVEL_ZERO);
        case "1":
          return new ImageApiProfile(url, LEVEL_ONE);
        case "2":
          return new ImageApiProfile(url, LEVEL_TWO);
        default:
          break;
      }
    }
    return new ImageApiProfile(url);
  }

  public ImageApiProfile() {
    super(null);
  }

  private ImageApiProfile(String url) {
    super(URI.create(url));
  }

  private ImageApiProfile(String url, ImageApiProfile profile) {
    this(url);
    this.setFormats(profile.getFormats());
    this.setQualities(profile.getQualities());
    this.setFeatures(profile.getFeatures());
    this.setMaxWidth(profile.getMaxWidth());
    this.setMaxHeight(profile.getMaxHeight());
    this.setMaxArea(profile.getMaxArea());
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

  public Long getMaxArea() {
    return maxArea;
  }

  public void setMaxArea(Long maxArea) {
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

  /**
   * Merge multiple profiles into one. Useful for image servers that want to consolidate the limits
   * given in a info.json.
   *
   * @param profiles profiles to be merged
   * @return merged profile
   */
  public static ImageApiProfile merge(List<Profile> profiles) {
    return profiles.stream()
        .filter(ImageApiProfile.class::isInstance)
        .map(ImageApiProfile.class::cast)
        .reduce(new ImageApiProfile(), ImageApiProfile::merge);
  }

  /**
   * Merge two profiles.
   *
   * @param other profile to be merged
   * @return merged profile
   */
  public ImageApiProfile merge(ImageApiProfile other) {
    ImageApiProfile merged = new ImageApiProfile();
    streamNotNull(this.features).forEach(merged::addFeature);
    streamNotNull(other.features).forEach(merged::addFeature);
    streamNotNull(this.formats).forEach(merged::addFormat);
    streamNotNull(other.formats).forEach(merged::addFormat);
    streamNotNull(this.qualities).forEach(merged::addQuality);
    streamNotNull(other.qualities).forEach(merged::addQuality);
    if (this.maxWidth != null && other.maxWidth == null) {
      merged.maxWidth = this.maxWidth;
    } else if (this.maxWidth == null && other.maxWidth != null) {
      merged.maxWidth = other.maxWidth;
    } else if (this.maxWidth != null) {
      merged.maxWidth = Math.min(this.maxWidth, other.maxWidth);
    }
    if (this.maxHeight != null && other.maxHeight == null) {
      merged.maxHeight = this.maxHeight;
    } else if (this.maxHeight == null && other.maxHeight != null) {
      merged.maxHeight = other.maxHeight;
    } else if (this.maxHeight != null) {
      merged.maxHeight = Math.min(this.maxHeight, other.maxHeight);
    }
    if (this.maxArea != null && other.maxArea == null) {
      merged.maxArea = this.maxArea;
    } else if (this.maxArea == null && other.maxArea != null) {
      merged.maxArea = other.maxArea;
    } else if (this.maxArea != null) {
      merged.maxArea = Math.min(this.maxArea, other.maxArea);
    }
    return merged;
  }

  private <T> Stream<T> streamNotNull(Collection<T> src) {
    if (src != null) {
      return src.stream();
    } else {
      return Stream.empty();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
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

  @Override
  public String toString() {
    if (this.getIdentifier() != null) {
      return this.getIdentifier().toString();
    } else {
      return super.toString();
    }
  }
}
