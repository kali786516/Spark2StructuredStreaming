genericconfig = configParser.RawConfigParser()
genericconfig.read("configFile")

self.config = ConfigParser.ConfigParser()
#important line for not lowering
# https://stackoverflow.com/questions/19359556/configparser-reads-capital-keys-and-make-them-lower-case
self.config.optionxform = str

genericSparkConfig = genericconfig.items("spark_properties")


sc_conf = SparkConf()
configObj = sc_conf.setAppName(job_name).setAll(genericSparkConfig)

spark = SparkSession.builder.enableHiveSupport().appName(job_name).enableHiveSupport().config(configObj).getOrCreate()