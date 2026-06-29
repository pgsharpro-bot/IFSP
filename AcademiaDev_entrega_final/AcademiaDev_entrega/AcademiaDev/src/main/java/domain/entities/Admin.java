package domain.entities;

public class Admin extends User {
    public Admin(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String toString() {
        return "Admin " + super.toString();
    }
}
