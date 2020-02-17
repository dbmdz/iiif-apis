package de.digitalcollections.iiif.model.openannotation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import java.net.URI;
import java.util.Collections;
import java.util.List;

/** A resource that contains embedded CSS Stylesheet definitions */
public class CssStyle extends ContentAsText {

  public static final String TYPE = "oa:CssStyle";

  @JsonProperty("@type")
  public List<String> getTypes() {
    if (this.getChars() == null) {
      return Collections.singletonList(TYPE);
    } else {
      return Lists.newArrayList(TYPE, ContentAsText.TYPE);
    }
  }

  public CssStyle(String stylesheet) {
    super(stylesheet);
    super.setType(TYPE);
  }

  @JsonCreator
  public CssStyle(@JsonProperty("@id") URI identifier) {
    this((String) null);
    super.setIdentifier(identifier);
  }
}
