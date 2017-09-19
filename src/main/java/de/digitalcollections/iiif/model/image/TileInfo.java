package de.digitalcollections.iiif.model.image;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;

/**
 * Describes an Image API tile.
 */
public class TileInfo {
  private Integer width;
  private Integer height;
  private List<Integer> scaleFactors;

  @JsonCreator
  public TileInfo(@JsonProperty("width") Integer width) {
    this.width = width;
  }

  public Integer getWidth() {
    return width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public List<Integer> getScaleFactors() {
    return scaleFactors;
  }

  public TileInfo addScaleFactor(Integer first, Integer... rest) {
    if (this.scaleFactors == null) {
      this.scaleFactors = new ArrayList<>();
    }
    this.scaleFactors.addAll(Lists.asList(first, rest));
    return this;
  }
}
