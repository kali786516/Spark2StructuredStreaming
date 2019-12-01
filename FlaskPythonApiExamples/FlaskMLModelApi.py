import os
import sys
from pyspark.sql.types import StringType, DoubleType, StructType, StructField
from pyspark.sql import Row
from pyspark.sql.functions import udf, col
from pyspark.ml import PipelineModel
from flask import Flask,jsonify,request,Response
import pyspark
from pyspark.sql import SQLContext
from pyspark.sql import SparkSession


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
python3 /Users/kalit_000/Downloads/gitCode/Spark2StructuredStreaming/FlaskPythonApiExamples/FlaskMLModelApi.py

cp /Users/kalit_000/.m2/repository/ml/dmlc/xgboost4j/0.80/xgboost4j-0.80.jar /Users/kalit_000/PycharmProjects/FlaskProject/XGBoostWrappers/
cp /Users/kalit_000/.m2/repository/ml/dmlc/xgboost4j-spark/0.80/xgboost4j-spark-0.80.jar /Users/kalit_000/PycharmProjects/FlaskProject/XGBoostWrappers/

http://localhost:5009/predict_churn?customer=IN|65|415|329-6603|no|no|0|129.1|137|21.95|228.5|83|19.42|208.8|111|9.4|12.7|6|3.43|4
'''

app = Flask(__name__)

spark = SparkSession.builder.master("local").appName("Spark Flask Api") \
    .config("spark.jars", "/Users/kalit_000/PycharmProjects/FlaskProject/XGBoostWrappers/xgboost4j-spark-0.80.jar") \
    .config("spark.jars", "/Users/kalit_000/PycharmProjects/FlaskProject/XGBoostWrappers/xgboost4j-0.80.jar") \
    .getOrCreate()


# .config("spark.jars", "/Users/kalit_000/.m2/repository/ml/dmlc/xgboost4j/0.80/xgboost4j-0.80.jar")\

# The below two zip files are python wrappers that XGBoost4Spark depends on when running in Python.
# Download URL: https://s3.amazonaws.com/qubole-preddy.us/xgb-py-dep/spark-xgb-wrapper.zip
spark.sparkContext.addPyFile("file:///Users/kalit_000/PycharmProjects/FlaskProject/MLModels/XGBoostWrappers/pyspark-xgb-wrapper.zip")
#spark.sparkContext.addPyFile("https://s3.amazonaws.com/qubole-preddy.us/xgb-py-dep/spark-xgb-wrapper.zip")
# Download URL: https://s3.amazonaws.com/qubole-preddy.us/xgb-py-dep/pyspark-xgb-wrapper.zip
spark.sparkContext.addPyFile("file:///Users/kalit_000/PycharmProjects/FlaskProject/MLModels/XGBoostWrappers/spark-xgb-wrapper.zip")

#spark.sparkContext.addPyFile("https://s3.amazonaws.com/qubole-preddy.us/xgb-py-dep/pyspark-xgb-wrapper.zip")

spark.sparkContext.addPyFile("file:///Users/kalit_000/PycharmProjects/FlaskProject/XGBoostWrappers/xgboost4j-0.80.jar")
spark.sparkContext.addPyFile("file:///Users/kalit_000/PycharmProjects/FlaskProject/XGBoostWrappers/xgboost4j-spark-0.80.jar")

@app.route('/')
def index():
    return"Hello, World!"


@app.route('/predict_churn', methods=['GET', 'POST'])
def predict_churn():
    data = request.args.get('customer', '')
    df = spark.createDataFrame([[data]], ['customer_profile'])
    schema = StructType([
        StructField("state", StringType(), True),
        StructField("account_length", DoubleType(), True),
        StructField("area_code", StringType(), True),
        StructField("phone", StringType(), True),
        StructField("intl_plan", StringType(), True),
        StructField("voice_mail_plan", StringType(), True),
        StructField("number_vmail_messages", DoubleType(), True),
        StructField("total_day_minutes", DoubleType(), True),
        StructField("total_day_calls", DoubleType(), True),
        StructField("total_day_charge", DoubleType(), True),
        StructField("total_eve_minutes", DoubleType(), True),
        StructField("total_eve_calls", DoubleType(), True),
        StructField("total_eve_charge", DoubleType(), True),
        StructField("total_night_minutes", DoubleType(), True),
        StructField("total_night_calls", DoubleType(), True),
        StructField("total_night_charge", DoubleType(), True),
        StructField("total_intl_minutes", DoubleType(), True),
        StructField("total_intl_calls", DoubleType(), True),
        StructField("total_intl_charge", DoubleType(), True),
        StructField("number_customer_service_calls", DoubleType(), True)
    ])

    def split_customer_(s):
        arr = s.split("|")
        return arr[0], float(arr[1]), arr[2], arr[3], arr[4], arr[5], \
               float(arr[6]), float(arr[7]), float(arr[8]), float(arr[9]), \
               float(arr[10]), float(arr[11]), float(arr[12]), float(arr[13]), \
               float(arr[14]), float(arr[15]), float(arr[16]), float(arr[17]), \
               float(arr[18]), float(arr[19])

    split_customer = udf(split_customer_, schema)

    transformed_df = df.withColumn("customer", split_customer(col("customer_profile")))
    selected_trans_df = transformed_df.select("customer.*")

    pipelineModel = PipelineModel.load \
        ("file:///Users/kalit_000/PycharmProjects/FlaskProject/MLModels/XGBoostMLModel/telco_churn_xg.model_v1")

    pipelineModelPredictions = pipelineModel.transform(selected_trans_df)

    prediction = pipelineModelPredictions.select("prediction").collect()
    print(prediction)

    predictionLabel = int(prediction[0].prediction)
    returnStr = ''
    print(predictionLabel)
    if predictionLabel == 1:
        print(predictionLabel)
        returnStr = """<html>
            <body>
            <h1>Welcome to the Offers Page</h1>
            <h3 style="color:blue;">
                     Thank you for being the best part of our Service. 
                     We are pleased to notify you that we are adding 
                     additional 2GB data to your plan for free. 
                     Please give 2 business days to reflect on your 
                     plan</h3>
                        </body>
                        </html>"""
    else:
        returnStr="""<html>
             <body>
              <h1>Welcome to the Offers Page</h1>
              <h3 style="color:blue;">
                 Thank you for Your loyality. 
                 At this time we have no offers, 
                 please come back here to find offers tailored for you
               </h3>
               </body>
               </html>"""

        return returnStr

app.run(port=5009)







