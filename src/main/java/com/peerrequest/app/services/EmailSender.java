package com.peerrequest.app.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * The class responsible for sending E-mails.
 */
public class EmailSender {
    /**
     * Class type field and refers to the object of the class EmailSender.
     */
    private static EmailSender instance;

    /**
     * java springs JavaMailSender to send the email.
     */
    private JavaMailSender mailSender;

    /**
     * Private constructor to prevent creation outside of itself.
     */
    private EmailSender() {
    }

    /**
     * This method send an email to the receiver.
     * @param receiver the user the email goes to
     * @param subject the subject of the email
     * @param message the message of the email
     */
    public void sendMail(String receiver, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiver);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    /**
     * This method returns the reference to instance of the class.
     * Since this methode is static, it can be accessed using the class name
     * @return instance of the class EmailSender
     */
    public static EmailSender getEmailSender() {
        if (instance == null) {
            instance = new EmailSender();
        }

        return instance;
    }
}
