package az.library.util;

public class IdGenerator {

    private static int bookCounter = 1;
    private static int magazineCounter = 1;
    private static int thesisCounter = 1;
    private static int memberCounter = 1;

    public static String generateBookId() {
        return String.format("B%03d", bookCounter++);
    }

    public static String generateMagazineId() {
        return String.format("M%03d", magazineCounter++);
    }

    public static String generateThesisId() {
        return String.format("T%03d", thesisCounter++);
    }

    public static String generateMemberId() {
        return String.format("U%03d", memberCounter++);
    }

    public static void syncItemId(String id) {
        syncId(id, 'B');
        syncId(id, 'M');
        syncId(id, 'T');
    }

    public static void syncMemberId(String id) {
        syncId(id, 'U');
    }

    private static void syncId(String id, char prefix) {
        if (id == null) {
            return;
        }

        String normalizedId = id.trim().toUpperCase();

        if (normalizedId.length() < 2 || normalizedId.charAt(0) != prefix) {
            return;
        }

        try {
            int number = Integer.parseInt(normalizedId.substring(1));
            int nextValue = number + 1;

            switch (prefix) {
                case 'B' -> bookCounter = Math.max(bookCounter, nextValue);
                case 'M' -> magazineCounter = Math.max(magazineCounter, nextValue);
                case 'T' -> thesisCounter = Math.max(thesisCounter, nextValue);
                case 'U' -> memberCounter = Math.max(memberCounter, nextValue);
                default -> {
                }
            }
        } catch (NumberFormatException ignored) {
            // Ignore custom IDs that do not follow the generated ID format.
        }
    }
}
