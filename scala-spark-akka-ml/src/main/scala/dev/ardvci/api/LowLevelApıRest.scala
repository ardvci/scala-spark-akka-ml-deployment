package dev.ardvci.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.headers.RawHeader
import akka.stream.ActorMaterializer
import spray.json._
import dev.ardvci.ml.prediction.GetPredictionWithJson
import dev.ardvci.ml.sparkBuilder.SparkBuilder
import dev.ardvci.ml.train.RegressionTrain
import dev.ardvci.ml.writer.ModelWriter

case class RequestData(area: Int, bedrooms: Int, bathrooms: Int, stories: Int, mainroad: Int, guestroom: Int,
basement: Int, hotwaterheating: Int, airconditioning: Int, parking: Int, prefarea: Int)
case class ResponseData(prediction: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val requestDataFormat: RootJsonFormat[RequestData] = jsonFormat11(RequestData)
  implicit val responseDataFormat: RootJsonFormat[ResponseData] = jsonFormat1(ResponseData)
}

object LowLevelApıRest extends App with JsonSupport {
  implicit val system: ActorSystem = ActorSystem("MySystem")
  val prediction = new GetPredictionWithJson
  val writer = new ModelWriter
  val train = new RegressionTrain
  val trainedModel = train.train
  writer.saveModel("scala-spark-akka-ml/src/out", trainedModel)
  val sparkBuilder = new SparkBuilder
  val spark = sparkBuilder.build("ml1", "local[*]")

  // Define the endpoint route
  val route1 =
    path("api" / "v1" / "predict") {
      post {
        respondWithHeader(RawHeader("Content-Type", "application/json"))
        entity(as[RequestData]) { requestData =>
          val predictionfromJson = prediction.predict(spark,requestData)
          val response = ResponseData(predictionfromJson)
          complete(response)
        }
      }
    }
  val route2 =
    path("api") {
      get {
        // Your logic for handling the GET request goes here
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
          """
            <html>
            |<head>
            |  <title>Welcome to the Spark - Akka - ML - Deployment Project</title>
            |</head>
            |<body>
            |  <h1>Welcome to the Spark - Akka - ML - Deployment Project</h1>
            |  <p>In order to use the API, please follow these instructions:</p>
            |  <ul>
            |    <li>Endpoint: /predict</li>
            |    <li>Method: POST</li>
            |    <li>Content-Type: application/json</li>
            |    <li>JSON Parameters:</li>
            |    <ul>
            |      <li>area: Int (e.g., 2000)</li>
            |      <li>bedrooms: Int (e.g., 3)</li>
            |      <li>bathrooms: Int (e.g., 2)</li>
            |      <li>stories: Int (e.g., 2)</li>
            |      <li>mainroad: Int (0 or 1)</li>
            |      <li>guestroom: Int (0 or 1)</li>
            |      <li>basement: Int (0 or 1)</li>
            |      <li>hotwaterheating: Int (0 or 1)</li>
            |      <li>airconditioning: Int (0 or 1)</li>
            |      <li>parking: Int (0 or 1)</li>
            |      <li>prefarea: Int (0 or 1)</li>
            |    </ul>
            |    <li>Example JSON:</li>
            |    <pre>
            |{
            |  "area": 2000,
            |  "bedrooms": 3,
            |  "bathrooms": 2,
            |  "stories": 2,
            |  "mainroad": 1,
            |  "guestroom": 0,
            |  "basement": 1,
            |  "hotwaterheating": 0,
            |  "airconditioning": 1,
            |  "parking": 1,
            |  "prefarea": 1
            |}
            |    </pre>
            |  </ul>
            |</body>
            |</html>
            |""".stripMargin))
      }
    }

  val combinedRoutes = route1 ~ route2

  // Start the HTTP server
  val bindingFuture = Http().newServerAt("localhost", 8080).bind(combinedRoutes)
  println(s"Server online at http://localhost:8080/api")
}
