package com.socrata.geospace

import org.geoscript.geometry.{builder => build}
import org.geoscript.layer._
import org.geoscript.workspace._

trait RegionCodingPerfTester {
  // Generates numPoints random points within the bounding box of layer, and times the georegion coding
  def benchmark(layer: Layer, numPoints: Int): Long = {
    val bbox = layer.getBounds

    println("Generating random points...")
    val points = Array.fill(numPoints)(build.Point(bbox.getMinX + util.Random.nextDouble * bbox.getWidth,
                                                   bbox.getMinY + util.Random.nextDouble * bbox.getHeight))

    println("Timing geo-region-coding...")
    val coder = SpatialIndex(layer)
    val startTime = System.currentTimeMillis
    var matches = 0
    for (i <- 0 until numPoints) {
      val regions = coder.firstContains(points(i))
      matches += regions.size
    }
    val endTime = System.currentTimeMillis

    println(s"$matches matches out of $numPoints")
    endTime - startTime
  }
}

/**
 * Benchmark geo region coding using a layer based on Shapefiles on disk.
 * This turns out to be really slow:
 *  - 49% of the time is mostly spent opening up .dbfs, loading and initializing the quadtree index
 *    for each point
 *  - 38% of the time is spent just seeking features on disk
 *  - 10% of the time is spent parsing the CQL query
 */
object RegionCodingPerfShapefileTest extends App with RegionCodingPerfTester {

  val TestShapefile = "data/Wards.shp"
  val NumPoints = 50000

  println("Loading shapefile...")
  val layer = Shapefile(TestShapefile)

  val millis = benchmark(layer, NumPoints)
  println(s"Coding $NumPoints points with shapefile $TestShapefile took $millis millis")
  println(s"Geo-region-coding speed: ${NumPoints / (millis / 1000.0)} points/sec/core")
}