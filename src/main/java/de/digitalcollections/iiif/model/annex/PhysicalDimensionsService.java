package de.digitalcollections.iiif.model.annex;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import de.digitalcollections.iiif.model.Service;
import java.net.URI;

/**
 * A service that describes  the physical dimensions of the resource it is associated to
 * (http://iiif.io/api/annex/services/#physical-dimensions).
 */
public class PhysicalDimensionsService extends Service {

  public enum Unit {
    MILLIMETERS("mm"), CENTIMETERS("cm"), INCHES("in");

    private final String unit;

    @JsonCreator
    Unit(String unit) {
      this.unit = unit;
    }

    @JsonValue
    @Override
    public String toString() {
      return this.unit;
    }
  }

  public static final String CONTEXT = "http://iiif.io/api/annex/services/physdim/1/context.json";
  public static final String PROFILE = "http://iiif.io/api/annex/services/physdim";

  private final double physicalScale;
  private final Unit physicalUnits;

  @JsonCreator
  public PhysicalDimensionsService(@JsonProperty("physicalScale") double physicalScale,
    @JsonProperty("physicalUnits") Unit units) {
    super(URI.create(CONTEXT));
    this.addProfile(PROFILE);
    this.physicalScale = physicalScale;
    this.physicalUnits = units;
  }

  public PhysicalDimensionsService(String identifier, double physicalScale, Unit units) {
    this(physicalScale, units);
    this.setIdentifier(URI.create(identifier));
  }

  public double getPhysicalScale() {
    return physicalScale;
  }

  public Unit getPhysicalUnits() {
    return physicalUnits;
  }
}
