package arena.ui;

import arena.modelo.Entrada;

import java.util.Scanner;

/**
 * Entrada de consola robusta: los formatos incorrectos se vuelven a pedir
 * y nunca terminan el programa.
 */
public final class Consola implements Entrada {

    private final Scanner scanner;

    public Consola() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine().trim();
    }

    @Override
    public int leerEntero(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje);
            try {
                return Integer.parseInt(valor.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Entrada inválida: se esperaba un número entero.");
            }
        }
    }

    @Override
    public double leerDecimal(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje);
            try {
                return Double.parseDouble(valor.trim().replace(',', '.'));
            } catch (NumberFormatException ex) {
                System.out.println("Entrada inválida: se esperaba un número.");
            }
        }
    }

    public void cerrar() {
        scanner.close();
    }
}
