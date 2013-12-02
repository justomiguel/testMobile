package com.jmv.frre.moduloestudiante.constants;

/**
 * Created by Cleo on 12/1/13.
 */
public enum HomePageLinks {

    AVISOS("Avisos"),
    EXAMENES("Exámenes"),
    ESTADO_ACADEMICO("Estado académico"),
    MATERIAS_DEL_PLAN("Materias del plan"),
    INSCRIPCION_A_EXAMEN("Inscripción a examen"),
    CAMBIO_DE_CONTRASENA("Cambio de Contraseña"),
    INSCRIPCION_A_CURSADO("Inscripción a cursado"),
    CORRELATIVIDAD_PARA_RENDIR("Correlatividad para rendir"),
    CORRELATIVIDAD_PARA_CURSAR("Correlatividad para cursar"),
    CURSADO_NOTAS_ENCUESTAS("Cursado / Notas de parciales / Encuestas");

    private String link;

    HomePageLinks(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public static HomePageLinks getLinkByValue(String value){
        HomePageLinks[] links = HomePageLinks.values();
        for (HomePageLinks link : links){
            if (link.getLink().equalsIgnoreCase(value)){
                return link;
            }
        }
        return null;
    }
}
