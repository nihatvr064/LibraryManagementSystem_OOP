package az.library.main;

import az.library.persistence.FileManager;
import az.library.model.Book;
import az.library.model.LibraryItem;
import az.library.model.Magazine;
import az.library.model.Member;
import az.library.model.Thesis;
import az.library.model.member.BasicMember;
import az.library.model.member.GoldMember;
import az.library.model.member.SilverMember;
import az.library.service.Library;
import az.library.util.IdGenerator;
import az.library.util.ItemTablePrinter;
import az.library.util.SearchResult;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";
    private static final String BLUE = "\u001B[34m";

    public static void main(String[] args) {
        Library lib = new Library();
        Scanner sc = new Scanner(System.in);

        FileManager.loadAll(lib);
        while (true) {
            try {
                clearScreen();
                printHeader();
                printStats();
                printMenu();

                int choice = readInt(sc, "Enter choice ➜ ");

                switch (choice) {
                    case 1 -> {
                        addItem(sc, lib);
                        FileManager.saveAll(lib);
                    }
                    case 2 -> {
                        registerMember(sc, lib);
                        FileManager.saveAll(lib);
                    }
                    case 3 -> {
                        borrowItem(sc, lib);
                        FileManager.saveAll(lib);
                    }
                    case 4 -> {
                        returnItem(sc, lib);
                        FileManager.saveAll(lib);
                    }
                    case 5 -> searchItems(sc, lib);
                    case 6 -> viewMemberReport(sc, lib);
                    case 7 -> viewAvailableItems(lib);
                    case 8 -> {
                        FileManager.saveAll(lib);
                        goodbye();
                        sc.close();
                        return;
                    }
                    default -> error("Invalid choice.");
                }

            } catch (Exception e) {
                error(e.getMessage());
            }

            pause(sc);
        }
    }

    private static void seedDemoData(Library lib) {
        lib.addItem(new Book(IdGenerator.generateBookId(), "Clean Code", "Robert C. Martin"));
        lib.addItem(new Book(IdGenerator.generateBookId(), "Effective Java", "Joshua Bloch"));
        lib.addItem(new Magazine(IdGenerator.generateMagazineId(), "National Geographic", "Various"));
        lib.addItem(new Thesis(IdGenerator.generateThesisId(), "AI in Education", "Nihat Rustamli"));

        lib.registerMember(new BasicMember(IdGenerator.generateMemberId(), "Ali"));
        lib.registerMember(new SilverMember(IdGenerator.generateMemberId(), "Leyla"));
        lib.registerMember(new GoldMember(IdGenerator.generateMemberId(), "Nihat"));
    }

    private static void addItem(Scanner sc, Library lib) {
        section("ADD NEW ITEM");

        System.out.println("1. Book");
        System.out.println("2. Magazine");
        System.out.println("3. Thesis");

        int type = readInt(sc, "Choose type ➜ ");

        String title = readText(sc, "Title ➜ ");
        String author = readText(sc, "Author ➜ ");

        String id;

        loading("Adding item");

        switch (type) {
            case 1 -> {
                id = IdGenerator.generateBookId();
                lib.addItem(new Book(id, title, author));
            }
            case 2 -> {
                id = IdGenerator.generateMagazineId();
                lib.addItem(new Magazine(id, title, author));
            }
            case 3 -> {
                id = IdGenerator.generateThesisId();
                lib.addItem(new Thesis(id, title, author));
            }
            default -> {
                error("Invalid item type.");
                return;
            }
        }

        receipt(
                "ITEM ADDED",
                "Generated ID: " + id,
                "Title: " + title,
                "Author: " + author
        );
    }

    private static void registerMember(Scanner sc, Library lib) {
        section("REGISTER NEW MEMBER");

        System.out.println("1. Basic  - limit 2");
        System.out.println("2. Silver - limit 4");
        System.out.println("3. Gold   - limit 6");

        int type = readInt(sc, "Choose tier ➜ ");

        String name = readText(sc, "Name ➜ ");
        String id = IdGenerator.generateMemberId();

        loading("Registering member");

        switch (type) {
            case 1 -> lib.registerMember(new BasicMember(id, name));
            case 2 -> lib.registerMember(new SilverMember(id, name));
            case 3 -> lib.registerMember(new GoldMember(id, name));
            default -> {
                error("Invalid member type.");
                return;
            }
        }

        receipt(
                "MEMBER REGISTERED",
                "Generated ID: " + id,
                "Name: " + name
        );
    }

    private static void borrowItem(Scanner sc, Library lib) {
        section("BORROW ITEM");

        section("AVAILABLE ITEMS");
        if (!displayAvailableItems(lib)) {
            info("There are no available items to borrow.");
            return;
        }

        String memberId = readText(sc, "Member ID ➜ ");
        String itemId = readText(sc, "Item ID ➜ ");

        loading("Processing borrow request");

        lib.borrowItem(memberId, itemId);
    }

    private static void returnItem(Scanner sc, Library lib) {
        section("RETURN ITEM");

        String memberId = readText(sc, "Member ID ➜ ");
        Member member = lib.getMemberById(memberId);

        if (member == null) {
            error("Member not found.");
            return;
        }

        section("BORROWED ITEMS");
        if (member.getBorrowedItems().isEmpty()) {
            info("This member has no borrowed items.");
            return;
        }

        ItemTablePrinter.printItems(member.getBorrowedItems());

        String itemId = readText(sc, "Item ID ➜ ");
        int overdueDays = readInt(sc, "Overdue days ➜ ");

        loading("Processing return request");

        lib.returnItem(memberId, itemId, overdueDays);
    }

    private static void searchItems(Scanner sc, Library lib) {
        section("SEARCH ITEMS");

        System.out.println("1. Search by title");
        System.out.println("2. Search by author");

        int type = readInt(sc, "Choose option ➜ ");
        String keyword = readText(sc, "Keyword ➜ ");

        loading("Searching");

        List<LibraryItem> results;

        if (type == 1) {
            results = lib.searchByTitle(keyword);
        } else if (type == 2) {
            results = lib.searchByAuthor(keyword);
        } else {
            error("Invalid search option.");
            return;
        }

        SearchResult<LibraryItem> searchResult = new SearchResult<>(results);

        section("SEARCH RESULTS");
        searchResult.display();
        info("Total found: " + searchResult.getCount());
    }

    private static void viewMemberReport(Scanner sc, Library lib) {
        section("MEMBER REPORT");

        String memberId = readText(sc, "Member ID ➜ ");
        lib.getMemberReport(memberId);
    }

    private static void viewAvailableItems(Library lib) {
        section("AVAILABLE ITEMS");
        lib.listAllAvailable();
    }

    private static boolean displayAvailableItems(Library lib) {
        List<LibraryItem> availableItems = lib.getAllItems().stream()
                .filter(LibraryItem::isAvailable)
                .toList();

        ItemTablePrinter.printItems(availableItems);

        return !availableItems.isEmpty();
    }

    private static int readInt(Scanner sc, String message) {
        while (true) {
            try {
                System.out.print(YELLOW + message + RESET);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                error("Please enter a valid number.");
            }
        }
    }

    private static String readText(Scanner sc, String message) {
        while (true) {
            System.out.print(YELLOW + message + RESET);
            String value = sc.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            error("This field cannot be empty.");
        }
    }

    private static void printHeader() {
        System.out.println(CYAN + """
╔══════════════════════════════════════════════════════════╗
║              LIBRARY MANAGEMENT SYSTEM                   ║
║              Basic • Silver • Gold Edition               ║
║              OOP Java Final Project                      ║
╚══════════════════════════════════════════════════════════╝
""" + RESET);
    }

    private static void printStats() {
        System.out.println(BLUE + """
┌──────────────────────────────────────────────────────────┐
│Fine: 0.25 AZN/day | Book: 14d | Magazine: 7d | Thesis:21d│
└──────────────────────────────────────────────────────────┘
""" + RESET);
    }

    private static void printMenu() {
        System.out.println(PURPLE + """
┌──────────────────────────────────────────────────────────┐
│  1. Add new item                                         │
│  2. Register new member                                  │
│  3. Borrow item                                          │
│  4. Return item                                          │
│  5. Search items                                         │
│  6. View member report                                   │
│  7. View all available items                             │
│  8. Exit                                                 │
└──────────────────────────────────────────────────────────┘
""" + RESET);
    }

    private static void section(String title) {
        System.out.println(CYAN + "\n════════ " + title + " ════════" + RESET);
    }

    private static void receipt(String title, String... lines) {
        System.out.println(GREEN + "\n╔════════ " + title + " ════════╗" + RESET);
        for (String line : lines) {
            System.out.println(GREEN + "  " + line + RESET);
        }
        System.out.println(GREEN + "╚════════════════════════════════╝" + RESET);
    }

    private static void loading(String message) {
        System.out.print(BLUE + message);
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(250);
                System.out.print(".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println(RESET);
    }

    private static void pause(Scanner sc) {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void success(String msg) {
        System.out.println(GREEN + "✔ " + msg + RESET);
    }

    private static void error(String msg) {
        System.out.println(RED + "✘ " + msg + RESET);
    }

    private static void info(String msg) {
        System.out.println(BLUE + "ℹ " + msg + RESET);
    }

    private static void goodbye() {
        System.out.println(GREEN + """
╔════════════════════════════════════════════╗
║      Thank you for using the system!      ║
║                 Goodbye!                  ║
╚════════════════════════════════════════════╝
""" + RESET);
    }
}
