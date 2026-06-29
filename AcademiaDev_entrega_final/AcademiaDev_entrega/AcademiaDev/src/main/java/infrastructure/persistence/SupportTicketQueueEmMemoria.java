package infrastructure.persistence;

import application.repositories.SupportTicketQueue;
import domain.entities.SupportTicket;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public class SupportTicketQueueEmMemoria implements SupportTicketQueue {

    // ArrayDeque garante comportamento FIFO
    private final Queue<SupportTicket> fila = new ArrayDeque<>();

    @Override
    public void addTicket(SupportTicket ticket) {
        fila.offer(ticket);
    }

    @Override
    public Optional<SupportTicket> nextTicket() {
        return Optional.ofNullable(fila.poll());
    }

    @Override
    public boolean isEmpty() {
        return fila.isEmpty();
    }

    @Override
    public int size() {
        return fila.size();
    }
}
