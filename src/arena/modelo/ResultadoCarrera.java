package arena.modelo;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ResultadoCarrera implements Resultado {

    public static final class Estadistica {

        private final int puntos;
        private final int diferencia;
        private final boolean victoria;

        public Estadistica(int puntos, int diferencia, boolean victoria) {
            this.puntos = puntos;
            this.diferencia = diferencia;
            this.victoria = victoria;
        }

        public int getPuntos() {
            return puntos;
        }

        public int getDiferencia() {
            return diferencia;
        }

        public boolean isVictoria() {
            return victoria;
        }
    }

    private final Map<Participante, Estadistica> estadisticas;
    private final String descripcion;

    public ResultadoCarrera(Map<Participante, Estadistica> estadisticas, String descripcion) {
        this.estadisticas = Collections.unmodifiableMap(new LinkedHashMap<>(estadisticas));
        this.descripcion = descripcion;
    }

    @Override
    public Collection<Participante> participantes() {
        return estadisticas.keySet();
    }

    @Override
    public int puntos(Participante participante) {
        return estadistica(participante).getPuntos();
    }

    @Override
    public int diferencia(Participante participante) {
        return estadistica(participante).getDiferencia();
    }

    @Override
    public boolean victoria(Participante participante) {
        return estadistica(participante).isVictoria();
    }

    @Override
    public String descripcion() {
        return descripcion;
    }

    private Estadistica estadistica(Participante participante) {
        Estadistica estadistica = estadisticas.get(participante);
        if (estadistica == null) {
            throw new ReglaNegocioException("El participante no forma parte de este resultado.");
        }
        return estadistica;
    }
}
