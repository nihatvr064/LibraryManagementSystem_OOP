package az.library.model;

public class Book extends LibraryItem implements Borrowable {

    public Book(String id, String title, String author) {
        super(id, title, author);
    }

    @Override
    public String getItemType() {
        return "Book";
    }

    @Override
    public int getMaxLoanDays() {
        return 14;
    }

    @Override
    public void borrow(Member member) {
        member.borrowItem(this);
        setAvailable(false);
    }

    @Override
    public void returnItem(Member member) {
        member.returnItem(this);
        setAvailable(true);
    }

    @Override
    public double calculateFine(int overdueDays) {
        return overdueDays <= 0 ? 0 : overdueDays * 0.25;
    }
}