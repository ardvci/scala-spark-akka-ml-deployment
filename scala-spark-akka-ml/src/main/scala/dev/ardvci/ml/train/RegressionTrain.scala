package dev.ardvci.ml.train

import org.apache.spark.ml.Pipeline
import org.apache.spark.sql
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel}
import org.apache.spark.sql.functions.{col, expr, when}
import dev.ardvci.ml.reader.ReadCsv

class RegressionTrain {

  private def transformBoolDataToNumeric(colName: String, df: sql.DataFrame): sql.DataFrame = {
    var resultedDf = df
    resultedDf = resultedDf.withColumn(
      colName,expr(s"case when $colName = 'yes' then 1 else 0 end").alias("new")
    )
    resultedDf
  }

  def train: LinearRegressionModel = {
    val reader = new ReadCsv
    val df =  reader.readHousingCsv
    val data1 = transformBoolDataToNumeric("mainroad", df)
    val data2 = transformBoolDataToNumeric("guestroom", data1)
    val data3 = transformBoolDataToNumeric("basement", data2)
    val data4 = transformBoolDataToNumeric("hotwaterheating", data3)
    val data5 = transformBoolDataToNumeric("airconditioning", data4)
    val data6 = transformBoolDataToNumeric("prefarea", data5)
    data6.printSchema()
    val featureCols = Array(
      "area", "bedrooms", "bathrooms",
      "stories", "mainroad", "guestroom", "basement", "hotwaterheating",
      "airconditioning", "parking", "prefarea"
    )

    // Assemble the features into a single vector column
    val featureAssembler = new VectorAssembler()
      .setInputCols(featureCols)
      .setOutputCol("features")

    val dataWithFeatures = featureAssembler.transform(data6)

    // Prepare the target column
    val targetCol = "price"

    val Array(trainData, testData) = dataWithFeatures.randomSplit(Array(0.7, 0.3), seed = 1234)

    val lr = new LinearRegression()
      .setLabelCol(targetCol)
      .setFeaturesCol("features")

    val lrModel: LinearRegressionModel = lr.fit(trainData)
    lrModel
  }
}
