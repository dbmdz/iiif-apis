package de.digitalcollections.iiif.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.jackson.serialization.PropertyValueDeserializer;
import de.digitalcollections.iiif.model.jackson.serialization.PropertyValueSerializer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Type for strings that are intended to be displayed to the user.
 *
 * Is organized as a mapping of languages to one or more values.
 * See http://iiif.io/api/presentation/2.1/#language-of-property-values and
 * http://iiif.io/api/presentation/2.1/#html-markup-in-property-values for more information.
 */
@JsonSerialize(using = PropertyValueSerializer.class)
@JsonDeserialize(using = PropertyValueDeserializer.class)
public class PropertyValue  {
  private Map<Locale, List<String>> localizations = new LinkedHashMap<>();

  public PropertyValue() { }

  public PropertyValue(String first, String... rest) {
    this(Locale.ROOT, first, rest);
  }

  public PropertyValue(Locale language, String first, String... rest) {
    setValues(language, first, rest);
  }

  public void setValues(String first, String... rest) {
    this.setValues(Locale.ROOT, first, rest);
  }

  public void setValues(Locale language, String firstValue, String... rest) {
    checkNotNull(language);
    checkNotNull(firstValue);
    checkArgument(Arrays.stream(rest).allMatch(Objects::nonNull));
    // Need to wrap it with an ArrayList since we might want to add new values later on
    this.localizations.put(language, new ArrayList<>(Lists.asList(firstValue, rest)));
  }

  public void addValue(String first, String... rest) {
    addValue(Locale.ROOT, first, rest);
  }

  public void addValue(Locale language, String first, String... rest) {
    if (this.localizations.containsKey(language)) {
      this.localizations.get(language).addAll(Lists.asList(first, rest));
    } else {
      setValues(language, first, rest);
    }
  }

  public Set<Locale> getLocalizations() {
    return this.localizations.keySet();
  }

  public List<String> getValues() {
    Locale defaultLang = Locale.getDefault();
    if (localizations.containsKey(defaultLang)) {
      return localizations.get(defaultLang);
    } else if (localizations.containsKey(Locale.ROOT)) {
      return localizations.get(Locale.ROOT);
    } else {
      return localizations.entrySet().iterator().next().getValue();
    }
  }

  public List<String> getValues(Locale locale) {
    return localizations.get(locale);
  }

  public String getFirstValue() {
    return getValues().get(0);
  }

  public String getFirstValue(Locale locale) {
    return getValues(locale).get(0);
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();
    for (Entry<Locale, List<String>> e : this.localizations.entrySet()) {
      String language = e.getKey().toLanguageTag();
      String values;
      if (e.getValue().size() == 1) {
        values = e.getValue().get(0);
      } else {
        values = "{" + String.join(", ", e.getValue()) + "}";
      }
      out.append(String.format("%s=%s", language, values));
    }
    return String.format("PropertyValue(%s)", out.toString());
  }
}
