package domain.entities;

public class BasicPlan implements SubscriptionPlan {
    private static final int MAX_ACTIVE_ENROLLMENTS = 3;

    @Override
    public boolean allowsEnrollment(int currentActiveEnrollments) {
        return currentActiveEnrollments < MAX_ACTIVE_ENROLLMENTS;
    }

    @Override
    public String getPlanName() {
        return "BasicPlan";
    }

    @Override
    public String toString() {
        return "BasicPlan (max " + MAX_ACTIVE_ENROLLMENTS + " matrículas ativas)";
    }
}
