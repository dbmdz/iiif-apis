package de.digitalcollections.iiif.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A motivation for an annotation.
 *
 * Try to use the defined constants and only fall back to the constructor if your motivation is not covered by these.
 */
public class Motivation {

  public static final Motivation PAINTING = new Motivation("sc:painting");
  public static final Motivation COMMENTING = new Motivation("oa:commenting");
  public static final Motivation LINKING = new Motivation("oa:linking");

  private final String motivation;

  @JsonCreator
  public Motivation(String motivation) {
    this.motivation = motivation;
  }

  @JsonValue
  @Override
  public String toString() {
    return this.motivation;
  }

  @Override
  public boolean equals(Object other) {
    return (other instanceof Motivation && other.toString().equals(this.motivation));
  }
}
