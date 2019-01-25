package de.digitalcollections.iiif.model.openannotation;

import java.util.ArrayList;
import java.util.List;

public interface Choice<T> {

  List<T> getAlternatives();

  void setAlternatives(List<T> alternatives);

  default void addAlternative(T alternative) {
    if (this.getAlternatives() == null) {
      this.setAlternatives(new ArrayList<>());
    }
    this.getAlternatives().add(alternative);
  }
}
