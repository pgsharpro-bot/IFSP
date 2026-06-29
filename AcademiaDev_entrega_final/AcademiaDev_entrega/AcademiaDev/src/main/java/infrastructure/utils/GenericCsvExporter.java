package infrastructure.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Exportador genérico para CSV usando Reflection.
 * Reside em infrastructure.utils por ser um detalhe de implementação.
 * A camada application (UseCases) não sabe da existência desta classe.
 */
public class GenericCsvExporter {

    /**
     * Gera uma String no formato CSV a partir de qualquer lista de objetos.
     *
     * @param data    lista de objetos a exportar
     * @param colunas nomes dos campos desejados (ex: "title", "instructorName")
     * @param <T>     tipo dos objetos na lista
     * @return String formatada como CSV
     */
    public <T> String exportar(List<T> data, List<String> colunas) {
        if (data == null || data.isEmpty()) {
            return "Nenhum dado para exportar.";
        }

        StringBuilder csv = new StringBuilder();

        // Cabeçalho
        StringJoiner header = new StringJoiner(",");
        colunas.forEach(header::add);
        csv.append(header).append("\n");

        // Linhas de dados
        for (T obj : data) {
            StringJoiner row = new StringJoiner(",");
            for (String coluna : colunas) {
                String valor = extrairValor(obj, coluna);
                // Envolve em aspas se contiver vírgula ou aspas
                if (valor.contains(",") || valor.contains("\"")) {
                    valor = "\"" + valor.replace("\"", "\"\"") + "\"";
                }
                row.add(valor);
            }
            csv.append(row).append("\n");
        }

        return csv.toString();
    }

    private <T> String extrairValor(T obj, String campo) {
        String getter = "get" + Character.toUpperCase(campo.charAt(0)) + campo.substring(1);
        try {
            Method method = encontrarMetodo(obj.getClass(), getter);
            if (method == null) {
                // Tenta "is" para booleanos
                getter = "is" + Character.toUpperCase(campo.charAt(0)) + campo.substring(1);
                method = encontrarMetodo(obj.getClass(), getter);
            }
            if (method != null) {
                Object result = method.invoke(obj);
                return result != null ? result.toString() : "";
            }
        } catch (Exception e) {
            // Campo não encontrado ou erro de acesso
        }
        return "N/A";
    }

    private Method encontrarMetodo(Class<?> clazz, String nomeMetodo) {
        // Percorre hierarquia de classes
        Class<?> current = clazz;
        while (current != null) {
            for (Method m : current.getDeclaredMethods()) {
                if (m.getName().equals(nomeMetodo) && m.getParameterCount() == 0) {
                    return m;
                }
            }
            // Verifica interfaces
            for (Class<?> iface : current.getInterfaces()) {
                Method m = encontrarMetodo(iface, nomeMetodo);
                if (m != null) return m;
            }
            current = current.getSuperclass();
        }
        return null;
    }
}
