package arena.modelo;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ResultadoEncuentro implements Resultado {

    private final Participante local;
    private final Participante visitante;
    private final int puntosLocal;
    private final int puntosVisitante;
    private final int diferenciaLocal;
    private final int diferenciaVisitante;
    private final boolean victoriaLocal;
    private final boolean victoriaVisitante;
    private final String descripcion;

    public ResultadoEncuentro(Participante local, Participante visitante,
            int puntosLocal, int puntosVisitante,
            int diferenciaLocal, int diferenciaVisitante,
            boolean victoriaLocal, boolean victoriaVisitante,
            String descripcion) {
        this.local = local;
        this.visitante = visitante;
        this.puntosLocal = puntosLocal;
        this.puntosVisitante = puntosVisitante;
        this.diferenciaLocal = diferenciaLocal;
        this.diferenciaVisitante = diferenciaVisitante;
        this.victoriaLocal = victoriaLocal;
        this.victoriaVisitante = victoriaVisitante;
        this.descripcion = descripcion;
    }

    @Override
    public Collection<Participante> participantes() {
        return Arrays.asList(local, visitante);
    }

    @Override
    public int puntos(Participante participante) {
        return indice(participante) == 0 ? puntosLocal : puntosVisitante;
    }

    @Override
    public int diferencia(Participante participante) {
        return indice(participante) == 0 ? diferenciaLocal : diferenciaVisitante;
    }

    @Override
    public boolean victoria(Participante participante) {
        return indice(participante) == 0 ? victoriaLocal : victoriaVisitante;
    }

    @Override
    public String descripcion() {
        return descripcion;
    }

    private int indice(Participante participante) {
        if (local.equals(participante)) {
            return 0;
        }
        if (visitante.equals(participante)) {
            return 1;
        }
        throw new ReglaNegocioException("El participante no forma parte de este resultado.");
    }
}
