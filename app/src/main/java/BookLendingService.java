import java.time.LocalDate;

/**
 * Minimal skeleton for the book lending service.
 * Follows spec decisions: normal books = 14 days, popular books = 7 days,
 * duplicate lending should be an error.
 *
 * TODO: implement
 */
public class BookLendingService {

    // Spec constants
    public static final int NORMAL_PERIOD_DAYS = 14;
    public static final int POPULAR_PERIOD_DAYS = 7;

    /**
     * Lend a book to a user.
     * - Normal book: 14 days
     * - Popular book: 7 days
     * - Duplicate lending: should error (not implemented)
     *
     * This is a dummy implementation to satisfy compilation.
     *
     * TODO: implement
     */
    public LoanRecord lend(User user, Book book, LocalDate borrowedAt) {
        if (user == null || book == null || borrowedAt == null) {
            throw new IllegalArgumentException("user, book, and borrowedAt must be non-null");
        }

        // Dummy logic: choose period based on popularity flag only.
        int days = book.isPopular() ? POPULAR_PERIOD_DAYS : NORMAL_PERIOD_DAYS;

        // NOTE: Duplicate lending check is intentionally not implemented.
        // TODO: implement

        LocalDate dueDate = borrowedAt.plusDays(days);
        return new LoanRecord(user.id(), book.id(), dueDate);
    }

    /** Simple value object of a lending result. */
    public record LoanRecord(String userId, String bookId, LocalDate dueDate) { }
}

/**
 * User model (same package).
 *
 * TODO: implement
 */
record User(String id) { }

/**
 * Book model (same package).
 *
 * TODO: implement
 */
record Book(String id, boolean isPopular) { }

