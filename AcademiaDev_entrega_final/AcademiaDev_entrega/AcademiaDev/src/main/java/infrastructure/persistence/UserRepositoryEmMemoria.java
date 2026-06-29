package infrastructure.persistence;

import application.repositories.UserRepository;
import domain.entities.Student;
import domain.entities.User;

import java.util.*;
import java.util.stream.Collectors;

public class UserRepositoryEmMemoria implements UserRepository {

    // Map com email como chave garante unicidade
    private final Map<String, User> usuariosPorEmail = new LinkedHashMap<>();
    private final Map<String, User> usuariosPorId = new LinkedHashMap<>();

    @Override
    public void save(User user) {
        usuariosPorEmail.put(user.getEmail(), user);
        usuariosPorId.put(user.getId(), user);
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(usuariosPorId.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usuariosPorEmail.get(email));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usuariosPorId.values());
    }

    @Override
    public List<Student> findAllStudents() {
        return usuariosPorId.values().stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .collect(Collectors.toList());
    }
}
