package com.wittybrains.busbookingsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wittybrains.busbookingsystem.model.Booking;
import com.wittybrains.busbookingsystem.model.User;



public interface BookingRepository extends JpaRepository<Booking, Long> {

	

	
	@Query("SELECT b FROM Booking b WHERE b.status IN ?1")
    List<Booking> findAllByStatusIn(List<String> statuses);
	   

    
	   
	   

    List<Booking> findByUser(User user);
	
}
