package com.dataframe.part32.SFJavaGet; /**
 * Created by kjfg254 on 12/11/2017.
 */
import com.springml.salesforce.wave.api.APIFactory;
import com.springml.salesforce.wave.api.ForceAPI;
import com.springml.salesforce.wave.model.SOQLResult;
import java.util.List;
import java.util.Map;

public class SFGetTestJavaApi {

    public static void main(String[] args){

        try {

            ForceAPI forceAPI = APIFactory.getInstance().forceAPI("blah",
                    "blah",
                    "https://blah.my.salesforce.com/services/Soap/u/22");
            String soql = "select column1,column2 from table";
            SOQLResult result = forceAPI.query(soql);
            List<Map<String, Object>> records = result.getRecords();
            System.out.println(records);
// By default Salesforce will return 2000 records in a single call
// To query more use queryMore()
            /*
            while (!result.isDone()) {
                result = forceAPI.queryMore();
                records.addAll(result.getRecords());
            }*/
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}
