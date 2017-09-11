package de.digitalcollections.iiif.model.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A list of result terms for an autocomplete query.
 *
 * See http://iiif.io/api/search/1.0/#response
 */
public class TermList {
  public static final String CONTEXT = SearchResult.CONTEXT;
  public static final String TYPE = "search:TermList";

  @JsonProperty("@id")
  private final URI identifier;

  private Set<String> ignored;
  private List<Term> terms;

  @JsonCreator
  public TermList(@JsonProperty("@id") String identifier) {
    this.identifier = URI.create(identifier);
  }

  @JsonProperty("@context")
  private String getContext() {
    return CONTEXT;
  }

  @JsonProperty("@type")
  private String getType() {
    return TYPE;
  }

  public URI getIdentifier() {
    return identifier;
  }

  public Set<String> getIgnored() {
    return ignored;
  }

  public void setIgnored(Set<String> ignored) {
    this.ignored = ignored;
  }

  public void addIgnored(String first, String... rest) {
    if (this.ignored == null) {
      this.ignored = new LinkedHashSet<>();
    }
    this.ignored.addAll(Lists.asList(first, rest));
  }

  public List<Term> getTerms() {
    return terms;
  }

  public void setTerms(List<Term> terms) {
    this.terms = terms;
  }

  public void addTerm(Term first, Term... rest) {
    if (this.terms == null) {
      this.terms = new ArrayList<>();
    }
    this.terms.addAll(Lists.asList(first, rest));
  }
}
