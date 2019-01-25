package de.digitalcollections.iiif.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.reflections.ReflectionUtils;

/**
 * Some static utility methods used for (de-)serialization and sanity checks.
 */
public class ModelUtilities {

  public enum Completeness {
    EMPTY, ID_ONLY, ID_AND_TYPE, ID_AND_TYPE_AND_LABEL, COMPLEX
  }

  private static boolean containsOnly(Set<String> vals, String... toCheck) {
    return (vals.size() == toCheck.length
            && Arrays.stream(toCheck).allMatch(vals::contains));
  }

  /**
   * Obtain the "completeness" (i.e. "empty", "id and type", "it, type and label", "id only" or "complex") of
   * a IIIF resource. Can be useful for determining how to serialize the resource, e.g. often resources with only
   * an id are serialized as a string.
   *
   * @param res The IIIF resource to check the completeness of
   * @param type The type of the IIIF resource
   * @return the completeness
   */
  public static Completeness getCompleteness(Object res, Class<?> type) {
    Set<Method> getters = ReflectionUtils.getAllMethods(
        type,
        ReflectionUtils.withModifier(Modifier.PUBLIC),
        ReflectionUtils.withPrefix("get"));
    Set<String> gettersWithValues = getters.stream()
        .filter(g -> g.getAnnotation(JsonIgnore.class) == null) // Only JSON-serializable fields
        .filter(g -> returnsValue(g, res))
        .map(Method::getName)
        .collect(Collectors.toSet());

    boolean hasOnlyTypeAndId = (gettersWithValues.size() == 2
                                && Stream.of("getType", "getIdentifier").allMatch(gettersWithValues::contains));
    if (gettersWithValues.isEmpty()) {
      return Completeness.EMPTY;
    } else if (containsOnly(gettersWithValues, "getType", "getIdentifier")) {
      return Completeness.ID_AND_TYPE;
    } else if (containsOnly(gettersWithValues, "getType", "getIdentifier", "getLabels")) {
      return Completeness.ID_AND_TYPE_AND_LABEL;
    } else if (containsOnly(gettersWithValues, "getIdentifier")) {
      return Completeness.ID_ONLY;
    } else {
      return Completeness.COMPLEX;
    }
  }

  private static boolean returnsValue(Method method, Object obj) {
    try {
      return method.invoke(obj) != null;
    } catch (Exception e) {
      return false;
    }
  }
}
