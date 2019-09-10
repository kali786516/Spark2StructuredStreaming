#First, we need to install a few dependencies
!pip install boto3 cython PyHamcrest

#Now, we have to perform some imports

from __future__ import print_function # Python 2/3 compatibility
import boto3
import json
import decimal
import sys
import random
import pandas as pd
from time import sleep

#Let's create our 'Movies' table and upload our moviedata.json dataset

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

#Let's upload our data
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

#Let's perform some queries
dynamodb = boto3.resource('dynamodb',  region_name='us-east-1', )
table = dynamodb.Table('Movies')

response = table.scan()

item_list = []
for i in response['Items']:
    item = {'year':i['year'],
            'title':i['title'],
            'actor' :i['actor'],
            'rating' :i['rating'],
            'running_time' :i['running_time'],
            'uploaded' :i['uploaded']}
    item_list.append(item)
df = pd.DataFrame(data=item_list)
df.head()

#Now, let's perform a query for all movies that start with 'T' released in 2013

from boto3.dynamodb.conditions import Key, Attr

dynamodb = boto3.resource('dynamodb',  region_name='us-east-1')
table = dynamodb.Table('Movies')

response = table.query(
    KeyConditionExpression=Key('year').eq(2013) & Key('title').begins_with('T')
)

item_list = []
for i in response['Items']:
    item = {'year':i['year'], 'title':i['title'], 'actor' :i['actor']}
    item_list.append(item)
df = pd.DataFrame(data=item_list)
df

#Now, we need to create a Global Secondary Index
dynamodb = boto3.resource('dynamodb',  region_name='us-east-1')
table = dynamodb.Table('Movies')
table.update(
    AttributeDefinitions = [
        {
            "AttributeName": "uploaded", "AttributeType": 'S'
        },
        {
            "AttributeName": "rating", "AttributeType": 'N'
        }
    ],
    GlobalSecondaryIndexUpdates=[
        {
            'Create': {
                'IndexName': 'rating-title-index',
                'KeySchema': [
                    {
                        'AttributeName': 'uploaded',
                        'KeyType': 'HASH'
                    },
                    {
                        'AttributeName': 'rating',
                        'KeyType': 'RANGE'
                    }
                ],
                'Projection': {
                    'ProjectionType': 'ALL'
                },
                'ProvisionedThroughput': {
                    'ReadCapacityUnits': 2,
                    'WriteCapacityUnits': 2
                }
            }
        }
    ]
)

#Finally, let's find all movies that haven't been uploaded with a rating above 7

dynamodb = boto3.resource('dynamodb',  region_name='us-east-1')
table = dynamodb.Table('Movies')

response = table.query(
    IndexName='rating-title-index',
    KeyConditionExpression=Key('uploaded').eq('no') & Key('rating').gte(7)
)

item_list = []
for i in response['Items']:
    item = {'uploaded':i['uploaded'], 'title':i['title'], 'rating' :i['rating']}
    item_list.append(item)
df = pd.DataFrame(data=item_list)
df



