package arena.modelo;

import java.util.Locale;
import java.util.Objects;

public final class Empresa {

    private final String nombre;

    public Empresa(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre de la empresa no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Empresa)) {
            return false;
        }
        Empresa empresa = (Empresa) otro;
        return nombre.equalsIgnoreCase(empresa.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre.toLowerCase(Locale.ROOT));
    }

    @Override
    public String toString() {
        return nombre;
    }
}
