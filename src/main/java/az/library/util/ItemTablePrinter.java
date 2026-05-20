package az.library.util;

import az.library.model.LibraryItem;

import java.util.Comparator;
import java.util.List;

public final class ItemTablePrinter {

    private static final int ID_WIDTH = 6;
    private static final int TYPE_WIDTH = 9;
    private static final int TITLE_WIDTH = 34;
    private static final int AUTHOR_WIDTH = 24;
    private static final int LOAN_WIDTH = 8;
    private static final int STATUS_WIDTH = 10;

    private ItemTablePrinter() {
    }

    public static void printItems(List<? extends LibraryItem> items) {
        if (items.isEmpty()) {
            System.out.println("No items found.");
            return;
        }

        printBorder();
        printRow("ID", "Type", "Title", "Author", "Loan", "Status");
        printBorder();

        items.stream()
                .sorted(Comparator.comparing(LibraryItem::getId))
                .forEach(ItemTablePrinter::printItem);

        printBorder();
    }

    private static void printItem(LibraryItem item) {
        printRow(
                item.getId(),
                item.getItemType(),
                item.getTitle(),
                item.getAuthor(),
                item.getMaxLoanDays() + " days",
                item.isAvailable() ? "Available" : "Borrowed"
        );
    }

    private static void printRow(String id, String type, String title, String author, String loan, String status) {
        System.out.printf(
                "| %-" + ID_WIDTH + "s | %-" + TYPE_WIDTH + "s | %-" + TITLE_WIDTH + "s | %-"
                        + AUTHOR_WIDTH + "s | %-" + LOAN_WIDTH + "s | %-" + STATUS_WIDTH + "s |%n",
                fit(id, ID_WIDTH),
                fit(type, TYPE_WIDTH),
                fit(title, TITLE_WIDTH),
                fit(author, AUTHOR_WIDTH),
                fit(loan, LOAN_WIDTH),
                fit(status, STATUS_WIDTH)
        );
    }

    private static void printBorder() {
        System.out.println(
                "+" + "-".repeat(ID_WIDTH + 2)
                        + "+" + "-".repeat(TYPE_WIDTH + 2)
                        + "+" + "-".repeat(TITLE_WIDTH + 2)
                        + "+" + "-".repeat(AUTHOR_WIDTH + 2)
                        + "+" + "-".repeat(LOAN_WIDTH + 2)
                        + "+" + "-".repeat(STATUS_WIDTH + 2)
                        + "+"
        );
    }

    private static String fit(String value, int width) {
        if (value == null) {
            return "";
        }

        if (value.length() <= width) {
            return value;
        }

        return value.substring(0, width - 3) + "...";
    }
}
