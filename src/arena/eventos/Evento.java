package arena.eventos;

import arena.modelo.Entrada;
import arena.modelo.EstadoEvento;
import arena.modelo.Participante;
import arena.modelo.ReglaNegocioException;
import arena.modelo.Resultado;
import arena.torneo.Torneo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Evento de un torneo. Tanto los encuentros como las carreras comparten
 * programación, estado y resultado, pero difieren en cómo se capturan y
 * validan sus datos. El calendario solo conoce esta abstracción.
 */
public abstract class Evento implements Comparable<Evento> {

    private final String codigo;
    private final Torneo torneo;
    private final List<Participante> participantes;
    private LocalDate fecha;
    private int hora;
    private String sede;
    private EstadoEvento estado;
    private Resultado resultado;

    protected Evento(String codigo, Torneo torneo, Collection<Participante> participantes) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ReglaNegocioException("El código del evento no puede estar vacío.");
        }
        if (torneo == null) {
            throw new ReglaNegocioException("El evento debe pertenecer a un torneo.");
        }
        if (participantes == null || participantes.isEmpty()) {
            throw new ReglaNegocioException("El evento debe tener participantes.");
        }
        this.codigo = codigo.trim();
        this.torneo = torneo;
        this.participantes = Collections.unmodifiableList(new ArrayList<>(participantes));
        this.hora = -1;
        this.sede = "";
        this.estado = EstadoEvento.SIN_PROGRAMAR;
    }

    public abstract String getTipoEvento();

    public abstract String getDescripcion();

    public abstract Resultado leerResultado(Entrada entrada);

    /**
     * Aplica una programación ya validada por ArenaGT. La verificación de
     * conflictos de sede y de participantes vive en el sistema.
     */
    public void aplicarProgramacion(LocalDate fecha, int hora, String sede) {
        if (estado == EstadoEvento.FINALIZADO) {
            throw new ReglaNegocioException("No se puede reprogramar un evento finalizado.");
        }
        if (fecha == null) {
            throw new ReglaNegocioException("La fecha es obligatoria.");
        }
        if (hora < 0 || hora > 23) {
            throw new ReglaNegocioException("La hora debe estar entre 0 y 23.");
        }
        if (sede == null || sede.trim().isEmpty()) {
            throw new ReglaNegocioException("La sede no puede estar vacía.");
        }
        this.fecha = fecha;
        this.hora = hora;
        this.sede = sede.trim();
        this.estado = EstadoEvento.PROGRAMADO;
    }

    /**
     * Registra un resultado previamente validado por la disciplina.
     */
    public void registrarResultado(Resultado resultado) {
        if (estado == EstadoEvento.FINALIZADO) {
            throw new ReglaNegocioException("El evento ya tiene un resultado registrado.");
        }
        if (estado != EstadoEvento.PROGRAMADO) {
            throw new ReglaNegocioException("Solo se puede registrar resultado a un evento programado.");
        }
        if (resultado == null) {
            throw new ReglaNegocioException("El resultado no puede ser nulo.");
        }
        if (!mismosParticipantes(resultado)) {
            throw new ReglaNegocioException("El resultado no corresponde a los participantes del evento.");
        }
        this.resultado = resultado;
        this.estado = EstadoEvento.FINALIZADO;
    }

    private boolean mismosParticipantes(Resultado resultado) {
        if (resultado.participantes().size() != participantes.size()) {
            return false;
        }
        for (Participante participante : participantes) {
            if (!resultado.participantes().contains(participante)) {
                return false;
            }
        }
        return true;
    }

    public int puntosDe(Participante participante) {
        if (resultado == null || !involucra(participante)) {
            return 0;
        }
        return resultado.puntos(participante);
    }

    public int diferenciaDe(Participante participante) {
        if (resultado == null || !involucra(participante)) {
            return 0;
        }
        return resultado.diferencia(participante);
    }

    public boolean victoriaDe(Participante participante) {
        return resultado != null && involucra(participante) && resultado.victoria(participante);
    }

    public boolean involucra(Participante participante) {
        return participantes.contains(participante);
    }

    public boolean estaProgramado() {
        return estado != EstadoEvento.SIN_PROGRAMAR;
    }

    public boolean estaFinalizado() {
        return estado == EstadoEvento.FINALIZADO;
    }

    public String getCodigo() {
        return codigo;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public List<Participante> getParticipantes() {
        return participantes;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getHora() {
        return hora;
    }

    public String getSede() {
        return sede;
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public Optional<Resultado> getResultado() {
        return Optional.ofNullable(resultado);
    }

    public String getHorarioTexto() {
        if (!estaProgramado()) {
            return "Sin programar";
        }
        return String.format("%s %02d:00 - %s", fecha, hora, sede);
    }

    @Override
    public int compareTo(Evento otro) {
        Comparator<Evento> comparador = Comparator
                .comparing(Evento::getFecha, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparingInt(Evento::getHora)
                .thenComparing(Evento::getSede, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Evento::getCodigo, String.CASE_INSENSITIVE_ORDER);
        return comparador.compare(this, otro);
    }

    @Override
    public String toString() {
        return codigo + " | " + getTipoEvento() + " | " + getDescripcion()
                + " | " + getHorarioTexto() + " | " + estado;
    }
}
