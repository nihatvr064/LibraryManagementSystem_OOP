package az.library.model;

public interface Borrowable {

    void borrow(Member member);

    void returnItem(Member member);

    double getFinePerOverdueDay();

    default double calculateFine(int overdueDays) {
        if (overdueDays <= 0) {
            return 0;
        }

        return overdueDays * getFinePerOverdueDay();
    }
}
