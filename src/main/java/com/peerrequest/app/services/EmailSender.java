package com.peerrequest.app.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.io.InputStream;

/**
 * The class responsible for sending E-mails.
 */
public class EmailSender implements JavaMailSender {
    /**
     * Class type field and refers to the object of the class EmailSender.
     */
    private static volatile EmailSender instance = null;

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

    /**
     * Create a new JavaMail MimeMessage for the underlying JavaMail Session
     * of this sender. Needs to be called to create MimeMessage instances
     * that can be prepared by the client and passed to send(MimeMessage).
     *
     * @return the new MimeMessage instance
     * @see #send(MimeMessage)
     * @see #send(MimeMessage[])
     */
    @Override
    public MimeMessage createMimeMessage() {
        //TODO Implement createMimeMessage()
        throw new RuntimeException("not implemented");
    }

    /**
     * Create a new JavaMail MimeMessage for the underlying JavaMail Session
     * of this sender, using the given input stream as the message source.
     *
     * @param contentStream the raw MIME input stream for the message
     * @return the new MimeMessage instance
     * @throws MailParseException in case of message creation failure
     */
    @Override
    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
        //TODO Implement createMimeMessage(InputStream contentStream)
        throw new RuntimeException("not implemented");
    }

    /**
     * Send the given JavaMail MIME message.
     * The message needs to have been created with {@link #createMimeMessage()}.
     *
     * @param mimeMessage message to send
     * @throws MailAuthenticationException in case of authentication failure
     * @throws MailSendException           in case of failure when sending the message
     * @see #createMimeMessage
     */
    @Override
    public void send(MimeMessage mimeMessage) throws MailException {
        //TODO Implement send(MimeMessage mimeMessage)
        throw new RuntimeException("not implemented");
    }

    /**
     * Send the given array of JavaMail MIME messages in batch.
     * The messages need to have been created with {@link #createMimeMessage()}.
     *
     * @param mimeMessages messages to send
     * @throws MailAuthenticationException in case of authentication failure
     * @throws MailSendException           in case of failure when sending a message
     * @see #createMimeMessage
     */
    @Override
    public void send(MimeMessage... mimeMessages) throws MailException {
        //TODO Implement send(MimeMessage... mimeMessages)
        throw new RuntimeException("not implemented");
    }

    /**
     * Send the JavaMail MIME message prepared by the given MimeMessagePreparator.
     * <p>Alternative way to prepare MimeMessage instances, instead of
     * {@link #createMimeMessage()} and {@link #send(MimeMessage)} calls.
     * Takes care of proper exception conversion.
     *
     * @param mimeMessagePreparator the preparator to use
     * @throws MailPreparationException    in case of failure when preparing the message
     * @throws MailParseException          in case of failure when parsing the message
     * @throws MailAuthenticationException in case of authentication failure
     * @throws MailSendException           in case of failure when sending the message
     */
    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
        //TODO Implement send(MimeMessagePreparator mimeMessagePreparator)
        throw new RuntimeException("not implemented");
    }

    /**
     * Send the JavaMail MIME messages prepared by the given MimeMessagePreparators.
     * <p>Alternative way to prepare MimeMessage instances, instead of
     * {@link #createMimeMessage()} and {@link #send(MimeMessage[])} calls.
     * Takes care of proper exception conversion.
     *
     * @param mimeMessagePreparators the preparator to use
     * @throws MailPreparationException    in case of failure when preparing a message
     * @throws MailParseException          in case of failure when parsing a message
     * @throws MailAuthenticationException in case of authentication failure
     * @throws MailSendException           in case of failure when sending a message
     */
    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
        //TODO Implement send(MimeMessagePreparator... mimeMessagePreparators)
        throw new RuntimeException("not implemented");
    }

    /**
     * Send the given simple mail message.
     *
     * @param simpleMessage the message to send
     * @throws MailParseException          in case of failure when parsing the message
     * @throws MailAuthenticationException in case of authentication failure
     * @throws MailSendException           in case of failure when sending the message
     */
    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        //TODO Implement send(SimpleMailMessage simpleMessage)
        throw new RuntimeException("not implemented");
    }

    /**
     * Send the given array of simple mail messages in batch.
     *
     * @param simpleMessages the messages to send
     * @throws MailParseException          in case of failure when parsing a message
     * @throws MailAuthenticationException in case of authentication failure
     * @throws MailSendException           in case of failure when sending a message
     */
    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        //TODO Implement send(SimpleMailMessage... simpleMessages)
        throw new RuntimeException("not implemented");
    }
}
