package activehub;

/**
 * Shared console styling for ActiveHub.
 * Clean boxes, colour, and aligned menus (optional ANSI).
 */
public final class ConsoleUI {
    public static final int WIDTH = 58;

    private static final boolean COLOR =
            !Boolean.getBoolean("activehub.nocolor")
                    && System.getenv("NO_COLOR") == null;

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String DIM = "\u001B[2m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";

    private ConsoleUI() {
    }

    private static String paint(String code, String text) {
        if (!COLOR) {
            return text;
        }
        return code + text + RESET;
    }

    public static String bold(String text) {
        return paint(BOLD, text);
    }

    public static String cyan(String text) {
        return paint(CYAN, text);
    }

    public static String green(String text) {
        return paint(GREEN, text);
    }

    public static String yellow(String text) {
        return paint(YELLOW, text);
    }

    public static String red(String text) {
        return paint(RED, text);
    }

    public static String blue(String text) {
        return paint(BLUE, text);
    }

    public static String magenta(String text) {
        return paint(MAGENTA, text);
    }

    public static String dim(String text) {
        return paint(DIM, text);
    }

    public static String money(double amount) {
        return String.format("RM%.2f", amount);
    }

    public static void blank() {
        System.out.println();
    }

    public static void line() {
        System.out.println(cyan("  " + "─".repeat(WIDTH)));
    }

    public static void boxTop() {
        System.out.println(cyan("  ╭" + "─".repeat(WIDTH) + "╮"));
    }

    public static void boxBottom() {
        System.out.println(cyan("  ╰" + "─".repeat(WIDTH) + "╯"));
    }

    public static void boxRow(String content) {
        String plain = stripAnsi(content);
        int pad = WIDTH - plain.length();
        if (pad < 0) {
            System.out.println(cyan("  │") + content + cyan("│"));
            return;
        }
        System.out.println(cyan("  │") + content + " ".repeat(pad) + cyan("│"));
    }

    public static void boxBlank() {
        boxRow("");
    }

    public static void boxCenter(String content) {
        String plain = stripAnsi(content);
        int pad = Math.max(0, WIDTH - plain.length());
        int left = pad / 2;
        int right = pad - left;
        System.out.println(cyan("  │") + " ".repeat(left) + content + " ".repeat(right) + cyan("│"));
    }

    public static void section(String title) {
        blank();
        boxTop();
        boxCenter(bold(title));
        boxBottom();
    }

    public static void menuItem(int number, String label) {
        System.out.println("   " + cyan(bold("[" + number + "]")) + "  " + label);
        line();
    }

    public static void menuItem(int number, String label, String hint) {
        System.out.println("   " + cyan(bold("[" + number + "]")) + "  " + label
                + dim("  — " + hint));
        line();
    }

    public static void prompt(String text) {
        System.out.print("  " + yellow("› ") + text);
    }

    public static void success(String message) {
        System.out.println("  " + green("✓ " + message));
        line();
    }

    public static void error(String message) {
        System.out.println("  " + red("✗ " + message));
        line();
    }

    public static void warn(String message) {
        System.out.println("  " + yellow("! " + message));
        line();
    }

    public static void info(String message) {
        System.out.println("  " + blue("i " + message));
        line();
    }

    public static void tip(String message) {
        System.out.println("  " + dim("· " + message));
        line();
    }

    public static void kv(String key, String value) {
        int pad = Math.max(1, 20 - key.length());
        System.out.println("  " + dim(key) + " ".repeat(pad) + value);
    }

    public static void tableHeader(String format, Object... cols) {
        System.out.println("  " + bold(String.format(format, cols)));
        line();
    }

    public static void tableRow(String format, Object... cols) {
        System.out.println("  " + String.format(format, cols));
        line();
    }

    public static void dividerSoft() {
        System.out.println("  " + dim("· · · · · · · · · · · · · · · · · · · · · · · · · · ·"));
    }

    public static void welcomeSplash() {
        blank();
        boxTop();
        boxBlank();
        boxCenter(bold(cyan("ACTIVEHUB")));
        boxCenter("Sports Centre Management System");
        boxBlank();
        boxCenter(dim("Booking  ·  Rentals  ·  Promotions  ·  Payments"));
        boxBlank();
        boxBottom();
    }

    public static void goodbye() {
        blank();
        boxTop();
        boxBlank();
        boxCenter(green(bold("Data saved successfully")));
        boxCenter("Thank you for using ActiveHub");
        boxBlank();
        boxBottom();
        blank();
    }

    public static String stripAnsi(String input) {
        return input.replaceAll("\u001B\\[[;\\d]*m", "");
    }
}
