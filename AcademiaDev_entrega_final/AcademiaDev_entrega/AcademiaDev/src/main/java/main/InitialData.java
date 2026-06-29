package main;

import application.repositories.CourseRepository;
import application.repositories.EnrollmentRepository;
import application.repositories.SupportTicketQueue;
import application.repositories.UserRepository;
import domain.entities.*;
import domain.enums.CourseStatus;
import domain.enums.DifficultyLevel;

/**
 * Popula os repositórios em memória com dados iniciais.
 * Reside na camada externa (main), pois conhece todas as camadas.
 */
public class InitialData {

    public static void popular(CourseRepository courseRepo,
                               UserRepository userRepo,
                               EnrollmentRepository enrollmentRepo,
                               SupportTicketQueue ticketQueue) {
        // ── Cursos ──
        Course c1 = new Course("C001", "Java do Zero ao Avançado", "Curso completo de Java",
                "Ana Lima", 60, DifficultyLevel.BEGINNER);
        Course c2 = new Course("C002", "Spring Boot na Prática", "API REST com Spring",
                "Carlos Mendes", 40, DifficultyLevel.INTERMEDIATE);
        Course c3 = new Course("C003", "Clean Architecture em Java", "Princípios de Uncle Bob",
                "Ana Lima", 30, DifficultyLevel.ADVANCED);
        Course c4 = new Course("C004", "Algoritmos e Estruturas de Dados", "Fundamentos de CS",
                "Beatriz Santos", 50, DifficultyLevel.INTERMEDIATE);
        Course c5 = new Course("C005", "Docker e Kubernetes", "Containers na produção",
                "Carlos Mendes", 35, DifficultyLevel.ADVANCED);
        Course c6 = new Course("C006", "HTML e CSS para Iniciantes", "Front-end básico",
                "Diego Rocha", 20, DifficultyLevel.BEGINNER);

        // C5 começa inativo
        c5.deactivate();

        courseRepo.save(c1);
        courseRepo.save(c2);
        courseRepo.save(c3);
        courseRepo.save(c4);
        courseRepo.save(c5);
        courseRepo.save(c6);

        // ── Usuários ──
        Admin admin1 = new Admin("A001", "Fernanda Admin", "admin@academiadev.com");
        Admin admin2 = new Admin("A002", "Roberto Admin", "roberto@academiadev.com");

        Student s1 = new Student("S001", "Pedro Alves", "pedro@email.com", new BasicPlan());
        Student s2 = new Student("S002", "Maria Costa", "maria@email.com", new PremiumPlan());
        Student s3 = new Student("S003", "João Silva", "joao@email.com", new BasicPlan());
        Student s4 = new Student("S004", "Larissa Souza", "larissa@email.com", new PremiumPlan());

        userRepo.save(admin1);
        userRepo.save(admin2);
        userRepo.save(s1);
        userRepo.save(s2);
        userRepo.save(s3);
        userRepo.save(s4);

        // ── Matrículas ──
        Enrollment e1 = new Enrollment("E001", s1, c1);
        e1.updateProgress(45);
        Enrollment e2 = new Enrollment("E002", s1, c4);
        e2.updateProgress(20);
        Enrollment e3 = new Enrollment("E003", s2, c1);
        e3.updateProgress(100);
        Enrollment e4 = new Enrollment("E004", s2, c2);
        e4.updateProgress(70);
        Enrollment e5 = new Enrollment("E005", s2, c3);
        e5.updateProgress(30);
        Enrollment e6 = new Enrollment("E006", s3, c6);
        e6.updateProgress(50);
        Enrollment e7 = new Enrollment("E007", s4, c2);
        e7.updateProgress(80);
        Enrollment e8 = new Enrollment("E008", s4, c3);
        e8.updateProgress(10);
        Enrollment e9 = new Enrollment("E009", s4, c4);
        e9.updateProgress(60);

        enrollmentRepo.save(e1);
        enrollmentRepo.save(e2);
        enrollmentRepo.save(e3);
        enrollmentRepo.save(e4);
        enrollmentRepo.save(e5);
        enrollmentRepo.save(e6);
        enrollmentRepo.save(e7);
        enrollmentRepo.save(e8);
        enrollmentRepo.save(e9);

        // ── Tickets de suporte ──
        SupportTicket t1 = new SupportTicket("T001", s1, "Vídeo não carrega", "O vídeo da aula 3 não está abrindo.");
        SupportTicket t2 = new SupportTicket("T002", s2, "Dúvida sobre certificado", "Quando recebo o certificado?");
        SupportTicket t3 = new SupportTicket("T003", admin1, "Atualização de conteúdo", "Precisamos revisar o curso C002.");

        ticketQueue.addTicket(t1);
        ticketQueue.addTicket(t2);
        ticketQueue.addTicket(t3);
    }
}
