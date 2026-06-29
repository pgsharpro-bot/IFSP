package application.usecases;

import application.repositories.SupportTicketQueue;
import application.repositories.UserRepository;
import domain.entities.SupportTicket;
import domain.entities.User;
import domain.exceptions.BusinessException;

import java.util.UUID;

public class AbrirTicketUseCase {

    private final SupportTicketQueue ticketQueue;
    private final UserRepository userRepository;

    public AbrirTicketUseCase(SupportTicketQueue ticketQueue, UserRepository userRepository) {
        this.ticketQueue = ticketQueue;
        this.userRepository = userRepository;
    }

    public SupportTicket execute(String userId, String title, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado: " + userId));

        SupportTicket ticket = new SupportTicket(UUID.randomUUID().toString(), user, title, message);
        ticketQueue.addTicket(ticket);
        return ticket;
    }
}
