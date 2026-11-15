package arena.modelo;

import java.util.Objects;

public final class Participante {

    private final String codigo;
    private final String nombre;
    private final Empresa empresa;

    public Participante(String codigo, String nombre, Empresa empresa) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ReglaNegocioException("El código del participante no puede estar vacío.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre del participante no puede estar vacío.");
        }
        if (empresa == null) {
            throw new ReglaNegocioException("El participante debe pertenecer a una empresa.");
        }
        this.codigo = codigo.trim();
        this.nombre = nombre.trim();
        this.empresa = empresa;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Participante)) {
            return false;
        }
        Participante participante = (Participante) otro;
        return codigo.equalsIgnoreCase(participante.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo.toUpperCase(java.util.Locale.ROOT));
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + empresa.getNombre() + ")";
    }
}
