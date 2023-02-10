package com.peerrequest.app.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 *  Describes the functionality of a CategoryService.
 */
@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String domain = "PeerRequest";
    private static final String welcoming = "Hi %s,\n";


    private void sendEmail(String toEmail, String subject, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(toEmail);
        mail.setText(message);
        mail.setSubject(subject);

        try {
            mailSender.send(mail);
        } catch (MailSendException e) {
            //log error
        }
    }


    public void sendReviewNotification(String emitterId,
                                       String receiverId,
                                       Long reviewId,
                                       ReviewMessageTemplates messageTemplate) {
        //todo: get necessary data from other services and replace mock data
        sendEmail("trashmailmm420@gmail.com", messageTemplate.getSubject(), "messageTemplate.getMessage()");
    }



    /**
     * Message templates for a review messages.
     */
    public enum ReviewMessageTemplates {
        /**
         * Message template when a researcher presses the 'notify reviewer' button.
         * Informs the reviewer about changes of the answers-from-the-authors-box of a review.
         */
        EDIT_RESEARCHER(
            domain + " - Researcher Update",
            welcoming + "%s updated the the \"answers from the authors\"-box of the review of the entry %s."
                + "\nTake a look!"
        ),
        /**
         * Message template when a reviewer presses the 'notify researcher' button.
         * Informs the researcher about changes of the comment boxes except
         * answers-from-the-authors-box of a review.
         */
        EDIT_REVIEWER(
            domain + " - Reviewer Update",
            welcoming + "%s updated their review of the entry %s.\nTake a look!"
        ),

        /**
         * Message template when a message is posted to a message board of a review.
         * Informs the other user about the post.
         */
        ADD_MESSAGE(
            domain + " - New Message",
            welcoming + "%s posted a comment on the message board of the review of the entry %s.\nTake a look!"
        );

        public String getMessage(String emitter, String entryName) {
            return String.format(message, emitter, entryName);
        }

        @Getter
        private final String subject;

        private final String message;

        ReviewMessageTemplates(String subject, String message) {
            this.subject = subject;
            this.message = message;
        }
    }

    /**
     * Message templates for a entry messages.
     */
    public enum EntryMessageTemplates {
        /**
         * Message template when an entry got deleted.
         * Notifies all reviewers of the entry about the deletion.
         */
        ENTRY_DELETED(domain + " - Entry Deleted",
            welcoming + "%s deleted their entry %s. Therefore your review is no longer needed."
        ),

        /**
         * Message template when a researcher of an entry requests a user to review their entry.
         * Notifies requested user about the direct request.
         */
        DIRECT_REQUEST(domain + " - Review Request",
            welcoming + "%s has requested a review for his entry %s from you.\nTake a look!"
        ),

        /**
         * Message template when a user claims an open slot of a direct request process.
         * Notifies the researcher about the claim.
         */
        OPEN_SLOT_CLAIMED(domain + " - New Reviewer",
            welcoming + "%s claimed an open slot of your entry %s."
        ),

        /**
         * Message template when a requested user accepts a direct request for a review of the entry.
         * Notifies the researcher that the requested user accepted.
         */
        REQUEST_ACCEPTED(domain + " - New Reviewer",
            welcoming + "%s accepted your review request for your entry %s."
        ),

        /**
         * Message template when a requested user declines a direct request for a review of the entry.
         * Notifies the researcher that the requested user declined.
         */
        REQUEST_DECLINED(domain + " - Request Declined",
            welcoming + "%s declined your review request for your entry %s."
        ),
        /**
         * Message template when an entry got deleted and requests for the entry were ongoing.
         * Notifies a requested user with the request state PENDING about the deletion.
         */
        REQUEST_WITHDRAWN(domain + " - Request Withdrawn",
            welcoming + "%s withdrew their request to you for their entry %s."
        );

        @Getter
        private final String subject;
        @Getter
        private final String message;

        EntryMessageTemplates(String subject, String message) {
            this.subject = subject;
            this.message = message;
        }
    }
}
