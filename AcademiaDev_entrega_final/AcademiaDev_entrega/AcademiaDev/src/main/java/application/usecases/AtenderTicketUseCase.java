package application.usecases;

import application.repositories.SupportTicketQueue;
import domain.entities.SupportTicket;
import domain.exceptions.BusinessException;

import java.util.Optional;

public class AtenderTicketUseCase {

    private final SupportTicketQueue ticketQueue;

    public AtenderTicketUseCase(SupportTicketQueue ticketQueue) {
        this.ticketQueue = ticketQueue;
    }

    public SupportTicket execute() {
        if (ticketQueue.isEmpty()) {
            throw new BusinessException("Não há tickets na fila de suporte.");
        }
        Optional<SupportTicket> ticket = ticketQueue.nextTicket();
        return ticket.orElseThrow(() -> new BusinessException("Erro ao recuperar ticket."));
    }

    public int getQueueSize() {
        return ticketQueue.size();
    }
}
