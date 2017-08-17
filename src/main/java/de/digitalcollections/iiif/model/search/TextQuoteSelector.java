package de.digitalcollections.iiif.model.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.digitalcollections.iiif.model.api.Selector;

public class TextQuoteSelector implements Selector {
  public static final String TYPE = "oa:TextQuoteSelector";
  private final String exact;
  private String prefix;
  private String suffix;

  @JsonCreator
  public TextQuoteSelector(@JsonProperty("exact") String exact) {
    this.exact = exact;
  }

  public TextQuoteSelector(String exact, String prefix, String suffix) {
    this(exact);
    this.prefix = prefix;
    this.suffix = suffix;
  }

  @JsonProperty("@type")
  private String getType() {
    return TYPE;
  }

  public String getExact() {
    return exact;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }
}
