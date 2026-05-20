package az.library.service;

import az.library.exception.BorrowLimitExceededException;
import az.library.exception.ItemNotAvailableException;
import az.library.model.Borrowable;
import az.library.model.LibraryItem;
import az.library.model.Member;
import az.library.util.ItemTablePrinter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Library {

    private final Map<String, LibraryItem> catalog;
    private final Map<String, Member> members;

    public Library() {
        this.catalog = new HashMap<>();
        this.members = new HashMap<>();
    }

    public void addItem(LibraryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }

        String itemId = normalizeId(item.getId());

        if (catalog.containsKey(itemId)) {
            throw new IllegalArgumentException("Item ID already exists: " + item.getId());
        }

        catalog.put(itemId, item);
    }

    public void registerMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null.");
        }

        String memberId = normalizeId(member.getMemberId());

        if (members.containsKey(memberId)) {
            throw new IllegalArgumentException("Member ID already exists: " + member.getMemberId());
        }

        members.put(memberId, member);
    }

    public void borrowItem(String memberId, String itemId) {
        Member member = members.get(normalizeId(memberId));
        LibraryItem item = catalog.get(normalizeId(itemId));

        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        if (!item.isAvailable()) {
            throw new ItemNotAvailableException("Item is not available.");
        }

        if (member.getBorrowedItems().size() >= member.getMaxBorrowLimit()) {
            throw new BorrowLimitExceededException(
                    "Borrow limit exceeded. Max limit: " + member.getMaxBorrowLimit()
            );
        }

        if (item instanceof Borrowable borrowable) {
            borrowable.borrow(member);
        } else {
            throw new IllegalArgumentException("Item is not borrowable.");
        }

        System.out.println("Borrow successful.");
        System.out.println("Return within " + item.getMaxLoanDays() + " days.");
    }

    public void returnItem(String memberId, String itemId, int overdueDays) {
        if (overdueDays < 0) {
            throw new IllegalArgumentException("Overdue days cannot be negative.");
        }

        Member member = members.get(normalizeId(memberId));
        LibraryItem item = catalog.get(normalizeId(itemId));

        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        if (!member.getBorrowedItems().contains(item)) {
            System.out.println("This item was not borrowed by this member.");
            return;
        }

        double fine;

        if (item instanceof Borrowable borrowable) {
            borrowable.returnItem(member);
            fine = borrowable.calculateFine(overdueDays);
        } else {
            throw new IllegalArgumentException("Item is not borrowable.");
        }

        System.out.println("Returned successfully.");

        if (fine > 0) {
            System.out.printf("Fine: %.2f AZN%n", fine);
        }
    }

    public List<LibraryItem> searchByTitle(String keyword) {
        List<LibraryItem> result = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }

        for (LibraryItem item : catalog.values()) {
            if (item.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(item);
            }
        }

        return result;
    }

    public List<LibraryItem> searchByAuthor(String keyword) {
        List<LibraryItem> result = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }

        for (LibraryItem item : catalog.values()) {
            if (item.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(item);
            }
        }

        return result;
    }

    public void listAllAvailable() {
        List<LibraryItem> availableItems = catalog.values().stream()
                .filter(LibraryItem::isAvailable)
                .toList();

        if (availableItems.isEmpty()) {
            System.out.println("No available items found.");
            return;
        }

        ItemTablePrinter.printItems(availableItems);
    }

    public void getMemberReport(String memberId) {
        Member member = members.get(normalizeId(memberId));

        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        System.out.println("ID: " + member.getMemberId());
        System.out.println("Name: " + member.getName());
        System.out.println("Tier: " + member.getClass().getSimpleName().replace("Member", ""));
        System.out.println("Borrow limit: " + member.getMaxBorrowLimit());
        System.out.println("Currently borrowed: " + member.getBorrowedItems().size());

        if (member.getBorrowedItems().isEmpty()) {
            System.out.println("No borrowed items.");
            return;
        }

        ItemTablePrinter.printItems(member.getBorrowedItems());
    }

    public List<LibraryItem> getAllItems() {
        return new ArrayList<>(catalog.values());
    }

    public List<Member> getAllMembers() {
        return new ArrayList<>(members.values());
    }

    public Member getMemberById(String memberId) {
        return members.get(normalizeId(memberId));
    }

    public void loadBorrowedItem(String memberId, String itemId) {
        Member member = members.get(normalizeId(memberId));
        LibraryItem item = catalog.get(normalizeId(itemId));

        if (member == null || item == null || member.getBorrowedItems().contains(item) || isBorrowed(item)) {
            return;
        }

        if (item instanceof Borrowable borrowable) {
            borrowable.borrow(member);
        }
    }

    public void syncItemAvailabilityWithBorrows() {
        Set<LibraryItem> borrowedItems = new HashSet<>();

        for (Member member : members.values()) {
            borrowedItems.addAll(member.getBorrowedItems());
        }

        for (LibraryItem item : catalog.values()) {
            item.setAvailable(!borrowedItems.contains(item));
        }
    }

    private boolean isBorrowed(LibraryItem item) {
        for (Member member : members.values()) {
            if (member.getBorrowedItems().contains(item)) {
                return true;
            }
        }

        return false;
    }

    private String normalizeId(String id) {
        return id == null ? null : id.trim().toUpperCase();
    }
}
