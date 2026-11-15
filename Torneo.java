package arena.torneo;

import arena.disciplinas.Disciplina;
import arena.eventos.Evento;
import arena.modelo.EstadoTorneo;
import arena.modelo.Participante;
import arena.modelo.ReglaNegocioException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Torneo {

    private final String codigo;
    private final String nombre;
    private final Disciplina disciplina;
    private final List<Participante> participantes;
    private final List<Evento> eventos;
    private EstadoTorneo estado;

    public Torneo(String codigo, String nombre, Disciplina disciplina) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ReglaNegocioException("El código del torneo no puede estar vacío.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre del torneo no puede estar vacío.");
        }
        if (disciplina == null) {
            throw new ReglaNegocioException("El torneo debe tener una disciplina.");
        }
        this.codigo = codigo.trim();
        this.nombre = nombre.trim();
        this.disciplina = disciplina;
        this.participantes = new ArrayList<>();
        this.eventos = new ArrayList<>();
        this.estado = EstadoTorneo.EN_INSCRIPCION;
    }

    /**
     * Inscribe un participante sin cobrar. El cobro lo realiza ArenaGT solo
     * cuando la inscripción viene de una operación del usuario; así los datos
     * iniciales no generan ingresos.
     */
    public void inscribir(Participante participante) {
        if (estado != EstadoTorneo.EN_INSCRIPCION) {
            throw new ReglaNegocioException("El torneo no está en inscripción.");
        }
        if (participante == null) {
            throw new ReglaNegocioException("El participante es obligatorio.");
        }
        if (participantes.size() >= disciplina.getMaxParticipantes()) {
            throw new ReglaNegocioException("El torneo alcanzó el máximo de participantes de "
                    + disciplina.getNombre() + ".");
        }
        if (participantes.contains(participante)) {
            throw new ReglaNegocioException("El participante ya está inscrito en este torneo.");
        }
        long inscritosDeEmpresa = participantes.stream()
                .filter(actual -> actual.getEmpresa().equals(participante.getEmpresa()))
                .count();
        if (inscritosDeEmpresa >= 2) {
            throw new ReglaNegocioException("La empresa ya tiene dos participantes inscritos en este torneo.");
        }
        participantes.add(participante);
    }

    public void cerrarInscripciones() {
        if (estado != EstadoTorneo.EN_INSCRIPCION) {
            throw new ReglaNegocioException("Solo se pueden cerrar inscripciones de un torneo en inscripción.");
        }
        if (participantes.size() < disciplina.getMinParticipantes()) {
            throw new ReglaNegocioException("El torneo necesita al menos "
                    + disciplina.getMinParticipantes() + " participantes para cerrar inscripciones.");
        }
        List<Evento> generados = disciplina.generarEventos(this);
        if (generados.isEmpty()) {
            throw new ReglaNegocioException("No se pudieron generar los eventos del torneo.");
        }
        estado = EstadoTorneo.EN_JUEGO;
        eventos.addAll(generados);
    }

    /**
     * Finaliza automáticamente el torneo cuando todos sus eventos tienen
     * resultado. Devuelve true solo en la transición a FINALIZADO.
     */
    public boolean verificarFinalizacion() {
        if (estado != EstadoTorneo.EN_JUEGO) {
            return false;
        }
        if (eventos.isEmpty()) {
            return false;
        }
        for (Evento evento : eventos) {
            if (!evento.estaFinalizado()) {
                return false;
            }
        }
        estado = EstadoTorneo.FINALIZADO;
        return true;
    }

    public TablaPosiciones getTablaPosiciones() {
        return new TablaPosiciones(participantes, eventos);
    }

    public Participante getCampeon() {
        if (estado != EstadoTorneo.FINALIZADO) {
            throw new ReglaNegocioException("El torneo todavía no ha finalizado.");
        }
        return getTablaPosiciones().iterator().next().getParticipante();
    }

    public double getPremio() {
        return disciplina.calcularPremio(participantes.size());
    }

    public int contarEventosFinalizados() {
        int total = 0;
        for (Evento evento : eventos) {
            if (evento.estaFinalizado()) {
                total++;
            }
        }
        return total;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public EstadoTorneo getEstado() {
        return estado;
    }

    public boolean estaFinalizado() {
        return estado == EstadoTorneo.FINALIZADO;
    }

    public List<Participante> getParticipantes() {
        return Collections.unmodifiableList(participantes);
    }

    public List<Evento> getEventos() {
        return Collections.unmodifiableList(eventos);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + disciplina.getNombre() + ", " + estado + ")";
    }
}
