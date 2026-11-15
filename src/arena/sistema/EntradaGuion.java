package arena.sistema;

import arena.modelo.Entrada;
import arena.modelo.ReglaNegocioException;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Entrada con respuestas prefijadas. Se usa para cargar los datos iniciales
 * y para las pruebas automatizadas sin depender de la consola.
 */
public final class EntradaGuion implements Entrada {

    private final Deque<String> respuestas;

    public EntradaGuion(String... respuestas) {
        this.respuestas = new ArrayDeque<>();
        for (String respuesta : respuestas) {
            this.respuestas.add(respuesta);
        }
    }

    @Override
    public String leerTexto(String mensaje) {
        return siguiente();
    }

    @Override
    public int leerEntero(String mensaje) {
        String valor = siguiente();
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException ex) {
            throw new ReglaNegocioException("Se esperaba un número entero y se recibió: " + valor);
        }
    }

    @Override
    public double leerDecimal(String mensaje) {
        String valor = siguiente();
        try {
            return Double.parseDouble(valor.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new ReglaNegocioException("Se esperaba un número decimal y se recibió: " + valor);
        }
    }

    private String siguiente() {
        if (respuestas.isEmpty()) {
            throw new ReglaNegocioException("No hay más respuestas preparadas para la entrada.");
        }
        return respuestas.removeFirst();
    }
}
