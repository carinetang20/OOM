package activehub;

/**
 * Abstract promotion rule. Concrete subclasses A, B and C override
 * eligibility and saving calculations (inheritance + polymorphism).
 */
public abstract class Promotion {
    private final String code;
    private final String name;

    protected Promotion(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public abstract boolean isEligible(Transaction transaction);

    public abstract double calculateSaving(Transaction transaction);
}
