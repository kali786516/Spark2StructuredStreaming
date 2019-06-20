import os
import sys
from pyspark.sql.types import *
from pyspark.sql import Row
from pyspark.sql.functions import udf, col
from pyspark.ml import PipelineModel
from flask import Flask,jsonify,request,Response
import pyspark
from pyspark.sql import SQLContext
from pyspark.sql import SparkSession
from pyspark.ml.clustering import KMeansModel
from pyspark.ml.feature import VectorAssembler
from pyspark.ml.linalg import *
from pyspark.ml.recommendation import ALSModel
from pyspark.sql import *

'''
pip3 install pyspark==2.3.1
https://stackoverflow.com/questions/50987944/key-not-found-pyspark-driver-callback-host
pip3 install numpy
pip3 install xgboost


export SPARK_HOME=/usr/local/Cellar/apache-spark/2.4.3/libexec
export PYTHONPATH=$SPARK_HOME/python/:$PYTHONPATH
export PYTHONPATH=$SPARK_HOME/python/lib/py4j-0.10.7-src.zip:$PYTHONPATH
export IPYTHON=1
export PYSPARK_PYTHON=/usr/local/bin/python3
export PYSPARK_DRIVER_PYTHON=ipython3
python3 /Users/kalit_000/PycharmProjects/FlaskProject/movieRecomendationApi.py

http://localhost:5009/predict_churn?customer=IN|65|415|329-6603|no|no|0|129.1|137|21.95|228.5|83|19.42|208.8|111|9.4|12.7|6|3.43|4

http://localhost:5009/movie_prediction?movie=547|1865|1.0


cp -rf /Users/kalit_000/Downloads/gitCode/Spark2StructuredStreaming/SparkMLModels/RatingCollaborative/Recomendation.model_v1 /Users/kalit_000/PycharmProjects/FlaskProject/RecomendationMLModels

'''

app = Flask(__name__)

spark = SparkSession.builder.master("local").appName("Movie Recomendation") \
    .config("spark.jars", "/Users/kalit_000/PycharmProjects/FlaskProject/XGBoostWrappers/xgboost4j-spark-0.80.jar") \
    .config("spark.jars", "/Users/kalit_000/PycharmProjects/FlaskProject/XGBoostWrappers/xgboost4j-0.80.jar") \
    .getOrCreate()

@app.route('/')
def index():
    return"Movie Recomendation Prediction"

@app.route('/movie_prediction', methods=['GET', 'POST'])
def movie_prediction():
    data = request.args.get('movie', '')
    df = spark.createDataFrame([[data]], ['movie_profile'])
    schema = StructType([
        StructField("userID", IntegerType(), True),
        StructField("movieId", IntegerType(), True),
        StructField("rating", DoubleType(), True)
    ])

    def split_customer_(s):
        arr = s.split("|")
        return int(arr[0]), int(arr[1]), float(arr[2])

    split_customer = udf(split_customer_, schema)

    transformed_df = df.withColumn("movie", split_customer(col("movie_profile")))
    selected_trans_df = transformed_df.select("movie.*")

    selected_trans_df.show(10)

    pipelineModel = ALSModel.load \
        ("file:///Users/kalit_000/PycharmProjects/FlaskProject/RecomendationMLModels/Recomendation.model_v1")

    pipelineModelPredictions = pipelineModel.transform(selected_trans_df)

    pipelineModelPredictions.show(10)

    pipelineModel.recommendForAllUsers(1).select("recommendations").registerTempTable("test")

    top3Movies = str(spark.sql("select replace(split(cast(recommendations as string),',')[0],'[','') as MovieID from test").collect()).replace("'","").replace("Row","").replace("(","").replace(")","").replace("[","").replace("]","").replace("MovieID=","")

    #pipelineModel.recommendForAllUsers(1).select("recommendations").collect()

    #processedvalue = str.split(str.replace(str.replace(''.join(top3Movies),"[[",""),"]]",""),",")

    #finalMovieId = processedvalue[0]

    #print(finalMovieId)

    #print(str.replace(str.replace(''.join(top3Movies),"[[",""),"]]",""))

    print(top3Movies)

    returnStr = """<html>
            <body>
            <h1>Top 3 Movies</h1>
            <h3 style="color:blue;">
                 Top3Movies :- {}
             </h3>
                </body>
                </html>""".format(top3Movies)
    return returnStr

app.run(port=5009)

