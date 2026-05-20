package az.library.model;

import az.library.exception.BorrowLimitExceededException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Member {

    protected static final double BASE_FINE = 0.25;

    private String memberId;
    private String name;
    private final List<LibraryItem> borrowedItems;

    public Member(String memberId, String name) {
        setMemberId(memberId);
        setName(name);
        this.borrowedItems = new ArrayList<>();
    }

    public abstract int getMaxBorrowLimit();

    public abstract double getFineMultiplier();

    public void borrowItem(LibraryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }

        if (borrowedItems.size() >= getMaxBorrowLimit()) {
            throw new BorrowLimitExceededException(
                    name + " cannot borrow more than " + getMaxBorrowLimit() + " items."
            );
        }

        borrowedItems.add(item);
    }

    public void returnItem(LibraryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }

        borrowedItems.remove(item);
    }

    public List<LibraryItem> getBorrowedItems() {
        return Collections.unmodifiableList(borrowedItems);
    }

    public String getMemberId() {
        return memberId;
    }

    private void setMemberId(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("Member ID cannot be empty.");
        }
        this.memberId = memberId.trim().toUpperCase();
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be empty.");
        }
        this.name = name.trim();
    }
}
