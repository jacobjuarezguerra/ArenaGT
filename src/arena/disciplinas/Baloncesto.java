package arena.disciplinas;

import arena.eventos.Encuentro;
import arena.modelo.Entrada;
import arena.modelo.Participante;
import arena.modelo.ReglaNegocioException;
import arena.modelo.Resultado;
import arena.modelo.ResultadoEncuentro;

public class Baloncesto extends DisciplinaEncuentro {

    public Baloncesto() {
        super("Baloncesto", 4, 6, 500.0);
    }

    @Override
    public Resultado leerResultado(Encuentro encuentro, Entrada entrada) {
        Participante local = encuentro.getLocal();
        Participante visitante = encuentro.getVisitante();
        int puntosLocal = entrada.leerEntero("Puntos de " + local.getNombre() + ": ");
        int puntosVisitante = entrada.leerEntero("Puntos de " + visitante.getNombre() + ": ");
        if (puntosLocal < 0 || puntosVisitante < 0) {
            throw new ReglaNegocioException("Los puntos deben ser enteros no negativos.");
        }
        if (puntosLocal == puntosVisitante) {
            throw new ReglaNegocioException("El baloncesto no admite marcadores iguales.");
        }

        boolean ganaLocal = puntosLocal > puntosVisitante;
        String descripcion = local.getNombre() + " " + puntosLocal + " - "
                + puntosVisitante + " " + visitante.getNombre();
        return new ResultadoEncuentro(local, visitante,
                ganaLocal ? 2 : 1, ganaLocal ? 1 : 2,
                puntosLocal - puntosVisitante, puntosVisitante - puntosLocal,
                ganaLocal, !ganaLocal, descripcion);
    }
}
