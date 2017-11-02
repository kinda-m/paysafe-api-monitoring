package com.paysafe.monitoring.controller;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paysafe.monitoring.api.MonitoringServiceAPI;
import com.paysafe.monitoring.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/v1/")
public class MonitoringController {

	@Resource
	private MonitoringServiceAPI monitoringService;

	@RequestMapping(value = "/start", method = RequestMethod.POST)
	public ResponseEntity<String> startMonitoring(@RequestParam("interval") int interval,
			@RequestParam("hostname") String hostname) {
		try { 
			monitoringService.startMonitoring(interval, hostname);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	public ResponseEntity<String> stoptMonitoring(@RequestParam("hostname") String hostname) {
		try { 
			monitoringService.stopMonitoring(hostname);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}

	}

	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public ResponseEntity<String> displayStatus(@RequestParam("hostname") String hostname) {
		// TODO validate hostname before
		try {
			return new ResponseEntity<String>(monitoringService.displayInfo(hostname),
					HttpStatus.OK);
		} catch (ResourceNotFoundException rnfe) {
			return new ResponseEntity<String>(rnfe.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

}
