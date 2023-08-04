package dev.ardvci.ml.prediction

import dev.ardvci.api.RequestData
import dev.ardvci.ml.reader.ReadModel
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.io.ByteArrayInputStream

class GetPredictionWithJson {
  def predict(spark:SparkSession ,jsonString: RequestData): String = {
    val reader = new ReadModel
    val model = reader.load("scala-spark-akka-ml/src/main/scala/out")
    val features = new VectorAssembler().setInputCols(
      Array("area", "bedrooms", "bathrooms",
        "stories", "mainroad", "guestroom", "basement", "hotwaterheating",
        "airconditioning", "parking", "prefarea"
      )
    ).setOutputCol("features")
    import spark.implicits._
    val dataset: DataFrame = Seq(jsonString).toDF()
    val finalDataset = features.transform(dataset)
    val data = model.transform(finalDataset)
    val prediction = data.select("prediction").head()
    prediction.toString()
  }

}
