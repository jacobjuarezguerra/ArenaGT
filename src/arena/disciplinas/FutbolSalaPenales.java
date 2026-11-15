package arena.disciplinas;

import arena.eventos.Encuentro;
import arena.modelo.Entrada;
import arena.modelo.Participante;
import arena.modelo.ReglaNegocioException;
import arena.modelo.Resultado;
import arena.modelo.ResultadoEncuentro;

/**
 * Fútbol sala sin empates. Si el tiempo reglamentario termina igualado se
 * deben registrar penales distintos; el ganador por penales recibe 2 puntos
 * y el perdedor 1. La diferencia solo considera los goles reglamentarios.
 */
public class FutbolSalaPenales extends FutbolSala {

    public FutbolSalaPenales() {
        super("Fútbol sala con penales", 750.0);
    }

    @Override
    public Resultado leerResultado(Encuentro encuentro, Entrada entrada) {
        Participante local = encuentro.getLocal();
        Participante visitante = encuentro.getVisitante();
        int golesLocal = entrada.leerEntero("Goles de " + local.getNombre() + ": ");
        int golesVisitante = entrada.leerEntero("Goles de " + visitante.getNombre() + ": ");
        if (golesLocal < 0 || golesVisitante < 0) {
            throw new ReglaNegocioException("Los goles deben ser enteros no negativos.");
        }

        if (golesLocal != golesVisitante) {
            return construirResultado(encuentro, golesLocal, golesVisitante, null);
        }

        int penalesLocal = entrada.leerEntero("Penales de " + local.getNombre() + ": ");
        int penalesVisitante = entrada.leerEntero("Penales de " + visitante.getNombre() + ": ");
        if (penalesLocal < 0 || penalesVisitante < 0) {
            throw new ReglaNegocioException("Los penales deben ser enteros no negativos.");
        }
        if (penalesLocal == penalesVisitante) {
            throw new ReglaNegocioException("En fútbol sala con penales, los penales no pueden ser iguales.");
        }

        boolean ganaLocal = penalesLocal > penalesVisitante;
        int puntosLocal = ganaLocal ? 2 : 1;
        int puntosVisitante = ganaLocal ? 1 : 2;
        String detalle = "penales " + penalesLocal + "-" + penalesVisitante;
        String descripcion = local.getNombre() + " " + golesLocal + " - " + golesVisitante
                + " " + visitante.getNombre() + " (" + detalle + ")";

        return new ResultadoEncuentro(local, visitante,
                puntosLocal, puntosVisitante, 0, 0,
                ganaLocal, !ganaLocal, descripcion);
    }
}
