package arena.disciplinas;

import arena.eventos.Encuentro;
import arena.eventos.Evento;
import arena.modelo.Entrada;
import arena.modelo.Participante;
import arena.modelo.Resultado;
import arena.torneo.Torneo;

import java.util.ArrayList;
import java.util.List;

public abstract class DisciplinaEncuentro extends Disciplina {

    protected DisciplinaEncuentro(String nombre, int minParticipantes, int maxParticipantes, double cuota) {
        super(nombre, minParticipantes, maxParticipantes, cuota);
    }

    @Override
    public List<Evento> generarEventos(Torneo torneo) {
        List<Participante> participantes = new ArrayList<>(torneo.getParticipantes());
        List<Evento> eventos = new ArrayList<>();
        int numero = 0;
        for (int i = 0; i < participantes.size(); i++) {
            for (int j = i + 1; j < participantes.size(); j++) {
                numero++;
                eventos.add(new Encuentro(torneo.getCodigo() + "-E" + numero,
                        torneo, this, participantes.get(i), participantes.get(j)));
            }
        }
        return eventos;
    }

    public abstract Resultado leerResultado(Encuentro encuentro, Entrada entrada);
}
