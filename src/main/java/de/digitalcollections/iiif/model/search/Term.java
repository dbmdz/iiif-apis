package de.digitalcollections.iiif.model.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.digitalcollections.iiif.model.PropertyValue;
import java.net.URI;

/**
 * A term in an autocomplete query response.
 *
 * See http://iiif.io/api/search/1.0/#response
 */
public class Term {

  private URI url;
  private String match;
  private Integer count;
  private PropertyValue label;

  @JsonCreator
  public Term(@JsonProperty("url") URI url, @JsonProperty("match") String match) {
    this.url = url;
    this.match = match;
  }

  public Term(String url, String match, Integer count) {
    this(URI.create(url), match);
    this.count = count;
  }

  public Term(String url, String match, Integer count, String label) {
    this(URI.create(url), match);
    this.count = count;
    this.label = new PropertyValue(label);
  }

  public URI getUrl() {
    return url;
  }

  public String getMatch() {
    return match;
  }

  public Integer getCount() {
    return count;
  }

  public PropertyValue getLabel() {
    return label;
  }

  public void setLabel(PropertyValue label) {
    this.label = label;
  }

  @JsonIgnore
  public String getLabelString() {
    return label.getFirstValue();
  }
}
