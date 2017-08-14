package de.digitalcollections.iiif.model.api;

public interface PageContainer<T> {
  T getFirst();
  void setFirst(T first);

  T getLast();
  void setLast(T last);

  Integer getTotal();
  void setTotal(int total);
}
