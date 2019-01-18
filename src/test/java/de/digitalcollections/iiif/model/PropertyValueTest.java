package de.digitalcollections.iiif.model;

import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import java.util.Locale;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyValueTest {

  @Test
  public void testDefaultLocale() throws Exception {
    PropertyValue propVal = new PropertyValue();
    propVal.setValues("First", "Second", "Third");
    propVal.addValue(Locale.FRENCH, "Français");

    // There is a value matching the default locale
    Locale.setDefault(Locale.FRENCH);
    assertThat(propVal.getValues()).containsExactly("Français");

    // No values matching the default locale, get values without language
    Locale.setDefault(Locale.GERMAN);
    assertThat(propVal.getValues()).containsExactly("First", "Second", "Third");

    // No values matching the default locale, no language-less values, get values
    // of first language
    propVal = new PropertyValue();
    propVal.addValue(Locale.FRENCH, "Français");
    assertThat(propVal.getValues()).containsExactly("Français");
  }

  @Test
  public void multiValuedLanguage() throws Exception {
    IiifObjectMapper mapper = new IiifObjectMapper();
    PropertyValue propVal = new PropertyValue();
    propVal.addValue(Locale.ENGLISH, "one", "two");
    String json = mapper.writeValueAsString(propVal);
    assertThat(json).isEqualTo(
      "[{'@language':'en','@value':'one'},{'@language':'en','@value':'two'}]".replace("'", "\""));
    PropertyValue deserialized = mapper.readValue(json, PropertyValue.class);
    assertThat(deserialized.getLocalizations()).containsOnly(Locale.ENGLISH);
    assertThat(deserialized.getValues(Locale.ENGLISH)).containsExactly("one", "two");
  }

}
