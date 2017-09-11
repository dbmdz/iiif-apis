package de.digitalcollections.iiif.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The direction that a sequence of canvases should be displayed to the user.
 */
public enum ViewingDirection {
  LEFT_TO_RIGHT("left-to-right"),
  RIGHT_TO_LEFT("right-to-left"),
  TOP_TO_BOTTOM("top-to-bottom"),
  BOTTOM_TO_TOP("bottom-to-top");

  private final String stringValue;

  ViewingDirection(String stringValue) {
    this.stringValue = stringValue;
  }

  @JsonValue
  @Override
  public String toString() {
    return stringValue;
  }
}

