# Rodrigo Querino do Amaral - GU3065553

# AcademiaDev — Plataforma de Cursos Online

## Como executar

**Pré-requisito:** Java 17+

```bash
# Via JAR (recomendado)
java -Dfile.encoding=UTF-8 -jar AcademiaDev.jar

# Via código-fonte: compilar e executar
javac -encoding UTF-8 --release 17 -d out $(find src -name "*.java")
java -Dfile.encoding=UTF-8 -cp out main.Main
```

> **Nota sobre encoding:** o projeto usa caracteres especiais do português (ã, ç, ó etc.).
> Sempre passe `-Dfile.encoding=UTF-8` para garantir exibição correta no terminal.

---

## Logins de teste

| Email | Perfil | Plano |
|---|---|---|
| `admin@academiadev.com` | Admin | — |
| `roberto@academiadev.com` | Admin | — |
| `pedro@email.com` | Student | BasicPlan (max 3 matrículas) |
| `maria@email.com` | Student | PremiumPlan (ilimitado) |
| `joao@email.com` | Student | BasicPlan |
| `larissa@email.com` | Student | PremiumPlan |

---

## Justificativa de Design

### 1. A Regra da Dependência

A Dependency Rule da Clean Architecture determina que dependências no código-fonte
só podem apontar para dentro: `infrastructure → application → domain`.

- Nenhuma classe do `domain` importa nada de `application` ou `infrastructure`.
- Nenhuma classe de `application` importa nada de `infrastructure`.
- A camada `main` é a única que conhece todas as outras (Composition Root).

Isso foi verificado: os arquivos de `domain` não possuem nenhum `import` de pacotes
externos à própria camada. Os UseCases em `application` importam apenas `domain.*`
e as interfaces de `application.repositories` — jamais as implementações concretas.

### 2. Como a Injeção de Dependência foi feita no Main.java

O `Main.java` é a **Composition Root** — único ponto da aplicação que conhece todas
as camadas simultaneamente. A injeção de dependência é feita manualmente via construtor:

```java
// Implementações concretas instanciadas aqui (infrastructure)
CourseRepository courseRepository = new CourseRepositoryEmMemoria();

// UseCases recebem as INTERFACES, nunca as classes concretas
MatricularAlunoUseCase matricularAluno =
    new MatricularAlunoUseCase(enrollmentRepository, courseRepository, userRepository);

// Controller recebe os UseCases já montados
ConsoleController controller = new ConsoleController(scanner, view, csv, matricularAluno, ...);
```

Os UseCases dependem de abstrações (`CourseRepository`, `EnrollmentRepository` etc.)
satisfazendo o Princípio de Inversão de Dependência (DIP): `application` nunca depende
de detalhes de implementação.

### 3. Como a camada domain foi mantida pura

Todas as classes do `domain` (entidades, enums, exceções) contêm apenas lógica de
negócio pura e não importam nada de fora do próprio pacote `domain.*`. Exemplos:

- **`Student.canEnroll(int)`** — a regra de limite de matrículas vive no próprio
  domínio, delegando para a estratégia `SubscriptionPlan` (padrão Strategy).
- **`Enrollment.updateProgress(int)`** — valida a faixa 0–100 e lança
  `BusinessException` sem depender de nenhuma camada externa.
- **`BasicPlan` e `PremiumPlan`** implementam `SubscriptionPlan` (interface do domínio),
  encapsulando a regra de limite de matrículas sem vazar para fora do domínio.

### 4. Como os detalhes foram isolados na camada infrastructure

**Persistência em memória (`infrastructure.persistence`):**

As implementações `*EmMemoria` são as únicas que conhecem as estruturas de dados concretas:

- `CourseRepositoryEmMemoria` — usa `Map<String, Course>` com `title` como chave,
  garantindo unicidade de título em O(1).
- `UserRepositoryEmMemoria` — usa `Map<String, User>` com `email` como chave,
  garantindo unicidade de e-mail em O(1).
- `SupportTicketQueueEmMemoria` — usa `ArrayDeque<SupportTicket>`, garantindo
  comportamento FIFO estrito (`offer` insere no fim, `poll` remove do início).

Se a equipe decidir migrar para um banco de dados real, basta criar novas implementações
das interfaces de `application.repositories` e trocá-las no `Main.java`. Nenhuma linha
de `domain` ou `application` precisa ser alterada — esse é o benefício direto do DIP.

**Exportação CSV via Reflection (`infrastructure.utils`):**

A classe `GenericCsvExporter` usa `java.lang.reflect.Method` para inspecionar os
getters de qualquer objeto em tempo de execução. Isso é um detalhe de
framework/infraestrutura e por isso reside em `infrastructure.utils`.

Os UseCases **não sabem que ela existe**. Quem a usa é o `ConsoleController`, que:
1. Chama um UseCase para obter a lista de dados (`List<Course>`, `List<Student>` etc.)
2. Passa essa lista ao `GenericCsvExporter` junto com as colunas escolhidas pelo admin.

**Lógica de relatórios com Stream API:**

Os relatórios vivem nos UseCases (`GerarRelatoriosUseCase`), que solicitam dados brutos
dos repositórios via interface e aplicam a lógica de Stream internamente. A UI recebe
os resultados prontos e apenas os exibe — sem lógica de negócio na camada de apresentação.

### 5. Estruturas de dados utilizadas

| Finalidade | Estrutura | Motivo |
|---|---|---|
| Cursos por título | `LinkedHashMap<String, Course>` | Unicidade de título, busca O(1) |
| Usuários por e-mail | `LinkedHashMap<String, User>` | Unicidade de e-mail, busca O(1) |
| Fila de tickets | `ArrayDeque<SupportTicket>` | FIFO garantido (`poll` = remove da frente) |
| Instrutores únicos | `TreeSet<String>` (via Stream) | Deduplicação + ordenação alfabética automática |
| Agrupamento de alunos | `Map<String, List<Student>>` (`Collectors.groupingBy`) | Agrupamento por nome do plano |

---

## Funcionalidades implementadas

### Operações de Admin
- Consultar catálogo (cursos ativos, ordenados alfabeticamente)
- Ativar / Inativar cursos existentes
- Alterar plano de assinatura de aluno (BasicPlan ↔ PremiumPlan)
- Atender próximo ticket da fila (FIFO)
- Abrir ticket de suporte
- Relatórios: cursos por nível, instrutores únicos, alunos por plano, média de progresso, aluno destaque
- Exportar CSV com colunas selecionáveis dinamicamente (via Reflection)

### Operações de Aluno
- Consultar catálogo de cursos ativos
- Matricular-se em curso (valida plano e status do curso)
- Consultar matrículas com progresso
- Atualizar progresso (0–100%)
- Cancelar matrícula (libera vaga no BasicPlan)
- Abrir ticket de suporte

### Regras de negócio validadas
- BasicPlan: máximo de 3 matrículas ativas simultâneas
- PremiumPlan: sem limite de matrículas
- Curso INACTIVE não aceita novas matrículas
- Aluno já matriculado no mesmo curso não pode se matricular novamente
- Progresso fora do intervalo [0, 100] lança `BusinessException`
- Tickets processados em ordem FIFO estrita
- E-mail único por usuário (Admin ou Student)
- Título único por curso

---

## Estrutura do projeto

```
src/main/java/
├── domain/                        ← Camada mais interna. Não depende de ninguém.
│   ├── entities/
│   │   ├── User.java              (abstrata)
│   │   ├── Admin.java
│   │   ├── Student.java           (canEnroll delegando para SubscriptionPlan)
│   │   ├── SubscriptionPlan.java  (interface — padrão Strategy)
│   │   ├── BasicPlan.java
│   │   ├── PremiumPlan.java
│   │   ├── Course.java
│   │   ├── Enrollment.java
│   │   └── SupportTicket.java
│   ├── enums/
│   │   ├── CourseStatus.java
│   │   └── DifficultyLevel.java
│   └── exceptions/
│       ├── BusinessException.java
│       └── EnrollmentException.java
│
├── application/                   ← Orquestra o domínio. Depende só do domain.
│   ├── repositories/              (interfaces — contratos de persistência)
│   │   ├── CourseRepository.java
│   │   ├── UserRepository.java
│   │   ├── EnrollmentRepository.java
│   │   └── SupportTicketQueue.java
│   └── usecases/
│       ├── MatricularAlunoUseCase.java
│       ├── AtualizarProgressoUseCase.java
│       ├── CancelarMatriculaUseCase.java
│       ├── AbrirTicketUseCase.java
│       ├── AtenderTicketUseCase.java
│       ├── GerenciarStatusCursoUseCase.java
│       ├── AlterarPlanoAlunoUseCase.java
│       ├── ConsultarCatalogoUseCase.java
│       ├── ConsultarMatriculasUseCase.java
│       └── GerarRelatoriosUseCase.java
│
├── infrastructure/                ← Detalhes de implementação. Conhece application e domain.
│   ├── persistence/
│   │   ├── CourseRepositoryEmMemoria.java
│   │   ├── UserRepositoryEmMemoria.java
│   │   ├── EnrollmentRepositoryEmMemoria.java
│   │   └── SupportTicketQueueEmMemoria.java
│   ├── ui/
│   │   ├── ConsoleController.java (entrada do usuário → chama UseCases → trata exceções)
│   │   └── ConsoleView.java       (apenas imprime no console, zero lógica de negócio)
│   └── utils/
│       └── GenericCsvExporter.java (Reflection — detalhe de framework)
│
└── main/                          ← Composition Root. Conhece tudo.
    ├── Main.java                  (injeção de dependência manual)
    └── InitialData.java           (popula repositórios na inicialização)
```
