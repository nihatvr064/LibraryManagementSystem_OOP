package az.library.util;

import az.library.model.LibraryItem;

import java.util.Collections;
import java.util.List;

public class SearchResult<T extends LibraryItem> {

    private final List<T> results;

    public SearchResult(List<T> results) {
        if (results == null) {
            throw new IllegalArgumentException("Results cannot be null.");
        }

        this.results = results;
    }

    public void display() {
        if (results.isEmpty()) {
            System.out.println("No results found.");
            return;
        }

        ItemTablePrinter.printItems(results);
    }

    public int getCount() {
        return results.size();
    }

    public List<T> getResults() {
        return Collections.unmodifiableList(results);
    }
}
