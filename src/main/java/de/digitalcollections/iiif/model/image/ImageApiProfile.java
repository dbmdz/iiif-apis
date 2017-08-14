package de.digitalcollections.iiif.model.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import de.digitalcollections.core.model.api.MimeType;
import de.digitalcollections.iiif.model.Profile;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

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

    @Override
    public String toString() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
    }
  }

  public enum Quality {
    COLOR, GRAY, BITRONAL, DEFAULT;

    @Override
    public String toString() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
    }
  }

  public enum Feature {
    BASE_URI_REDIRECT,
    CANONICAL_LINK_HEADER,
    CORS,
    JSONLD_MEDIA_TYPE,
    MIRRORING,
    PROFILE_LINK_HEADER,
    REGION_BY_PCT,
    REGION_BY_PX,
    ROTATION_ARBITRARY,
    ROTATION_BY_90S,
    SIZE_ABOVE_FULL,
    SIZE_BY_WH_LISTED,
    SIZE_BY_FORCED_WH,
    SIZE_BY_H,
    SIZE_BY_PCT,
    SIZE_BY_W,
    SIZE_BY_WH;

    @Override
    public String toString() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
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

  public void addFormat(Format first, Format... rest) {
    if (this.formats == null) {
      this.formats = new HashSet<>();
    }
    this.formats.addAll(Lists.asList(first, rest));
  }

  public Set<Quality> getQualities() {
    return qualities;
  }

  public void setQualities(Set<Quality> qualities) {
    this.qualities = qualities;
  }

  public void addQuality(Quality first, Quality... rest) {
    if (this.qualities == null) {
      this.qualities = new HashSet<>();
    }
    this.qualities.addAll(Lists.asList(first, rest));
  }

  public Set<Feature> getFeatures() {
    return features;
  }

  public void setFeatures(Set<Feature> features) {
    this.features = features;
  }

  public void addFeature(Feature first, Feature... rest) {
    if (this.features == null) {
      this.features = new HashSet<>();
    }
    this.features.addAll(Lists.asList(first, rest));
  }
}
