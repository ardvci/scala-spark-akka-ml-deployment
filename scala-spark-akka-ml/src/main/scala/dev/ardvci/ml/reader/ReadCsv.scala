package dev.ardvci.ml.reader

import dev.ardvci.ml.sparkBuilder.SparkBuilder
import org.apache.spark.sql

class ReadCsv {
  private val sparkBuilder = new SparkBuilder
  private val spark = sparkBuilder.build("spark-ml-train","local[*]")
  def readHousingCsv: sql.DataFrame = {
    val df = spark.read.option("header","true").option("inferSchema","true").csv("scala-spark-akka-ml/src/main/scala/static/Housing.csv")
    df
  }

}
