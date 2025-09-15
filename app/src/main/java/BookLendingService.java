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
    
    // Day5 implement: repository dependency via constructor injection
    private final LoanRepository loanRepository;

    // Day5 implement: constructor for DI
    public BookLendingService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    // Day5 fix: reset helper for tests (no-op after DI)
    public static void clearLoansForTest() {
        // no-op
    }

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

        // Day5 implement: repository-based duplicate check
        if (loanRepository.hasActiveLoan(user.id(), book.id())) {
            throw new IllegalStateException("Duplicate lending is not allowed for the same user and book");
        }

        // Dummy logic: choose period based on popularity flag only.
        int days = book.isPopular() ? POPULAR_PERIOD_DAYS : NORMAL_PERIOD_DAYS;

        // Day5 implement: record loan in repository
        LocalDate dueDate = borrowedAt.plusDays(days);
        loanRepository.addLoan(user.id(), book.id(), dueDate);
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
