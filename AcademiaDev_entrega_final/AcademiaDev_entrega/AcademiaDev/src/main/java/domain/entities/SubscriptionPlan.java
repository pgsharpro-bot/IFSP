package domain.entities;

public interface SubscriptionPlan {
    boolean allowsEnrollment(int currentActiveEnrollments);
    String getPlanName();
}
