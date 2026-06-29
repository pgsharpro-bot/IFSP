package domain.entities;

public class PremiumPlan implements SubscriptionPlan {

    @Override
    public boolean allowsEnrollment(int currentActiveEnrollments) {
        return true;
    }

    @Override
    public String getPlanName() {
        return "PremiumPlan";
    }

    @Override
    public String toString() {
        return "PremiumPlan (matrículas ilimitadas)";
    }
}
