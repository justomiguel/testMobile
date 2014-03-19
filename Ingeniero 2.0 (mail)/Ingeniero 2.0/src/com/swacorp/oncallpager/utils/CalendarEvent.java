package com.swacorp.oncallpager.utils;

public class CalendarEvent {
	
	private String id;
	private String tittle;
	private String description;
	private String startDate;
	private String endDate;
	private String location;
	
	
	public CalendarEvent(String id, String tittle,
			String description, String startDate, String endDate,
			String location) {
		super();
		this.id = id;
		this.tittle = tittle;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.location = location;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTittle() {
		return tittle;
	}
	public void setTittle(String tittle) {
		this.tittle = tittle;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	@Override
	public String toString() {
		return "[id=" + id + ", tittle=" + tittle
				+ ", description=" + description + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", location=" + location + "]";
	}
	
	
}
