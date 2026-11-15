package arena.disciplinas;

import arena.eventos.Carrera;
import arena.modelo.Entrada;
import arena.modelo.Participante;
import arena.modelo.ReglaNegocioException;
import arena.modelo.Resultado;
import arena.modelo.ResultadoCarrera;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Relevos extends DisciplinaCarrera {

    private static final int[] PUNTOS_POR_LUGAR = {10, 8, 6, 5, 4, 3, 2, 1};

    public Relevos() {
        super("Relevos", 3, 8, 300.0);
    }

    @Override
    public Resultado leerResultado(Carrera carrera, Entrada entrada) {
        Map<Participante, Double> tiempos = new LinkedHashMap<>();
        List<Participante> descalificados = new ArrayList<>();

        for (Participante participante : carrera.getParticipantes()) {
            String valor = entrada.leerTexto("Tiempo en segundos de " + participante.getNombre()
                    + " ('D' si está descalificado): ");
            if (valor == null || valor.trim().isEmpty()) {
                throw new ReglaNegocioException("Debe indicar un tiempo o 'D' para cada participante.");
            }
            if (valor.trim().equalsIgnoreCase("D")) {
                descalificados.add(participante);
                continue;
            }
            double tiempo;
            try {
                tiempo = Double.parseDouble(valor.trim().replace(',', '.'));
            } catch (NumberFormatException ex) {
                throw new ReglaNegocioException("Tiempo inválido para " + participante.getNombre() + ".");
            }
            if (tiempo <= 0) {
                throw new ReglaNegocioException("El tiempo debe ser mayor que cero.");
            }
            for (Map.Entry<Participante, Double> previo : tiempos.entrySet()) {
                if (Double.compare(previo.getValue(), tiempo) == 0) {
                    throw new ReglaNegocioException("Los tiempos no pueden repetirse: "
                            + previo.getKey().getNombre() + " y " + participante.getNombre() + ".");
                }
            }
            tiempos.put(participante, tiempo);
        }

        List<Map.Entry<Participante, Double>> clasificados = new ArrayList<>(tiempos.entrySet());
        clasificados.sort(Comparator.comparingDouble(Map.Entry::getValue));

        Map<Participante, ResultadoCarrera.Estadistica> estadisticas = new LinkedHashMap<>();
        StringBuilder descripcion = new StringBuilder("Carrera:");
        for (int i = 0; i < clasificados.size(); i++) {
            Map.Entry<Participante, Double> clasificado = clasificados.get(i);
            int puntos = i < PUNTOS_POR_LUGAR.length ? PUNTOS_POR_LUGAR[i] : 0;
            estadisticas.put(clasificado.getKey(),
                    new ResultadoCarrera.Estadistica(puntos, 0, i == 0));
            descripcion.append(String.format(Locale.US, " %d. %s (%.2f s, %d pts);",
                    i + 1, clasificado.getKey().getNombre(), clasificado.getValue(), puntos));
        }
        for (Participante descalificado : descalificados) {
            estadisticas.put(descalificado,
                    new ResultadoCarrera.Estadistica(0, 0, false));
            descripcion.append(" ").append(descalificado.getNombre()).append(" (DQ, 0 pts);");
        }
        descripcion.setLength(descripcion.length() - 1);
        return new ResultadoCarrera(estadisticas, descripcion.toString());
    }
}
