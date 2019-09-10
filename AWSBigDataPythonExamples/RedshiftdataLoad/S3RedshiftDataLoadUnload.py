#Let's install some requirements and set some variables:
!pip install psycopg2-binary boto3


#Uncomment the HOST and DATA_BUCKET variables and add the values from the Learning Activity Credentials screen. Example values have been provided in order to help ensure you use the right ones.
# HOST = "cfst-CHANGEME-redshiftcluster-otherjibberish.123123abcabc.us-east-1.redshift.amazonaws.com" # Change this too
# DATA_BUCKET = "cfst-1279-9d31b9a9fc45a278465028065914d2-s3bucket-1vwpozq2bm4ss" # Change this
DATABASE = "dev"
USER = "clouduser"
PASSWORD = "Fa%YrN^Pq4.xM"
PORT = 5439

#Now, we will establish a connection to the database and test that connection by reading the database name:

import psycopg2
from pprint import pprint
​
query = '''SELECT datname FROM pg_database;'''
​
conn = psycopg2.connect(
    host=HOST,
    user=USER,
    port=PORT,
    password=PASSWORD,
    dbname=DATABASE
)
​
def runquery(conn,query,commit_bool=False):
    """
    Just run a query given a connection
    """

    curr=conn.cursor()
    curr.execute(query)
    if commit_bool:
        conn.commit()
        return None
    for row in curr.fetchall():
        pprint(row)
    return None
​
runquery(conn, query)

#Next, we create a Redshift Table

table_create_query = '''
create table movies(
    title varchar(300) not null,
    year integer not null,
    rating real not null,
    running_time_secs integer not null
);
'''
​
runquery(conn, table_create_query, commit_bool=True)
​
#Let's view the databases to ensure it was created:

view_public_tables_query = '''
SELECT DISTINCT
  tablename
FROM
  PG_TABLE_DEF
WHERE
  schemaname = 'public';
'''
​
​
runquery(conn, view_public_tables_query)

#Now to get our data into S3

!aws s3 ls
import gzip
import shutil
with open('./data.csv', 'rb') as f_in:
    with gzip.open('data.csv.gz', 'wb') as f_out:
        shutil.copyfileobj(f_in, f_out)
​
import boto3
s3 = boto3.resource('s3')
s3.meta.client.upload_file('./data.csv.gz', DATA_BUCKET, 'data.csv.gz')

#Copy Data from S3 to Redshift
# IAM_ROLE = "arn:aws:iam::123456789123:role/cfst-1279-b8287a53b943ea3-CloudUserAndRedshiftIAMR-1DY76SW011SCJ"

copy_query = '''
copy movies from 's3://{0}/data.csv.gz'
iam_role '{1}'
CSV
GZIP
IGNOREHEADER 1;
'''.format(DATA_BUCKET, IAM_ROLE)
​
​
runquery(conn, copy_query, commit_bool=True)

#Now, we can check the data for the best movies of 2013

best_2013_movies_query = '''
SELECT title, rating FROM movies
WHERE year = 2013 and rating > 8.0
ORDER BY rating DESC;
'''.format(DATA_BUCKET, IAM_ROLE)
​
​
runquery(conn, best_2013_movies_query)

#Now that we know this works we can unload the data to S3 so that it can be reviewed later

unload_best_2013_movies_query = '''
UNLOAD ('
    SELECT title, rating FROM movies
    WHERE year = 2013 and rating > 8.0
    ORDER BY rating DESC;'
)
TO 's3://{0}/output/'
iam_role '{1}'
'''.format(DATA_BUCKET, IAM_ROLE)
​
​
runquery(conn, unload_best_2013_movies_query)

#Here's a nifty little query that can fix things sometimes if you edit the queries and they fail.

rollback_query = '''rollback;'''
runquery(conn, rollback_query, commit_bool=True)
All Done! Awesome Job!

