package arena.eventos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Vista ordenada de eventos programados. El criterio de orden vive aquí
 * (fecha, hora y nombre de sede) y quien recorre el calendario no conoce
 * la estructura interna ni repite el ordenamiento.
 */
public final class Calendario implements Iterable<Evento> {

    private final List<Evento> eventos;

    public Calendario(Collection<Evento> eventos) {
        List<Evento> programados = new ArrayList<>();
        for (Evento evento : eventos) {
            if (evento.estaProgramado()) {
                programados.add(evento);
            }
        }
        Collections.sort(programados);
        this.eventos = Collections.unmodifiableList(programados);
    }

    public int tamanio() {
        return eventos.size();
    }

    public boolean estaVacio() {
        return eventos.isEmpty();
    }

    @Override
    public Iterator<Evento> iterator() {
        return eventos.iterator();
    }
}
