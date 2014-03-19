package com.jmv.frre.moduloestudiante.comps;

import android.content.Context;
import android.widget.TableRow;


public class NotaTableRow extends CustomTableRow {

	private int nota;

	public NotaTableRow(Context context, int nota) {
		super(context);
		this.nota = nota;
	}

	public int getNota() {
		return nota;
	}

	public void setNota(int nota) {
		this.nota = nota;
	}
	

}
