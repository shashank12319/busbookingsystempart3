package com.wittybrains.busbookingsystem.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.wittybrains.busbookingsystem.dto.TravelScheduleDTO;
import com.wittybrains.busbookingsystem.model.Bus;
import com.wittybrains.busbookingsystem.model.Driver;
import com.wittybrains.busbookingsystem.model.Stop;
import com.wittybrains.busbookingsystem.model.TravelSchedule;
import com.wittybrains.busbookingsystem.repository.TravelScheduleRepository;
import com.wittybrains.busbookingsystem.service.TravelScheduleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/schedules")
public class TravelScheduleController {

	private static final Logger logger = LoggerFactory.getLogger(TravelScheduleController.class);

	private final TravelScheduleService travelScheduleService;
	private final TravelScheduleRepository scheduleRepository;

	public TravelScheduleController(TravelScheduleService travelScheduleService,
			TravelScheduleRepository scheduleRepository) {
		this.travelScheduleService = travelScheduleService;
		this.scheduleRepository = scheduleRepository;
	}

	

	 
	@PostMapping
	public ResponseEntity<?> createTravelSchedule(@RequestBody TravelScheduleDTO travelScheduleDTO)
			throws ParseException {
		logger.info("Creating travel schedule with DTO={}", travelScheduleDTO);
		ResponseEntity<?> travelSchedule = travelScheduleService.createSchedule(travelScheduleDTO);

		if (travelSchedule != null) {
			logger.info("Successfully created travel schedule");
			return ResponseEntity.ok(travelSchedule);
		} else {
			logger.error("Failed to create travel schedule");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create travel schedule");
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> updateSchedule(@PathVariable Long id, @RequestBody TravelScheduleDTO updatedSchedule)
			throws ParseException {
		try {
			logger.info("Updating travel schedule with id={}, DTO={}", id, updatedSchedule);
			ResponseEntity<?> savedSchedule = travelScheduleService.updateSchedule(id, updatedSchedule);
			if (savedSchedule != null) {
				logger.info("Successfully updated travel schedule");
				return ResponseEntity.ok(savedSchedule);
			} else {
				logger.warn("Travel schedule not found with id={}", id);
				return ResponseEntity.notFound().build();
			}
		} catch (EntityNotFoundException ex) {
			logger.error("Failed to update travel schedule with id={}", id, ex);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getTravelSchedule(@PathVariable Long id) {
		try {
			logger.info("Fetching travel schedule with id={}", id);
			TravelSchedule travelSchedule = travelScheduleService.getScheduleById(id);
			if (travelSchedule != null) {
				logger.info("Successfully fetched travel schedule with id={}", id);
				return ResponseEntity.ok(travelSchedule);
			} else {
				logger.warn("Travel schedule not found with id={}", id);
				return ResponseEntity.notFound().build();
			}
		} catch (EntityNotFoundException ex) {
			logger.error("Failed to fetch travel schedule with id={}", id, ex);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
		Optional<TravelSchedule> existingScheduleOptional = scheduleRepository.findById(id);
		if (existingScheduleOptional.isPresent()) {
			logger.info("Deleting travel schedule with id={}", id);
			scheduleRepository.deleteById(id);
			return ResponseEntity.noContent().build();
		} else {
			logger.warn("Travel schedule not found with id={}", id);
			return ResponseEntity.notFound().build();
		}
	}
	



	@GetMapping("/schedules")
	public ResponseEntity<Page<TravelSchedule>> searchSchedules(
	        @RequestParam(required = false) String source,
	        @RequestParam(required = false) String destination,
	        @RequestParam(required = false) Long busId,
	        @RequestParam(required = false) Long driverId,
	        @RequestParam(required = false) String estimatedArrivalTimeStart,
	        @RequestParam(required = false) String estimatedArrivalTimeEnd,
	        @RequestParam(required = false) String estimatedDepartureTimeStart,
	        @RequestParam(required = false) String estimatedDepartureTimeEnd,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "3") int size) {

	    Pageable pageable = PageRequest.of(page, size);

	    Page<TravelSchedule> schedules = scheduleRepository.findByCriteria(source, destination, busId, driverId,
	            estimatedArrivalTimeStart, estimatedArrivalTimeEnd, estimatedDepartureTimeStart,
	            estimatedDepartureTimeEnd, pageable);

	    return ResponseEntity.ok(schedules);
	}


	@GetMapping
	public ResponseEntity<List<TravelScheduleDTO>> getAllSchedules() {
		logger.info("Getting all travel schedules");
		List<TravelScheduleDTO> travelScheduleDTOList = travelScheduleService.getAllSchedules();
		return ResponseEntity.ok().body(travelScheduleDTOList);
	}
}
