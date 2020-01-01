package com.socure.treasurehunt.dto;

public class MetricDTO {

	private Long id;
	
	private String severity;

	private String status;

	private UserMetricDTO user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public UserMetricDTO getUser() {
		return user;
	}

	public void setUser(UserMetricDTO user) {
		this.user = user;
	}
}
