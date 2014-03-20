package com.jmv.frre.moduloestudiante.constants;

/**
 * Created by Cleo on 12/11/13.
 */
public enum Notas {

    UNO("uno", 1),
    DOS("dos", 2),
    TRES("tres", 3),
    CUATRO("cuatro", 4),
    CINCO("cinco", 5),
    SEIS("seis", 6),
    SIETE("siete", 7),
    OCHO("ocho", 8),
    NUEVE("nueve", 9),
    DIEZ("diez", 10);

    private final int value;

    private final String nota;

    Notas(String nota, int value) {
        this.nota = nota;
        this.value = value;
    }

    public String getNota() {
        return nota;
    }

    public static int getNotaByValue(String value){
        Notas[] links = Notas.values();
        for (Notas link : links){
            if (link.getNota().equalsIgnoreCase(value.trim())){
                return link.value;
            }
        }
        return 0;
    }

}
