package activehub;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates A, B and C polymorphically. Promotions cannot be combined.
 * Applies only the greatest saving. Tie: lower alphabetical code (A then B then C).
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
            boolean eligible = promo.isEligible(transaction);
            double saving = eligible ? promo.calculateSaving(transaction) : 0.0;

            if (eligible && saving > 0) {
                eligibleLines.add(promo.getCode() + " - " + promo.getName()
                        + "  saving " + ConsoleUI.money(saving));

                if (saving > bestSaving) {
                    bestSaving = saving;
                    best = promo;
                } else if (Math.abs(saving - bestSaving) < 0.0001 && best != null
                        && promo.getCode().compareTo(best.getCode()) < 0) {
                    best = promo;
                    bestSaving = saving;
                }
            }
        }

        if (best != null && bestSaving > 0) {
            transaction.setSelectedPromotion(best, bestSaving);
            transaction.setEligiblePromotionSummary(eligibleLines);
        } else {
            transaction.clearPromotion();
            transaction.setEligiblePromotionSummary(eligibleLines);
        }
    }

    public Promotion findByCode(String code) {
        for (Promotion promo : promotions) {
            if (promo.getCode().equalsIgnoreCase(code)) {
                return promo;
            }
        }
        return null;
    }
}
