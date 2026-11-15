package arena;

import arena.sistema.ArenaGT;
import arena.ui.Consola;
import arena.ui.Menu;

public final class Principal {

    private Principal() {
    }

    public static void main(String[] args) {
        ArenaGT sistema = ArenaGT.cargarDatosIniciales();
        Consola consola = new Consola();
        System.out.println("Bienvenido a ArenaGT.");
        new Menu(sistema, consola).ejecutar();
        consola.cerrar();
        System.out.println("Hasta luego.");
    }
}
