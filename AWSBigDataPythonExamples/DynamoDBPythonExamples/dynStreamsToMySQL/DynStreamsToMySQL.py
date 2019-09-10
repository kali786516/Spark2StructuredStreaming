pip install mysql-connector boto3 cython PyHamcrest

#endpoint = "35.172.134.237" # This is the IP for the Database
#lambdaRole = "arn:aws:iam::410253224468:role/cfst-1048-95df4023b0fe7844f9265c3f01cb-ALambdaRole-1396SFBQTLRG1" # This is the role for the lambda function
username = "cloud_user"
password = "linuxacademy"
dbname = "moviesdb"
tablename = "Movies"

import mysql.connector

mydb = mysql.connector.connect(
    host= endpoint,
    user= username,
    passwd= password
)

mycursor = mydb.cursor()

mycursor = mydb.cursor()
mycursor.execute("CREATE DATABASE " + dbname)

mycursor.execute("SHOW DATABASES")

for x in mycursor:
    print(x)

mydb = mysql.connector.connect(
    host= endpoint,
    user= username,
    passwd= password,
    database= dbname
)

mycursor = mydb.cursor()


mycursor.execute("CREATE TABLE " + tablename + " (Year INT, Title VARCHAR(255), Actor VARCHAR(255), Rating INT, Runtime INT, Uploaded VARCHAR(255))")

mycursor.execute("ALTER TABLE " + tablename + " ADD COLUMN id INT AUTO_INCREMENT PRIMARY KEY")

#Let's add a sample record and SELECT from our database to ensure the database is setup properly

sql = "INSERT INTO " + tablename + " (Year, Title, Actor, Rating, Runtime, Uploaded) VALUES (%s, %s, %s, %s, %s, %s)"
val = ("2018", "The DynamoDB Movie", "Derek Morgan", "10", "7920", "yes")
mycursor.execute(sql, val)

mydb.commit()

print(mycursor.rowcount, "record inserted.")


mydb.commit()
mycursor = mydb.cursor()
mycursor.execute("SELECT * FROM " + tablename)

myresult = mycursor.fetchall()

for x in myresult:
    print(x)

#Now that we have setup the relational database, let's create our "Movies" DynamoDB table:

import boto3
dynamodb = boto3.resource('dynamodb',  region_name='us-east-1')


table = dynamodb.create_table(
    TableName='Movies',
    KeySchema=[
        {
            'AttributeName': 'year',
            'KeyType': 'HASH'  #Partition key
        },
        {
            'AttributeName': 'title',
            'KeyType': 'RANGE'  #Sort key
        }
    ],
    AttributeDefinitions=[
        {
            'AttributeName': 'year',
            'AttributeType': 'N'
        },
        {
            'AttributeName': 'title',
            'AttributeType': 'S'
        },

    ],
    ProvisionedThroughput={
        'ReadCapacityUnits': 2,
        'WriteCapacityUnits': 2
    }
)

# Wait until the table exists.
table.meta.client.get_waiter('table_exists').wait(TableName='Movies')
print('Table is ready, please continue as instructed.')

#Now, let's create our Lambda function. This will process the DynamoDB Stream and insert the records into our relational database:

import boto3
client = boto3.client('lambda',  region_name='us-east-1')


response = client.create_function(
    FunctionName='ddbStream',
    Runtime='nodejs8.10',
    Role= lambdaRole,
    Handler='ddbStream.handler',
    Code={
        'ZipFile': open('ddbStream.zip', 'rb').read()
    },
    Description='Extracts from DynamoDB Stream and adds to Relational DB',
    Timeout=5,
    Environment={
        'Variables': {
            'endPoint': endpoint
        }
    }
)

#Let's add two test items to initialize the function before we upload a lot more data

import boto3
dynamodb = boto3.resource('dynamodb',  region_name='us-east-1')
table = dynamodb.Table('Movies')
table.put_item(
    Item={
        "year": 2018,
        "title": "DynamoDB Streams",
        "actor": "Derek Morgan",
        "rating": 10,
        "running_time": 1800,
        "uploaded": "yes"
    }
)

import boto3
dynamodb = boto3.resource('dynamodb',  region_name='us-east-1')
table = dynamodb.Table('Movies')
table.put_item(
    Item={
        "year": 2019,
        "title": "DynamoDB Streams 2: The Streamening",
        "actor": "Derek Morgan",
        "rating": 10,
        "running_time": 7200,
        "uploaded": "yes"
    }
)

#Now, let's check the relational database to ensure the data streamed. If it didn't, we can modify the data above and try again to ensure it's active:

import pandas as pd

mydb.commit()
mycursor = mydb.cursor()
mycursor.execute("SELECT * FROM " + tablename +";")

myresult = mycursor.fetchall()

item_list = []
for i in myresult:
    item = {'id':i[6],
            'title':i[1],
            'actor' :i[2],
            'rating' :i[3],
            'running_time' :i[4],
            'uploaded' :i[5],
            'year' :i[0]}
    item_list.append(item)
df = pd.DataFrame(data=item_list,columns=['id','year','title','actor','running_time','rating','uploaded'])
df.head(30)

#Now, let's add 100 records to our DynamoDB table

from __future__ import print_function # Python 2/3 compatibility
import boto3
import json
import decimal
import sys
import random
dynamodb = boto3.resource('dynamodb',  region_name='us-east-1')
table = dynamodb.Table('Movies')

choices = ['yes', 'no']
i = 0
with open("moviedata.json") as json_file:
    with table.batch_writer() as batch:
        movies = json.load(json_file, parse_float = decimal.Decimal)
        for movie in movies:
            i = i + 1
            if i == 101:
                break
            year = int(movie['year'])
            title = movie['title']
            star = movie['actors'][0]
            rating = movie['rating']
            running_time = movie['running_time_secs']
            uploaded = random.choice(choices)

            print("Adding movie:", year, title, star, rating, running_time, uploaded)

            batch.put_item(
                Item={
                    'year': year,
                    'title': title,
                    'actor': star,
                    'rating': rating,
                    'running_time': running_time,
                    'uploaded' : uploaded
                }
            )

#Finally, let's find all items in our relational database where the rating was higher than '6' and uploaded is equal to 'no':

import pandas as pd

mydb.commit()
mycursor = mydb.cursor()
mycursor.execute("SELECT * FROM " + tablename + " WHERE uploaded = 'no' AND rating > 6;")

myresult = mycursor.fetchall()

item_list = []
for i in myresult:
    item = {'id':i[6],
            'title':i[1],
            'actor' :i[2],
            'rating' :i[3],
            'running_time' :i[4],
            'uploaded' :i[5],
            'year' :i[0]}
    item_list.append(item)
df = pd.DataFrame(data=item_list,columns=['id','year','title','actor','running_time','rating','uploaded'])
df.head(30)






