package de.digitalcollections.iiif.model;

import java.util.Locale;
import org.junit.Test;

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

}