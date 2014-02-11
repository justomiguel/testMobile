package com.jmv.frre.moduloestudiante.logic;

public class ExamenIns {
	
	private int anio;
	private String materia;
	private String url;
	private int plan;
	private int codigo;
	private String estado;

	public ExamenIns(int anio, String materia,  String estado, String url, int plan,
			int codigo) {
		super();
		this.anio = anio;
		this.materia = materia;
		this.url = url;
		this.plan = plan;
		this.codigo = codigo;
		this.estado = estado;
	}
	
	

	public String getEstado() {
		return estado;
	}



	public void setEstado(String estado) {
		this.estado = estado;
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

	
	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
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
