package az.library.model;

public class Thesis extends LibraryItem implements Borrowable {

    public Thesis(String id, String title, String author) {
        super(id, title, author);
    }

    @Override
    public String getItemType() {
        return "Thesis";
    }

    @Override
    public int getMaxLoanDays() {
        return 21;
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