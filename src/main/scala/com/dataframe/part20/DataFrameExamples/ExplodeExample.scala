package com.dataframe.part20.DataFrameExamples
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{SQLContext, SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.storage.StorageLevel

object ExplodeConcatExcelReadExample {
  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("BikeBuyersDecisionTrees")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val spark = SparkSession.builder().appName("Use Collaborative Filtering for movie Recommendations").master("local[*]").getOrCreate()
    val sc = spark.sparkContext

    import spark.implicits._

    //spark-shell --packages com.crealytics:spark-excel_2.11:0.9.12

    val dfNew1 = spark.read.format("com.crealytics.spark.excel").option("header","false")
      .option("sheetName", "Source File").option("useHeader", "true").option("treatEmptyValuesAsNulls","false").option("inferSchema","false")
      .option("addColorColumns", "false").option("startColumn", 0).option("endColumn", 28).option("timestampFormat","MM-dd-yyyy HH:mm:ss").option("maxRowsInMemory",20)
      .option("excerptSize",10).load("/LG_MAPS_Bridge_Mapping.xlsx")


    val dfNew2 = spark.read.format("com.crealytics.spark.excel").option("header","false").option("sheetName", "Source File")
      .option("useHeader", "false") .option("treatEmptyValuesAsNulls", "false").option("inferSchema","false").option("addColorColumns", "false")
      .option("startColumn", 29).option("endColumn", 200).option("timestampFormat", "MM-dd-yyyy HH:mm:ss").option("maxRowsInMemory", 20)
      .option("excerptSize", 10).load("/LG_MAPS_Bridge_Mapping.xlsx")

    dfNew1.registerTempTable("payers_data")

    dfNew2.registerTempTable("kali")

    val test2=spark.sql("select * from kali limit 1")

    val dfResults = dfNew2.select(concat_ws(",",test2.columns.map(c => col(c)): _*).alias("joined_cols"))

    dfResults.registerTempTable("kali2")

    val df3=spark.sql("with cte as ( select explode(split(joined_cols, ',')) as product from kali2 ), cte_2 as ( " +
      "select prod_sk,prod_id,prod_nm,prod_desc from " +
      "edh_dsl.dds_prod_d where upper(prod_lvl_id)='BRAND') , cte_3 as ( select * from payers_data cross join cte ) select '2018-03-01',b.prod_sk,b.prod_id," +
      "b.prod_nm,a.ims_plan_id, " +
      "a.ims_plan_name, a.ims_plan_bridge_id, a.ims_plan_hcm_id, a.ims_pbm_name, a.imspbm_id, a.ims_modelvartype, a.ims_modelvartype_id, " +
      "a.ims_payer_name, a.imspayer_id, a.plan_hca_cust_sk," +
      "'' as plan_id,'' as cust_nm,a.az_pay_type,a.az_pay_type_id, a.account_hca_cust_sk, a.az_account_name, a.az_account_bridge_id, " +
      "a.az_payer_hca_cust_sk, a.az_payer_name, a.az_payer_bridge_id, " +
      "a.az_pbm_hca_cust_sk, a.az_pbm_name, a.az_pbm_bridge_id, a.az_payer_corporate_parent_name, a.az_payer_corporate_patent_id, a.hq_state, a.broad_market_trx, " +
      "a.plan_lives," +
      "'2018-03-26' as partition_date from cte_3 as a inner join cte_2 as b on " +
      "a.product=b.prod_nm")


    df3.registerTempTable("kali3")

    spark.sql("drop table if exists lg_base.lg_payers_bridge_tmp")

    spark.sql("create table lg_base.lg_payers_bridge_tmp as select * from kali3")


  }

}
