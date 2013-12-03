package com.jmv.frre.moduloestudiante.constants;

/**
 * Created by Cleo on 12/1/13.
 */
public enum HomePageLinks {

    AVISOS("menuAvisosAlumnos.asp"),
    EXAMENES("examenes.asp"),
    ESTADO_ACADEMICO("estadoAcademico.asp"),
    MATERIAS_DEL_PLAN("materiasPlan.asp"),
    INSCRIPCION_A_EXAMEN("materiasExamen.asp"),
    CAMBIO_DE_CONTRASENA("cambioPassword.asp"),
    INSCRIPCION_A_CURSADO("materiasCursado.asp"),
    CORRELATIVIDAD_PARA_RENDIR("correlatividadExamen.asp"),
    CORRELATIVIDAD_PARA_CURSAR("correlatividadCursado.asp"),
    CURSADO_NOTAS_ENCUESTAS("notasParciales.asp"),
    SALIR("loginAlumno.asp?refrescar");

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
