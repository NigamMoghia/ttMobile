package com;

import com.sun.mail.smtp.SMTPMessage;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;


/**
 * Created by rgupta on 8/28/2017.
 * Modified by ecallejon on 10/17/2022.
 */
public class GenericReportEmail {

    private static Message buildSimpleMessageForReport(Session session, String subject, String content) throws MessagingException {

        SMTPMessage m = new SMTPMessage(session);

        MimeMultipart mp = new MimeMultipart();

        MimeBodyPart htmlPart = new MimeBodyPart();

        htmlPart.setContent(content,"text/html");

        mp.addBodyPart(htmlPart);

        m.setContent(mp);

        m.setSubject(subject);

        return m;

    }



    private static Session buildSession() {
        Properties mailProps = new Properties();
        mailProps.put("mail.transport.protocol", "smtp");
        mailProps.put("mail.host", "awsmtp.utd.com");
        mailProps.put("mail.from", "UTD\\rgupta");
        Session session = Session.getDefaultInstance(mailProps);

        session.setDebug(true);

        return session;
    }

    public static void sendReportEmailTo(String emailRecipients, String subject, String content) throws MessagingException {
        Session wkMail = buildSession();
        Message msg =  buildSimpleMessageForReport(wkMail, subject, content);
        msg.setFrom(new InternetAddress("Selenium.Automation@wolterskluwer.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailRecipients));
        Transport.send(msg);
    }

}
