package arena.disciplinas;

import arena.eventos.Encuentro;
import arena.modelo.Entrada;
import arena.modelo.Participante;
import arena.modelo.ReglaNegocioException;
import arena.modelo.Resultado;
import arena.modelo.ResultadoEncuentro;

public class Voleibol extends DisciplinaEncuentro {

    public Voleibol() {
        super("Voleibol", 4, 6, 450.0);
    }

    @Override
    public Resultado leerResultado(Encuentro encuentro, Entrada entrada) {
        Participante local = encuentro.getLocal();
        Participante visitante = encuentro.getVisitante();
        int setsLocal = entrada.leerEntero("Sets de " + local.getNombre() + ": ");
        int setsVisitante = entrada.leerEntero("Sets de " + visitante.getNombre() + ": ");
        if (setsLocal < 0 || setsVisitante < 0) {
            throw new ReglaNegocioException("Los sets deben ser enteros no negativos.");
        }
        boolean ganaLocal = setsLocal == 3 && setsVisitante >= 0 && setsVisitante <= 2;
        boolean ganaVisitante = setsVisitante == 3 && setsLocal >= 0 && setsLocal <= 2;
        if (!ganaLocal && !ganaVisitante) {
            throw new ReglaNegocioException(
                    "En voleibol el ganador debe tener exactamente 3 sets y el perdedor entre 0 y 2.");
        }

        int setsPerdedor = ganaLocal ? setsVisitante : setsLocal;
        int puntosGanador = setsPerdedor <= 1 ? 3 : 2;
        int puntosPerdedor = setsPerdedor <= 1 ? 0 : 1;
        String descripcion = local.getNombre() + " " + setsLocal + " - "
                + setsVisitante + " " + visitante.getNombre();

        return new ResultadoEncuentro(local, visitante,
                ganaLocal ? puntosGanador : puntosPerdedor,
                ganaLocal ? puntosPerdedor : puntosGanador,
                setsLocal - setsVisitante, setsVisitante - setsLocal,
                ganaLocal, ganaVisitante, descripcion);
    }
}
