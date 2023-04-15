package com;

import com.sun.mail.smtp.SMTPMessage;
import util.GlobalVariables;

import  javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


/**
 * Created by rgupta on 8/28/2017.
 */
public class Email {

    private static Message buildSimpleMessageForDailyNightlyExportTest(Session session)

            throws MessagingException {

        SMTPMessage m = new SMTPMessage(session);

        MimeMultipart mp = new MimeMultipart();

        MimeBodyPart htmlPart = new MimeBodyPart();

        htmlPart.setContent( BuildReportFromDB.getReportHtml(),"text/html");
//        htmlPart.setContent( BuildReport.getReportHtml(),"text/html");

        mp.addBodyPart(htmlPart);

        m.setContent(mp);
        if(BuildReportFromDB.reportHtml.contains("class=\"fail\""))
        //if(BuildReport.reportHtml.contains("<td class=\"failures\""))
            m.setSubject("REPORT CD: Content Publishing Status Report as on "+  getDate()+" (" +BuildReportFromDB.passCount +" Passed, "+BuildReportFromDB.failCount+" Failed)");
        else
            m.setSubject("REPORT CD: DAILY PUBLISHING - FOR LAST NIGHT");

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

    public static void sendEmailTo(String emailRecipient) throws MessagingException {
        Session wkMail = buildSession();
        Message msg =  buildSimpleMessageForDailyNightlyExportTest(wkMail);
        msg.setFrom(new InternetAddress("Selenium.Automation@wolterskluwer.com"));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(emailRecipient));
        Transport.send( msg);
    }

    public static void sendEmailTo() throws MessagingException {
        Session wkMail = buildSession();
        Message msg = buildSimpleMessageForDailyNightlyExportTest(wkMail);
        msg.setFrom(new InternetAddress("Selenium.Automation@wolterskluwer.com"));
        Address[]  emailRecipient = new Address []{
                new InternetAddress("CS-UTD-DL-Editorial_Developer_Group@wolterskluwer.com"),
                new InternetAddress("Dhananjay.Tambe@wolterskluwer.com"),
                new InternetAddress("Bonnie.Zeigler@wolterskluwer.com"),
                new InternetAddress("Rithika.Gupta@wolterskluwer.com")
        };
        msg.addRecipients(Message.RecipientType.TO, emailRecipient);
        Transport.send( msg);
    }


    private static  String  getDate(){
        Date date = new Date();
        String modifiedDate = new SimpleDateFormat("MM/dd/yyyy").format(date);
        return  modifiedDate;
    }

    public static void buildSimpleMessageForPerfAnalysisTest(String emailRecipient ) throws MessagingException {
        Session session = buildSession();
        SMTPMessage m = new SMTPMessage(session);
        MimeMultipart mp = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent( BuildReportFromDB.getReportHtml(),"text/html");
        mp.addBodyPart(htmlPart);
        m.setContent(mp);
        m.setSubject("REPORT PERF DATA: Topic operations performence before/after deployment for region : " + GlobalVariables.exportRegion);
        m.setFrom(new InternetAddress("Selenium.Automation@wolterskluwer.com"));
        m.setRecipient(Message.RecipientType.TO, new InternetAddress(emailRecipient));
        Transport.send(m);
    }

    public static void buildSimpleMessageForCertificateDataTest(String emailRecipient ) throws MessagingException {
        Session session = buildSession();
        SMTPMessage m = new SMTPMessage(session);
        MimeMultipart mp = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent( BuildReportFromDB.getReportHtml(),"text/html");
        mp.addBodyPart(htmlPart);
        m.setContent(mp);
        if(GlobalVariables.certFlagMonthly) {
            m.setSubject("CERTIFICATES STATUS: ConDel app certificates status as on : " + getDate());
        }
        if(!GlobalVariables.certFlagMonthly)
        {
            m.setHeader("X-Priority", "1");
            m.setHeader("x-msmail-priority", "high");
            m.setSubject("ACTION REQUIRED: CERTIFICATES STATUS: ConDel app certificates status as on : " + getDate());
        }
        m.setFrom(new InternetAddress("Selenium.Automation@wolterskluwer.com"));
        m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailRecipient));
        Transport.send(m);
    }


}
