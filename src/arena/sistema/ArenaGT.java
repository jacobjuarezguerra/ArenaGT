package arena.sistema;

import arena.disciplinas.Baloncesto;
import arena.disciplinas.Disciplina;
import arena.disciplinas.FutbolSala;
import arena.disciplinas.FutbolSalaPenales;
import arena.disciplinas.Relevos;
import arena.disciplinas.Voleibol;
import arena.eventos.Calendario;
import arena.eventos.Evento;
import arena.modelo.Empresa;
import arena.modelo.Entrada;
import arena.modelo.Participante;
import arena.modelo.ReglaNegocioException;
import arena.modelo.Resultado;
import arena.torneo.Finanzas;
import arena.torneo.RankingEmpresas;
import arena.torneo.TablaPosiciones;
import arena.torneo.Torneo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fachada del sistema. Centraliza las búsquedas, la validación de conflictos
 * de programación y la contabilidad; las reglas propias de cada disciplina
 * quedan en las disciplinas y en los eventos.
 */
public final class ArenaGT {

    private final Map<String, Participante> participantes;
    private final Map<String, Torneo> torneos;
    private final Map<String, Empresa> empresas;
    private final List<Disciplina> disciplinas;
    private final Finanzas finanzas;

    public ArenaGT() {
        this.participantes = new LinkedHashMap<>();
        this.torneos = new LinkedHashMap<>();
        this.empresas = new LinkedHashMap<>();
        this.finanzas = new Finanzas();
        this.disciplinas = List.of(
                new FutbolSala(),
                new FutbolSalaPenales(),
                new Baloncesto(),
                new Voleibol(),
                new Relevos());
    }

    public Participante registrarParticipante(String codigo, String nombre, String empresa) {
        String codigoLimpio = validarTexto(codigo, "El código del participante");
        String nombreLimpio = validarTexto(nombre, "El nombre del participante");
        String empresaLimpia = validarTexto(empresa, "El nombre de la empresa");
        String clave = clave(codigoLimpio);
        if (participantes.containsKey(clave)) {
            throw new ReglaNegocioException("Ya existe un participante con el código " + codigoLimpio + ".");
        }
        Empresa empresaObjeto = empresas.computeIfAbsent(clave(empresaLimpia), k -> new Empresa(empresaLimpia));
        Participante participante = new Participante(codigoLimpio, nombreLimpio, empresaObjeto);
        participantes.put(clave, participante);
        return participante;
    }

    public Torneo crearTorneo(String codigo, String nombre, String nombreDisciplina) {
        String codigoLimpio = validarTexto(codigo, "El código del torneo");
        String nombreLimpio = validarTexto(nombre, "El nombre del torneo");
        Disciplina disciplina = getDisciplina(nombreDisciplina);
        String clave = clave(codigoLimpio);
        if (torneos.containsKey(clave)) {
            throw new ReglaNegocioException("Ya existe un torneo con el código " + codigoLimpio + ".");
        }
        Torneo torneo = new Torneo(codigoLimpio, nombreLimpio, disciplina);
        torneos.put(clave, torneo);
        return torneo;
    }

    public double cotizar(String nombreDisciplina, int cantidadParticipantes) {
        Disciplina disciplina = getDisciplina(nombreDisciplina);
        if (cantidadParticipantes <= 0) {
            throw new ReglaNegocioException("La cantidad de participantes debe ser positiva.");
        }
        if (cantidadParticipantes < disciplina.getMinParticipantes()
                || cantidadParticipantes > disciplina.getMaxParticipantes()) {
            throw new ReglaNegocioException("La cantidad debe estar entre "
                    + disciplina.getMinParticipantes() + " y "
                    + disciplina.getMaxParticipantes() + " para " + disciplina.getNombre() + ".");
        }
        return disciplina.getCuota() * cantidadParticipantes;
    }

    /**
     * Inscribe y cobra la cuota. Si la inscripción es rechazada, no se cobra.
     */
    public double inscribir(String codigoTorneo, String codigoParticipante) {
        Torneo torneo = buscarTorneo(codigoTorneo);
        Participante participante = buscarParticipante(codigoParticipante);
        torneo.inscribir(participante);
        double cuota = torneo.getDisciplina().getCuota();
        finanzas.registrarIngreso(torneo.getDisciplina(), cuota);
        return cuota;
    }

    public void cerrarInscripciones(String codigoTorneo) {
        buscarTorneo(codigoTorneo).cerrarInscripciones();
    }

    /**
     * Valida y aplica una programación o reprogramación. El evento mismo no
     * conoce los demás torneos; por eso los conflictos se comprueban aquí.
     */
    public void programarEvento(String codigoEvento, LocalDate fecha, int hora, String sede) {
        Evento evento = buscarEvento(codigoEvento);
        if (evento.estaFinalizado()) {
            throw new ReglaNegocioException("No se puede programar un evento finalizado.");
        }
        if (fecha == null) {
            throw new ReglaNegocioException("La fecha es obligatoria.");
        }
        if (hora < 0 || hora > 23) {
            throw new ReglaNegocioException("La hora debe estar entre 0 y 23.");
        }
        String sedeLimpia = validarTexto(sede, "La sede");

        for (Torneo torneo : torneos.values()) {
            for (Evento otro : torneo.getEventos()) {
                if (otro == evento || !otro.estaProgramado()) {
                    continue;
                }
                if (!fecha.equals(otro.getFecha()) || hora != otro.getHora()) {
                    continue;
                }
                if (sedeLimpia.equalsIgnoreCase(otro.getSede())) {
                    throw new ReglaNegocioException("La sede " + sedeLimpia
                            + " ya tiene otro evento en esa fecha y hora.");
                }
                for (Participante participante : evento.getParticipantes()) {
                    if (otro.involucra(participante)) {
                        throw new ReglaNegocioException("El participante "
                                + participante.getNombre()
                                + " ya tiene otro evento en esa fecha y hora.");
                    }
                }
            }
        }
        evento.aplicarProgramacion(fecha, hora, sedeLimpia);
    }

    /**
     * Pide a la disciplina los datos del resultado y solo modifica el estado
     * cuando todo es válido. Si el torneo queda completo, entrega el premio.
     */
    public Resultado registrarResultado(String codigoEvento, Entrada entrada) {
        Evento evento = buscarEvento(codigoEvento);
        if (!evento.estaProgramado()) {
            throw new ReglaNegocioException("Solo se puede registrar resultado a un evento programado.");
        }
        if (evento.estaFinalizado()) {
            throw new ReglaNegocioException("El evento ya tiene un resultado registrado.");
        }
        Resultado resultado = evento.leerResultado(entrada);
        evento.registrarResultado(resultado);
        if (evento.getTorneo().verificarFinalizacion()) {
            finanzas.registrarPremio(evento.getTorneo());
        }
        return resultado;
    }

    public Calendario calendarioGeneral() {
        List<Evento> eventos = new ArrayList<>();
        for (Torneo torneo : torneos.values()) {
            eventos.addAll(torneo.getEventos());
        }
        return new Calendario(eventos);
    }

    public Calendario calendarioTorneo(String codigoTorneo) {
        return new Calendario(buscarTorneo(codigoTorneo).getEventos());
    }

    public Calendario calendarioParticipante(String codigoParticipante) {
        Participante participante = buscarParticipante(codigoParticipante);
        List<Evento> eventos = new ArrayList<>();
        for (Torneo torneo : torneos.values()) {
            for (Evento evento : torneo.getEventos()) {
                if (evento.involucra(participante)) {
                    eventos.add(evento);
                }
            }
        }
        return new Calendario(eventos);
    }

    public TablaPosiciones getTablaPosiciones(String codigoTorneo) {
        return buscarTorneo(codigoTorneo).getTablaPosiciones();
    }

    public RankingEmpresas getRankingEmpresas() {
        return new RankingEmpresas(participantes.values(), torneos.values());
    }

    public List<Disciplina> getDisciplinas() {
        return Collections.unmodifiableList(disciplinas);
    }

    public Disciplina getDisciplina(String nombre) {
        String buscado = validarTexto(nombre, "La disciplina");
        for (Disciplina disciplina : disciplinas) {
            if (disciplina.getNombre().equalsIgnoreCase(buscado)) {
                return disciplina;
            }
        }
        throw new ReglaNegocioException("No existe la disciplina " + buscado + ".");
    }

    public Participante buscarParticipante(String codigo) {
        String buscado = validarTexto(codigo, "El código del participante");
        Participante participante = participantes.get(clave(buscado));
        if (participante == null) {
            throw new ReglaNegocioException("No existe el participante " + buscado + ".");
        }
        return participante;
    }

    public Torneo buscarTorneo(String codigo) {
        String buscado = validarTexto(codigo, "El código del torneo");
        Torneo torneo = torneos.get(clave(buscado));
        if (torneo == null) {
            throw new ReglaNegocioException("No existe el torneo " + buscado + ".");
        }
        return torneo;
    }

    public Evento buscarEvento(String codigo) {
        String buscado = validarTexto(codigo, "El código del evento");
        for (Torneo torneo : torneos.values()) {
            for (Evento evento : torneo.getEventos()) {
                if (evento.getCodigo().equalsIgnoreCase(buscado)) {
                    return evento;
                }
            }
        }
        throw new ReglaNegocioException("No existe el evento " + buscado + ".");
    }

    public Collection<Participante> getParticipantes() {
        return Collections.unmodifiableCollection(participantes.values());
    }

    public Collection<Torneo> getTorneos() {
        return Collections.unmodifiableCollection(torneos.values());
    }

    public Finanzas getFinanzas() {
        return finanzas;
    }

    private static String validarTexto(String valor, String descripcion) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ReglaNegocioException(descripcion + " no puede estar vacío.");
        }
        return valor.trim();
    }

    private static String clave(String valor) {
        return valor.trim().toUpperCase(Locale.ROOT);
    }

    // ------------------------------------------------------------------
    // Datos iniciales
    // ------------------------------------------------------------------

    public static ArenaGT cargarDatosIniciales() {
        ArenaGT sistema = new ArenaGT();

        Participante p01 = sistema.registrarParticipante("P01", "Los Tigres", "Alfa");
        Participante p02 = sistema.registrarParticipante("P02", "Halcones", "Alfa");
        Participante p03 = sistema.registrarParticipante("P03", "Jaguares", "Beta");
        Participante p04 = sistema.registrarParticipante("P04", "Serpientes", "Beta");
        Participante p05 = sistema.registrarParticipante("P05", "Cóndores", "Gamma");
        Participante p06 = sistema.registrarParticipante("P06", "Águilas", "Gamma");

        Torneo futbol = sistema.crearTorneo("T-FUT", "Copa Fútbol Sala", "Fútbol sala");
        Torneo penales = sistema.crearTorneo("T-PEN", "Copa Penales", "Fútbol sala con penales");
        Torneo basquet = sistema.crearTorneo("T-BAS", "Copa Baloncesto", "Baloncesto");
        Torneo voleibol = sistema.crearTorneo("T-VOL", "Copa Voleibol", "Voleibol");
        Torneo relevos = sistema.crearTorneo("T-REL", "Carrera de Relevos", "Relevos");

        // Inscripción directa: no pasa por ArenaGT.inscribir, por lo que no
        // genera ingresos ni premios.
        futbol.inscribir(p01);
        futbol.inscribir(p02);
        futbol.inscribir(p03);
        futbol.inscribir(p04);
        futbol.inscribir(p05);

        penales.inscribir(p01);
        penales.inscribir(p02);
        penales.inscribir(p03);
        penales.inscribir(p04);
        penales.inscribir(p05);

        basquet.inscribir(p01);
        basquet.inscribir(p02);
        basquet.inscribir(p03);
        basquet.inscribir(p04);

        voleibol.inscribir(p01);
        voleibol.inscribir(p02);
        voleibol.inscribir(p03);
        voleibol.inscribir(p04);

        relevos.inscribir(p01);
        relevos.inscribir(p02);
        relevos.inscribir(p03);
        relevos.inscribir(p04);
        relevos.inscribir(p05);
        relevos.inscribir(p06);

        for (Torneo torneo : sistema.getTorneos()) {
            torneo.cerrarInscripciones();
        }

        LocalDate base = LocalDate.of(2027, 1, 10);
        String[] sedes = {"ArenaGT Central", "Complejo Sur", "Polideportivo Norte"};
        int turno = 0;
        for (Torneo torneo : sistema.getTorneos()) {
            for (Evento evento : torneo.getEventos()) {
                LocalDate fecha = base.plusDays(turno);
                int hora = 8 + (turno % 10);
                String sede = sedes[turno % sedes.length];
                sistema.programarEvento(evento.getCodigo(), fecha, hora, sede);
                turno++;
            }
        }

        // Desempate por diferencia: P01 y P02 quedan con 3 puntos, pero
        // P01 tiene +3 y P02 +2.
        registrarMarcador(sistema, futbol, p01, p03, 3, 0);
        registrarMarcador(sistema, futbol, p02, p04, 2, 0);
        // Empate de fútbol sala: cada equipo suma 1 punto de tabla.
        registrarMarcador(sistema, futbol, p03, p04, 1, 1);

        // Fútbol sala con penales: P01 y P03 tienen 3 puntos con una victoria
        // por penales, mientras P05 tiene 3 puntos con cero victorias.
        registrarPenales(sistema, penales, p01, p03, 1, 1, 4, 3);
        registrarPenales(sistema, penales, p01, p04, 0, 0, 3, 4);
        registrarPenales(sistema, penales, p02, p05, 0, 0, 5, 4);
        registrarPenales(sistema, penales, p03, p05, 1, 1, 4, 2);
        registrarPenales(sistema, penales, p04, p05, 2, 2, 3, 2);
        // Definido en tiempo reglamentario: 3 puntos al ganador y 1 al
        // perdedor, igual que en fútbol sala.
        registrarMarcador(sistema, penales, p02, p04, 2, 1);

        registrarMarcador(sistema, basquet, p01, p02, 80, 70);
        registrarMarcador(sistema, voleibol, p03, p04, 3, 2);
        registrarMarcador(sistema, voleibol, p01, p02, 3, 0);

        return sistema;
    }

    private static void registrarMarcador(ArenaGT sistema, Torneo torneo,
            Participante a, Participante b, int valorA, int valorB) {
        Evento evento = eventoEntre(torneo, a, b);
        boolean aPrimero = evento.getParticipantes().get(0).equals(a);
        sistema.registrarResultado(evento.getCodigo(), new EntradaGuion(
                Integer.toString(aPrimero ? valorA : valorB),
                Integer.toString(aPrimero ? valorB : valorA)));
    }

    private static void registrarPenales(ArenaGT sistema, Torneo torneo,
            Participante a, Participante b,
            int golesA, int golesB, int penalesA, int penalesB) {
        Evento evento = eventoEntre(torneo, a, b);
        boolean aPrimero = evento.getParticipantes().get(0).equals(a);
        sistema.registrarResultado(evento.getCodigo(), new EntradaGuion(
                Integer.toString(aPrimero ? golesA : golesB),
                Integer.toString(aPrimero ? golesB : golesA),
                Integer.toString(aPrimero ? penalesA : penalesB),
                Integer.toString(aPrimero ? penalesB : penalesA)));
    }

    private static Evento eventoEntre(Torneo torneo, Participante a, Participante b) {
        for (Evento evento : torneo.getEventos()) {
            if (evento.getParticipantes().size() == 2
                    && evento.getParticipantes().contains(a)
                    && evento.getParticipantes().contains(b)) {
                return evento;
            }
        }
        throw new ReglaNegocioException("No se encontró el evento entre "
                + a.getNombre() + " y " + b.getNombre() + ".");
    }
}
