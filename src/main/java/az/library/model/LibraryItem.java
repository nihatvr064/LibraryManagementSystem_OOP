package az.library.model;

public abstract class LibraryItem {
    private String id;
    private String title;
    private String author;
    private boolean available;

    public LibraryItem(String id, String title, String author) {
        setId(id);
        setTitle(title);
        setAuthor(author);
        this.available = true;
    }

    public abstract String getItemType();
    public abstract int getMaxLoanDays();

    public void displayInfo() {
        System.out.printf(
                "ID: %s | Type: %s | Title: %s | Author: %s | Loan: %d days | Available: %s%n",
                id, getItemType(), title, author, getMaxLoanDays(), available ? "Yes" : "No"
        );
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID cannot be empty.");
        }
        this.id = id.trim().toUpperCase();
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        this.title = title.trim();
    }

    public String getAuthor() {
        return author;
    }

    private void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty.");
        }
        this.author = author.trim();
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
