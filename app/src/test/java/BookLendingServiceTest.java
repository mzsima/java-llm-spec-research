import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;

/**
 * BookLendingService の仕様に基づくレッドテスト。
 * 外部依存は本テスト内でスタブ化し、SUT には不完全な暫定実装を与えて失敗を確認する。
 */
public class BookLendingServiceTest {

    // ===== テスト対象の契約 =====
    interface BookLendingService {
        LoanRecord lend(String userId, String bookId, LocalDate borrowedAt);
    }

    // ===== 外部依存（テスト内スタブ化用の契約） =====
    interface LoanRepository {
        boolean hasActiveLoan(String userId, String bookId);
    }

    interface BookInfo {
        boolean isPopular(String bookId);
    }

    // ===== ドメイン的な簡易モデル =====
    static class LoanRecord {
        final String userId;
        final String bookId;
        final LocalDate dueDate;

        LoanRecord(String userId, String bookId, LocalDate dueDate) {
            this.userId = userId;
            this.bookId = bookId;
            this.dueDate = dueDate;
        }
    }

    // ===== 不完全な暫定実装（あえて仕様に違反させ、テストが失敗することを確認） =====
    static class NotImplementedBookLendingService implements BookLendingService {
        private final LoanRepository loanRepository;
        private final BookInfo bookInfo;

        NotImplementedBookLendingService(LoanRepository loanRepository, BookInfo bookInfo) {
            this.loanRepository = loanRepository;
            this.bookInfo = bookInfo;
        }

        @Override
        public LoanRecord lend(String userId, String bookId, LocalDate borrowedAt) {
            // 仕様に反して、常に3日後を返し、重複貸出チェックも行わない暫定実装
            int wrongPeriodDays = 3;
            return new LoanRecord(userId, bookId, borrowedAt.plusDays(wrongPeriodDays));
        }
    }

    @Test
    @DisplayName("通常本の貸出期限は14日であるべき")
    void normalBook_dueDate_shouldBe14Days() {
        // 仕様の根拠: 「貸出期間（通常本）: 固定14日。」
        LoanRepository loanRepositoryStub = (userId, bookId) -> false; // 重複なし
        BookInfo bookInfoStub = (bookId) -> false; // 通常本
        BookLendingService sut = new NotImplementedBookLendingService(loanRepositoryStub, bookInfoStub);

        LocalDate borrowedAt = LocalDate.of(2025, 1, 1);
        LocalDate expectedDue = borrowedAt.plusDays(14);

        LoanRecord actual = sut.lend("U1", "B1", borrowedAt);

        // 現状の暫定実装は3日を返すため、このアサーションは失敗する（レッド）
        Assertions.assertEquals(expectedDue, actual.dueDate);
    }

    @Test
    @DisplayName("同じ本を同一利用者が重複して借りようとしたらエラー")
    void duplicateBorrow_shouldThrowError() {
        // 仕様の根拠: 「重複貸出: 同じ本を借りようとした場合はエラーとする。」
        LoanRepository loanRepositoryStub = (userId, bookId) -> true; // すでに貸出中
        BookInfo bookInfoStub = (bookId) -> false; // 通常本
        BookLendingService sut = new NotImplementedBookLendingService(loanRepositoryStub, bookInfoStub);

        // 本来は例外が投げられるべきだが、暫定実装は投げないため失敗（レッド）
        Assertions.assertThrows(IllegalStateException.class, () ->
                sut.lend("U1", "B1", LocalDate.of(2025, 1, 1))
        );
    }

    @Test
    @DisplayName("人気本の貸出期限は7日であるべき")
    void popularBook_dueDate_shouldBe7Days() {
        // 仕様の根拠: 「人気本の貸出期間: 固定7日（通常本より短い）。」
        LoanRepository loanRepositoryStub = (userId, bookId) -> false; // 重複なし
        BookInfo bookInfoStub = (bookId) -> true; // 人気本
        BookLendingService sut = new NotImplementedBookLendingService(loanRepositoryStub, bookInfoStub);

        LocalDate borrowedAt = LocalDate.of(2025, 1, 1);
        LocalDate expectedDue = borrowedAt.plusDays(7);

        LoanRecord actual = sut.lend("U1", "B2", borrowedAt);

        // 現状の暫定実装は3日を返すため、このアサーションは失敗する（レッド）
        Assertions.assertEquals(expectedDue, actual.dueDate);
    }
}

