package com.punjab.route.service;

import com.punjab.route.model.Road;
import com.punjab.route.model.RouteResponse;
import com.punjab.route.model.RouteSegment;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DijkstraService {

    private final GraphService graphService;

    public DijkstraService(GraphService graphService) {
        this.graphService = graphService;
    }

    public RouteResponse findShortestRoute(String source, String destination) {
        Map<String, List<Road>> graph = graphService.getAdjacency();

        // Validate cities
        if (!graph.containsKey(source))
            throw new IllegalArgumentException("City not found: " + source);
        if (!graph.containsKey(destination))
            throw new IllegalArgumentException("City not found: " + destination);
        if (source.equalsIgnoreCase(destination))
            throw new IllegalArgumentException("Source and destination must be different");

        // ── Dijkstra ──
        Map<String, Integer> dist    = new HashMap<>();
        Map<String, String>  prevCity= new HashMap<>();
        Map<String, String>  prevHw  = new HashMap<>();

        graph.keySet().forEach(c -> dist.put(c, Integer.MAX_VALUE));
        dist.put(source, 0);

        // Min-heap: [distance, cityName]
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        List<String> cityList = new ArrayList<>(graph.keySet());
        pq.offer(new int[]{0, cityList.indexOf(source)});

        // Use name-based PQ instead for clarity
        PriorityQueue<String> namePq = new PriorityQueue<>(
                Comparator.comparingInt(dist::get));
        namePq.add(source);
        Set<String> visited = new HashSet<>();

        while (!namePq.isEmpty()) {
            String u = namePq.poll();
            if (visited.contains(u)) continue;
            visited.add(u);
            if (u.equals(destination)) break;

            for (Road road : graph.getOrDefault(u, List.of())) {
                String v  = road.getTo();
                int    nd = dist.get(u) + road.getDistanceKm();
                if (nd < dist.get(v)) {
                    dist.put(v, nd);
                    prevCity.put(v, u);
                    prevHw.put(v, road.getHighway());
                    namePq.offer(v);
                }
            }
        }

        if (dist.get(destination) == Integer.MAX_VALUE)
            throw new RuntimeException("No route found between " + source + " and " + destination);

        // ── Reconstruct path ──
        List<String> path = new ArrayList<>();
        String cur = destination;
        while (cur != null) { path.add(0, cur); cur = prevCity.get(cur); }

        // ── Build segments ──
        List<RouteSegment> segments = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i), to = path.get(i + 1);
            String hw   = prevHw.getOrDefault(to, "SH");
            int    km   = graph.get(from).stream()
                    .filter(r -> r.getTo().equals(to))
                    .mapToInt(Road::getDistanceKm).findFirst().orElse(0);
            segments.add(new RouteSegment(from, to, km, hw, formatTime(km)));
        }

        int total = dist.get(destination);
        RouteResponse res = new RouteResponse();
        res.setSource(source);
        res.setDestination(destination);
        res.setCityPath(path);
        res.setRouteCities(path.stream()
                .map(graphService.getCities()::get)
                .filter(Objects::nonNull)
                .toList());
        res.setSegments(segments);
        res.setTotalDistanceKm(total);
        res.setTotalEstimatedTime(formatTime(total));
        res.setNumberOfStops(path.size() - 2);
        return res;
    }

    private String formatTime(int km) {
        int h = km / 65, m = Math.round(((float) km / 65 - h) * 60);
        return h > 0 ? h + "h " + m + "m" : m + " min";
    }
}
