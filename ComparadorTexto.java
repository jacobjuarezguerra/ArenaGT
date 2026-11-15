package arena.modelo;

import java.text.Collator;
import java.util.Locale;

/**
 * Comparación alfabética en español, para que nombres con acentos se
 * ordenen como espera un usuario y no por su código Unicode.
 */
public final class ComparadorTexto {

    private ComparadorTexto() {
    }

    public static int comparar(String a, String b) {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("es-GT"));
        collator.setStrength(Collator.PRIMARY);
        return collator.compare(a, b);
    }
}
