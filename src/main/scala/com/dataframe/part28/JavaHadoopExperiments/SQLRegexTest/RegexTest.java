package com.dataframe.part28.JavaHadoopExperiments.SQLRegexTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

    public static void main(String[] args){

        String sqlString = "insert overwrite table x  select * from (xnxx) as a";
        String testPattern="(.+?) (?=partition)(.+) (?=select)(.+)";
        Pattern test=Pattern.compile(testPattern);
        Matcher matcher=test.matcher(sqlString);

        while (matcher.find()){
            System.out.println("found1:- " + matcher.group(1) + "found2:- " + matcher.group(2)
                    + "found 3:- " + matcher.group(3));
        }
    }

}
