package com.paysafe.monitoring.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.paysafe.monitoring.api.MonitoringServiceAPI;
import com.paysafe.monitoring.dto.MonitoringStatusDTO;
import com.paysafe.monitoring.exception.ResourceNotFoundException;
import com.paysafe.monitoring.model.MonitoringInfo;

/**
 * This class represents the monitoring service.
 */
@Service
public class DefaultMonitoringServiceAPI implements MonitoringServiceAPI {

	private static final Logger logger = LoggerFactory.getLogger(DefaultMonitoringServiceAPI.class);

	private Map<String, List<MonitoringInfo>> monitoredHosts = new ConcurrentHashMap<>();
	@Resource
	private RestTemplate restTemplate;

	@Value("${paysafe.monitoring.url}")
	private String paysafeUrl;

	private Timer timer = new Timer();
	// assuming only one timer can exist through the lifetime of the process
	private TimerTask ts = null;

	/***
	 * It pings a given hostname every certain period using TimerTask as long as
	 * monitoring this host was not stopped.
	 * 
	 * @interval measured in sec
	 */
	@Override
	public void startMonitoring(int interval, String hostname) {

		String key = hostname + ":" + interval;
		logger.debug("started monitoring host: " + hostname);
		timer.scheduleAtFixedRate(new MyTimerTask(paysafeUrl, restTemplate, monitoredHosts, key), new Date(),
				interval * 1000);

	}

	/**
	 * This method sets the interval of monitored host to 0, so no need to be pinged
	 * from now on.
	 */
	@Override
	public void stopMonitoring(String hostname) {
		// you can not stop monitoring host that is not already under monitoring
		String monitoredHostKey = getFullHostname(hostname);
		List<MonitoringInfo> updatedInfo = monitoredHosts.get(monitoredHostKey);
		updatedInfo.add(new MonitoringInfo(new Date(), "STOPPED"));
		monitoredHosts.put(monitoredHostKey, updatedInfo);
		timer.cancel();
		logger.debug("Stopped monitoring host " + hostname);
	}

	/**
	 * Returns the historical information of monitoring a given host. Throws an
	 * exception if this host is not under monitoring.
	 */
	@Override
	public String displayInfo(String hostname) {

		List<MonitoringInfo> infos = monitoredHosts.get(getFullHostname(hostname));
		return infos.stream().map(info -> info.toString()).collect(Collectors.joining(", "));

	}

	private String getFullHostname(String hostname) {

		List<String> hosts = findIfHostIsMonitored(hostname);
		if (hosts.size() > 1 || hosts.isEmpty()) {
			throw new ResourceNotFoundException(
					hostname + " either does not exist or dupliacte.. both cases should not happen!!");
		} else {
			return hosts.get(0);
		}
	}

	private List<String> findIfHostIsMonitored(String hostname) {
		return monitoredHosts.keySet().stream().filter(key -> key.startsWith(hostname)).collect(Collectors.toList());
	}
}

class MyTimerTask extends TimerTask {

	private static final Logger logger = LoggerFactory.getLogger(MyTimerTask.class);
	
	// hacked
	private String key;
	private Map<String, List<MonitoringInfo>> monitoredHosts;
	private RestTemplate restTemplate;
	private String paysafeUrl;

	public MyTimerTask(String paysafeUrl, RestTemplate restTemplate, Map<String, List<MonitoringInfo>> monitoredHosts,
			String key) {
		this.monitoredHosts = monitoredHosts;
		this.key = key;
		this.restTemplate = restTemplate;
		this.paysafeUrl = paysafeUrl;
	}

	@Override
	public void run() {
		ResponseEntity<MonitoringStatusDTO> response = null;
		List<MonitoringInfo> info = new ArrayList<>();

		try {
			response = restTemplate.exchange(paysafeUrl, HttpMethod.GET, new HttpEntity<String>(new HttpHeaders()),
					MonitoringStatusDTO.class);

			String apiResponseStatus = response.getBody().getStatus();
			String hostStatus = "";
			if (apiResponseStatus.equalsIgnoreCase("Ready")) {
				hostStatus = "UP";
			} else {
				hostStatus = "DOWN";
			}
			if (!monitoredHosts.containsKey(key)) {
				info.add(new MonitoringInfo(new Date(), hostStatus));
			} else {
				info = monitoredHosts.get(key);
				info.add(new MonitoringInfo(new Date(), hostStatus));
			}
			monitoredHosts.put(key, info);
		} catch (RestClientException e) {
			throw new ResourceNotFoundException("Failed to connect to " + paysafeUrl + e.getMessage());
		}
	}

}