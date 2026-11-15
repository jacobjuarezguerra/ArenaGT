package arena.modelo;

/**
 * Excepción lanzada cuando una operación no cumple una regla del negocio.
 * El estado del sistema no debe modificarse antes de lanzarla.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
