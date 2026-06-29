package infrastructure.ui;

import application.usecases.*;
import domain.entities.*;
import domain.enums.DifficultyLevel;
import domain.exceptions.BusinessException;
import domain.exceptions.EnrollmentException;
import infrastructure.utils.GenericCsvExporter;

import java.util.*;

/**
 * Orquestra a entrada do usuário, chama os UseCases e exibe resultados via ConsoleView.
 * Trata exceções de negócio e exibe mensagens amigáveis.
 */
public class ConsoleController {

    private final Scanner scanner;
    private final ConsoleView view;
    private final GenericCsvExporter csvExporter;

    // UseCases injetados via construtor
    private final MatricularAlunoUseCase matricularAlunoUseCase;
    private final AtualizarProgressoUseCase atualizarProgressoUseCase;
    private final CancelarMatriculaUseCase cancelarMatriculaUseCase;
    private final AbrirTicketUseCase abrirTicketUseCase;
    private final AtenderTicketUseCase atenderTicketUseCase;
    private final GerenciarStatusCursoUseCase gerenciarStatusCursoUseCase;
    private final AlterarPlanoAlunoUseCase alterarPlanoAlunoUseCase;
    private final ConsultarCatalogoUseCase consultarCatalogoUseCase;
    private final ConsultarMatriculasUseCase consultarMatriculasUseCase;
    private final GerarRelatoriosUseCase gerarRelatoriosUseCase;
    private final application.repositories.UserRepository userRepository;

    public ConsoleController(Scanner scanner,
                              ConsoleView view,
                              GenericCsvExporter csvExporter,
                              MatricularAlunoUseCase matricularAlunoUseCase,
                              AtualizarProgressoUseCase atualizarProgressoUseCase,
                              CancelarMatriculaUseCase cancelarMatriculaUseCase,
                              AbrirTicketUseCase abrirTicketUseCase,
                              AtenderTicketUseCase atenderTicketUseCase,
                              GerenciarStatusCursoUseCase gerenciarStatusCursoUseCase,
                              AlterarPlanoAlunoUseCase alterarPlanoAlunoUseCase,
                              ConsultarCatalogoUseCase consultarCatalogoUseCase,
                              ConsultarMatriculasUseCase consultarMatriculasUseCase,
                              GerarRelatoriosUseCase gerarRelatoriosUseCase,
                              application.repositories.UserRepository userRepository) {
        this.scanner = scanner;
        this.view = view;
        this.csvExporter = csvExporter;
        this.matricularAlunoUseCase = matricularAlunoUseCase;
        this.atualizarProgressoUseCase = atualizarProgressoUseCase;
        this.cancelarMatriculaUseCase = cancelarMatriculaUseCase;
        this.abrirTicketUseCase = abrirTicketUseCase;
        this.atenderTicketUseCase = atenderTicketUseCase;
        this.gerenciarStatusCursoUseCase = gerenciarStatusCursoUseCase;
        this.alterarPlanoAlunoUseCase = alterarPlanoAlunoUseCase;
        this.consultarCatalogoUseCase = consultarCatalogoUseCase;
        this.consultarMatriculasUseCase = consultarMatriculasUseCase;
        this.gerarRelatoriosUseCase = gerarRelatoriosUseCase;
        this.userRepository = userRepository;
    }

    public void iniciar() {
        boolean rodando = true;
        while (rodando) {
            view.exibirMenuPrincipal();
            String opcao = scanner.nextLine().trim();
            switch (opcao) {
                case "1" -> fazerLogin();
                case "0" -> {
                    view.exibirInfo("Até logo!");
                    rodando = false;
                }
                default -> view.exibirErro("Opção inválida.");
            }
        }
    }

    private void fazerLogin() {
        view.pedirInput("Email");
        String email = scanner.nextLine().trim();

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            view.exibirErro("Email não encontrado: " + email);
            return;
        }

        User user = userOpt.get();
        if (user instanceof Admin admin) {
            menuAdmin(admin);
        } else if (user instanceof Student student) {
            menuStudent(student);
        }
    }

    // ────────── MENU ADMIN ──────────

    private void menuAdmin(Admin admin) {
        boolean logado = true;
        while (logado) {
            view.exibirMenuAdmin(admin.getName());
            String opcao = scanner.nextLine().trim();
            switch (opcao) {
                case "1" -> exibirCatalogo();
                case "2" -> gerenciarStatusCurso();
                case "3" -> alterarPlanoAluno();
                case "4" -> atenderTicket();
                case "5" -> abrirTicket(admin);
                case "6" -> menuRelatorios();
                case "7" -> menuExportacaoCsv();
                case "0" -> logado = false;
                default  -> view.exibirErro("Opção inválida.");
            }
        }
    }

    private void gerenciarStatusCurso() {
        view.exibirCursos(consultarCatalogoUseCase.listarTodos());
        view.pedirInput("ID do curso");
        String id = scanner.nextLine().trim();
        view.exibirInfo("Ação: 1-Ativar  2-Inativar");
        view.pedirInput("Escolha");
        String acao = scanner.nextLine().trim();
        try {
            Course curso = switch (acao) {
                case "1" -> gerenciarStatusCursoUseCase.ativar(id);
                case "2" -> gerenciarStatusCursoUseCase.inativar(id);
                default  -> throw new BusinessException("Ação inválida.");
            };
            view.exibirSucesso("Status atualizado: " + curso);
        } catch (BusinessException e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void alterarPlanoAluno() {
        userRepository.findAllStudents().forEach(s ->
                view.exibirInfo(s.getId() + " | " + s.getName() + " | " + s.getSubscriptionPlan().getPlanName()));
        view.pedirInput("ID do aluno");
        String id = scanner.nextLine().trim();
        view.exibirInfo("Planos disponíveis: BASIC | PREMIUM");
        view.pedirInput("Novo plano");
        String plano = scanner.nextLine().trim();
        try {
            Student student = alterarPlanoAlunoUseCase.execute(id, plano);
            view.exibirSucesso("Plano alterado: " + student);
        } catch (BusinessException e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void atenderTicket() {
        try {
            view.exibirInfo("Tickets na fila: " + atenderTicketUseCase.getQueueSize());
            SupportTicket ticket = atenderTicketUseCase.execute();
            view.exibirTicket(ticket);
        } catch (BusinessException e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void menuRelatorios() {
        boolean ativo = true;
        while (ativo) {
            view.exibirMenuRelatorios();
            String opcao = scanner.nextLine().trim();
            switch (opcao) {
                case "1" -> relatorioCursosPorNivel();
                case "2" -> view.exibirInstrutores(gerarRelatoriosUseCase.instrutoresDeAtivos());
                case "3" -> view.exibirAlunosPorPlano(gerarRelatoriosUseCase.alunosPorPlano());
                case "4" -> view.exibirMediaProgresso(gerarRelatoriosUseCase.mediaGeralProgresso());
                case "5" -> view.exibirAlunoDestaque(gerarRelatoriosUseCase.alunoComMaisMatriculas());
                case "0" -> ativo = false;
                default  -> view.exibirErro("Opção inválida.");
            }
        }
    }

    private void relatorioCursosPorNivel() {
        view.exibirMenuNivelDificuldade();
        String opcao = scanner.nextLine().trim();
        DifficultyLevel level = switch (opcao) {
            case "1" -> DifficultyLevel.BEGINNER;
            case "2" -> DifficultyLevel.INTERMEDIATE;
            case "3" -> DifficultyLevel.ADVANCED;
            default  -> null;
        };
        if (level == null) {
            view.exibirErro("Opção inválida.");
            return;
        }
        view.exibirNivelDificuldade(level, gerarRelatoriosUseCase.cursosPorNivel(level));
    }

    private void menuExportacaoCsv() {
        view.exibirMenuExportacaoCsv();
        String opcao = scanner.nextLine().trim();
        switch (opcao) {
            case "1" -> exportarCursos();
            case "2" -> exportarAlunos();
            case "3" -> exportarMatriculas();
            case "0" -> { /* voltar */ }
            default  -> view.exibirErro("Opção inválida.");
        }
    }

    private void exportarCursos() {
        view.exibirInfo("Colunas disponíveis: id, title, description, instructorName, durationInHours, difficultyLevel, status");
        view.pedirInput("Colunas desejadas (separadas por vírgula)");
        List<String> colunas = Arrays.asList(scanner.nextLine().trim().split("\\s*,\\s*"));
        String csv = csvExporter.exportar(gerarRelatoriosUseCase.todosCursos(), colunas);
        view.exibirCsv(csv);
    }

    private void exportarAlunos() {
        view.exibirInfo("Colunas disponíveis: id, name, email (+ subscriptionPlan via getPlanName)");
        view.pedirInput("Colunas desejadas (separadas por vírgula)");
        List<String> colunas = Arrays.asList(scanner.nextLine().trim().split("\\s*,\\s*"));
        String csv = csvExporter.exportar(gerarRelatoriosUseCase.todosAlunos(), colunas);
        view.exibirCsv(csv);
    }

    private void exportarMatriculas() {
        view.exibirInfo("Colunas disponíveis: id, progress, active");
        view.pedirInput("Colunas desejadas (separadas por vírgula)");
        List<String> colunas = Arrays.asList(scanner.nextLine().trim().split("\\s*,\\s*"));
        String csv = csvExporter.exportar(gerarRelatoriosUseCase.todasMatriculas(), colunas);
        view.exibirCsv(csv);
    }

    // ────────── MENU STUDENT ──────────

    private void menuStudent(Student student) {
        boolean logado = true;
        while (logado) {
            view.exibirMenuStudent(student.getName());
            String opcao = scanner.nextLine().trim();
            switch (opcao) {
                case "1" -> exibirCatalogo();
                case "2" -> matricularEmCurso(student);
                case "3" -> consultarMatriculas(student);
                case "4" -> atualizarProgresso(student);
                case "5" -> cancelarMatricula(student);
                case "6" -> abrirTicket(student);
                case "0" -> logado = false;
                default  -> view.exibirErro("Opção inválida.");
            }
        }
    }

    private void exibirCatalogo() {
        view.exibirCursos(consultarCatalogoUseCase.listarCursosAtivos());
    }

    private void matricularEmCurso(Student student) {
        exibirCatalogo();
        view.pedirInput("ID do curso para matrícula");
        String courseId = scanner.nextLine().trim();
        try {
            Enrollment e = matricularAlunoUseCase.execute(student.getId(), courseId);
            view.exibirSucesso("Matrícula realizada! " + e);
        } catch (BusinessException e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void consultarMatriculas(Student student) {
        view.exibirMatriculas(consultarMatriculasUseCase.execute(student.getId()));
    }

    private void atualizarProgresso(Student student) {
        consultarMatriculas(student);
        view.pedirInput("ID da matrícula");
        String enrollmentId = scanner.nextLine().trim();
        view.pedirInput("Novo progresso (0-100)");
        try {
            int progresso = Integer.parseInt(scanner.nextLine().trim());
            atualizarProgressoUseCase.execute(student.getId(), enrollmentId, progresso);
            view.exibirSucesso("Progresso atualizado para " + progresso + "%.");
        } catch (NumberFormatException e) {
            view.exibirErro("Valor inválido.");
        } catch (BusinessException e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void cancelarMatricula(Student student) {
        consultarMatriculas(student);
        view.pedirInput("ID da matrícula a cancelar");
        String enrollmentId = scanner.nextLine().trim();
        try {
            cancelarMatriculaUseCase.execute(student.getId(), enrollmentId);
            view.exibirSucesso("Matrícula cancelada.");
        } catch (BusinessException e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void abrirTicket(User user) {
        view.pedirInput("Título do ticket");
        String titulo = scanner.nextLine().trim();
        view.pedirInput("Mensagem");
        String mensagem = scanner.nextLine().trim();
        try {
            SupportTicket ticket = abrirTicketUseCase.execute(user.getId(), titulo, mensagem);
            view.exibirSucesso("Ticket aberto: " + ticket.getId());
        } catch (BusinessException e) {
            view.exibirErro(e.getMessage());
        }
    }
}
