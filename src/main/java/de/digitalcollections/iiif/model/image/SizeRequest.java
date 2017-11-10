package de.digitalcollections.iiif.model.image;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.math.BigDecimal;
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
   * @throws IllegalArgumentException if the request string is malformed
   */
  @JsonCreator
  public static SizeRequest fromString(String str) throws IllegalArgumentException {
    if (str.equals("full")) {
      return new SizeRequest();
    }
    if (str.equals("max")) {
      return new SizeRequest(true);
    }
    Matcher matcher = PARSE_PAT.matcher(str);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Bad format: " + str);
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
   * If isMax is false, ise it behaves identically to the default constructor.
   */
  public SizeRequest(boolean isMax) {
    this.max = isMax;
  }

  /**
   * Create a size request for a given width or height.
   *
   * One of both can be null (the other value will be determined based on the aspect ratio of the image region), but
   * not both at once.
   *
   * @throws IllegalArgumentException if neither width nor height are specified
   */
  public SizeRequest(Integer width, Integer height) throws IllegalArgumentException {
    if (width == null && height == null) {
      throw new IllegalArgumentException("Either width or height must be specified!");
    }
    this.width = width;
    this.height = height;
  }

  /**
   * Create a size request for a given width and height and signal that the server can decide to render smaller
   * resolutions as it deems neccessary.
   */
  public SizeRequest(int width, int height, boolean bestFit) {
    this(width, height);
    this.bestFit = bestFit;
  }

  /**
   * Create a size request that scaled both dimensions according to a fixed percentage, maintaining the aspect ratio.
   *
   * @throws IllegalArgumentException if the percentage is not between 0 and 100
   */
  public SizeRequest(BigDecimal percentage) throws IllegalArgumentException {
    if (percentage.doubleValue() < 0 || percentage.doubleValue() > 100) {
      throw new IllegalArgumentException("Percentage must be between 0 and 100!");
    }
    this.percentage = percentage;
  }

  /**
   * Return whether the maximum resolution was requested.
   */
  public boolean isMax() {
    return max;
  }

  /**
   * Return whether the server can decide to render smaller resolutions than desired.
   */
  public boolean isBestFit() {
    return bestFit;
  }

  /**
   * Get the requested width
   */
  public Integer getWidth() {
    return width;
  }

  /**
   * Get the requested height
   */
  public Integer getHeight() {
    return height;
  }

  /**
   * Get the requested percentage to be used for scaling
   */
  public BigDecimal getPercentage() {
    return percentage;
  }

  /**
   * Resolve the request to dimensions that can be used for scaling, based on the native size of the image region
   * and the available profile.
   */
  public Dimension resolve(Dimension nativeSize, ImageApiProfile profile) {
    double aspect = (double) nativeSize.width / (double) nativeSize.height;
    if (max) {
      // By default, identical to "full"
      Dimension dim = new Dimension(nativeSize);
      if (profile != null && profile.maxWidth != null) {
        // If maximum width is set, width cannot exceed it
        dim.width = profile.maxWidth;
        dim.height = (int) (aspect * profile.maxWidth);
        if (profile.maxHeight != null && profile.maxHeight != null && dim.height > profile.maxHeight) {
          // Adjust height if it exceeds maximum height
          dim.height = profile.maxHeight;
          dim.width = (int) (aspect * dim.height);
        } else {
          dim.height = (int) (dim.width / aspect);
        }
      }
      if (profile != null && profile.maxArea != null) {
        // Fit width and height into the maximum available area, preserving the aspect ratio
        int currentArea = dim.width * dim.height;
        if (currentArea > profile.maxArea) {
          dim.width = (int) Math.sqrt(aspect / (double) profile.maxArea);
          dim.height = (int) (dim.width / aspect);
        }
      }
      return dim;
    }
    if (percentage != null) {
      double ratio = percentage.doubleValue() / 100.0;
      return new Dimension((int) (ratio * nativeSize.width), (int) (ratio * nativeSize.height));
    }
    if (width == null && height == null) {
      // "full"
      return nativeSize;
    }
    Dimension out = new Dimension();
    if (width != null) {
      out.width = width;
    }
    if (height != null) {
      out.height = height;
    }
    if (width == null) {
      out.width = (int) (out.height * aspect);
    }
    if (height == null) {
      out.height = (int) (out.width / aspect);
    }
    return out;
  }

  /**
   * Like {@link #resolve(Dimension, ImageApiProfile)}, but can be used with a {@link Rectangle}, e.g. as returned
   * from {@link RegionRequest#resolve(Dimension)}.
   */
  public Dimension resolve(Rectangle region, ImageApiProfile profile) {
    return resolve(
        new Dimension(region.width, region.height),
        profile);
  }

  /**
   * Create an IIIF Image API compliant size request string
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
}
