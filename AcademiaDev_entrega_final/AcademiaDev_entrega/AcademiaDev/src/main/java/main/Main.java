package main;

import application.repositories.*;
import application.usecases.*;
import infrastructure.persistence.*;
import infrastructure.ui.ConsoleController;
import infrastructure.ui.ConsoleView;
import infrastructure.utils.GenericCsvExporter;

import java.util.Scanner;

/**
 * Ponto de entrada da aplicação e Raiz de Composição (Composition Root).
 *
 * Este é o único lugar que conhece todas as camadas.
 * Aqui ocorre a Injeção de Dependência manual:
 *   - As implementações concretas (infrastructure) são instanciadas aqui
 *   - São passadas para os UseCases (application) via construtor
 *   - Os UseCases são passados ao ConsoleController (infrastructure.ui) via construtor
 *
 * Fluxo de dependências (Dependency Rule):
 *   main -> infrastructure -> application -> domain
 */
public class Main {

    public static void main(String[] args) {

        // ── 1. Instanciar repositórios concretos (infrastructure.persistence) ──
        CourseRepository courseRepository         = new CourseRepositoryEmMemoria();
        UserRepository userRepository             = new UserRepositoryEmMemoria();
        EnrollmentRepository enrollmentRepository = new EnrollmentRepositoryEmMemoria();
        SupportTicketQueue ticketQueue            = new SupportTicketQueueEmMemoria();

        // ── 2. Popular dados iniciais (InitialData conhece tudo) ──
        InitialData.popular(courseRepository, userRepository, enrollmentRepository, ticketQueue);

        // ── 3. Instanciar UseCases (application), injetando as interfaces de repositório ──
        MatricularAlunoUseCase matricularAluno     = new MatricularAlunoUseCase(enrollmentRepository, courseRepository, userRepository);
        AtualizarProgressoUseCase atualizarProgresso = new AtualizarProgressoUseCase(enrollmentRepository, userRepository);
        CancelarMatriculaUseCase cancelarMatricula   = new CancelarMatriculaUseCase(enrollmentRepository, userRepository);
        AbrirTicketUseCase abrirTicket               = new AbrirTicketUseCase(ticketQueue, userRepository);
        AtenderTicketUseCase atenderTicket            = new AtenderTicketUseCase(ticketQueue);
        GerenciarStatusCursoUseCase gerenciarStatus  = new GerenciarStatusCursoUseCase(courseRepository);
        AlterarPlanoAlunoUseCase alterarPlano        = new AlterarPlanoAlunoUseCase(userRepository);
        ConsultarCatalogoUseCase consultarCatalogo   = new ConsultarCatalogoUseCase(courseRepository);
        ConsultarMatriculasUseCase consultarMatriculas = new ConsultarMatriculasUseCase(enrollmentRepository, userRepository);
        GerarRelatoriosUseCase gerarRelatorios       = new GerarRelatoriosUseCase(courseRepository, enrollmentRepository, userRepository);

        // ── 4. Instanciar componentes de UI e utilitários (infrastructure) ──
        ConsoleView view       = new ConsoleView();
        GenericCsvExporter csv = new GenericCsvExporter();
        Scanner scanner        = new Scanner(System.in);

        // ── 5. Instanciar o controller, injetando todos os UseCases ──
        ConsoleController controller = new ConsoleController(
                scanner, view, csv,
                matricularAluno, atualizarProgresso, cancelarMatricula,
                abrirTicket, atenderTicket, gerenciarStatus, alterarPlano,
                consultarCatalogo, consultarMatriculas, gerarRelatorios,
                userRepository
        );

        // ── 6. Iniciar a aplicação ──
        controller.iniciar();
        scanner.close();
    }
}
