package activehub;

/**
 * Shared console styling helpers for a cleaner ActiveHub UI.
 * Uses box drawing + optional ANSI colour (falls back to plain text).
 */
public final class ConsoleUI {
    public static final int WIDTH = 62;

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
    private static final String WHITE = "\u001B[97m";
    private static final String BG_CYAN = "\u001B[46m";
    private static final String BG_BLUE = "\u001B[44m";
    private static final String BLACK = "\u001B[30m";

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

    public static void doubleLine() {
        System.out.println(cyan("  " + "═".repeat(WIDTH)));
    }

    /** Top of a rounded box. */
    public static void boxTop() {
        System.out.println(cyan("  ╭" + "─".repeat(WIDTH) + "╮"));
    }

    /** Bottom of a rounded box. */
    public static void boxBottom() {
        System.out.println(cyan("  ╰" + "─".repeat(WIDTH) + "╯"));
    }

    /** One padded row inside a box. */
    public static void boxRow(String content) {
        String plain = stripAnsi(content);
        int pad = WIDTH - plain.length();
        if (pad < 0) {
            // Truncate plain length carefully for overflow
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

    public static void banner(String title, String subtitle) {
        blank();
        boxTop();
        boxBlank();
        if (COLOR) {
            boxCenter(BG_BLUE + BLACK + BOLD + "  " + title + "  " + RESET);
        } else {
            boxCenter("*** " + title + " ***");
        }
        if (subtitle != null && !subtitle.isEmpty()) {
            boxCenter(dim(subtitle));
        }
        boxBlank();
        boxBottom();
    }

    public static void section(String title) {
        blank();
        System.out.println(cyan("  ┌" + "─".repeat(WIDTH) + "┐"));
        String label = "  " + bold(title);
        String plain = "  " + title;
        int pad = Math.max(0, WIDTH - plain.length());
        System.out.println(cyan("  │") + label + " ".repeat(pad) + cyan("│"));
        System.out.println(cyan("  └" + "─".repeat(WIDTH) + "┘"));
    }

    public static void menuItem(int number, String label) {
        String num = cyan(bold(String.format("[%d]", number)));
        System.out.println("   " + num + "  " + label);
    }

    public static void menuItem(int number, String label, String hint) {
        String num = cyan(bold(String.format("[%d]", number)));
        System.out.println("   " + num + "  " + label + dim("  — " + hint));
    }

    public static void prompt(String text) {
        System.out.print("  " + yellow("› ") + text);
    }

    public static void success(String message) {
        System.out.println("  " + green("✓ " + message));
    }

    public static void error(String message) {
        System.out.println("  " + red("✗ " + message));
    }

    public static void warn(String message) {
        System.out.println("  " + yellow("! " + message));
    }

    public static void info(String message) {
        System.out.println("  " + blue("i " + message));
    }

    public static void tip(String message) {
        System.out.println("  " + dim("· " + message));
    }

    public static void kv(String key, String value) {
        String label = key;
        int pad = Math.max(1, 22 - label.length());
        System.out.println("  " + dim(label) + " ".repeat(pad) + value);
    }

    public static void tableHeader(String format, Object... cols) {
        System.out.println("  " + bold(String.format(format, cols)));
        System.out.println("  " + dim("─".repeat(WIDTH)));
    }

    public static void tableRow(String format, Object... cols) {
        System.out.println("  " + String.format(format, cols));
    }

    public static void dividerSoft() {
        System.out.println("  " + dim("· · · · · · · · · · · · · · · · · · · · · · · · · · · · · ·"));
    }

    public static void goodbye() {
        blank();
        boxTop();
        boxBlank();
        boxCenter(green(bold("Data saved successfully")));
        boxCenter("Thank you for using ActiveHub");
        boxCenter(dim("Stay active. Play fair."));
        boxBlank();
        boxBottom();
        blank();
    }

    public static void welcomeSplash() {
        blank();
        boxTop();
        boxBlank();
        boxCenter(bold(cyan("ACTIVEHUB")));
        boxCenter(bold("Sports Centre Management"));
        boxBlank();
        boxCenter(dim("Facility Booking  ·  Rentals  ·  Promotions"));
        boxCenter(dim("Console Edition"));
        boxBlank();
        boxBottom();
    }

    /** Remove ANSI codes so width calculations stay accurate. */
    public static String stripAnsi(String input) {
        return input.replaceAll("\u001B\\[[;\\d]*m", "");
    }
}
