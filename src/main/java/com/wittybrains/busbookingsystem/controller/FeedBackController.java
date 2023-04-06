package com.wittybrains.busbookingsystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wittybrains.busbookingsystem.dto.FeedBackDTO;
import com.wittybrains.busbookingsystem.model.Booking;
import com.wittybrains.busbookingsystem.model.FeedBack;
import com.wittybrains.busbookingsystem.repository.BookingRepository;
import com.wittybrains.busbookingsystem.repository.FeedBackRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/booking/{bookingId}/feedback")
public class FeedBackController {
    private final BookingRepository bookingRepository;
    private final FeedBackRepository feedbackRepository;

    public FeedBackController(BookingRepository bookingRepository, FeedBackRepository feedbackRepository) {
        this.bookingRepository = bookingRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @PostMapping
    public ResponseEntity<FeedBackDTO> submitFeedback(@PathVariable Long bookingId, @Valid @RequestBody FeedBackDTO feedbackDto) {
        // Verify that the booking exists
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        // Parse the feedback DTO for rating and comment
        int rating = feedbackDto.getRating();
        String comment = feedbackDto.getComment();

        // Validate the rating
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body(new FeedBackDTO("Invalid rating"));
        }

        // Create a new feedback object
        FeedBack feedback = new FeedBack();
        feedback.setBooking(booking);
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedback.setCreatedAt(LocalDateTime.now());

        // Add the feedback to the database
        feedbackRepository.save(feedback);

        // Return the feedback object in the response
        return ResponseEntity.status(HttpStatus.CREATED).body(new FeedBackDTO(feedback));
    }


    @GetMapping
    public ResponseEntity<List<FeedBackDTO>> getFeedbackForBooking(@PathVariable Long bookingId) {
        // Verify that the booking exists
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        // Retrieve the feedback for the given booking
        List<FeedBack> feedbackList = feedbackRepository.findByBooking(booking);

        // Convert the feedback list to DTOs and return them in the response
        List<FeedBackDTO> feedbackDTOList = feedbackList.stream()
                .map(FeedBackDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(feedbackDTOList);
    }
}
    
    
   



