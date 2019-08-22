package com.dataframe.part29.JavaExperiments.FileToEmailApp;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileToEmailApplication {
    private static Logger logger = Logger.getLogger(FileToEmailApplication.class.getName());

    public static void main(String[] args) {
        Long starttime=System.currentTimeMillis();

        if (args.length < 5)
        {
            logger.log(Level.SEVERE,"=> wrong parameters number");
            System.err.println("Usage: " + "\n" +
                    "argument length :-   " + args.length + "\n" +
                    "inputfilename   :-   test.txt" + "\n" +
                    "appfromemail    :-   blah@gmail.com" + "\n" +
                    "appusername     :-   blah@gmail.com" + "\n" +
                    "apppassword     :-   xxxxxx" + "\n" +
                    "delimeter       :-   ~" + "\n" +
                    "emailsubject    :-   blah Alert");
            System.exit(1);
        }

        String jobname="Email Sales Rep";
        String inputfilename=args[0];
        String appfromemail=args[1];
        String appusername=args[2];
        String apppassword=args[3];
        String delimeter=args[4];
        String emailsubject=args[5];

        System.out.println("JobName                :- Email Sales Rep");
        System.out.println("inputfilename argument value  :- "+inputfilename);

        File file = new File(inputfilename);
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            DataInputStream myInput = new DataInputStream(fis);
            String currentLine;
            ArrayList<String> oneRowData = null;
            ArrayList<ArrayList<String>> allRowAndColData = null;

            while ((currentLine = myInput.readLine()) != null) {
                String[] temp;
                temp = currentLine.split(delimeter);
                if (temp[0].isEmpty()) {
                    System.out.println("Bad Record Line");
                    System.out.println(currentLine);
                } else {
                    String emailid = temp[0].replaceAll("\"", "");
                    String htmltag = temp[1].replaceAll("\"", "");
                    System.out.println(emailid);
                    System.out.println(htmltag);

                    HtmlEmailClass sendemailattachmenttest = new HtmlEmailClass();
                    int emailop = sendemailattachmenttest.sendemailmethod(emailid, appfromemail, appusername, apppassword, htmltag, emailsubject);
                    if (emailop == 0) {
                        System.out.println("Email Sent for user :-" + emailid);
                    }
                }
            }


            Long endTime = System.currentTimeMillis();
            Long totalTime = endTime - starttime;
            System.out.println("JobName " + jobname + " took (" + (totalTime / 1000d) + ") seconds to process ");
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
