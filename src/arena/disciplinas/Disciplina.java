package arena.disciplinas;

import arena.eventos.Evento;
import arena.modelo.ReglaNegocioException;
import arena.torneo.Torneo;

import java.util.List;

/**
 * Reglas comunes a todas las disciplinas: cupo, cuota y generación de
 * eventos. Los puntos de tabla, la diferencia y la captura del resultado
 * son responsabilidad de cada subclase.
 */
public abstract class Disciplina {

    private final String nombre;
    private final int minParticipantes;
    private final int maxParticipantes;
    private final double cuota;

    protected Disciplina(String nombre, int minParticipantes, int maxParticipantes, double cuota) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la disciplina es obligatorio.");
        }
        if (minParticipantes <= 0 || maxParticipantes < minParticipantes) {
            throw new IllegalArgumentException("Rango de participantes inválido.");
        }
        if (cuota <= 0) {
            throw new IllegalArgumentException("La cuota debe ser positiva.");
        }
        this.nombre = nombre.trim();
        this.minParticipantes = minParticipantes;
        this.maxParticipantes = maxParticipantes;
        this.cuota = cuota;
    }

    public abstract List<Evento> generarEventos(Torneo torneo);

    public String getNombre() {
        return nombre;
    }

    public int getMinParticipantes() {
        return minParticipantes;
    }

    public int getMaxParticipantes() {
        return maxParticipantes;
    }

    public double getCuota() {
        return cuota;
    }

    /**
     * Premio del primer lugar: 50 % de la cuota por la cantidad de inscritos.
     */
    public double calcularPremio(int participantesInscritos) {
        if (participantesInscritos < 0) {
            throw new ReglaNegocioException("La cantidad de inscritos no puede ser negativa.");
        }
        return 0.5 * cuota * participantesInscritos;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
