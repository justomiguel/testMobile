package com.jmv.frre.moduloestudiante.customcomponents;

import com.google.common.base.Function;

import android.view.View;

public class CustomItem {
	
	private boolean visible;
	
	private View theView;
	
	private String label;
	
	private Integer idResource;
	
	private Integer position;

	private Function<View, Void> nextFunction;

	public CustomItem(String label, Integer idResource, Integer position,Function<View, Void> nextFunction) {
		super();
		this.label = label;
		this.idResource = idResource;
		this.position = position;
		this.nextFunction = nextFunction;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		if (theView!=null){
			theView.setVisibility(visible? View.VISIBLE:View.GONE);
		}
	}

	public View getTheView() {
		return theView;
	}

	public Function<View, Void> getNextFunction() {
		return nextFunction;
	}

	public void setNextFunction(Function<View, Void> nextFunction) {
		this.nextFunction = nextFunction;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public void setTheView(View theView) {
		this.theView = theView;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getIdResource() {
		return idResource;
	}

	public void setIdResource(Integer idResource) {
		this.idResource = idResource;
	}
	
	

}
