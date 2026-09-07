package activehub;

/**
 * Common contract for rule-based promotions (polymorphism).
 */
public interface Promotion {
    String getCode();

    String getName();

    boolean isEligible(Transaction transaction);

    double calculateSaving(Transaction transaction);
}
