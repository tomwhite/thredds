package ucar.sparr;

import ucar.nc2.grib.collection.*;

import java.util.*;

/**
 * Create shared coordinates across the same variable in different partitions
 * Create the union of all coordinates.
 *
 * @author John
 * @since 12/10/13
 */
public class CoordinateUnionizer {

  List<Coordinate> unionCoords = new ArrayList<>();
  CoordinateND<GribCollection.Record> result;

  CoordinateBuilder runtimeBuilder ;
  CoordinateBuilder timeBuilder;
  CoordinateBuilder timeIntvBuilder;
  CoordinateBuilder vertBuilder;

  public void addCoords(List<Coordinate> coords) {
    for (Coordinate coord : coords) {
      switch (coord.getType()) {
        case runtime:
          if (runtimeBuilder == null) runtimeBuilder = new CoordinateRuntime.Builder();
          runtimeBuilder.addAll(coord);
          break;
        case time:
          if (timeBuilder == null) timeBuilder = new CoordinateTime.Builder(coord.getCode());
          timeBuilder.addAll(coord);
          break;
        case timeIntv:
          if (timeIntvBuilder == null) timeIntvBuilder = new CoordinateTimeIntv.Builder(null, null, coord.getCode());
          timeIntvBuilder.addAll(coord);
          break;
        case vert:
          if (vertBuilder == null) vertBuilder = new CoordinateVert.Builder(coord.getCode());
          vertBuilder.addAll(coord);
          break;
      }
    }
  }

  public List<Coordinate> finish() {
    if (runtimeBuilder != null)
      unionCoords.add(runtimeBuilder.finish());
    if (timeBuilder != null)
      unionCoords.add(timeBuilder.finish());
    if (timeIntvBuilder != null)
      unionCoords.add(timeIntvBuilder.finish());
    if (vertBuilder != null)
      unionCoords.add(vertBuilder.finish());

    result = new CoordinateND<>(unionCoords);
    return unionCoords;
  }

  /**
   * Reindex with shared coordinates and return new CoordinateND
   * @param prev  previous
   * @return new CoordinateND containing shared coordinates and sparseArray for the new coordinates
   */
  public void addIndex(CoordinateND<GribCollection.Record> prev) {
    result.reindex(prev);
  }

  public CoordinateND<GribCollection.Record> getCoordinateND() {
    return result;
  }

}
