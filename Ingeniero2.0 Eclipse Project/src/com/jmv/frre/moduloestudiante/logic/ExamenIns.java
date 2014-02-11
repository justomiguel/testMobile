package com.jmv.frre.moduloestudiante.logic;

public class ExamenIns {
	
	private int anio;
	private String materia;
	private String estado;
	private int plan;
	private int codigo;

	public ExamenIns(int anio, String materia, String estado, int plan,
			int codigo) {
		super();
		this.anio = anio;
		this.materia = materia;
		this.estado = estado;
		this.plan = plan;
		this.codigo = codigo;
	}

	public int getAnio() {
		return anio;
	}

	public void setAnio(int anio) {
		this.anio = anio;
	}

	public String getMateria() {
		return materia;
	}

	public void setMateria(String materia) {
		this.materia = materia;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getPlan() {
		return plan;
	}

	public void setPlan(int plan) {
		this.plan = plan;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	
	

}
