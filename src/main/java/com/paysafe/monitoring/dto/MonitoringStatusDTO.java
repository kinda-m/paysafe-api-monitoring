package com.paysafe.monitoring.dto;

/**
 * This class represents the response returned from hitting:
 * https://api.test.paysafe.com/accountmanagement/monitor
 */
public class MonitoringStatusDTO {
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
