package arena.modelo;

public final class FilaTabla implements Comparable<FilaTabla> {

    private final Participante participante;
    private final int puntos;
    private final int diferencia;
    private final int victorias;

    public FilaTabla(Participante participante, int puntos, int diferencia, int victorias) {
        this.participante = participante;
        this.puntos = puntos;
        this.diferencia = diferencia;
        this.victorias = victorias;
    }

    public Participante getParticipante() {
        return participante;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getDiferencia() {
        return diferencia;
    }

    public int getVictorias() {
        return victorias;
    }

    @Override
    public int compareTo(FilaTabla otra) {
        int comparacion = Integer.compare(otra.puntos, this.puntos);
        if (comparacion != 0) {
            return comparacion;
        }
        comparacion = Integer.compare(otra.diferencia, this.diferencia);
        if (comparacion != 0) {
            return comparacion;
        }
        comparacion = Integer.compare(otra.victorias, this.victorias);
        if (comparacion != 0) {
            return comparacion;
        }
        comparacion = ComparadorTexto.comparar(
                this.participante.getNombre(), otra.participante.getNombre());
        if (comparacion != 0) {
            return comparacion;
        }
        return this.participante.getCodigo().compareToIgnoreCase(otra.participante.getCodigo());
    }
}
