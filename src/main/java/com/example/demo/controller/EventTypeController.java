package com.example.demo.controller;

import com.example.demo.util.enums.EventType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller to expose the EventType enum values.
 * This is useful for front-end applications that need to populate dropdowns or
 * other UI elements with a list of available audit event types.
 */
@RestController
@RequestMapping("/api/public/event-types")
public class EventTypeController extends BaseController {

    /**
     * Retrieves all available EventType values as a list of strings.
     *
     * @return A list of strings representing the names of the enum constants.
     */
    @GetMapping
    public ResponseEntity<?> getAllEventTypes() {
        List<String> eventTypes = Arrays.stream(EventType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return buildSuccessResponse(HttpStatus.OK, "Event types retrieved successfully.", eventTypes);
    }
}
