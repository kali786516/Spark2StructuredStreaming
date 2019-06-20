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
python3 /Users/kalit_000/PycharmProjects/FlaskProject/TitaniClusteringFlaskApi.py

http://localhost:5009/predict_churn?customer=IN|65|415|329-6603|no|no|0|129.1|137|21.95|228.5|83|19.42|208.8|111|9.4|12.7|6|3.43|4

http://localhost:5009/predict_titanic_survival?passenger=3|22|7.25|0|[3,22,7.25,0]
cp -rf /Users/kalit_000/Downloads/gitCode/Spark2StructuredStreaming/SparkMLModels/RatingCollaborative/Recomendation.model_v1 /Users/kalit_000/PycharmProjects/FlaskProject/RecomendationMLModels

'''

app = Flask(__name__)

spark = SparkSession.builder.master("local").appName("Spark Titanic Survival Prediction") \
    .config("spark.jars", "/Users/kalit_000/PycharmProjects/FlaskProject/XGBoostWrappers/xgboost4j-spark-0.80.jar") \
    .config("spark.jars", "/Users/kalit_000/PycharmProjects/FlaskProject/XGBoostWrappers/xgboost4j-0.80.jar") \
    .getOrCreate()

@app.route('/')
def index():
    return"Titanic Survival Prediction"


@app.route('/predict_titanic_survival', methods=['GET', 'POST'])
def predict_titanic_survival():
    data = request.args.get('passenger', '')
    df = spark.createDataFrame([[data]], ['passenger_profile'])
    schema = StructType([
        StructField("Pclass", IntegerType(), True),
        StructField("Age", IntegerType(), True),
        StructField("Fare", DoubleType(), True),
        StructField("Sex", IntegerType(), True),
        StructField("features", StringType , True)
    ])

    def split_customer_(s):
        arr = s.split("|")
        return int(arr[0]), int(arr[1]), float(arr[2]), int(arr[3]), arr[4]

    split_customer = udf(split_customer_, schema)

    transformed_df = df.withColumn("passenger", split_customer(col("passenger_profile")))
    selected_trans_df = transformed_df.select("passenger.*")

    selected_trans_df.show(10)

    requiredFeatures = ["Pclass", "Age", "Fare", "Sex"]

    assembler = VectorAssembler(inputCols=requiredFeatures,outputCol='features1')

    assemberDF = assembler.transform(selected_trans_df)

    assemberDF.show(10)

    assemberDF.printSchema()

    pipelineModel = KMeansModel.load \
        ("file:///Users/kalit_000/PycharmProjects/FlaskProject/ClusteringMLModels/TitanicClustering/TitanicClustering.model_v1")

    pipelineModelPredictions = pipelineModel.transform(selected_trans_df)

    pipelineModelPredictions.show(10)

    prediction = pipelineModelPredictions.select("prediction").collect()
    print(prediction)

    print("I am here")

    predictionLabel = int(prediction[0].prediction)
    returnStr = ''
    print(predictionLabel)
    if predictionLabel == 1:
        print(predictionLabel)
        returnStr="""<html>
            <body>
            <h1>Welcome to the Offers Page</h1>
            <h3 style="color:blue;">
                 predictionLabel :- {}
             plan</h3>
                </body>
                </html>""".format(predictionLabel)
        return returnStr
    else:
        returnStr="""<html>
             <body>
              <h1>Welcome to the Offers Page</h1>
              <h3 style="color:blue;">
                  predictionLabel :- {}
               </h3>
               </body>
               </html>""".format(predictionLabel)

        return returnStr

app.run(port=5009)
