package com.paysafe.monitoring.model;

import java.util.Date;

/**
 * This class stores information related to the monitoring process. What
 * monitoring action {start/stop} occurs and when.
 */
public class MonitoringInfo {

	private Date pingedDate;
	// TODO enum where values are: started, up, down
	private String status;

	public MonitoringInfo(Date pingedDate, String status) {
		this.pingedDate = pingedDate;
		this.status = status;
	}

	public Date getPingedDate() {
		return pingedDate;
	}

	public void setPingedDate(Date pingedDate) {
		this.pingedDate = pingedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "On: " + pingedDate + " it was " + status;
	}
}
