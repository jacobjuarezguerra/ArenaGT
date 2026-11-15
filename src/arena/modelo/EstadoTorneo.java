package arena.modelo;

public enum EstadoTorneo {
    EN_INSCRIPCION("En inscripción"),
    EN_JUEGO("En juego"),
    FINALIZADO("Finalizado");

    private final String etiqueta;

    EstadoTorneo(String etiqueta) {
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
