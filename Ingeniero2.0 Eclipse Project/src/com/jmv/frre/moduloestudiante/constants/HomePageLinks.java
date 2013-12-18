package com.jmv.frre.moduloestudiante.constants;

/**
 * Created by Cleo on 12/1/13.
 */
public enum HomePageLinks {

    AVISOS("menuAvisosAlumnos.asp", true),
    EXAMENES("examenes.asp", true),
    ESTADO_ACADEMICO("estadoAcademico.asp", false),
    MATERIAS_DEL_PLAN("materiasPlan.asp", false),
    INSCRIPCION_A_EXAMEN("materiasExamen.asp", true),
    CAMBIO_DE_CONTRASENA("cambioPassword.asp", false),
    INSCRIPCION_A_CURSADO("materiasCursado.asp", false),
    CORRELATIVIDAD_PARA_RENDIR("correlatividadExamen.asp", false),
    CORRELATIVIDAD_PARA_CURSAR("correlatividadCursado.asp", false),
    CURSADO_NOTAS_ENCUESTAS("notasParciales.asp", false),
    SALIR("loginAlumno.asp?refrescar", false);

    private String link;
    private boolean enabled;

    HomePageLinks(String link, boolean enabled) {
        this.link = link;
        this.enabled = enabled;
    }

    public String getLink() {
        return link;
    }
    
    public boolean isEnabled() {
		return enabled;
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
