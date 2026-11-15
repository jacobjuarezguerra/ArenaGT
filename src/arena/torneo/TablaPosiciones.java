package arena.torneo;

import arena.eventos.Evento;
import arena.modelo.FilaTabla;
import arena.modelo.Participante;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Tabla calculada a partir de los resultados; no duplica puntos en los
 * participantes. Implementa Iterable para que el menú recorra las filas
 * sin exponer la lista interna.
 */
public final class TablaPosiciones implements Iterable<FilaTabla> {

    private final List<FilaTabla> filas;

    public TablaPosiciones(Collection<Participante> participantes, Collection<Evento> eventos) {
        List<FilaTabla> calculadas = new ArrayList<>();
        for (Participante participante : participantes) {
            int puntos = 0;
            int diferencia = 0;
            int victorias = 0;
            for (Evento evento : eventos) {
                if (!evento.estaFinalizado()) {
                    continue;
                }
                puntos += evento.puntosDe(participante);
                diferencia += evento.diferenciaDe(participante);
                if (evento.victoriaDe(participante)) {
                    victorias++;
                }
            }
            calculadas.add(new FilaTabla(participante, puntos, diferencia, victorias));
        }
        Collections.sort(calculadas);
        this.filas = Collections.unmodifiableList(calculadas);
    }

    public int tamanio() {
        return filas.size();
    }

    public boolean estaVacia() {
        return filas.isEmpty();
    }

    @Override
    public Iterator<FilaTabla> iterator() {
        return filas.iterator();
    }
}
