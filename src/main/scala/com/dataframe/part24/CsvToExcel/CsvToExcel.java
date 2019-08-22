package com.dataframe.part24.CsvToExcel;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.ddf.EscherColorRef;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


public class CsvToExcel {

    public int csvtoexcelmethod(String sourcefilename,String targetfilename,String applicationsheetname) throws IOException {

        ArrayList<ArrayList<String>> allRowAndColData = null;
        ArrayList<String> oneRowData = null;
        String inputfilename=sourcefilename;
        String outputfilename=targetfilename;
        String sheetname=applicationsheetname;
        String fName = inputfilename;
        String currentLine;
        FileInputStream fis = new FileInputStream(fName);
        DataInputStream myInput = new DataInputStream(fis);
        int l = 0;

        allRowAndColData = new ArrayList<ArrayList<String>>();
        while ((currentLine = myInput.readLine()) != null) {
            oneRowData = new ArrayList<String>();
            String oneRowArray[] = currentLine.split(",");
            for (int j = 0; j < oneRowArray.length; j++) {
                oneRowData.add(oneRowArray[j]);
                System.out.println(oneRowArray[j]);
            }
            allRowAndColData.add(oneRowData);
            System.out.println();
            l++;
        }

        try {
            HSSFWorkbook workBook = new HSSFWorkbook();
            HSSFSheet sheet = workBook.createSheet(sheetname);
            for (int i = 0; i < allRowAndColData.size(); i++) {
                ArrayList<?> ardata = allRowAndColData.get(i);
                HSSFRow row = sheet.createRow((short) 0 + i);
                for (int k = 0; k < ardata.size(); k++) {
                    System.out.print(ardata.get(k));
                    HSSFCell cell = row.createCell((short) k);
                    cell.setCellValue(ardata.get(k).toString());
                }
                System.out.println();
            }
            FileOutputStream fileOutputStream =  new FileOutputStream(outputfilename);
            workBook.write(fileOutputStream);
            fileOutputStream.close();
            return 0;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

}
