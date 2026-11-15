package arena.disciplinas;

import arena.eventos.Carrera;
import arena.eventos.Evento;
import arena.modelo.Entrada;
import arena.modelo.Participante;
import arena.modelo.Resultado;
import arena.torneo.Torneo;

import java.util.ArrayList;
import java.util.List;

public abstract class DisciplinaCarrera extends Disciplina {

    protected DisciplinaCarrera(String nombre, int minParticipantes, int maxParticipantes, double cuota) {
        super(nombre, minParticipantes, maxParticipantes, cuota);
    }

    @Override
    public List<Evento> generarEventos(Torneo torneo) {
        List<Participante> participantes = new ArrayList<>(torneo.getParticipantes());
        List<Evento> eventos = new ArrayList<>();
        eventos.add(new Carrera(torneo.getCodigo() + "-C1", torneo, this, participantes));
        return eventos;
    }

    public abstract Resultado leerResultado(Carrera carrera, Entrada entrada);
}
