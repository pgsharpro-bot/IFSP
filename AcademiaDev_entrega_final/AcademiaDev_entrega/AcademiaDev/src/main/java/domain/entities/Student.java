package domain.entities;

public class Student extends User {
    private SubscriptionPlan subscriptionPlan;

    public Student(String id, String name, String email, SubscriptionPlan subscriptionPlan) {
        super(id, name, email);
        this.subscriptionPlan = subscriptionPlan;
    }

    public boolean canEnroll(int currentActiveEnrollments) {
        return subscriptionPlan.allowsEnrollment(currentActiveEnrollments);
    }

    public SubscriptionPlan getSubscriptionPlan() { return subscriptionPlan; }

    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    @Override
    public String toString() {
        return "Student " + super.toString() + " | Plano: " + subscriptionPlan.getPlanName();
    }
}
