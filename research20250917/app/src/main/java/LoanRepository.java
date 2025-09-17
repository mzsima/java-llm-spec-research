import java.time.LocalDate;

// Day5 implement: shared repository contract for loans
public interface LoanRepository {
    boolean hasActiveLoan(String userId, String bookId);
    void addLoan(String userId, String bookId, LocalDate dueDate);
}

