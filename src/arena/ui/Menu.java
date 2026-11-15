package arena.ui;

import arena.disciplinas.Disciplina;
import arena.eventos.Calendario;
import arena.eventos.Evento;
import arena.modelo.Empresa;
import arena.modelo.EstadoTorneo;
import arena.modelo.FilaRanking;
import arena.modelo.FilaTabla;
import arena.modelo.Participante;
import arena.modelo.ReglaNegocioException;
import arena.modelo.Resultado;
import arena.torneo.Finanzas;
import arena.torneo.TablaPosiciones;
import arena.torneo.Torneo;
import arena.sistema.ArenaGT;
import arena.modelo.Entrada;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class Menu {

    private final ArenaGT sistema;
    private final Entrada entrada;

    public Menu(ArenaGT sistema, Entrada entrada) {
        this.sistema = sistema;
        this.entrada = entrada;
    }

    public void ejecutar() {
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            int opcion = entrada.leerEntero("Opción: ");
            if (opcion == 0) {
                salir = true;
                continue;
            }
            try {
                procesar(opcion);
            } catch (ReglaNegocioException ex) {
                System.out.println("Operación rechazada: " + ex.getMessage());
            } catch (RuntimeException ex) {
                String mensaje = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
                System.out.println("Operación rechazada: " + mensaje);
            }
            System.out.println();
        }
    }

    private void mostrarMenu() {
        System.out.println("==============================================");
        System.out.println(" ArenaGT - Gestión de copas deportivas");
        System.out.println("==============================================");
        System.out.println(" 1) Cotizar inscripción");
        System.out.println(" 2) Inscribir participante en un torneo");
        System.out.println(" 3) Registrar participante");
        System.out.println(" 4) Crear torneo");
        System.out.println(" 5) Cerrar inscripciones");
        System.out.println(" 6) Programar o reprogramar evento");
        System.out.println(" 7) Registrar resultado");
        System.out.println(" 8) Consultar torneos");
        System.out.println(" 9) Consultar participantes");
        System.out.println("10) Calendario general");
        System.out.println("11) Calendario de un torneo");
        System.out.println("12) Calendario de un participante");
        System.out.println("13) Tabla de posiciones");
        System.out.println("14) Reporte de ingresos y premios");
        System.out.println("15) Ranking de empresas");
        System.out.println(" 0) Salir");
        System.out.println("----------------------------------------------");
    }

    private void procesar(int opcion) {
        switch (opcion) {
            case 1:
                cotizar();
                break;
            case 2:
                inscribir();
                break;
            case 3:
                registrarParticipante();
                break;
            case 4:
                crearTorneo();
                break;
            case 5:
                cerrarInscripciones();
                break;
            case 6:
                programarEvento();
                break;
            case 7:
                registrarResultado();
                break;
            case 8:
                consultarTorneos();
                break;
            case 9:
                listarParticipantes();
                break;
            case 10:
                imprimirCalendario(sistema.calendarioGeneral());
                break;
            case 11:
                imprimirCalendario(sistema.calendarioTorneo(
                        entrada.leerTexto("Código del torneo: ")));
                break;
            case 12:
                imprimirCalendario(sistema.calendarioParticipante(
                        entrada.leerTexto("Código del participante: ")));
                break;
            case 13:
                mostrarTabla();
                break;
            case 14:
                mostrarFinanzas();
                break;
            case 15:
                mostrarRanking();
                break;
            default:
                System.out.println("Opción inválida. Elija una opción del menú.");
        }
    }

    private void cotizar() {
        Disciplina disciplina = elegirDisciplina();
        int cantidad = entrada.leerEntero("Cantidad de participantes: ");
        double total = sistema.cotizar(disciplina.getNombre(), cantidad);
        System.out.println(String.format(Locale.US,
                "Cotización para %s (%d participantes): Q%.2f", disciplina.getNombre(), cantidad, total));
        System.out.println("La cotización no inscribe ni cobra.");
    }

    private void inscribir() {
        listarTorneosEnInscripcion();
        listarParticipantes();
        String codigoTorneo = entrada.leerTexto("Código del torneo: ");
        String codigoParticipante = entrada.leerTexto("Código del participante: ");
        double cuota = sistema.inscribir(codigoTorneo, codigoParticipante);
        System.out.println(String.format(Locale.US,
                "Inscripción realizada. Se cobró Q%.2f.", cuota));
    }

    private void registrarParticipante() {
        String codigo = entrada.leerTexto("Código del participante: ");
        String nombre = entrada.leerTexto("Nombre del participante: ");
        String empresa = entrada.leerTexto("Empresa: ");
        Participante participante = sistema.registrarParticipante(codigo, nombre, empresa);
        System.out.println("Participante registrado: " + participante);
    }

    private void crearTorneo() {
        String codigo = entrada.leerTexto("Código del torneo: ");
        String nombre = entrada.leerTexto("Nombre del torneo: ");
        Disciplina disciplina = elegirDisciplina();
        Torneo torneo = sistema.crearTorneo(codigo, nombre, disciplina.getNombre());
        System.out.println("Torneo creado: " + torneo);
    }

    private void cerrarInscripciones() {
        listarTorneosEnInscripcion();
        String codigo = entrada.leerTexto("Código del torneo a cerrar: ");
        sistema.cerrarInscripciones(codigo);
        Torneo torneo = sistema.buscarTorneo(codigo);
        System.out.println("Inscripciones cerradas. Se generaron "
                + torneo.getEventos().size() + " eventos para " + torneo.getNombre() + ".");
    }

    private void programarEvento() {
        listarEventosNoFinalizados();
        String codigo = entrada.leerTexto("Código del evento: ");
        Evento evento = sistema.buscarEvento(codigo);
        System.out.println("Evento actual: " + evento);
        LocalDate fecha = leerFecha("Fecha (AAAA-MM-DD): ");
        int hora = entrada.leerEntero("Hora de inicio (0 a 23): ");
        String sede = entrada.leerTexto("Sede: ");
        sistema.programarEvento(codigo, fecha, hora, sede);
        System.out.println("Evento actualizado: " + sistema.buscarEvento(codigo));
    }

    private void registrarResultado() {
        listarEventosNoFinalizados();
        String codigo = entrada.leerTexto("Código del evento: ");
        Resultado resultado = sistema.registrarResultado(codigo, entrada);
        System.out.println("Resultado registrado: " + resultado.descripcion());
        Evento evento = sistema.buscarEvento(codigo);
        if (evento.getTorneo().estaFinalizado()) {
            System.out.println("El torneo " + evento.getTorneo().getNombre()
                    + " finalizó automáticamente. Campeón: "
                    + evento.getTorneo().getCampeon().getNombre() + ".");
        }
    }

    private void consultarTorneos() {
        if (sistema.getTorneos().isEmpty()) {
            System.out.println("No hay torneos registrados.");
            return;
        }
        for (Torneo torneo : sistema.getTorneos()) {
            System.out.println("----------------------------------------------");
            System.out.println(torneo.getCodigo() + " - " + torneo.getNombre());
            System.out.println("  Disciplina: " + torneo.getDisciplina().getNombre());
            System.out.println("  Estado: " + torneo.getEstado());
            System.out.println("  Participantes (" + torneo.getParticipantes().size() + "): "
                    + nombres(torneo));
            System.out.println("  Eventos: " + torneo.contarEventosFinalizados()
                    + "/" + torneo.getEventos().size() + " finalizados");
            System.out.println(String.format(Locale.US,
                    "  Premio del primer lugar: Q%.2f", torneo.getPremio()));
            for (Evento evento : torneo.getEventos()) {
                System.out.println("    " + evento);
            }
            if (torneo.estaFinalizado()) {
                System.out.println("  Campeón: " + torneo.getCampeon().getNombre());
            }
        }
    }

    private void mostrarTabla() {
        String codigo = entrada.leerTexto("Código del torneo: ");
        Torneo torneo = sistema.buscarTorneo(codigo);
        TablaPosiciones tabla = sistema.getTablaPosiciones(codigo);
        System.out.println("Tabla de posiciones: " + torneo.getNombre());
        System.out.println("Pos | Participante        | Empresa | Pts | Dif | Victorias");
        int posicion = 1;
        for (FilaTabla fila : tabla) {
            System.out.println(String.format(Locale.US, "%3d | %-19s | %-7s | %3d | %+3d | %9d",
                    posicion++, fila.getParticipante().getNombre(),
                    fila.getParticipante().getEmpresa().getNombre(),
                    fila.getPuntos(), fila.getDiferencia(), fila.getVictorias()));
        }
        if (torneo.estaFinalizado()) {
            System.out.println("Campeón: " + torneo.getCampeon().getNombre());
        }
    }

    private void mostrarFinanzas() {
        Finanzas finanzas = sistema.getFinanzas();
        System.out.println("Reporte de ingresos por inscripciones");
        for (Disciplina disciplina : sistema.getDisciplinas()) {
            System.out.println(String.format(Locale.US, "  %-24s Q%.2f",
                    disciplina.getNombre(), finanzas.getIngresoDe(disciplina.getNombre())));
        }
        System.out.println(String.format(Locale.US, "  %-24s Q%.2f", "Total", finanzas.getTotalIngresos()));
        System.out.println(String.format(Locale.US, "Premios entregados: Q%.2f", finanzas.getTotalPremios()));
        for (Finanzas.Premio premio : finanzas.getPremios()) {
            System.out.println(String.format(Locale.US, "  %s - %s: Q%.2f",
                    premio.getCodigoTorneo(), premio.getNombreTorneo(), premio.getMonto()));
        }
    }

    private void mostrarRanking() {
        System.out.println("Ranking de empresas");
        System.out.println("Pos | Empresa | Puntos");
        int posicion = 1;
        for (FilaRanking fila : sistema.getRankingEmpresas()) {
            System.out.println(String.format(Locale.US, "%3d | %-20s | %d",
                    posicion++, fila.getEmpresa().getNombre(), fila.getPuntos()));
        }
    }

    private void listarParticipantes() {
        if (sistema.getParticipantes().isEmpty()) {
            System.out.println("No hay participantes registrados.");
            return;
        }
        System.out.println("Participantes:");
        for (Participante participante : sistema.getParticipantes()) {
            System.out.println("  " + participante);
        }
    }

    private void listarTorneosEnInscripcion() {
        boolean hay = false;
        System.out.println("Torneos en inscripción:");
        for (Torneo torneo : sistema.getTorneos()) {
            if (torneo.getEstado() == EstadoTorneo.EN_INSCRIPCION) {
                System.out.println("  " + torneo.getCodigo() + " - " + torneo.getNombre()
                        + " (" + torneo.getDisciplina().getNombre() + ")");
                hay = true;
            }
        }
        if (!hay) {
            System.out.println("  (ninguno)");
        }
    }

    private void listarEventosNoFinalizados() {
        boolean hay = false;
        System.out.println("Eventos no finalizados:");
        for (Torneo torneo : sistema.getTorneos()) {
            for (Evento evento : torneo.getEventos()) {
                if (!evento.estaFinalizado()) {
                    System.out.println("  " + evento.getCodigo() + " | " + torneo.getNombre()
                            + " | " + evento.getDescripcion() + " | " + evento.getHorarioTexto()
                            + " | " + evento.getEstado());
                    hay = true;
                }
            }
        }
        if (!hay) {
            System.out.println("  (ninguno)");
        }
    }

    private void imprimirCalendario(Calendario calendario) {
        if (calendario.estaVacio()) {
            System.out.println("No hay eventos programados.");
            return;
        }
        for (Evento evento : calendario) {
            System.out.println(String.format("%s %02d:00 | %-20s | %-20s | %-8s | %s",
                    evento.getFecha(), evento.getHora(), evento.getSede(),
                    evento.getTorneo().getNombre(), evento.getTipoEvento(),
                    evento.getDescripcion()));
        }
    }

    private Disciplina elegirDisciplina() {
        System.out.println("Disciplinas:");
        int indice = 1;
        for (Disciplina disciplina : sistema.getDisciplinas()) {
            System.out.println(String.format(Locale.US, "  %d) %s - cuota Q%.2f, %d a %d participantes",
                    indice++, disciplina.getNombre(), disciplina.getCuota(),
                    disciplina.getMinParticipantes(), disciplina.getMaxParticipantes()));
        }
        int opcion = entrada.leerEntero("Elija una disciplina: ");
        if (opcion < 1 || opcion > sistema.getDisciplinas().size()) {
            throw new ReglaNegocioException("Opción de disciplina inválida.");
        }
        return sistema.getDisciplinas().get(opcion - 1);
    }

    private LocalDate leerFecha(String mensaje) {
        String valor = entrada.leerTexto(mensaje);
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException ex) {
            throw new ReglaNegocioException("Fecha inválida. Use el formato AAAA-MM-DD.");
        }
    }

    private String nombres(Torneo torneo) {
        StringBuilder texto = new StringBuilder();
        for (Participante participante : torneo.getParticipantes()) {
            if (texto.length() > 0) {
                texto.append(", ");
            }
            texto.append(participante.getNombre());
        }
        return texto.toString();
    }
}
