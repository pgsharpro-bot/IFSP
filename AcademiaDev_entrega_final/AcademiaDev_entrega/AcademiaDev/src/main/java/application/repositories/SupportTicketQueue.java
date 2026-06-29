package application.repositories;

import domain.entities.SupportTicket;

import java.util.Optional;

public interface SupportTicketQueue {
    void addTicket(SupportTicket ticket);
    Optional<SupportTicket> nextTicket();
    boolean isEmpty();
    int size();
}
