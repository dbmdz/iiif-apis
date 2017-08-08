package de.digitalcollections.iiif.presentation.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

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

