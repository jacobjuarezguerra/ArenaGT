package arena.modelo;

public final class FilaRanking implements Comparable<FilaRanking> {

    private final Empresa empresa;
    private final int puntos;

    public FilaRanking(Empresa empresa, int puntos) {
        this.empresa = empresa;
        this.puntos = puntos;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public int getPuntos() {
        return puntos;
    }

    @Override
    public int compareTo(FilaRanking otra) {
        int comparacion = Integer.compare(otra.puntos, this.puntos);
        if (comparacion != 0) {
            return comparacion;
        }
        return ComparadorTexto.comparar(
                this.empresa.getNombre(), otra.empresa.getNombre());
    }
}
