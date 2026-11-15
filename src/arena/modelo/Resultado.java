package arena.modelo;

import java.util.Collection;

/**
 * Resultado ya validado de un evento. Cada disciplina construye la
 * implementación adecuada y el resto del sistema solo consulta puntos,
 * diferencia y victoria de forma polimórfica.
 */
public interface Resultado {

    Collection<Participante> participantes();

    int puntos(Participante participante);

    int diferencia(Participante participante);

    boolean victoria(Participante participante);

    String descripcion();
}
