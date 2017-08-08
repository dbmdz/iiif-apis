package de.digitalcollections.iiif.presentation.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.digitalcollections.iiif.presentation.model.jackson.PropertyValueDeserializer;
import de.digitalcollections.iiif.presentation.model.jackson.PropertyValueSerializer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@JsonSerialize(using = PropertyValueSerializer.class)
@JsonDeserialize(using = PropertyValueDeserializer.class)
public class PropertyValue  {
  private Map<Locale, List<String>> localizations = new HashMap<>();

  public PropertyValue() { }

  public PropertyValue(String... values) {
    this(Locale.ROOT, values);
  }

  public PropertyValue(Locale language, String... values) {
    setValues(language, values);
  }

  public void setValues(String... values) {
    this.setValues(Locale.ROOT, values);
  }

  public void setValues(Locale language, String... values) {
    checkNotNull(language);
    checkArgument(values.length > 0);
    checkArgument(Arrays.stream(values).allMatch(Objects::nonNull));
    this.localizations.put(language, new ArrayList<>(Arrays.asList(values)));
  }

  public void addValues(String... values) {
    addValues(Locale.ROOT, values);
  }

  public void addValues(Locale language, String... values) {
    if (this.localizations.containsKey(language)) {
      this.localizations.get(language).addAll(Arrays.asList(values));
    } else {
      setValues(language, values);
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
}
