package activehub;

public interface Promotion {
    String getCode();
    String getName();
    boolean isEligible(Transaction transaction);
    double calculateSaving(Transaction transaction);

}
