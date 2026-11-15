package arena.modelo;

public enum EstadoEvento {
    SIN_PROGRAMAR("Sin programar"),
    PROGRAMADO("Programado"),
    FINALIZADO("Finalizado");

    private final String etiqueta;

    EstadoEvento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
