use comcast;
create table comcast_test (
unique_id uuid ,
column_order int,
column1 decimal,
column2 decimal,
column3 decimal,
column4 decimal,
column5 decimal,
created_ts timestamp ,
PRIMARY KEY (unique_id,created_ts)
)
WITH CLUSTERING ORDER BY (created_ts DESC);

CREATE INDEX ON comcast.comcast_test (column_order);