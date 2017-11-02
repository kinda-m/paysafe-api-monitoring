package com.paysafe.monitoring.api;

/**
 * Defines the contract of monitoring service.
 */
public interface MonitoringServiceAPI {

	void startMonitoring(int interval, String hostname);

	void stopMonitoring(String hostname);

	String displayInfo(String hostname);
	
}