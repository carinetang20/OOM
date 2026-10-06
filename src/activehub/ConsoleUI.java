package activehub;

import java.util.List;

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

    public static void dotBarrier() {
        System.out.println(dim("  " + "·".repeat(WIDTH)));
    }

    public static void boxTop() {
        drawBoxTop(WIDTH);
    }

    public static void boxBottom() {
        drawBoxBottom(WIDTH);
    }

    public static void boxDivider() {
        drawBoxDivider(WIDTH);
    }

    public static void boxRow(String content) {
        drawBoxRow(content, WIDTH);
    }

    public static void boxBlank() {
        boxRow("");
    }

    public static void boxCenter(String content) {
        drawBoxCenter(content, WIDTH);
    }

    /**
     * Label on the left, leader dots, value on the right — keeps figures aligned.
     */
    public static void boxKeyValue(String key, String value) {
        String left = "  " + key + " ";
        String right = " " + value + " ";
        int gap = WIDTH - visibleLen(left) - visibleLen(right);
        if (gap < 2) {
            gap = 2;
        }
        boxRow(left + dim("·".repeat(gap)) + right);
    }

    public static void boxSectionLabel(String label) {
        boxRow("  " + cyan(label));
    }

    private static void drawBoxTop(int width) {
        System.out.println(cyan("  ╭" + "─".repeat(width) + "╮"));
    }

    private static void drawBoxBottom(int width) {
        System.out.println(cyan("  ╰" + "─".repeat(width) + "╯"));
    }

    private static void drawBoxDivider(int width) {
        System.out.println(cyan("  ├" + "─".repeat(width) + "┤"));
    }

    private static void drawBoxRow(String content, int width) {
        String plain = stripAnsi(content);
        int pad = width - plain.length();
        if (pad < 0) {
            System.out.println(cyan("  │") + content + cyan("│"));
            return;
        }
        System.out.println(cyan("  │") + content + " ".repeat(pad) + cyan("│"));
    }

    private static void drawBoxCenter(String content, int width) {
        String plain = stripAnsi(content);
        int pad = Math.max(0, width - plain.length());
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

    /**
     * One boxed menu: title on top, numbered options inside the same frame.
     */
    public static void menu(String title, String... options) {
        blank();
        boxTop();
        boxCenter(bold(title));
        boxDivider();
        boxBlank();
        for (int i = 0; i < options.length; i++) {
            boxRow("  " + cyan(bold("[" + (i + 1) + "]")) + "  " + options[i]);
        }
        boxBlank();
        boxBottom();
    }

    public static void menuItem(int number, String label) {
        boxRow("  " + cyan(bold("[" + number + "]")) + "  " + label);
    }

    public static void menuItem(int number, String label, String hint) {
        boxRow("  " + cyan(bold("[" + number + "]")) + "  " + label
                + dim("  — " + hint));
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

    /**
     * Boxed table. Columns are padded using visible (non-ANSI) length so
     * colours do not break alignment. Numeric columns can be right-aligned.
     * Headers are cyan, not bold, so IntelliJ keeps a monospace grid.
     */
    public static void boxedTable(String title, String[] headers, boolean[] rightAlign,
                                  List<String[]> rows, String footer) {
        int n = headers.length;
        int[] widths = new int[n];
        for (int i = 0; i < n; i++) {
            widths[i] = visibleLen(headers[i]);
        }
        if (rows != null) {
            for (String[] row : rows) {
                for (int i = 0; i < n && i < row.length; i++) {
                    widths[i] = Math.max(widths[i], visibleLen(row[i]));
                }
            }
        }

        int inner = 1;
        for (int i = 0; i < n; i++) {
            inner += widths[i];
            if (i < n - 1) {
                inner += 2;
            }
        }
        inner += 1;
        if (footer != null) {
            inner = Math.max(inner, visibleLen(footer) + 4);
        }
        inner = Math.max(inner, visibleLen(title) + 4);
        inner = Math.max(inner, WIDTH);

        blank();
        drawBoxTop(inner);
        drawBoxCenter(bold(title), inner);
        drawBoxDivider(inner);
        drawBoxRow(formatCells(headers, widths, rightAlign, true), inner);
        drawBoxDivider(inner);
        if (rows == null || rows.isEmpty()) {
            drawBoxRow("  (none)", inner);
        } else {
            for (String[] row : rows) {
                drawBoxRow(formatCells(row, widths, rightAlign, false), inner);
            }
        }
        if (footer != null) {
            drawBoxDivider(inner);
            drawBoxRow("  " + dim(footer), inner);
        }
        drawBoxBottom(inner);
    }

    public static void boxedTable(String title, String[] headers, boolean[] rightAlign,
                                  List<String[]> rows) {
        boxedTable(title, headers, rightAlign, rows, null);
    }

    private static String formatCells(String[] cells, int[] widths, boolean[] rightAlign,
                                      boolean header) {
        StringBuilder sb = new StringBuilder(" ");
        for (int i = 0; i < widths.length; i++) {
            if (i > 0) {
                sb.append("  ");
            }
            String raw = (i < cells.length && cells[i] != null) ? cells[i] : "";
            boolean right = rightAlign != null && i < rightAlign.length && rightAlign[i];
            String cell = alignVisible(raw, widths[i], right);
            if (header) {
                cell = cyan(cell);
            }
            sb.append(cell);
        }
        return sb.toString();
    }

    public static int visibleLen(String text) {
        return stripAnsi(text == null ? "" : text).length();
    }

    public static String alignVisible(String text, int width, boolean right) {
        if (text == null) {
            text = "";
        }
        String plain = stripAnsi(text);
        if (plain.length() > width) {
            text = plain.substring(0, Math.max(0, width - 1)) + "…";
        }
        int pad = width - visibleLen(text);
        if (pad <= 0) {
            return text;
        }
        String spaces = " ".repeat(pad);
        return right ? spaces + text : text + spaces;
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
