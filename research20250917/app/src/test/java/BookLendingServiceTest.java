import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;

/**
 * BookLendingService の仕様に基づくレッドテスト。
 * 外部依存は本テスト内でスタブ化し、SUT には不完全な暫定実装を与えて失敗を確認する。
 */
public class BookLendingServiceTest {

    // ===== 外部依存（BookInfo はテスト内スタブ継続。LoanRepository は共有インタフェースを使用） =====
    interface BookInfo {
        boolean isPopular(String bookId);
    }

    // ===== 不完全な暫定実装（あえて仕様に違反させ、テストが失敗することを確認） =====
    // static class NotImplementedBookLendingService { /* 削除（実装クラスへ切替）*/ }

    @Test
    @DisplayName("通常本の貸出期限は14日であるべき")
    void normalBook_dueDate_shouldBe14Days() {
        // 仕様の根拠: 「貸出期間（通常本）: 固定14日。」
        LoanRepository loanRepositoryStub = new LoanRepository() {
            @Override public boolean hasActiveLoan(String userId, String bookId) { return false; }
            @Override public void addLoan(String userId, String bookId, LocalDate dueDate) { /* no-op */ }
        }; // 重複なし
        BookInfo bookInfoStub = (bookId) -> false; // 通常本（未使用）
        BookLendingService sut = new BookLendingService(loanRepositoryStub);

        LocalDate borrowedAt = LocalDate.of(2025, 1, 1);
        LocalDate expectedDue = borrowedAt.plusDays(14);

        User user = new User("U1");
        Book book = new Book("B1", false); // 通常本
        BookLendingService.LoanRecord actual = sut.lend(user, book, borrowedAt);

        // 現状の暫定実装は3日を返すため、このアサーションは失敗する（レッド）
        Assertions.assertEquals(expectedDue, actual.dueDate());
    }

    @Test
    @DisplayName("同じ本を同一利用者が重複して借りようとしたらエラー")
    void duplicateBorrow_shouldThrowError() {
        // 仕様の根拠: 「重複貸出: 同じ本を借りようとした場合はエラーとする。」
        LoanRepository loanRepositoryStub = new LoanRepository() {
            @Override public boolean hasActiveLoan(String userId, String bookId) { return true; }
            @Override public void addLoan(String userId, String bookId, LocalDate dueDate) { /* no-op */ }
        }; // すでに貸出中
        BookInfo bookInfoStub = (bookId) -> false; // 通常本（未使用）
        BookLendingService sut = new BookLendingService(loanRepositoryStub);

        // 本来は例外が投げられるべきだが、暫定実装は投げないため失敗（レッド）
        Assertions.assertThrows(IllegalStateException.class, () -> {
            User user = new User("U1");
            Book book = new Book("B1", false);
            sut.lend(user, book, LocalDate.of(2025, 1, 1));
        });
    }

    @Test
    @DisplayName("人気本の貸出期限は7日であるべき")
    void popularBook_dueDate_shouldBe7Days() {
        // 仕様の根拠: 「人気本の貸出期間: 固定7日（通常本より短い）。」
        LoanRepository loanRepositoryStub = new LoanRepository() {
            @Override public boolean hasActiveLoan(String userId, String bookId) { return false; }
            @Override public void addLoan(String userId, String bookId, LocalDate dueDate) { /* no-op */ }
        }; // 重複なし
        BookInfo bookInfoStub = (bookId) -> true; // 人気本（未使用）
        BookLendingService sut = new BookLendingService(loanRepositoryStub);

        LocalDate borrowedAt = LocalDate.of(2025, 1, 1);
        LocalDate expectedDue = borrowedAt.plusDays(7);

        User user = new User("U1");
        Book book = new Book("B2", true); // 人気本
        BookLendingService.LoanRecord actual = sut.lend(user, book, borrowedAt);

        // 現状の暫定実装は3日を返すため、このアサーションは失敗する（レッド）
        Assertions.assertEquals(expectedDue, actual.dueDate());
    }

    @AfterEach
    void tearDown() {
        // Clear in-memory loan state between tests to avoid leakage
        BookLendingService.clearLoansForTest();
    }
}
