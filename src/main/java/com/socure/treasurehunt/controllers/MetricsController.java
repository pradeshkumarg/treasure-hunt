package com.socure.treasurehunt.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.socure.treasurehunt.dto.IncidentDTO;
import com.socure.treasurehunt.dto.MetricDTO;
import com.socure.treasurehunt.dto.UserMetricDTO;
import com.socure.treasurehunt.model.Metric;
import com.socure.treasurehunt.model.User;
import com.socure.treasurehunt.repository.MetricsRepository;

@RestController
public class MetricsController {

	@Autowired
	MetricsRepository metricRepository;

	@CrossOrigin({ "http://localhost:9000", "https://cryptic-headland-55422.herokuapp.com" })
	@GetMapping("/metrics")
	public ResponseEntity<?> getAllMetrics() {
		List<Metric> metricList = metricRepository.findAll();
		List<MetricDTO> metricDTOList = new ArrayList<>();
		for (Metric metric : metricList) {
			final MetricDTO metricDTO = new MetricDTO();
			BeanUtils.copyProperties(metric, metricDTO);
			final UserMetricDTO userMetricDTO = new UserMetricDTO();
			User user = metric.getUser();
			if (null != user) {
				BeanUtils.copyProperties(user, userMetricDTO);
				metricDTO.setUser(userMetricDTO);
			}
			metricDTOList.add(metricDTO);
		}
		return ResponseEntity.ok(metricDTOList);
	}
	
	@CrossOrigin({ "http://localhost:9000", "https://cryptic-headland-55422.herokuapp.com" })
	@GetMapping("/metrics/{severity}")
	public ResponseEntity<?> getMetricBySeverity(@PathVariable String severity) {
		List<Metric> metricList = metricRepository.findBySeverity(severity);
		List<MetricDTO> metricDTOList = new ArrayList<>();
		for (Metric metric : metricList) {
			final MetricDTO metricDTO = new MetricDTO();
			BeanUtils.copyProperties(metric, metricDTO);
			final UserMetricDTO userMetricDTO = new UserMetricDTO();
			User user = metric.getUser();
			if (null != user) {
				BeanUtils.copyProperties(user, userMetricDTO);
				metricDTO.setUser(userMetricDTO);
			}
			metricDTOList.add(metricDTO);
		}
		return ResponseEntity.ok(metricDTOList);
	}
	
	@CrossOrigin({ "http://localhost:9000", "https://cryptic-headland-55422.herokuapp.com" })
	@GetMapping("/incidents")
	public IncidentDTO getIncidents() {
		IncidentDTO incidentDTO = new IncidentDTO();
		incidentDTO.setLow(metricRepository.getLowCount());
		incidentDTO.setHigh(metricRepository.getHighCount());
		return incidentDTO;
	}

}
