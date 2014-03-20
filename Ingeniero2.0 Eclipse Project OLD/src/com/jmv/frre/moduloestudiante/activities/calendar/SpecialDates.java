package com.jmv.frre.moduloestudiante.activities.calendar;

public enum SpecialDates {
	
	FERIADO("Feriado Nacional"),
	MESA_EXAMEN("Fecha de Examen Final"),
	EXAMEN_COMUN("Fecha de Examen Comun"),
	RECUPERATORIO("Fecha de Examen Recuperatorio");
	
	private String name;
	
	SpecialDates(String name){
		this.name = name;
	}
	
	@Override
	public String toString(){
		return name;
	}
}
