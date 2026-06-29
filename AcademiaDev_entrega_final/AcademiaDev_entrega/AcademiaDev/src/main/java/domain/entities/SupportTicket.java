package domain.entities;

public class SupportTicket {
    private final String id;
    private final User author;
    private final String title;
    private final String message;

    public SupportTicket(String id, User author, String title, String message) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.message = message;
    }

    public String getId() { return id; }
    public User getAuthor() { return author; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return String.format("Ticket[%s] '%s' | De: %s | Mensagem: %s",
                id, title, author.getName(), message);
    }
}
