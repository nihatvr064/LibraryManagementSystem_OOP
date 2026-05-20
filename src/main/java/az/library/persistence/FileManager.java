package az.library.persistence;

import az.library.model.*;
import az.library.model.member.BasicMember;
import az.library.model.member.GoldMember;
import az.library.model.member.SilverMember;
import az.library.service.Library;
import az.library.util.IdGenerator;

import java.io.*;

public class FileManager {

    private static final String ITEMS_FILE = "data/items.txt";
    private static final String MEMBERS_FILE = "data/members.txt";
    private static final String BORROWS_FILE = "data/borrows.txt";

    public static void saveAll(Library library) {
        saveItems(library);
        saveMembers(library);
        saveBorrows(library);
    }

    public static void loadAll(Library library) {
        loadItems(library);
        loadMembers(library);
        loadBorrows(library);
        library.syncItemAvailabilityWithBorrows();
    }

    private static void saveItems(Library library) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ITEMS_FILE))) {
            for (LibraryItem item : library.getAllItems()) {
                writer.write(
                        item.getItemType() + ";" +
                                item.getId() + ";" +
                                item.getTitle() + ";" +
                                item.getAuthor() + ";" +
                                item.isAvailable()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving items: " + e.getMessage());
        }
    }

    private static void saveMembers(Library library) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEMBERS_FILE))) {
            for (Member member : library.getAllMembers()) {
                writer.write(
                        member.getClass().getSimpleName() + ";" +
                                member.getMemberId() + ";" +
                                member.getName()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving members: " + e.getMessage());
        }
    }

    private static void saveBorrows(Library library) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BORROWS_FILE))) {
            for (Member member : library.getAllMembers()) {
                for (LibraryItem item : member.getBorrowedItems()) {
                    writer.write(member.getMemberId() + ";" + item.getId());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving borrows: " + e.getMessage());
        }
    }

    private static void loadItems(Library library) {
        File file = new File(ITEMS_FILE);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");

                if (data.length != 5) {
                    continue;
                }

                String type = data[0];
                String id = data[1];
                String title = data[2];
                String author = data[3];
                boolean available = Boolean.parseBoolean(data[4]);

                LibraryItem item = switch (type) {
                    case "Book" -> new Book(id, title, author);
                    case "Magazine" -> new Magazine(id, title, author);
                    case "Thesis" -> new Thesis(id, title, author);
                    default -> null;
                };

                if (item != null) {
                    item.setAvailable(available);
                    library.addItem(item);
                    IdGenerator.syncItemId(id);
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading items: " + e.getMessage());
        }
    }

    private static void loadMembers(Library library) {
        File file = new File(MEMBERS_FILE);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");

                if (data.length != 3) {
                    continue;
                }

                String type = data[0];
                String id = data[1];
                String name = data[2];

                Member member = switch (type) {
                    case "BasicMember" -> new BasicMember(id, name);
                    case "SilverMember" -> new SilverMember(id, name);
                    case "GoldMember" -> new GoldMember(id, name);
                    default -> null;
                };

                if (member != null) {
                    library.registerMember(member);
                    IdGenerator.syncMemberId(id);
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading members: " + e.getMessage());
        }
    }

    private static void loadBorrows(Library library) {
        File file = new File(BORROWS_FILE);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");

                if (data.length != 2) {
                    continue;
                }

                String memberId = data[0];
                String itemId = data[1];

                library.loadBorrowedItem(memberId, itemId);
            }

        } catch (IOException e) {
            System.out.println("Error loading borrows: " + e.getMessage());
        }
    }
}
