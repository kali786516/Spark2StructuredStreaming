package com.dataframe.part29.JavaExperiments.FileToEmailApp;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HtmlEmailClass {
    public int sendemailmethod(String apptoemail,String appfromemail,String appusername,String apppassword,String apphtml,String emailsubject){

        //reciver email address
        String to=apptoemail;

        //senders email address
        String from=appfromemail;

        //change accordingly
        final String username = appusername;

        //change accordingly
        final String password = apppassword;

        //email subject
        String subject=emailsubject;

        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "localhost");
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


            message.addRecipient(Message.RecipientType.BCC,new InternetAddress("kali.tummala@gmail.com"));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
             String html=apphtml;
            messageBodyPart.setContent(html, "text/html; charset=utf-8");


            // Create a multipar message
            Multipart multipart = new MimeMultipart();
            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            //multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e){
            throw new RuntimeException(e);
        }


        return 0;

    }

}
