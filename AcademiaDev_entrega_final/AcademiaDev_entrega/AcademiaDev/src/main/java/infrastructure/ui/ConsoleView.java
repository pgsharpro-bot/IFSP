package infrastructure.ui;

import domain.entities.*;
import domain.enums.DifficultyLevel;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Responsável apenas por imprimir menus e resultados no console.
 * Não contém lógica de negócio.
 */
public class ConsoleView {

    private static final String SEP = "─".repeat(60);

    public void exibirMenuPrincipal() {
        System.out.println("\n" + SEP);
        System.out.println("         ACADEMIADEV - Plataforma de Cursos Online");
        System.out.println(SEP);
        System.out.println("  1. Login");
        System.out.println("  0. Sair");
        System.out.print("  Escolha: ");
    }

    public void exibirMenuAdmin(String nome) {
        System.out.println("\n" + SEP);
        System.out.println("  Bem-vindo(a), " + nome + " [ADMIN]");
        System.out.println(SEP);
        System.out.println("  1. Consultar catálogo de cursos");
        System.out.println("  2. Gerenciar status de curso (ativar/inativar)");
        System.out.println("  3. Alterar plano de assinatura de aluno");
        System.out.println("  4. Atender próximo ticket de suporte");
        System.out.println("  5. Abrir ticket de suporte");
        System.out.println("  6. Relatórios e análises");
        System.out.println("  7. Exportar dados para CSV");
        System.out.println("  0. Logout");
        System.out.print("  Escolha: ");
    }

    public void exibirMenuStudent(String nome) {
        System.out.println("\n" + SEP);
        System.out.println("  Bem-vindo(a), " + nome + " [ALUNO]");
        System.out.println(SEP);
        System.out.println("  1. Consultar catálogo de cursos");
        System.out.println("  2. Matricular-se em curso");
        System.out.println("  3. Consultar minhas matrículas");
        System.out.println("  4. Atualizar progresso em curso");
        System.out.println("  5. Cancelar matrícula");
        System.out.println("  6. Abrir ticket de suporte");
        System.out.println("  0. Logout");
        System.out.print("  Escolha: ");
    }

    public void exibirMenuRelatorios() {
        System.out.println("\n" + SEP);
        System.out.println("  Relatórios e Análises");
        System.out.println(SEP);
        System.out.println("  1. Cursos por nível de dificuldade");
        System.out.println("  2. Instrutores únicos (cursos ativos)");
        System.out.println("  3. Alunos agrupados por plano");
        System.out.println("  4. Média geral de progresso");
        System.out.println("  5. Aluno com mais matrículas ativas");
        System.out.println("  0. Voltar");
        System.out.print("  Escolha: ");
    }

    public void exibirMenuNivelDificuldade() {
        System.out.println("  Nível de dificuldade:");
        System.out.println("  1. BEGINNER");
        System.out.println("  2. INTERMEDIATE");
        System.out.println("  3. ADVANCED");
        System.out.print("  Escolha: ");
    }

    public void exibirMenuExportacaoCsv() {
        System.out.println("\n  Exportar para CSV:");
        System.out.println("  1. Cursos");
        System.out.println("  2. Alunos");
        System.out.println("  3. Matrículas");
        System.out.println("  0. Voltar");
        System.out.print("  Escolha: ");
    }

    public void exibirCursos(List<Course> cursos) {
        System.out.println("\n" + SEP);
        System.out.println("  Cursos (" + cursos.size() + " encontrados):");
        System.out.println(SEP);
        if (cursos.isEmpty()) {
            System.out.println("  Nenhum curso encontrado.");
        } else {
            cursos.forEach(c -> System.out.println("  " + c));
        }
    }

    public void exibirMatriculas(List<Enrollment> matriculas) {
        System.out.println("\n" + SEP);
        System.out.println("  Suas matrículas (" + matriculas.size() + "):");
        System.out.println(SEP);
        if (matriculas.isEmpty()) {
            System.out.println("  Nenhuma matrícula encontrada.");
        } else {
            matriculas.forEach(e -> System.out.println("  " + e));
        }
    }

    public void exibirTicket(SupportTicket ticket) {
        System.out.println("\n" + SEP);
        System.out.println("  Ticket atendido:");
        System.out.println("  " + ticket);
        System.out.println(SEP);
    }

    public void exibirInstrutores(Set<String> instrutores) {
        System.out.println("\n  Instrutores únicos com cursos ativos:");
        instrutores.forEach(i -> System.out.println("  - " + i));
    }

    public void exibirAlunosPorPlano(Map<String, List<Student>> agrupamento) {
        System.out.println("\n  Alunos por plano de assinatura:");
        agrupamento.forEach((plano, alunos) -> {
            System.out.println("\n  [" + plano + "] - " + alunos.size() + " aluno(s):");
            alunos.forEach(a -> System.out.println("    - " + a.getName() + " <" + a.getEmail() + ">"));
        });
    }

    public void exibirMediaProgresso(double media) {
        System.out.printf("%n  Média geral de progresso: %.2f%%%n", media);
    }

    public void exibirAlunoDestaque(Optional<Student> aluno) {
        System.out.println("\n  Aluno com mais matrículas ativas:");
        aluno.ifPresentOrElse(
            a -> System.out.println("  " + a),
            () -> System.out.println("  Nenhum aluno com matrículas encontrado.")
        );
    }

    public void exibirCsv(String csv) {
        System.out.println("\n" + SEP);
        System.out.println("  Exportação CSV:");
        System.out.println(SEP);
        System.out.println(csv);
    }

    public void exibirSucesso(String msg) {
        System.out.println("\n  [OK] " + msg);
    }

    public void exibirErro(String msg) {
        System.out.println("\n  [ERRO] " + msg);
    }

    public void exibirInfo(String msg) {
        System.out.println("  " + msg);
    }

    public void pedirInput(String prompt) {
        System.out.print("  " + prompt + ": ");
    }

    public void exibirNivelDificuldade(DifficultyLevel level, List<Course> cursos) {
        System.out.println("\n  Cursos " + level + " (ordem alfabética):");
        if (cursos.isEmpty()) {
            System.out.println("  Nenhum curso neste nível.");
        } else {
            cursos.forEach(c -> System.out.println("  - " + c.getTitle() + " | " + c.getInstructorName()));
        }
    }
}
