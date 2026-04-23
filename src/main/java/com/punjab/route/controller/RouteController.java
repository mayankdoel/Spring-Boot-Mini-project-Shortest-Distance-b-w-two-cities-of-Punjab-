package com.punjab.route.controller;

import com.punjab.route.model.RouteResponse;
import com.punjab.route.model.City;
import com.punjab.route.service.DijkstraService;
import com.punjab.route.service.GraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
    @CrossOrigin(origins = "*")
public class RouteController {

    private final DijkstraService dijkstraService;
    private final GraphService graphService;

    // Constructor Injection
    public RouteController(DijkstraService dijkstraService, GraphService graphService) {
        this.dijkstraService = dijkstraService;
        this.graphService = graphService;
    }

    // ✅ GET all cities
    // URL: http://localhost:5001/api/cities
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities() {
        return ResponseEntity.ok(graphService.getCityNames());
    }

    @GetMapping("/cities/details")
    public ResponseEntity<List<City>> getCityDetails() {
        return ResponseEntity.ok(graphService.getCityDetails());
    }

    // ✅ GET shortest route
    // URL: http://localhost:5001/api/route?from=Amritsar&to=Patiala
    @GetMapping("/route")
    public ResponseEntity<?> getRoute(
            @RequestParam String from,
            @RequestParam String to) {

        try {
            RouteResponse route = dijkstraService.findShortestRoute(from, to);
            return ResponseEntity.ok(route);

        } catch (RuntimeException e) {   // ✅ FIXED ERROR HERE
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
