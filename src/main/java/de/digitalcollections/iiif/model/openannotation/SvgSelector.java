package de.digitalcollections.iiif.model.openannotation;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.digitalcollections.iiif.model.interfaces.Selector;

/**
 * A selector that selects a region of an image using SVG.
 */
@JsonTypeName(SvgSelector.TYPE)
public class SvgSelector extends ContentAsText implements Selector {

  public static final String TYPE = "oa:SvgSelector";

  public SvgSelector(String chars) {
    super(chars);
    super.setType(TYPE);
  }

  public SvgSelector() {
    this(null);
  }
}
