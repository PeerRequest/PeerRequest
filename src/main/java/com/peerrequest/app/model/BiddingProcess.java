package com.peerrequest.app.model;


import java.time.ZonedDateTime;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a bidding process of a category.
 */
public class BiddingProcess {

    /**
     * Minimum number a user can use for a bid.
     */
    public static final int MIN_RATING = 1;
    /**
     * Maximum number a user can use for a bid.
     */
    public static final int MAX_RATING = 5;
    @Getter
    private final BiddingProcessSelector id;
    @Getter
    private final Category.CategorySelector categorySelector;
    /**
     * Bidding slots of this bidding process.
     * Might be null.
     */
    @Getter
    private final List<BiddingSlot> biddingSlots = new ArrayList<>();
    /**
     * Bidding Requests of this bidding process.
     * Might be null.
     */
    @Getter
    @Setter
    private List<BiddingRequest> biddingRequests = new ArrayList<>();
    @Getter
    @Setter
    private ZonedDateTime deadline;

    /**
     * Constructor for a bidding process.
     *
     * @param id               id of the bidding process
     * @param categorySelector category id of the bidding process
     * @param deadline         deadline of the bidding process
     */
    public BiddingProcess(BiddingProcessSelector id,
                          Category.CategorySelector categorySelector, ZonedDateTime deadline) {
        this.id = id;
        this.categorySelector = categorySelector;
        this.deadline = deadline;
    }

    public List<BiddingSlot> evaluateBidding() {
        evaluate();
        return this.biddingSlots;
    }

    /**
     * Tries to allocate the same amount of reviews to reviewers considering their ratings.
     * Description of the algorithm:
     * First the algorithm adds up the ratings of a reviewer for each reviewer and stores it in totalUserRatings.
     * Next the algorithm sorts all ratings by their rating and stores them in different lists. Each list contains only
     * ratings with the same rating. The algorithm now iterates through each list starting at the highest possible
     * rating.
     * After that it will iterate through the next lower rating list.
     * During each iteration, the algorithm assigns the reviewers with the lowest total rating first, because it is
     * less likely that they have a high rating for other entries. The total rating of a user will be decreased by their
     * rating if they are assigned to a review.
     * If the limit of reviews for one reviewer is reached, the reviewer will not be considered for the assignment
     * anymore.
     * If the limit of reviews for a bidding slot is reached, the entry of the bidding slot will not be considered for
     * the assignment anymore.
     * Complexity: O(N*M*log(N)) ; N = total amount of ratings, M = amount of total review slots from all bidding slots
     */
    private void evaluate() {
        HashMap<User.UserSelector, Integer> totalUserRatings = new HashMap<>();
        HashMap<User.UserSelector, Integer> remainingReviewsUser = new HashMap<>();
        HashMap<BiddingSlot.BiddingSlotSelector, Integer> remainingReviewsBiddingSlot = new HashMap<>();
        // initialize totalUserRatings and remainingReviewsUser
        for (BiddingRequest request : this.biddingRequests) {
            if (request.getRequestState() == Request.RequestState.ACCEPTED) {
                totalUserRatings.put(request.getReviewerSelector(), 0);
                remainingReviewsUser.put(request.getReviewerSelector(), 0);
            }
        }

        int amountRatings = MAX_RATING - MIN_RATING + 1;
        // stores all ratings sorted by their rating
        ArrayList<List<Rating>> totalRatings = new ArrayList<>(amountRatings);
        for (int i = 0; i < amountRatings; i++) {
            totalRatings.add(new ArrayList<>());
        }

        double totalReviewSlots = 0;

        // adds up all ratings of a user and stores the sum in totalUserRatings
        // fills totalRatings, remainingReviewsBiddingSlot and counts totalReviewSlots
        for (BiddingSlot slot : this.biddingSlots) {

            remainingReviewsBiddingSlot.put(slot.getId(), slot.getReviewSlots());
            totalReviewSlots += slot.getReviewSlots();

            for (Rating rating : slot.getRatings()) {
                int currentTotal = totalUserRatings.get(rating.getReviewerSelector());
                totalUserRatings.put(rating.getReviewerSelector(), currentTotal + rating.getRating());

                totalRatings.get(rating.getRating() - MIN_RATING).add(rating);
            }
        }

        // maximum amount of reviews per reviewer. Rounded up.
        int maxReviews = (int) Math.ceil(totalReviewSlots / totalUserRatings.size());
        remainingReviewsUser.replaceAll((k, v) -> maxReviews);

        // sorts a list of ratings by the amount of total ratings from the reviewers
        Comparator<Rating> totalUserRatingsComparator = new Comparator<>() {
            @Override
            public int compare(Rating first, Rating second) {
                return Integer.compare(
                    totalUserRatings.get(first.getReviewerSelector()),
                    totalUserRatings.get(second.getReviewerSelector()));
            }
        };

        for (int i = totalRatings.size() - 1; i >= 0; i--) {
            totalRatings.get(i).sort(totalUserRatingsComparator);

            Iterator<Rating> iter = totalRatings.get(i).iterator();

            while (iter.hasNext()) {
                Rating rating = iter.next();
                if (remainingReviewsUser.get(rating.getReviewerSelector()) > 0
                    && remainingReviewsBiddingSlot.get(rating.getBiddingSlotSelector()) > 0) {

                    rating.setRatingState(Rating.RatingState.ALLOCATED);
                    // decrements amount of reviews left
                    remainingReviewsUser.put(rating.getReviewerSelector(),
                        remainingReviewsUser.get(rating.getReviewerSelector()) - 1);
                    remainingReviewsBiddingSlot.put(rating.getBiddingSlotSelector(),
                        remainingReviewsBiddingSlot.get(rating.getBiddingSlotSelector()) - 1);
                    // decreases the total amount of ratings of the selected reviewer by their rating
                    totalUserRatings.put(rating.getReviewerSelector(),
                        totalUserRatings.get(rating.getReviewerSelector()) - rating.getRating());
                    iter.remove();
                    // sorts the list again because the order could have been changed after decreasing the rating
                    totalRatings.get(i).sort(totalUserRatingsComparator);
                    iter = totalRatings.get(i).iterator();
                } else {
                    rating.setRatingState(Rating.RatingState.NOT_ALLOCATED);
                    iter.remove();
                }
            }
        }
    }

    /**
     * Adds a list of bidding slots to the bidding slots of this bidding process.
     *
     * @param slots slots to be added
     */
    public void addBiddingSlots(final List<BiddingSlot> slots) {
        this.biddingSlots.addAll(slots);
    }

    public void addBiddingRequests(final List<BiddingRequest> requests) {
        this.biddingRequests.addAll(requests);
    }

    /**
     * State of a bidding process.
     */
    public enum BiddingRequestStates {
        /**
         * If a bidding process is OPEN, selected reviewers
         * can bid on the entries of the category of the bidding process.
         */
        OPEN,
        /**
         * If a bidding process is CLOSED, reviewers can not bid anymore.
         * The researcher has to confirm their/the systems allocation of reviewers to entries.
         */
        CLOSED
    }

    /**
     * Identification of a BiddingProcess.
     *
     * @param id id of the BiddingProcess
     */
    public record BiddingProcessSelector(long id) {
    }
}
