#First, we need to install a few dependencies
pip install boto3 cython PyHamcrest

#Now, we have to perform some imports
from __future__ import print_function
import boto3
import json
import decimal
import sys
import random
from time import sleep
from boto3.dynamodb.conditions import Key, Attr
import pandas as pd

#Let's create our 'movies' table and Local Secondary Index

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
    LocalSecondaryIndexes=[
        {
            'IndexName': 'year-actor-index',
            'KeySchema': [
                {
                    'KeyType': 'HASH',
                    'AttributeName': 'year'
                },
                {
                    'KeyType': 'RANGE',
                    'AttributeName': 'actor'
                }
            ],

            'Projection': {
                'ProjectionType': 'ALL',
            }
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
        {
            'AttributeName': 'actor',
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
table.put_item(
    Item={
        'year': 2005,
        'title': 'Batman Begins',
        'actor': 'Christian Bale'
    }
)
table.put_item(
    Item={
        'year': 2008,
        'title': 'The Dark Knight Rises',
        'actor': 'Christian Bale'
    }
)
table.put_item(
    Item={
        'year': 2008,
        'title': 'Tropic Thunder',
        'actor': 'Robert Downey Jr.'
    }
)
table.put_item(
    Item={
        'year': 2008,
        'title': 'Iron Man',
        'actor': 'Robert Downey Jr.'
    }
)

response = table.scan()

for i in response['Items']:
    print("added item:", i['year'], ":", i['title'], ":", i['actor'])

#Now, let's perform some queries

dynamodb = boto3.resource('dynamodb',  region_name='us-east-1', )
table = dynamodb.Table('movies')

response = table.scan()

item_list = []
for i in response['Items']:
    item = {'year':i['year'], 'title':i['title'], 'actor' :i['actor']}
    item_list.append(item)
df = pd.DataFrame(data=item_list)
df

#Now, let's perform a query for all movies released in 2008 with titles that start with "T"

dynamodb = boto3.resource('dynamodb',  region_name='us-east-1')
table = dynamodb.Table('movies')

response = table.query(
    KeyConditionExpression=Key('year').eq(2008) & Key('title').begins_with('T')
)

item_list = []
for i in response['Items']:
    item = {'year':i['year'], 'title':i['title'], 'actor' :i['actor']}
    item_list.append(item)
df = pd.DataFrame(data=item_list)
df

#Next, we will query the Local Secondary Index for all movies released by Robert Downey Jr. in 2008

dynamodb = boto3.resource('dynamodb',  region_name='us-east-1', )
table = dynamodb.Table('movies')


response = table.query(
    IndexName = 'year-actor-index',
    KeyConditionExpression=Key('year').eq(2008) & Key('actor').eq('Robert Downey Jr.')

)

item_list = []
for i in response['Items']:
    item = {'year':i['year'], 'title':i['title'], 'actor' :i['actor']}
    item_list.append(item)
df = pd.DataFrame(data=item_list)
df


