package de.digitalcollections.iiif.model.image;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Objects;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SizeRequest {

  private static final Pattern PARSE_PAT = Pattern.compile("^(!|pct:)?(?:([0-9]+)?,([0-9]+)?|([0-9.]+))$");
  private boolean max = false;
  private boolean bestFit = false;
  private Integer width = null;
  private Integer height = null;
  private BigDecimal percentage = null;

  /**
   * Parse an IIIF Image API compliant size request string
   *
   * @param str IIIF Image API compliant size request string
   * @return parsed SizeRequest
   * @throws ResolvingException if the request string is malformed
   */
  @JsonCreator
  public static SizeRequest fromString(String str) throws ResolvingException {
    if (str.equals("full")) {
      return new SizeRequest();
    }
    if (str.equals("max")) {
      return new SizeRequest(true);
    }
    Matcher matcher = PARSE_PAT.matcher(str);
    if (!matcher.matches()) {
      throw new ResolvingException("Bad format: " + str);
    }
    if (matcher.group(1) != null) {
      if (matcher.group(1).equals("!")) {
        return new SizeRequest(
          Integer.valueOf(matcher.group(2)),
          Integer.valueOf(matcher.group(3)),
          true);
      } else if (matcher.group(1).equals("pct:")) {
        return new SizeRequest(new BigDecimal(matcher.group(4)));
      }
    }
    Integer width = null;
    Integer height = null;
    if (matcher.group(2) != null) {
      width = Integer.parseInt(matcher.group(2));
    }
    if (matcher.group(3) != null) {
      height = Integer.parseInt(matcher.group(3));
    }
    return new SizeRequest(width, height);
  }

  /**
   * Create a size request for the full native resolution of the image region.
   */
  public SizeRequest() {
    this(false);
  }

  /**
   * Create a size request for the maximum supported size of the image region, if isMax is true.
   * If isMax is false, it behaves identically to the default constructor.
   *
   * @param isMax true causes a size request for the maximum supported size of the image region
   */
  public SizeRequest(boolean isMax) {
    this.max = isMax;
  }

  /**
   * Create a size request for a given width or height.
   * One of both can be null (the other value will be determined based on the aspect ratio of the image region), but not both at once.
   *
   * @param width width of size request
   * @param height height of size request
   * @throws ResolvingException if neither width nor height are specified
   */
  public SizeRequest(Integer width, Integer height) throws ResolvingException {
    if (width == null && height == null) {
      throw new ResolvingException("Either width or height must be specified!");
    }
    this.width = width;
    this.height = height;
  }

  /**
   * Create a size request for a given width and height and signal that the server can decide to render smaller
   * resolutions as it deems neccessary.
   * @param width width of size request
   * @param height height of size request
   * @param bestFit true, if server can decide to render smaller resolutions as it deems neccessary
   * @throws de.digitalcollections.iiif.model.image.ResolvingException if params can not be resolved to Size Request
   */
  public SizeRequest(int width, int height, boolean bestFit) throws ResolvingException {
    this(width, height);
    this.bestFit = bestFit;
  }

  /**
   * Create a size request that scaled both dimensions according to a fixed percentage, maintaining the aspect ratio.
   *
   * @param percentage scaling percentage, maintaining aspect ratio
   * @throws ResolvingException if the percentage is not between 0 and 100
   */
  public SizeRequest(BigDecimal percentage) throws ResolvingException {
    if (percentage.doubleValue() < 0 || percentage.doubleValue() > 100) {
      throw new ResolvingException("Percentage must be between 0 and 100!");
    }
    this.percentage = percentage;
  }

  /**
   * Return whether the maximum resolution was requested.
   * @return true, if maximum resolution was requested
   */
  public boolean isMax() {
    return max;
  }

  /**
   * Return whether the server can decide to render smaller resolutions than desired.
   * @return true, if the server can decide to render smaller resolutions than desired
   */
  public boolean isBestFit() {
    return bestFit;
  }

  /**
   * Get the requested width
   * @return requested width
   */
  public Integer getWidth() {
    return width;
  }

  /**
   * Get the requested height
   * @return requested height
   */
  public Integer getHeight() {
    return height;
  }

  /**
   * Get the requested percentage to be used for scaling
   * @return requested percentage to be used for scaling
   */
  public BigDecimal getPercentage() {
    return percentage;
  }

  /**
   * Get the canonical form of this request.
   * @see <a href="http://iiif.io/api/image/2.1/#canonical-uri-syntax">IIIF Image API specification</a>
   *
   * @param nativeSize native size of request
   * @param profile image api profile
   * @return canonical form of this request
   * @throws de.digitalcollections.iiif.model.image.ResolvingException if nativeSize can not be converted to canonical form
   */
  public String getCanonicalForm(Dimension nativeSize, ImageApiProfile profile) throws ResolvingException {
    Dimension resolved = this.resolve(nativeSize, profile);
    // "w," requests are already canonical
    double nativeRatio = nativeSize.getWidth() / nativeSize.getHeight();
    double resolvedRatio = resolved.getWidth() / resolved.getHeight();
    if (resolved.equals(nativeSize)) {
      return "full";
    } else if (this.width != null && this.height == null) {
      return this.toString();
    } else if (Math.floor(resolvedRatio * nativeSize.getHeight()) == nativeSize.getWidth()
      || Math.ceil(resolvedRatio * nativeSize.getHeight()) == nativeSize.getWidth()) {
      return String.format("%d,", resolved.width);
    } else {
      return String.format("%d,%d", resolved.width, resolved.height);
    }
  }

  public Dimension resolve(Dimension nativeSize, ImageApiProfile profile) throws ResolvingException {
    return resolve(nativeSize, Collections.emptyList(), profile);
  }

  /**
   * Resolve the request to dimensions that can be used for scaling, based on the native size of the image region
   * and the available profile.
   * @param nativeSize native size of the image region
   * @param availableSizes available sizes
   * @param profile image api profile
   * @return resolved dimension
   * @throws de.digitalcollections.iiif.model.image.ResolvingException if params can not be resolved to Dimension
   */
  public Dimension resolve(Dimension nativeSize, List<Dimension> availableSizes, ImageApiProfile profile) throws ResolvingException {
    double aspect = (double) nativeSize.width / (double) nativeSize.height;
    // "max"
    if (max) {
      // By default, identical to the largest available size or the native size if no sizes were specified
      Dimension dim = availableSizes.stream()
        // Avoid upscaling when dealing with region requests
        .filter(s -> s.width <= nativeSize.width && s.height <= nativeSize.height)
        // Select the largest available size
        .max(Comparator.comparing(Dimension::getWidth).thenComparing(Dimension::getHeight))
        // Otherwise, fall back to the native size
        .orElse(new Dimension(nativeSize.width, nativeSize.height));
      if (profile != null && profile.maxWidth != null) {
        if (dim.width > profile.maxWidth) {
          // If maximum width is set, width cannot exceed it
          dim.width = profile.maxWidth;
          dim.height = (int) (profile.maxWidth / aspect);
        }
        int maxHeight = profile.maxHeight != null ? profile.maxHeight : profile.maxWidth;
        if (dim.height > maxHeight) {
          // Adjust height if it exceeds maximum height
          dim.height = maxHeight;
          dim.width = (int) (aspect * dim.height);
        }
      }
      if (profile != null && profile.maxArea != null) {
        // Fit width and height into the maximum available area, preserving the aspect ratio
        long currentArea = (long) dim.width * (long) dim.height;
        if (currentArea > profile.maxArea) {
          dim.width = (int) Math.sqrt(aspect * (double) profile.maxArea);
          dim.height = (int) (dim.width / aspect);
          if (dim.width <= 0 || dim.height <= 0) {
            throw new ResolvingException(String.format(
              "Cannot fit image with dimensions %dx%d into maximum area of %d pixels.",
              nativeSize.width, nativeSize.height, profile.maxArea));
          }
        }
      }
      return dim;
    }
    Dimension out;
    if (percentage != null || bestFit) {  // "pct:"
      double ratio;
      if (percentage != null) {
        ratio = percentage.doubleValue() / 100.0;
      } else {
        ratio = Math.min(width / nativeSize.getWidth(), height / nativeSize.getHeight());
      }
      out = new Dimension((int) (ratio * nativeSize.width), (int) (ratio * nativeSize.height));
    } else if (width == null && height == null) {  // "full"
      out = nativeSize;
    } else {
      out = new Dimension();
      if (width != null) {
        out.width = width;
      }
      if (height != null) {
        out.height = height;
      }
      if (width == null) {  // ",h"
        out.width = (int) (out.height * aspect);
      }
      if (height == null) { // "w,"
        out.height = (int) (out.width / aspect);
      }
    }
    Integer maxHeight = profile.maxHeight != null ? profile.maxHeight : profile.maxWidth;
    if (profile.maxWidth != null && out.width > profile.maxWidth) {
      throw new ResolvingException(String.format(
        "Requested width (%d) exceeds maximum width (%d) as specified in the profile.", out.width, profile.maxWidth));
    } else if (maxHeight != null && out.height > maxHeight) {
      throw new ResolvingException(String.format(
        "Requested height (%d) exceeds maximum height (%d) as specified in the profile.", out.height, maxHeight));
    } else if (profile.maxArea != null && out.height * out.width > profile.maxArea) {
      throw new ResolvingException(String.format(
        "Requested area (%d*%d = %d) exceeds maximum area (%d) as specified in the profile",
        out.width, out.height, out.width * out.height, profile.maxArea));
    } else if ((profile.features == null || !profile.features.contains(ImageApiProfile.Feature.SIZE_ABOVE_FULL))
      && (out.width > nativeSize.width || out.height > nativeSize.height)) {
      throw new ResolvingException(String.format(
        "Requested dimensions (%dx%d) exceed native dimensions (%dx%d), profile states that upscaling is not supported.",
        out.width, out.height, nativeSize.width, nativeSize.height));
    }
    return out;
  }

  /**
   * Like {@link #resolve(Dimension, ImageApiProfile)}, but can be used with a {@link Rectangle}, e.g. as returned from {@link RegionRequest#resolve(Dimension)}.
   * @param region image region
   * @param profile image api profile
   * @return resolved size dimension
   * @throws de.digitalcollections.iiif.model.image.ResolvingException if rectangle region can not be resolved
   */
  public Dimension resolve(Rectangle region, ImageApiProfile profile) throws ResolvingException {
    return resolve(
      new Dimension(region.width, region.height),
      profile);
  }

  /**
   * Create an IIIF Image API compliant size request string
   * @return String representation of size request
   */
  @JsonCreator
  @JsonValue
  @Override
  public String toString() {
    if (width == null && height == null && percentage == null) {
      return max ? "max" : "full";
    }
    if (percentage != null) {
      return String.format("pct:%s", percentage);
    }
    StringBuilder rv = new StringBuilder();
    if (width != null) {
      rv.append(String.valueOf(width));
    }
    rv.append(",");
    if (height != null) {
      rv.append(String.valueOf(height));
    }
    return bestFit ? "!" + rv.toString() : rv.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SizeRequest that = (SizeRequest) o;
    return max == that.max
      && bestFit == that.bestFit
      && Objects.equal(width, that.width)
      && Objects.equal(height, that.height)
      && Objects.equal(percentage, that.percentage);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(max, bestFit, width, height, percentage);
  }
}
