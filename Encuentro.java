package arena.eventos;

import arena.disciplinas.DisciplinaEncuentro;
import arena.modelo.Entrada;
import arena.modelo.Participante;
import arena.modelo.Resultado;
import arena.torneo.Torneo;

import java.util.Arrays;

public final class Encuentro extends Evento {

    private final DisciplinaEncuentro disciplina;
    private final Participante local;
    private final Participante visitante;

    public Encuentro(String codigo, Torneo torneo, DisciplinaEncuentro disciplina,
            Participante local, Participante visitante) {
        super(codigo, torneo, Arrays.asList(local, visitante));
        if (disciplina == null) {
            throw new IllegalArgumentException("La disciplina es obligatoria.");
        }
        this.disciplina = disciplina;
        this.local = local;
        this.visitante = visitante;
    }

    public Participante getLocal() {
        return local;
    }

    public Participante getVisitante() {
        return visitante;
    }

    @Override
    public String getTipoEvento() {
        return "Encuentro";
    }

    @Override
    public String getDescripcion() {
        return local.getNombre() + " vs " + visitante.getNombre();
    }

    @Override
    public Resultado leerResultado(Entrada entrada) {
        return disciplina.leerResultado(this, entrada);
    }
}
