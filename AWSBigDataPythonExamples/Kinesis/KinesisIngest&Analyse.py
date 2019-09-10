#Let's install boto3, import some dependencies and then set some variables:
pip install boto3

import boto3
import json
from datetime import datetime
import calendar
import random
import time
import uuid

# The name of the stream we'll be creating
my_stream_name = 'our-penguin-stream'

# Our Kinesis client that we'll use to interact with the Kinesis Service
kinesis_client = boto3.client('kinesis', region_name='us-east-1')

#Now we can create the Kinesis Data Stream

# Create the Stream
kinesis_client.create_stream(
    StreamName=my_stream_name,
    ShardCount=1
)

#And write a function that will add data to our stream

def put_to_stream(kin_client):
    record = {
        'purchase_id': str(uuid.uuid4()),
        'timestamp': str(calendar.timegm(datetime.utcnow().timetuple())),
        'item_sold': random.choice(['Shoes', 'Shirt', 'Jeans'])
    }
    kinesis_client.put_record(
        StreamName=my_stream_name,
        Data=json.dumps(record),
        PartitionKey='a-partition'
    )

#Let's run that function a few times to send that data into the stream (Wait for the stream to be created!)

i = 0
while i < 20:
    i += 1
    put_to_stream(kinesis_client)
    time.sleep(.3)

#Now, let's take a look at all that data

# Get some information on the stream required to read data from it
response = kinesis_client.describe_stream(StreamName=my_stream_name)
my_shard_id = response['StreamDescription']['Shards'][0]['ShardId']

# With the shard id then you can get the shard iterator to actually review the data
shard_iterator = kinesis_client.get_shard_iterator(
    StreamName=my_stream_name,
    ShardId=my_shard_id,
    ShardIteratorType='TRIM_HORIZON'
)

my_shard_iterator = shard_iterator['ShardIterator']

# Now you can actually look at the data inside the Kinesis data stream
record_response = kinesis_client.get_records(
    ShardIterator=my_shard_iterator,
    Limit=1
)

# Iterate over all the data
while 'NextShardIterator' in record_response:
    record_response = kinesis_client.get_records(
        ShardIterator=record_response['NextShardIterator'],
        Limit=1
    )
    print(record_response)
    print('')
    print(record_response['Records'][0]['Data'])
    print('\n\n')
    time.sleep(5)

#Now that we know the data is going into the stream, we can use Kinesis Analytics to analyze the streaming data

# Start sending streaming data into the Kinesis Data Stream, then we can connect it to Kinesis Analytics
while True:
    put_to_stream(kinesis_client)
    time.sleep(.3)

#SQL Code for Kinesis Analytics

CREATE OR REPLACE STREAM "DESTINATION_SQL_STREAM" (
    "item_sold" VARCHAR(8),
                count_items INTEGER
);


CREATE OR REPLACE PUMP "STREAM_PUMP" AS INSERT INTO "DESTINATION_SQL_STREAM"
SELECT STREAM
"item_sold",
COUNT(*) as count_items
FROM "SOURCE_SQL_STREAM_001"
GROUP BY
"item_sold",
FLOOR(("SOURCE_SQL_STREAM_001".ROWTIME - TIMESTAMP '1970-01-01 00:00:00') SECOND / 10 TO SECOND);



