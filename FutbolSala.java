package arena.disciplinas;

import arena.eventos.Encuentro;
import arena.modelo.Entrada;
import arena.modelo.Participante;
import arena.modelo.ReglaNegocioException;
import arena.modelo.Resultado;
import arena.modelo.ResultadoEncuentro;

public class FutbolSala extends DisciplinaEncuentro {

    public FutbolSala() {
        this("Fútbol sala", 600.0);
    }

    protected FutbolSala(String nombre, double cuota) {
        super(nombre, 4, 8, cuota);
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
        return construirResultado(encuentro, golesLocal, golesVisitante, null);
    }

    protected Resultado construirResultado(Encuentro encuentro, int golesLocal,
            int golesVisitante, String detalle) {
        int puntosLocal;
        int puntosVisitante;
        boolean victoriaLocal = false;
        boolean victoriaVisitante = false;

        if (golesLocal > golesVisitante) {
            puntosLocal = 3;
            puntosVisitante = 0;
            victoriaLocal = true;
        } else if (golesLocal < golesVisitante) {
            puntosLocal = 0;
            puntosVisitante = 3;
            victoriaVisitante = true;
        } else {
            puntosLocal = 1;
            puntosVisitante = 1;
        }

        String descripcion = encuentro.getLocal().getNombre() + " " + golesLocal
                + " - " + golesVisitante + " " + encuentro.getVisitante().getNombre();
        if (detalle != null && !detalle.isEmpty()) {
            descripcion += " (" + detalle + ")";
        }

        return new ResultadoEncuentro(encuentro.getLocal(), encuentro.getVisitante(),
                puntosLocal, puntosVisitante,
                golesLocal - golesVisitante, golesVisitante - golesLocal,
                victoriaLocal, victoriaVisitante, descripcion);
    }
}
