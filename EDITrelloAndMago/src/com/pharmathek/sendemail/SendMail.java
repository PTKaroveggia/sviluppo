package com.pharmathek.sendemail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
	
	
//	public static void main(String[] args) {
//		new SendMail().sendEmail("s.mezzani@pharmathek.com", "Prova", "Prova testo");
//	}


	public void sendEmail(String emailTo, String emailObject, String emailText) {

		Properties properties = System.getProperties();
		// Setup mail server
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		String from = "qualita@pharmathek.com";
		
//		Properties properties = System.getProperties();
//		// Setup mail server
//		properties.put("mail.smtp.host", "smtp.office365.com");
//		properties.put("mail.smtp.port", "587");
//		properties.put("mail.smtp.starttls.enable", "true");
//		properties.put("mail.smtp.auth", "true");
//		String from = "automation@mattec.it";

		
		
		// Get the Session object.// and pass username and password
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication("qualita@pharmathek.com", "Qual2014");
//				return new PasswordAuthentication(from, "MTCZileri2021-");

			}

		});

		// Used to debug SMTP issues
		session.setDebug(true);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));

			// Set Subject: header field
			message.setSubject(emailObject);

			// Now set the actual message
			message.setText(emailText);
			//message.setContent(emailText, "text/html"); 

			System.out.println("sending...");
			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}

	}

}