package de.digitalcollections.iiif.model.image;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Objects;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class RegionRequest {
  /** We use BigDecimals for the relative region, since we want to preserve the precision of the request **/
  private class RelativeBox {
    final BigDecimal x;
    final BigDecimal y;
    final BigDecimal w;
    final BigDecimal h;

    public RelativeBox(BigDecimal x, BigDecimal y, BigDecimal w, BigDecimal h) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      RelativeBox that = (RelativeBox) o;
      return Objects.equal(x, that.x) &&
          Objects.equal(y, that.y) &&
          Objects.equal(w, that.w) &&
          Objects.equal(h, that.h);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(x, y, w, h);
    }
  }

  private Rectangle absoluteBox;
  private RelativeBox relativeBox;
  private boolean square = false;

  private static final Pattern parsePat = Pattern.compile("^(pct:)?([0-9.]+),([0-9.]+),([0-9.]+),([0-9.]+)$");

  /**
   * Parse an IIIF Image API compliant region request string
   *
   * @throws IllegalArgumentException if the request string is malformed.
   */
  @JsonCreator
  public static RegionRequest fromString(String str) throws IllegalArgumentException {
    if (str.equals("full")) {
      return new RegionRequest();
    }
    if (str.equals("square")) {
      return new RegionRequest(true);
    }
    Matcher matcher = parsePat.matcher(str);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Bad format: " + str);
    }
    if (matcher.group(1) == null) {
      return new RegionRequest(
          Integer.valueOf(matcher.group(2)),
          Integer.valueOf(matcher.group(3)),
          Integer.valueOf(matcher.group(4)),
          Integer.valueOf(matcher.group(5)));
    } else {
      return new RegionRequest(
          new BigDecimal(matcher.group(2)),
          new BigDecimal(matcher.group(3)),
          new BigDecimal(matcher.group(4)),
          new BigDecimal(matcher.group(5)));
    }
  }

  /**
   * Create a region that encompasses the whole picture, i.e. the 'full' syntax.
   */
  public RegionRequest() {
    this(false);
  }

  /**
   * Pass 'true' to create a region that selects a square region from the image, i.e. the 'square' syntax.
   */
  public RegionRequest(boolean square) {
    this.square = square;
  }

  private RegionRequest(BigDecimal x, BigDecimal y, BigDecimal width, BigDecimal height) throws IllegalArgumentException {
    if (Stream.of(x, y, width, height).anyMatch(v -> v.doubleValue() > 100.0)) {
      throw new IllegalArgumentException("No parameter can be greater than 100!");
    }
    this.relativeBox = new RelativeBox(x, y, width, height);
  }

  /**
   * Create a RegionRequest request that is expressed using relative values, i.e. the "pct:x,y,w,h" syntax
   *
   * The values must be between 0.0 and 100.0.
   *
   * @throws IllegalArgumentException if the values fall outside of the allowed range
   */
  public RegionRequest(double x, double y, double width, double height) throws IllegalArgumentException {
    this(BigDecimal.valueOf(x), BigDecimal.valueOf(y), BigDecimal.valueOf(width), BigDecimal.valueOf(height));
  }

  /**
   * Create a RegionRequest request that is expressed using absolute values.
   */
  public RegionRequest(int x, int y, int width, int height) {
    this.absoluteBox = new Rectangle(x, y, width, height);
  }

  /**
   * Returns the requested region
   */
  public Rectangle2D getRegion() {
    if (isRelative()) {
      return new Rectangle2D.Double(
          relativeBox.x.doubleValue(), relativeBox.y.doubleValue(),
          relativeBox.w.doubleValue(), relativeBox.h.doubleValue());
    } else {
      return absoluteBox;
    }
  }

  /**
   * Returns whether the region is epxressed in relative terms.
   */
  public boolean isRelative() {
    return relativeBox != null;
  }

  /**
   * Returns whether a square region is selected.
   */
  public boolean isSquare() {
    return square;
  }

  /**
   * Create an IIIF Image API compliant region request string
   */
  @Override
  @JsonValue
  public String toString() {
    if (square) {
      return "square";
    }
    if (relativeBox == null && absoluteBox == null) {
      return "full";
    } else if (isRelative()) {
      return String.format("pct:%s,%s,%s,%s", relativeBox.x, relativeBox.y, relativeBox.w, relativeBox.h);
    } else {
      return String.format("%d,%d,%d,%d", absoluteBox.x, absoluteBox.y, absoluteBox.width, absoluteBox.height);
    }
  }

  /**
   * Resolve the region request into an actual region that can be used for cropping the image
   */
  public Rectangle resolve(Dimension imageDims) {
    if (square) {
      if (imageDims.width > imageDims.height) {
        return new Rectangle(
            (imageDims.width - imageDims.height) / 2,
            0,
            imageDims.height, imageDims.height);
      } else if (imageDims.height > imageDims.width) {
        return new Rectangle(
            0,
            (imageDims.height - imageDims.width) / 2,
            imageDims.width, imageDims.width);
      }
    }
    if (absoluteBox == null && relativeBox == null) {
      return new Rectangle(0, 0, imageDims.width, imageDims.height);
    }
    Rectangle rect;
    if (isRelative()) {
      rect = new Rectangle(
          (int) Math.round(relativeBox.x.doubleValue() / 100. * imageDims.getWidth()),
          (int) Math.round(relativeBox.y.doubleValue() / 100. * imageDims.getHeight()),
          (int) Math.round(relativeBox.w.doubleValue() / 100. * imageDims.getWidth()),
          (int) Math.round(relativeBox.h.doubleValue() / 100. * imageDims.getHeight()));
    } else {
      rect = absoluteBox;
    }
    if (rect.x + rect.width > imageDims.width) {
      rect.width = imageDims.width - rect.x;
    }
    if (rect.y + rect.height > imageDims.height) {
      rect.height = imageDims.height - rect.y;
    }
    return rect;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RegionRequest that = (RegionRequest) o;
    return square == that.square &&
        Objects.equal(absoluteBox, that.absoluteBox) &&
        Objects.equal(relativeBox, that.relativeBox);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(absoluteBox, relativeBox, square);
  }
}
