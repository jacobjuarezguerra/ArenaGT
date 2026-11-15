package arena.eventos;

import arena.disciplinas.DisciplinaCarrera;
import arena.modelo.Entrada;
import arena.modelo.Participante;
import arena.modelo.Resultado;
import arena.torneo.Torneo;

import java.util.ArrayList;
import java.util.Collection;

public final class Carrera extends Evento {

    private final DisciplinaCarrera disciplina;

    public Carrera(String codigo, Torneo torneo, DisciplinaCarrera disciplina,
            Collection<Participante> participantes) {
        super(codigo, torneo, new ArrayList<>(participantes));
        if (disciplina == null) {
            throw new IllegalArgumentException("La disciplina es obligatoria.");
        }
        this.disciplina = disciplina;
    }

    @Override
    public String getTipoEvento() {
        return "Carrera";
    }

    @Override
    public String getDescripcion() {
        StringBuilder texto = new StringBuilder("Carrera única (")
                .append(getParticipantes().size())
                .append(" participantes):");
        for (Participante participante : getParticipantes()) {
            texto.append(" ").append(participante.getNombre()).append(",");
        }
        texto.setLength(texto.length() - 1);
        return texto.toString();
    }

    @Override
    public Resultado leerResultado(Entrada entrada) {
        return disciplina.leerResultado(this, entrada);
    }
}
