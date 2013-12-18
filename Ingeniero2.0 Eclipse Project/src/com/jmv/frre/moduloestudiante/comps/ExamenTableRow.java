package com.jmv.frre.moduloestudiante.comps;

import android.content.Context;

import com.jmv.frre.moduloestudiante.ExamenIns;

public class ExamenTableRow extends CustomTableRow {

	private ExamenIns examen;

	public ExamenTableRow(Context context, ExamenIns examen) {
		super(context);
		this.examen = examen;
	}

	public ExamenIns getExamen() {
		return examen;
	}

	public void setExamen(ExamenIns examen) {
		this.examen = examen;
	}
	
	
}
