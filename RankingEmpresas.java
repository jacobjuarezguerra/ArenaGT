package arena.torneo;

import arena.eventos.Evento;
import arena.modelo.Empresa;
import arena.modelo.FilaRanking;
import arena.modelo.Participante;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RankingEmpresas implements Iterable<FilaRanking> {

    private final List<FilaRanking> filas;

    public RankingEmpresas(Collection<Participante> participantes, Collection<Torneo> torneos) {
        Map<Empresa, Integer> puntos = new LinkedHashMap<>();
        for (Participante participante : participantes) {
            puntos.putIfAbsent(participante.getEmpresa(), 0);
        }
        for (Torneo torneo : torneos) {
            for (Evento evento : torneo.getEventos()) {
                if (!evento.estaFinalizado()) {
                    continue;
                }
                for (Participante participante : evento.getParticipantes()) {
                    Empresa empresa = participante.getEmpresa();
                    puntos.merge(empresa, evento.puntosDe(participante), Integer::sum);
                }
            }
        }
        List<FilaRanking> calculadas = new ArrayList<>();
        for (Map.Entry<Empresa, Integer> entrada : puntos.entrySet()) {
            calculadas.add(new FilaRanking(entrada.getKey(), entrada.getValue()));
        }
        Collections.sort(calculadas);
        this.filas = Collections.unmodifiableList(calculadas);
    }

    @Override
    public Iterator<FilaRanking> iterator() {
        return filas.iterator();
    }
}
