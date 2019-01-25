package de.digitalcollections.iiif.model.interfaces;

public interface Pageable<T> {

  T getNext();

  void setNext(T next);

  T getPrevious();

  void setPrevious(T previous);

  Integer getStartIndex();

  void setStartIndex(int startIndex);
}
