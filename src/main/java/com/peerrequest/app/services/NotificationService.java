package com.peerrequest.app.services;

import com.peerrequest.app.services.messages.CategoryMessageTemplates;
import com.peerrequest.app.services.messages.EntryMessageTemplates;
import com.peerrequest.app.services.messages.ReviewMessageTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 *  Describes the functionality of a CategoryService.
 */
@Service
public class NotificationService {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EntryService entryService;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;


    private void sendEmail(String toEmail, String subject, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(toEmail);
        mail.setText(message);
        mail.setSubject(subject);
        mail.setFrom(from);
        try {
            mailSender.send(mail);
        } catch (Exception ignored) {
            // in the case that the mail server does not exist, no error handling in this case
        }
    }

    /**
     * Sends a category notification.
     *
     * @param receiverId      userId of the user who receives the notification
     * @param entryId         entryID of the entry of the review
     * @param messageTemplate review message
     */
    public void sendCategoryNotification(
            String receiverId,
            Long entryId,
            CategoryMessageTemplates messageTemplate) {


        var receiver = userService.get(receiverId);
        var entry = entryService.get(entryId);

        if (receiver.isEmpty() || entry.isEmpty()) {
            return;
        }

        sendEmail(
                receiver.get().getEmail(),
                messageTemplate.getSubject(),
                messageTemplate.getMessage(
                        receiver.get().firstName(),
                        entry.get().getName())
        );
    }

    /**
     * Sends an entry notification.
     *
     * @param emitterId       userID of the user who caused the notification
     * @param receiverId      userId of the user who receives the notification
     * @param entryId         entryID of the entry
     * @param messageTemplate entry message
     */
    public void sendEntryNotification(String emitterId,
                                      String receiverId,
                                      Long entryId,
                                      EntryMessageTemplates messageTemplate) {
        var emitter = userService.get(emitterId);
        var receiver = userService.get(receiverId);
        var entry = entryService.get(entryId);

        if (emitter.isEmpty() || receiver.isEmpty() || entry.isEmpty()) {
            return;
        }

        sendEmail(
                receiver.get().getEmail(),
                messageTemplate.getSubject(),
                messageTemplate.getMessage(
                        receiver.get().firstName(),
                        emitter.get().firstName() + " " + emitter.get().lastName(),
                        entry.get().getName())
        );
    }

    /**
     * Sends the open slot notification.
     *
     * @param emitterId       userID of the user who caused the notification
     * @param receiverId      userId of the user who receives the notification
     * @param entryId         entryID of the entry
     */
    public void sendOpenSlotNotification(String emitterId, String receiverId, Long entryId) {
        var emitter = userService.get(emitterId);
        var receiver = userService.get(receiverId);
        var entry = entryService.get(entryId);

        if (emitter.isEmpty() || receiver.isEmpty() || entry.isEmpty()) {
            return;
        }

        var category = categoryService.get(entry.get().getCategoryId());
        if (category.isEmpty()) {
            return;
        }

        EntryMessageTemplates messageTemplate = EntryMessageTemplates.OPEN_SLOTS;

        sendEmail(
                receiver.get().getEmail(),
                messageTemplate.getSubject(),
                messageTemplate.getMessage(
                        receiver.get().firstName(),
                        emitter.get().firstName() + " " + emitter.get().lastName(),
                        entry.get().getName(),
                        category.get().getName() + " " + category.get().getYear())
        );
    }

    /**
     * Sends a review notification.
     *
     * @param emitterId       userID of the user who caused the notification
     * @param receiverId      userId of the user who receives the notification
     * @param entryId         entryID of the entry of the review
     * @param messageTemplate review message
     */
    public void sendReviewNotification(String emitterId,
                                       String receiverId,
                                       Long entryId,
                                       ReviewMessageTemplates messageTemplate) {
        var emitter = userService.get(emitterId);
        var receiver = userService.get(receiverId);
        var entry = entryService.get(entryId);

        if (emitter.isEmpty() || receiver.isEmpty() || entry.isEmpty()) {
            return;
        }

        sendEmail(
                receiver.get().getEmail(),
                messageTemplate.getSubject(),
                messageTemplate.getMessage(
                        receiver.get().firstName(),
                        emitter.get().firstName() + " " + emitter.get().lastName(),
                        entry.get().getName())
        );
    }
}
