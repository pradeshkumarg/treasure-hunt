package com.socure.treasurehunt.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.socure.treasurehunt.model.Metric;

public interface MetricsRepository extends CrudRepository<Metric, Long> {
	
	@SuppressWarnings("unchecked")
	Metric save(Metric metric);
	
	List<Metric> findAll();
	
	List<Metric> findBySeverity(String severity);
}
