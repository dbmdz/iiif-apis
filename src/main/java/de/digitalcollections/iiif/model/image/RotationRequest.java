package de.digitalcollections.iiif.model.image;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RotationRequest {
  private static final Pattern PATTERN = Pattern.compile("^(!)?([0-9]+)$");
  int rotation;
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
        Integer.parseInt(matcher.group(2)),
        !(matcher.group(1) == null));
  }

  public RotationRequest(int rotation) {
    this(rotation, false);
  }

  /**
   * Create a new rotation request.
   *
   * @param rotation Rotation in degrees. Must be between 0 and 360
   * @param mirror Mirror the image when rotating
   * @throws IllegalArgumentException if the rotation degrees are not between 0 and 360
   */
  public RotationRequest(int rotation, boolean mirror) throws IllegalArgumentException {
    if (rotation < 0 || rotation > 360) {
      throw new IllegalArgumentException("Rotation must be between 0 and 360");
    }
    this.rotation = rotation;
    this.mirror = mirror;
  }

  public int getRotation() {
    return rotation;
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
    String out = Integer.toString(this.rotation);
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
