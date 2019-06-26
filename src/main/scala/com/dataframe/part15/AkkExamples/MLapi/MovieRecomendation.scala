package com.dataframe.part15.AkkExamples.MLapi

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.settings.ServerSettings
import com.typesafe.config.ConfigFactory
import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import org.apache.spark.ml.recommendation.{ALS, ALSModel}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import scala.io.StdIn
import scala.io.StdIn
import org.apache.spark.ml.recommendation.ALSModel

object MovieRecomendation {

  // needed to run the route
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext = system.dispatcher

  def main(args: Array[String]): Unit = {

    def getSparkSession():(SparkSession,ALSModel) = {
       val sparksession = SparkSession.builder.master("local").appName("Movie Recomendation")
      .config("spark.jars", "/Users/kalit_000/PycharmProjects/FlaskProject/XGBoostWrappers/xgboost4j-spark-0.80.jar")
      .config("spark.jars", "/Users/kalit_000/PycharmProjects/FlaskProject/XGBoostWrappers/xgboost4j-0.80.jar")
      .getOrCreate()

      val model = ALSModel.load("/Users/kalit_000/PycharmProjects/FlaskProject/RecomendationMLModels/Recomendation.model_v1")

      (sparksession,model)
    }

    def processData(data:String,spark:SparkSession,model:ALSModel):String={
      import spark.implicits._
      println(data)
      spark.sparkContext.parallelize(List(data)).toDF("raw_data").createOrReplaceTempView("rawdata")
      val selected_trans_df=spark.sql("select cast(split(raw_data,',')[0] as int) as userID,cast(split(raw_data,',')[1] as int) as movieId,cast(split(raw_data,',')[2] as int) as rating from rawdata")

      //val model = ALSModel.load("/Users/kalit_000/PycharmProjects/FlaskProject/RecomendationMLModels/Recomendation.model_v1")

      val pipelineModelPredictions = model.transform(selected_trans_df)

      pipelineModelPredictions.show(10)

      model.recommendForAllUsers(1).select("recommendations").registerTempTable("test")

      val top3Movies = spark.sql("select replace(split(cast(recommendations as string),',')[0],'[','') as MovieID from test").collect().toString.replace("'","").replace("Row","").replace("(","").replace(")","").replace("[","").replace("]","").replace("MovieID=","")

      println(top3Movies)

      top3Movies


    }

    def processPipeLine(rawData:String)={
      val (sparksession,alsmodel) = getSparkSession()
      processData(rawData,sparksession,alsmodel)
    }

    def route =
      pathPrefix("movie_prediction") {
        path("moviedata" / Segment) { moviedata =>
          get {
            println(moviedata)
            val top3Movies = processPipeLine(moviedata)
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Hello $top3Movies from Akka Http!</h1>"))
          } ~
            post {
              entity(as[String]) { entity =>
                println(moviedata)
                val top3Movies = processPipeLine(moviedata)
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<b>Thanks $top3Movies for posting  your message <i>$entity</i></b>"))
              }
            }
        }
      }


    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ ⇒ system.terminate()) // and shutdown when done



  }


}
