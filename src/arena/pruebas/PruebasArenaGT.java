package arena.pruebas;

import arena.disciplinas.Disciplina;
import arena.eventos.Calendario;
import arena.eventos.Evento;
import arena.modelo.FilaTabla;
import arena.modelo.Participante;
import arena.modelo.ReglaNegocioException;
import arena.sistema.ArenaGT;
import arena.sistema.EntradaGuion;
import arena.torneo.Finanzas;
import arena.torneo.TablaPosiciones;
import arena.torneo.Torneo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Pruebas ejecutables sin JUnit. Cada prueba lanza AssertionError si falla.
 * Se ejecuta con: java -cp out arena.pruebas.PruebasArenaGT
 */
public final class PruebasArenaGT {

    private static int ejecutadas;
    private static int fallidas;
    private static int turno;

    private PruebasArenaGT() {
    }

    public static void main(String[] args) {
        ejecutar("Cuotas de inscripción de las cinco disciplinas", PruebasArenaGT::probarCuotas);
        ejecutar("Premios de las cinco disciplinas", PruebasArenaGT::probarPremios);
        ejecutar("Puntos y diferencia de fútbol sala", PruebasArenaGT::probarFutbolSala);
        ejecutar("Fútbol sala con penales", PruebasArenaGT::probarPenales);
        ejecutar("Baloncesto", PruebasArenaGT::probarBaloncesto);
        ejecutar("Voleibol", PruebasArenaGT::probarVoleibol);
        ejecutar("Relevos con descalificación", PruebasArenaGT::probarRelevos);
        ejecutar("Desempates y orden alfabético", PruebasArenaGT::probarDesempates);
        ejecutar("Conflictos y orden cronológico", PruebasArenaGT::probarConflictos);
        ejecutar("Estados y finalización automática", PruebasArenaGT::probarFinalizacion);
        ejecutar("Rechazos sin modificar estado ni ingresos", PruebasArenaGT::probarRechazos);
        ejecutar("Ranking de empresas", PruebasArenaGT::probarRanking);

        System.out.println();
        System.out.println("Pruebas ejecutadas: " + ejecutadas + ", fallidas: " + fallidas);
        if (fallidas > 0) {
            System.exit(1);
        }
    }

    private static void ejecutar(String nombre, Runnable prueba) {
        ejecutadas++;
        try {
            prueba.run();
            System.out.println("[OK]    " + nombre);
        } catch (Throwable error) {
            fallidas++;
            System.out.println("[FALLO] " + nombre + ": " + error.getMessage());
            error.printStackTrace(System.out);
        }
    }

    private static void probarCuotas() {
        ArenaGT sistema = new ArenaGT();
        checkCasiIgual(2400.0, sistema.cotizar("Fútbol sala", 4), "cuota fútbol sala");
        checkCasiIgual(3000.0, sistema.cotizar("Fútbol sala con penales", 4), "cuota penales");
        checkCasiIgual(2000.0, sistema.cotizar("Baloncesto", 4), "cuota baloncesto");
        checkCasiIgual(1800.0, sistema.cotizar("Voleibol", 4), "cuota voleibol");
        checkCasiIgual(900.0, sistema.cotizar("Relevos", 3), "cuota relevos");
        checkCasiIgual(0.0, sistema.getFinanzas().getTotalIngresos(),
                "una cotización no debe generar ingresos");
        debeFallar(() -> sistema.cotizar("Fútbol sala", 0), "cotización con cantidad cero");
        debeFallar(() -> sistema.cotizar("Voleibol", 7), "cotización fuera del máximo");
    }

    private static void probarPremios() {
        verificarPremio("Fútbol sala", 4, 1200.0);
        verificarPremio("Fútbol sala con penales", 4, 1500.0);
        verificarPremio("Baloncesto", 4, 1000.0);
        verificarPremio("Voleibol", 4, 900.0);
        verificarPremio("Relevos", 3, 450.0);
    }

    private static void verificarPremio(String disciplina, int cantidad, double esperado) {
        ArenaGT sistema = sistemaCon(cantidad);
        Torneo torneo = sistema.crearTorneo("PRE", "Premio " + disciplina, disciplina);
        for (Participante participante : participantes(sistema)) {
            torneo.inscribir(participante);
        }
        checkCasiIgual(esperado, torneo.getPremio(), "premio de " + disciplina);
        checkCasiIgual(0.0, sistema.getFinanzas().getTotalPremios(),
                "el premio no se entrega antes de finalizar " + disciplina);
    }

    private static void probarFutbolSala() {
        ArenaGT sistema = sistemaCon(4);
        List<Participante> ps = participantes(sistema);
        Torneo torneo = crearTorneoConInscritos(sistema, "FUT", "Fútbol sala", ps);

        sistema.registrarResultado(eventoEntre(torneo, ps.get(0), ps.get(1)).getCodigo(),
                new EntradaGuion("2", "2"));
        sistema.registrarResultado(eventoEntre(torneo, ps.get(2), ps.get(3)).getCodigo(),
                new EntradaGuion("3", "1"));

        TablaPosiciones tabla = sistema.getTablaPosiciones("FUT");
        checkFila(tabla, ps.get(0), 1, 0, 0, "P1 fútbol");
        checkFila(tabla, ps.get(1), 1, 0, 0, "P2 fútbol");
        checkFila(tabla, ps.get(2), 3, 2, 1, "P3 fútbol");
        checkFila(tabla, ps.get(3), 0, -2, 0, "P4 fútbol");
        check(ps.get(2).equals(primero(tabla).getParticipante()), "P3 debe liderar la tabla de fútbol");
        checkCasiIgual(0.0, sistema.getFinanzas().getTotalIngresos(),
                "la carga directa no debe generar ingresos");
    }

    private static void probarPenales() {
        ArenaGT sistema = sistemaCon(4);
        List<Participante> ps = participantes(sistema);
        Torneo torneo = crearTorneoConInscritos(sistema, "PEN", "Fútbol sala con penales", ps);

        sistema.registrarResultado(eventoEntre(torneo, ps.get(0), ps.get(1)).getCodigo(),
                new EntradaGuion("1", "1", "4", "3"));
        sistema.registrarResultado(eventoEntre(torneo, ps.get(2), ps.get(3)).getCodigo(),
                new EntradaGuion("2", "0"));

        TablaPosiciones tabla = sistema.getTablaPosiciones("PEN");
        checkFila(tabla, ps.get(0), 2, 0, 1, "P1 penales");
        checkFila(tabla, ps.get(1), 1, 0, 0, "P2 penales");
        checkFila(tabla, ps.get(2), 3, 2, 1, "P3 penales");
        checkFila(tabla, ps.get(3), 0, -2, 0, "P4 penales");

        Evento invalido = eventoEntre(torneo, ps.get(0), ps.get(2));
        debeFallar(() -> sistema.registrarResultado(invalido.getCodigo(),
                new EntradaGuion("0", "0", "3", "3")), "penales iguales");
        check(!invalido.estaFinalizado(), "el evento no debe finalizar con penales iguales");
        checkFila(tabla, ps.get(0), 2, 0, 1, "P1 penales tras rechazo");
    }

    private static void probarBaloncesto() {
        ArenaGT sistema = sistemaCon(4);
        List<Participante> ps = participantes(sistema);
        Torneo torneo = crearTorneoConInscritos(sistema, "BAS", "Baloncesto", ps);

        sistema.registrarResultado(eventoEntre(torneo, ps.get(0), ps.get(1)).getCodigo(),
                new EntradaGuion("80", "70"));
        sistema.registrarResultado(eventoEntre(torneo, ps.get(2), ps.get(3)).getCodigo(),
                new EntradaGuion("60", "61"));

        TablaPosiciones tabla = sistema.getTablaPosiciones("BAS");
        checkFila(tabla, ps.get(0), 2, 10, 1, "P1 baloncesto");
        checkFila(tabla, ps.get(1), 1, -10, 0, "P2 baloncesto");
        checkFila(tabla, ps.get(2), 1, -1, 0, "P3 baloncesto");
        checkFila(tabla, ps.get(3), 2, 1, 1, "P4 baloncesto");
        check(ps.get(0).equals(primero(tabla).getParticipante()), "P1 debe liderar por diferencia");

        Evento invalido = eventoEntre(torneo, ps.get(0), ps.get(2));
        debeFallar(() -> sistema.registrarResultado(invalido.getCodigo(),
                new EntradaGuion("50", "50")), "baloncesto empatado");
        check(!invalido.estaFinalizado(), "el evento no debe finalizar con empate de baloncesto");
    }

    private static void probarVoleibol() {
        ArenaGT sistema = sistemaCon(4);
        List<Participante> ps = participantes(sistema);
        Torneo torneo = crearTorneoConInscritos(sistema, "VOL", "Voleibol", ps);

        sistema.registrarResultado(eventoEntre(torneo, ps.get(0), ps.get(1)).getCodigo(),
                new EntradaGuion("3", "0"));
        sistema.registrarResultado(eventoEntre(torneo, ps.get(2), ps.get(3)).getCodigo(),
                new EntradaGuion("3", "2"));

        TablaPosiciones tabla = sistema.getTablaPosiciones("VOL");
        checkFila(tabla, ps.get(0), 3, 3, 1, "P1 voleibol");
        checkFila(tabla, ps.get(1), 0, -3, 0, "P2 voleibol");
        checkFila(tabla, ps.get(2), 2, 1, 1, "P3 voleibol");
        checkFila(tabla, ps.get(3), 1, -1, 0, "P4 voleibol");

        Evento invalido = eventoEntre(torneo, ps.get(0), ps.get(2));
        debeFallar(() -> sistema.registrarResultado(invalido.getCodigo(),
                new EntradaGuion("2", "1")), "ganador de voleibol sin 3 sets");
        check(!invalido.estaFinalizado(), "el evento no debe finalizar con sets inválidos");
    }

    private static void probarRelevos() {
        ArenaGT sistema = sistemaCon(3);
        List<Participante> ps = participantes(sistema);
        Torneo torneo = crearTorneoConInscritos(sistema, "REL", "Relevos", ps);
        Evento carrera = torneo.getEventos().get(0);

        sistema.registrarResultado(carrera.getCodigo(),
                new EntradaGuion("50.1", "D", "52.4"));

        TablaPosiciones tabla = sistema.getTablaPosiciones("REL");
        checkFila(tabla, ps.get(0), 10, 0, 1, "P1 relevos");
        checkFila(tabla, ps.get(1), 0, 0, 0, "P2 descalificado");
        checkFila(tabla, ps.get(2), 8, 0, 0, "P3 relevos");
        check(ps.get(0).equals(primero(tabla).getParticipante()), "el primer lugar debe ser P1");
        check(torneo.estaFinalizado(), "la carrera única debe finalizar el torneo");
        check(ps.get(0).equals(torneo.getCampeon()), "el campeón debe ser P1");
        checkCasiIgual(450.0, sistema.getFinanzas().getTotalPremios(), "premio de relevos");
        checkCasiIgual(0.0, sistema.getFinanzas().getTotalIngresos(), "relevos sin ingresos");

        ArenaGT otro = sistemaCon(3);
        List<Participante> psOtro = participantes(otro);
        Torneo torneoOtro = crearTorneoConInscritos(otro, "REL2", "Relevos", psOtro);
        Evento carreraOtro = torneoOtro.getEventos().get(0);
        debeFallar(() -> otro.registrarResultado(carreraOtro.getCodigo(),
                new EntradaGuion("50.1", "50.1", "52.4")), "tiempos repetidos");
        check(!carreraOtro.estaFinalizado(), "no debe finalizar con tiempos repetidos");
        checkCasiIgual(0.0, otro.getFinanzas().getTotalPremios(),
                "no debe entregarse premio tras un resultado inválido");
    }

    private static void probarDesempates() {
        ArenaGT sistema = ArenaGT.cargarDatosIniciales();

        TablaPosiciones futbol = sistema.getTablaPosiciones("T-FUT");
        checkFila(futbol, sistema.buscarParticipante("P01"), 3, 3, 1, "P01 fútbol inicial");
        checkFila(futbol, sistema.buscarParticipante("P02"), 3, 2, 1, "P02 fútbol inicial");
        check(primero(futbol).getParticipante().getCodigo().equals("P01"),
                "el desempate por diferencia debe poner primero a P01");

        TablaPosiciones penales = sistema.getTablaPosiciones("T-PEN");
        FilaTabla p01 = fila(penales, sistema.buscarParticipante("P01"));
        FilaTabla p03 = fila(penales, sistema.buscarParticipante("P03"));
        FilaTabla p05 = fila(penales, sistema.buscarParticipante("P05"));
        check(p01.getPuntos() == 3 && p01.getVictorias() == 1, "P01 debe tener 3 puntos y 1 victoria");
        check(p03.getPuntos() == 3 && p03.getVictorias() == 1, "P03 debe tener 3 puntos y 1 victoria");
        check(p05.getPuntos() == 3 && p05.getVictorias() == 0, "P05 debe tener 3 puntos y 0 victorias");
        check(posicion(penales, sistema.buscarParticipante("P05"))
                > posicion(penales, sistema.buscarParticipante("P01")),
                "el desempate por victorias debe poner a P05 después de P01");
        check(posicion(penales, sistema.buscarParticipante("P05"))
                > posicion(penales, sistema.buscarParticipante("P03")),
                "el desempate por victorias debe poner a P05 después de P03");

        TablaPosiciones relevos = sistema.getTablaPosiciones("T-REL");
        check("Águilas".equals(primero(relevos).getParticipante().getNombre()),
                "el desempate por nombre debe ordenar alfabéticamente en español");
    }

    private static void probarConflictos() {
        ArenaGT sistema = sistemaCon(4);
        List<Participante> ps = participantes(sistema);
        Torneo torneo = crearTorneoConInscritosSinProgramar(sistema, "CON", "Fútbol sala", ps);

        Evento e12 = eventoEntre(torneo, ps.get(0), ps.get(1));
        Evento e13 = eventoEntre(torneo, ps.get(0), ps.get(2));
        Evento e24 = eventoEntre(torneo, ps.get(1), ps.get(3));
        Evento e34 = eventoEntre(torneo, ps.get(2), ps.get(3));

        LocalDate dia1 = LocalDate.of(2027, 8, 9);
        LocalDate dia2 = LocalDate.of(2027, 8, 10);
        sistema.programarEvento(e12.getCodigo(), dia2, 9, "Sede A");

        debeFallar(() -> sistema.programarEvento(e34.getCodigo(), dia2, 9, "Sede A"),
                "conflicto de sede");
        check(!e34.estaProgramado(), "el evento con conflicto de sede no debe programarse");

        debeFallar(() -> sistema.programarEvento(e13.getCodigo(), dia2, 9, "Sede B"),
                "conflicto de participante");
        check(!e13.estaProgramado(), "el evento con conflicto de participante no debe programarse");

        sistema.programarEvento(e34.getCodigo(), dia2, 9, "Sede B");
        sistema.programarEvento(e24.getCodigo(), dia1, 15, "Sede A");

        Calendario calendario = sistema.calendarioGeneral();
        List<Evento> ordenados = new ArrayList<>();
        calendario.forEach(ordenados::add);
        check(ordenados.size() == 3, "el calendario debe contener los tres eventos programados");
        check(ordenados.get(0).equals(e24), "primero debe ir el evento del día 9");
        check(ordenados.get(1).equals(e12), "luego el evento del día 10 en Sede A");
        check(ordenados.get(2).equals(e34), "luego el evento del día 10 en Sede B");

        Calendario deParticipante = sistema.calendarioParticipante(ps.get(0).getCodigo());
        List<Evento> delParticipante = new ArrayList<>();
        deParticipante.forEach(delParticipante::add);
        check(delParticipante.size() == 1 && delParticipante.get(0).equals(e12),
                "el calendario del participante debe incluir solo eventos programados suyos");
    }

    private static void probarFinalizacion() {
        ArenaGT sistema = sistemaCon(4);
        List<Participante> ps = participantes(sistema);
        Torneo torneo = crearTorneoConInscritos(sistema, "FIN", "Fútbol sala", ps);

        check(torneo.getEventos().size() == 6, "deben generarse seis encuentros");
        check(torneo.getEstado().name().equals("EN_JUEGO"), "el torneo debe estar en juego");
        debeFallar(torneo::cerrarInscripciones, "cerrar inscripciones dos veces");

        for (Evento evento : new ArrayList<>(torneo.getEventos())) {
            sistema.registrarResultado(evento.getCodigo(), new EntradaGuion("1", "0"));
        }

        check(torneo.estaFinalizado(), "el torneo debe finalizar cuando todos los eventos tienen resultado");
        check(torneo.getCampeon() != null, "el torneo finalizado debe tener campeón");
        checkCasiIgual(1200.0, sistema.getFinanzas().getTotalPremios(),
                "el premio debe entregarse una sola vez al finalizar");
        checkCasiIgual(0.0, sistema.getFinanzas().getTotalIngresos(),
                "la carga directa no debe generar ingresos");

        Evento primero = torneo.getEventos().get(0);
        debeFallar(() -> sistema.registrarResultado(primero.getCodigo(),
                new EntradaGuion("2", "0")), "resultado sobre evento finalizado");
        debeFallar(() -> sistema.programarEvento(primero.getCodigo(),
                LocalDate.of(2027, 9, 1), 10, "Sede X"), "reprogramar evento finalizado");
    }

    private static void probarRechazos() {
        ArenaGT sistema = sistemaCon(5);
        sistema.registrarParticipante("P06", "Participante 6", "Empresa 1");
        List<Participante> ps = new ArrayList<>(sistema.getParticipantes());
        Torneo torneo = sistema.crearTorneo("REC", "Torneo de rechazos", "Fútbol sala");

        double cuota = sistema.inscribir("REC", ps.get(0).getCodigo());
        checkCasiIgual(600.0, cuota, "cuota cobrada en inscripción válida");
        sistema.inscribir("REC", ps.get(1).getCodigo());
        checkCasiIgual(1200.0, sistema.getFinanzas().getTotalIngresos(), "ingresos tras dos inscripciones");

        int inscritosAntes = torneo.getParticipantes().size();
        debeFallar(() -> sistema.inscribir("REC", ps.get(0).getCodigo()), "inscripción duplicada");
        check(torneo.getParticipantes().size() == inscritosAntes,
                "la inscripción duplicada no debe cambiar los inscritos");
        checkCasiIgual(1200.0, sistema.getFinanzas().getTotalIngresos(),
                "la inscripción duplicada no debe cambiar los ingresos");

        debeFallar(() -> sistema.inscribir("REC", ps.get(5).getCodigo()),
                "tercer participante de la misma empresa");
        checkCasiIgual(1200.0, sistema.getFinanzas().getTotalIngresos(),
                "el rechazo de empresa no debe cambiar los ingresos");

        sistema.cotizar("Baloncesto", 6);
        checkCasiIgual(1200.0, sistema.getFinanzas().getTotalIngresos(),
                "la cotización no debe modificar los ingresos");

        debeFallar(() -> sistema.cerrarInscripciones("REC"), "cierre sin mínimo de participantes");
        check(torneo.getEstado().name().equals("EN_INSCRIPCION"),
                "el cierre inválido no debe cambiar el estado del torneo");

        debeFallar(() -> sistema.registrarParticipante("p01", "Duplicado", "Otra"),
                "código de participante repetido");
        debeFallar(() -> sistema.crearTorneo("rec", "Duplicado", "Voleibol"),
                "código de torneo repetido");
    }

    private static void probarRanking() {
        ArenaGT sistema = ArenaGT.cargarDatosIniciales();
        Finanzas finanzas = sistema.getFinanzas();
        checkCasiIgual(0.0, finanzas.getTotalIngresos(), "los datos iniciales no generan ingresos");
        checkCasiIgual(0.0, finanzas.getTotalPremios(), "los datos iniciales no generan premios");

        int alfa = 0;
        int beta = 0;
        int gamma = 0;
        for (arena.modelo.FilaRanking fila : sistema.getRankingEmpresas()) {
            if (fila.getEmpresa().getNombre().equals("Alfa")) {
                alfa = fila.getPuntos();
            } else if (fila.getEmpresa().getNombre().equals("Beta")) {
                beta = fila.getPuntos();
            } else if (fila.getEmpresa().getNombre().equals("Gamma")) {
                gamma = fila.getPuntos();
            }
        }
        check(alfa == 17, "Alfa debe sumar 17 puntos, fue " + alfa);
        check(beta == 10, "Beta debe sumar 10 puntos, fue " + beta);
        check(gamma == 3, "Gamma debe sumar 3 puntos, fue " + gamma);
    }

    // ------------------------------------------------------------------
    // Utilidades de las pruebas
    // ------------------------------------------------------------------

    private static ArenaGT sistemaCon(int cantidad) {
        ArenaGT sistema = new ArenaGT();
        for (int i = 1; i <= cantidad; i++) {
            sistema.registrarParticipante(String.format("P%02d", i),
                    "Participante " + i, "Empresa " + (((i - 1) / 2) + 1));
        }
        return sistema;
    }

    private static List<Participante> participantes(ArenaGT sistema) {
        return new ArrayList<>(sistema.getParticipantes());
    }

    private static Torneo crearTorneoConInscritos(ArenaGT sistema, String codigo,
            String disciplina, List<Participante> ps) {
        Torneo torneo = crearTorneoConInscritosSinProgramar(sistema, codigo, disciplina, ps);
        for (Evento evento : torneo.getEventos()) {
            sistema.programarEvento(evento.getCodigo(),
                    LocalDate.of(2027, 6, 1).plusDays(turno++), 9,
                    "Sede " + (turno % 4));
        }
        return torneo;
    }

    private static Torneo crearTorneoConInscritosSinProgramar(ArenaGT sistema, String codigo,
            String disciplina, List<Participante> ps) {
        Torneo torneo = sistema.crearTorneo(codigo, "Torneo " + codigo, disciplina);
        for (Participante participante : ps) {
            torneo.inscribir(participante);
        }
        torneo.cerrarInscripciones();
        return torneo;
    }

    private static Evento eventoEntre(Torneo torneo, Participante a, Participante b) {
        for (Evento evento : torneo.getEventos()) {
            if (evento.getParticipantes().contains(a) && evento.getParticipantes().contains(b)) {
                return evento;
            }
        }
        throw new AssertionError("No se encontró el evento entre "
                + a.getNombre() + " y " + b.getNombre());
    }

    private static FilaTabla fila(TablaPosiciones tabla, Participante participante) {
        for (FilaTabla fila : tabla) {
            if (fila.getParticipante().equals(participante)) {
                return fila;
            }
        }
        throw new AssertionError("No se encontró la fila de " + participante.getNombre());
    }

    private static void checkFila(TablaPosiciones tabla, Participante participante,
            int puntos, int diferencia, int victorias, String contexto) {
        FilaTabla fila = fila(tabla, participante);
        check(fila.getPuntos() == puntos,
                contexto + ": puntos esperados " + puntos + ", obtenidos " + fila.getPuntos());
        check(fila.getDiferencia() == diferencia,
                contexto + ": diferencia esperada " + diferencia + ", obtenida " + fila.getDiferencia());
        check(fila.getVictorias() == victorias,
                contexto + ": victorias esperadas " + victorias + ", obtenidas " + fila.getVictorias());
    }

    private static FilaTabla primero(TablaPosiciones tabla) {
        for (FilaTabla fila : tabla) {
            return fila;
        }
        throw new AssertionError("La tabla está vacía.");
    }

    private static int posicion(TablaPosiciones tabla, Participante participante) {
        int posicion = 0;
        for (FilaTabla fila : tabla) {
            if (fila.getParticipante().equals(participante)) {
                return posicion;
            }
            posicion++;
        }
        throw new AssertionError("No se encontró a " + participante.getNombre());
    }

    private static void debeFallar(Runnable accion, String contexto) {
        try {
            accion.run();
        } catch (ReglaNegocioException ex) {
            return;
        }
        throw new AssertionError(contexto + ": se esperaba una operación rechazada.");
    }

    private static void check(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    private static void checkCasiIgual(double esperado, double obtenido, String mensaje) {
        if (Math.abs(esperado - obtenido) > 0.0001) {
            throw new AssertionError(mensaje + ": esperado " + esperado + ", obtenido " + obtenido);
        }
    }
}
