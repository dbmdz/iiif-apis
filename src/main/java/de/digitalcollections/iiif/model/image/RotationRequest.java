package de.digitalcollections.iiif.model.image;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Objects;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RotationRequest {
  // Matches floating point values
  private static final Pattern PATTERN = Pattern.compile("^(!)?([0-9]*\\.?[0-9]+)$");
  BigDecimal rotation;
  boolean mirror = false;

  /**
   * Parse a rotation request from an IIIF Image API compliant rotation string.
   *
   * @throws IllegalArgumentException if the rotation string is malformed
   */
  @JsonCreator
  public static RotationRequest fromString(String str) {
    Matcher matcher = PATTERN.matcher(str);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Bad format: " + str);
    }
    return new RotationRequest(
        new BigDecimal(matcher.group(2)),
        !(matcher.group(1) == null));
  }

  public RotationRequest(int rotation) {
    this(BigDecimal.valueOf(rotation), false);
  }

  /**
   * Create a new rotation request.
   *
   * @param rotation Rotation in degrees. Must be between 0 and 360
   * @param mirror Mirror the image when rotating
   * @throws IllegalArgumentException if the rotation degrees are not between 0 and 360
   */
  public RotationRequest(BigDecimal rotation, boolean mirror) throws IllegalArgumentException {
    if (rotation.floatValue() < 0 || rotation.floatValue() > 360) {
      throw new IllegalArgumentException("Rotation must be between 0 and 360");
    }
    this.rotation = rotation;
    this.mirror = mirror;
  }

  public double getRotation() {
    return rotation.doubleValue();
  }

  public boolean isMirror() {
    return mirror;
  }

  /**
   * Create an IIIF Image API compatible rotation string.
   */
  @JsonValue
  @Override
  public String toString() {
    String out = this.rotation.toString();
    if (mirror) {
      out = "!" + out;
    }
    return out;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RotationRequest that = (RotationRequest) o;
    return mirror == that.mirror &&
        Objects.equal(rotation, that.rotation);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(rotation, mirror);
  }
}
