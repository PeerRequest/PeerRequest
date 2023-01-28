package com.peerrequest.app;

import static org.junit.jupiter.api.Assertions.*;

import com.peerrequest.app.model.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This class unit tests the BiddingProcess class.
 */
@SpringBootTest
public class BiddingProcessTests {

    private static BiddingProcess biddingProcess;
    private static final Random random = new Random();

    @Test
    public void evaluateBiddingTestAcceptedUsers() {
        for (BiddingSlot slot : biddingProcess.getBiddingSlots()) {
            for (Rating rating : slot.getRatings()) {
                for (BiddingRequest request : biddingProcess.getBiddingRequests()) {
                    if (rating.getReviewerSelector().equals(request.getReviewerSelector())) {
                        assertEquals(request.getRequestState(), Request.RequestState.ACCEPTED,
                            "User has not accepted the bidding request.");
                    }
                }
            }
        }
    }

    @Test
    public void evaluateBiddingTestDistributedReviews() {
        for (BiddingSlot slot : biddingProcess.getBiddingSlots()) {
            int distributedReviews = 0;
            for (Rating rating : slot.getRatings()) {
                if (rating.getRatingState() == Rating.RatingState.ALLOCATED) {
                    distributedReviews++;
                }
            }
            assertTrue(distributedReviews > 0, "Each entry should have at least one review.");
        }
    }

    /**
     * Initializes a bidding process with random values and evaluates the bidding.
     */
    @BeforeAll
    public static void setup() {
        biddingProcess = new BiddingProcess(new BiddingProcess.BiddingProcessSelector(0),
            new Category.CategorySelector(0),
            ZonedDateTime.now());

        List<BiddingSlot> biddingSlots = new ArrayList<>();
        List<BiddingRequest> biddingRequests = new ArrayList<>();

        int requestedReviewers = randomInt(8, 15);
        // -3 because at least one pending and one declined request needed
        int participatingReviewers = randomInt(4, requestedReviewers - 3);
        int biddingSlotAmount = randomInt(requestedReviewers, 2 * requestedReviewers);

        for (int i = 0; i < requestedReviewers; i++) {
            User.UserSelector user = new User.UserSelector(Integer.toString(i));

            BiddingRequest bidRequest
                = new BiddingRequest(new Request.RequestSelector(i), user, biddingProcess.getId());
            if (i < participatingReviewers) {
                bidRequest.setRequestState(Request.RequestState.ACCEPTED);
            } else if (i != requestedReviewers - 1) {
                bidRequest.setRequestState(Request.RequestState.DECLINED);
            }
            biddingRequests.add(bidRequest);
        }

        long ratingIds = 0;
        for (int i = 0; i < biddingSlotAmount; i++) {
            BiddingSlot bidSlot = new BiddingSlot(new BiddingSlot.BiddingSlotSelector(i), biddingProcess.getId(),
                new Entry.EntrySelector(i));
            bidSlot.setReviewSlots(randomInt(1, participatingReviewers));
            ArrayList<Rating> ratings = new ArrayList<>();
            for (BiddingRequest bidRequest : biddingRequests) {
                if (bidRequest.getRequestState() == Request.RequestState.ACCEPTED) {
                    Rating rating = new Rating(new Rating.RatingSelector(ratingIds), bidSlot.getId(),
                        bidRequest.getReviewerSelector());
                    rating.setRating(randomInt(BiddingProcess.MIN_RATING, BiddingProcess.MAX_RATING));
                    ratings.add(rating);
                    ratingIds++;
                }
            }
            bidSlot.setRatings(ratings);
            biddingSlots.add(bidSlot);
        }

        biddingProcess.addBiddingRequests(biddingRequests);
        biddingProcess.addBiddingSlots(biddingSlots);
        biddingProcess.evaluateBidding();
    }

    private static int randomInt(int lowerBorder, int upperBorder) {
        return random.nextInt(upperBorder - lowerBorder + 1) + lowerBorder;
    }

}
