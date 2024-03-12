import de.digitalcollections.iiif.model.sharedcanvas.Range;
class Test {
  public static void main(String[] args) {
    Range range = new Range("http://test/foo");
    range.addCanvas("http://test/baz");
  }
}
