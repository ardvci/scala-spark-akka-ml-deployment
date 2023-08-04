package dev.ardvci.ml.writer

import org.apache.spark.ml.regression.LinearRegressionModel

class ModelWriter {
  def saveModel(path: String, model: LinearRegressionModel): Unit = {
    model.write.overwrite().save(path)
  }
}
