package com.dataframe.part26.HadoopMRToMsExcel;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.ReduceContext;
import org.apache.hadoop.mapreduce.Reducer;
import org.zuinnote.hadoop.office.format.common.dao.SpreadSheetCellDAO;
import org.zuinnote.hadoop.office.format.common.dao.TextArrayWritable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import org.zuinnote.hadoop.office.format.common.util.MSExcelUtil;

public class HadoopOfficeExcelReducer extends Reducer<Text,TextArrayWritable,NullWritable,SpreadSheetCellDAO> {

    private static final Log LOG = LogFactory.getLog(HadoopOfficeExcelReducer.class);
    private static final String CSV_SEPARATOR = ",";
    private static final NullWritable EMPTYKEY = NullWritable.get();
    private int currentRowNum = 0;

    public void reduce(Text key, Iterable<TextArrayWritable> values, Reducer<Text, TextArrayWritable, NullWritable, SpreadSheetCellDAO>.Context context)
            throws IOException, InterruptedException
    {
        for (TextArrayWritable currentRow : values)
        {
            String[] currentRowTextArray = currentRow.toStrings();
            if (currentRowTextArray.length > 0)
            {
                int currentColumnNum = 0;
                for (String currentColumn : currentRowTextArray)
                {
                    String formattedValue = currentColumn;
                    String comment = "";
                    String formula = "";
                    String address = MSExcelUtil.getCellAddressA1Format(this.currentRowNum, currentColumnNum);
                    String sheetName = key.toString();
                    SpreadSheetCellDAO currentSCDAO = new SpreadSheetCellDAO(formattedValue, comment, formula, address, sheetName);
                    context.write(EMPTYKEY, currentSCDAO);
                    currentColumnNum++;
                }
                this.currentRowNum += 1;
            }
        }
    }

}
