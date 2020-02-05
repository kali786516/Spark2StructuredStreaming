genericconfig = configParser.RawConfigParser()
genericconfig.read("configFile")

genericSparkConfig = genericconfig.items("spark_properties")

sc_conf = SparkConf()
configObj = sc_conf.setAppName(job_name).setAll(genericSparkConfig)

spark = SparkSession.builder.enableHiveSupport().appName(job_name).enableHiveSupport().config(configObj).getOrCreate()