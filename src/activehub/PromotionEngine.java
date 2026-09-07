package activehub;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates all promotions and applies the single best saving.
 * Tie-break: lower alphabetical code (A before B before C).
 */
public class PromotionEngine {
    private final List<Promotion> promotions;

    public PromotionEngine() {
        promotions = new ArrayList<>();
        promotions.add(new OffPeakSaver());
        promotions.add(new TeamBookingReward());
        promotions.add(new EquipmentBundleDiscount());
    }

    public void applyBestPromotion(Transaction transaction) {
        List<String> eligibleLines = new ArrayList<>();
        Promotion best = null;
        double bestSaving = -1.0;

        for (Promotion promo : promotions) {
            if (promo.isEligible(transaction)) {
                double saving = promo.calculateSaving(transaction);
                eligibleLines.add(promo.getCode() + " - " + promo.getName()
                        + " (saving RM" + String.format("%.2f", saving) + ")");

                if (saving > bestSaving) {
                    bestSaving = saving;
                    best = promo;
                } else if (Math.abs(saving - bestSaving) < 0.0001 && best != null) {
                    // Same saving: prefer lower alphabetical code
                    if (promo.getCode().compareTo(best.getCode()) < 0) {
                        best = promo;
                        bestSaving = saving;
                    }
                }
            }
        }

        transaction.setEligiblePromotionSummary(eligibleLines);

        if (best != null && bestSaving > 0) {
            transaction.setSelectedPromotion(best, bestSaving);
        } else {
            transaction.clearPromotion();
            transaction.setEligiblePromotionSummary(eligibleLines);
        }
    }
}
