#Install dependencies
pip install boto3 cython PyHamcrest

#Import dependencies, create a table, load some data
from __future__ import print_function # Python 2/3 compatibility
import boto3
import json
import decimal
import sys
import random
from time import sleep
from boto3.dynamodb.conditions import Key, Attr


dynamodb = boto3.resource('dynamodb',  region_name='us-east-1')


table = dynamodb.create_table(
    TableName= 'movies',
    KeySchema=[
        {
            'KeyType': 'HASH',
            'AttributeName': 'year'
        },
        {
            'KeyType': 'RANGE',
            'AttributeName': 'title'
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
        }
    ],
    ProvisionedThroughput={
        'ReadCapacityUnits': 2,
        'WriteCapacityUnits': 2
    }
)
# Wait until the table exists.
table.meta.client.get_waiter('table_exists').wait(TableName='movies')
print('Table is ready, please continue as instructed.')

#Let's upload some data

dynamodb = boto3.resource('dynamodb',  region_name='us-east-1')
table = dynamodb.Table('movies')

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
                    'year': year, # add a +1 to this the second time around.
                    'title': title,
                    'actor': star,
                    'rating': rating,
                    'running_time': running_time,
                    'uploaded' : uploaded
                }
            )

#Now, let's perform some queries:

import pprint

dynamodb = boto3.resource('dynamodb',  region_name='us-east-1', )
table = dynamodb.Table('movies')

response = table.scan(
    ReturnConsumedCapacity='TOTAL',
    ConsistentRead=True  # Also try with Consistent Reads
)

total_consumed_read_capacity = response['ConsumedCapacity']
print(total_consumed_read_capacity)


#Now, let's do a query that uses the entire primary key

import pprint

dynamodb = boto3.resource('dynamodb',  region_name='us-east-1', )
table = dynamodb.Table('movies')

response = table.query(
    ReturnConsumedCapacity='TOTAL',
    KeyConditionExpression=Key('year').eq(2011) & Key('title').eq('Byzantium')
)


total_consumed_read_capacity = response['ConsumedCapacity']
print(total_consumed_read_capacity)






