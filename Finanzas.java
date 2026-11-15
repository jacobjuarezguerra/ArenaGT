package arena.torneo;

import arena.disciplinas.Disciplina;
import arena.modelo.ReglaNegocioException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registra únicamente ingresos por inscripciones efectivas y premios
 * entregados. Una cotización nunca llama a esta clase.
 */
public final class Finanzas {

    public static final class Premio {

        private final String codigoTorneo;
        private final String nombreTorneo;
        private final double monto;

        public Premio(String codigoTorneo, String nombreTorneo, double monto) {
            this.codigoTorneo = codigoTorneo;
            this.nombreTorneo = nombreTorneo;
            this.monto = monto;
        }

        public String getCodigoTorneo() {
            return codigoTorneo;
        }

        public String getNombreTorneo() {
            return nombreTorneo;
        }

        public double getMonto() {
            return monto;
        }
    }

    private final Map<String, Double> ingresosPorDisciplina = new LinkedHashMap<>();
    private final List<Premio> premios = new ArrayList<>();
    private double totalIngresos;

    public void registrarIngreso(Disciplina disciplina, double monto) {
        if (disciplina == null) {
            throw new ReglaNegocioException("La disciplina es obligatoria.");
        }
        if (monto <= 0) {
            throw new ReglaNegocioException("El monto de ingreso debe ser positivo.");
        }
        ingresosPorDisciplina.merge(disciplina.getNombre(), monto, Double::sum);
        totalIngresos += monto;
    }

    public boolean registrarPremio(Torneo torneo) {
        if (torneo == null) {
            throw new ReglaNegocioException("El torneo es obligatorio.");
        }
        if (!torneo.estaFinalizado()) {
            throw new ReglaNegocioException("Solo se entrega premio a un torneo finalizado.");
        }
        for (Premio premio : premios) {
            if (premio.getCodigoTorneo().equalsIgnoreCase(torneo.getCodigo())) {
                return false;
            }
        }
        double monto = torneo.getPremio();
        premios.add(new Premio(torneo.getCodigo(), torneo.getNombre(), monto));
        return true;
    }

    public double getIngresoDe(String disciplina) {
        Double monto = ingresosPorDisciplina.get(disciplina);
        return monto == null ? 0.0 : monto;
    }

    public double getTotalIngresos() {
        return totalIngresos;
    }

    public double getTotalPremios() {
        double total = 0.0;
        for (Premio premio : premios) {
            total += premio.getMonto();
        }
        return total;
    }

    public Map<String, Double> getIngresosPorDisciplina() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(ingresosPorDisciplina));
    }

    public List<Premio> getPremios() {
        return Collections.unmodifiableList(premios);
    }
}
