package com.dataframe.part25.SendAttachmentInEmail;

import java.io.File;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendAttachmentInEmail {

    public static void main(String[] args) {

        //reciver email address
        String to="email";

        //senders email address
        String from="email";

        //change accordingly
        final String username = "email";

        //change accordingly
        final String password = "xxxxx";

        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "local.net");
        props.put("mail.smtp.port", "25");
        props.put("mail.debug", "true");

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            System.out.println("blah blah ");

            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject("Testing Subject");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            String html="<!DOCTYPE html> <html> <head> <style> table {     font-family: arial, sans-serif;     border-collapse: collapse;    " +
                    " width: 100%; }  td, th {     border: 1px solid #dddddd;     text-align: left;     padding: 8px; }  tr:nth-child(even) {     " +
                    "background-color: #dddddd; } </style> </head> <body>    <table>     <thead>       <tr>         <th>columnname</th>         " +
                    "<th>columnname</th>         <th>columnname</th>         <th>columnname</th>         <th>columnname</th>        " +
                    " <th>columnname</th>         <th>columnname</th>         <th>columnname</th>       </tr>     </thead>       " +
                    "<tr>         <td>data</td>         <td>data</td>         <td>data</td>         <td>data</td>         " +
                    "<td>data</td>         <td>data</td>         <td> data </td>         <td>data</td>       " +
                    "</tr>       <tr>         <td>data</td>         <td>data</td>         <td>data</td>         <td>data</td>         <td>data</td>         " +
                    "<td>data</td>         <td> data </td>         <td>data</td>       </tr>       <tr>         " +
                    "<td>data</td>         <td>data</td>         <td>data</td>         <td>data</td>         <td>data</td>        " +
                    " <td>data</td>         <td> data </td>         <td>data</td>      " +
                    " </tr>   </table> </body> </html>";


            messageBodyPart.setContent(html, "text/html; charset=utf-8");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();
            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            //multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            String filename = "outputFile.xls";
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            //get just file name from file not the complete path
            messageBodyPart.setFileName(new File(filename).getName());
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e){
            throw new RuntimeException(e);
        }


    }

}
