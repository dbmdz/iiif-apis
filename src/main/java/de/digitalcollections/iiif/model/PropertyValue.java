package de.digitalcollections.iiif.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.jackson.serialization.PropertyValueDeserializer;
import de.digitalcollections.iiif.model.jackson.serialization.PropertyValueSerializer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * Type for strings that are intended to be displayed to the user.
 *
 * <p>Is organized as a mapping of languages to one or more values. See
 * http://iiif.io/api/presentation/2.1/#language-of-property-values and
 * http://iiif.io/api/presentation/2.1/#html-markup-in-property-values for more information.
 */
@JsonSerialize(using = PropertyValueSerializer.class)
@JsonDeserialize(using = PropertyValueDeserializer.class)
public class PropertyValue {

  private Map<Locale, List<String>> localizations = new LinkedHashMap<>();

  public PropertyValue() {}

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

  public PropertyValue addValue(String first, String... rest) {
    return addValue(Locale.ROOT, first, rest);
  }

  public PropertyValue addValue(Locale language, String first, String... rest) {
    if (this.localizations.containsKey(language)) {
      this.localizations.get(language).addAll(Lists.asList(first, rest));
    } else {
      setValues(language, first, rest);
    }
    return this;
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
    } else if (localizations.size() > 0) {
      return localizations.entrySet().iterator().next().getValue();
    } else {
      return Collections.emptyList();
    }
  }

  public List<String> getValues(Locale locale) {
    return localizations.get(locale);
  }

  public String getFirstValue() {
    if (getValues() == null) {
      return null;
    }
    return getValues().get(0);
  }

  /*
   * see http://iiif.io/api/presentation/2.1/#language-of-property-values
   * example: {"description": {"@value": "Here is a longer description of the object", "@language": "en"}}
   *
   * In the case where multiple values are supplied, clients must use the following algorithm to determine
   * which values to display to the user.
   *
   * <ul>
   * <li>If none of the values have a language associated with them, the client must display all of the values.</li>
   * <li>Else, the client should try to determine the user’s language preferences, or failing that use some
   * default language preferences. Then:</li>
   *   <ul>
   *   <li>If any of the values have a language associated with them, the client must display all of the values
   *     associated with the language that best matches the language preference.</li>
   *   <li>If all of the values have a language associated with them, and none match the language preference,
   *     the client must select a language and display all of the values associated with that language.</li>
   *   <li>If some of the values have a language associated with them, but none match the language preference,
   *     the client must display all of the values that do not have a language associated with them.</li>
   *   </ul>
   * </ul>
   */
  public String getFirstValue(Locale locale) {
    List<String> values = getValues(locale);
    if (values == null) {
      return getFirstValue();
    } else {
      return values.get(0);
    }
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
