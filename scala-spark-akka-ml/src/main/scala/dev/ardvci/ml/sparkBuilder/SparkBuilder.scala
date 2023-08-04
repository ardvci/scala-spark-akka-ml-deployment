package dev.ardvci.ml.sparkBuilder

import org.apache.spark.sql._

class SparkBuilder {
  def build(appName: String, master: String): SparkSession = {
    val spark = SparkSession.
      builder()
      .appName(appName)
      .master(master)
      .getOrCreate()
    spark
  }

}
