package com.jmv.frre.moduloestudiante.constants;

import com.jmv.frre.moduloestudiante.R;

/**
 * Created by Cleo on 12/2/13.
 */
public enum HomePageImages {

    AVISOS(HomePageLinks.AVISOS, R.drawable.messages_list),
    EXAMENES(HomePageLinks.EXAMENES,R.drawable.test_exam),
    ESTADO_ACADEMICO(HomePageLinks.ESTADO_ACADEMICO, R.drawable.academic_status),
    MATERIAS_DEL_PLAN(HomePageLinks.MATERIAS_DEL_PLAN, R.drawable.lista),
    INSCRIPCION_A_EXAMEN(HomePageLinks.INSCRIPCION_A_EXAMEN, R.drawable.examenes),
    CAMBIO_DE_CONTRASENA(HomePageLinks.CAMBIO_DE_CONTRASENA, R.drawable.password),
    INSCRIPCION_A_CURSADO(HomePageLinks.INSCRIPCION_A_CURSADO, R.drawable.student_img),
    CORRELATIVIDAD_PARA_RENDIR(HomePageLinks.CORRELATIVIDAD_PARA_RENDIR, R.drawable.correlatividades),
    CORRELATIVIDAD_PARA_CURSAR(HomePageLinks.CORRELATIVIDAD_PARA_CURSAR, R.drawable.tickker),
    CURSADO_NOTAS_ENCUESTAS(HomePageLinks.CURSADO_NOTAS_ENCUESTAS, R.drawable.notas_draw),
    SALIR(HomePageLinks.SALIR, R.drawable.logout);

    private final int drawable;
    private HomePageLinks link;

    HomePageImages(HomePageLinks link, int drawable) {
        this.link = link;
        this.drawable = drawable;
    }

    public int getDrawable() {
        return drawable;
    }

    public HomePageLinks getLink() {
        return link;
    }

    public static int getLinkByValue(HomePageLinks value){
        HomePageImages[] links = HomePageImages.values();
        for (HomePageImages link : links){
            if (link.getLink().equals(value)){
                return link.getDrawable();
            }
        }
        return 0;
    }
}
