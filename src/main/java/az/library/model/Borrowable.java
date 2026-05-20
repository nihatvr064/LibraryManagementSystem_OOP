package az.library.model;

public interface Borrowable {

    void borrow(Member member);

    void returnItem(Member member);

    double calculateFine(int overdueDays);
}