package dev.ardvci.ml.reader

import org.apache.spark.ml.regression.LinearRegressionModel

class ReadModel{
  def load(path: String): LinearRegressionModel = {
    LinearRegressionModel.load(path)
  }


}
