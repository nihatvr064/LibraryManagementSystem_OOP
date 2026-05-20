package az.library.model;

public class Magazine extends LibraryItem implements Borrowable {

    public Magazine(String id, String title, String author) {
        super(id, title, author);
    }

    @Override
    public String getItemType() {
        return "Magazine";
    }

    @Override
    public int getMaxLoanDays() {
        return 7;
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