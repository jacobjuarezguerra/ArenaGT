package arena.modelo;

/**
 * Puerto de entrada usado por las disciplinas para solicitar los datos
 * de un resultado. La consola y las pruebas proporcionan implementaciones.
 */
public interface Entrada {

    String leerTexto(String mensaje);

    int leerEntero(String mensaje);

    double leerDecimal(String mensaje);
}
