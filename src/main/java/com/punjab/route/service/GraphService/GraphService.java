package com.punjab.route.service;

import com.punjab.route.model.City;
import com.punjab.route.model.Road;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphService {

    private final Map<String, City>       cities    = new LinkedHashMap<>();
    private final Map<String, List<Road>> adjacency = new HashMap<>();

    public GraphService() {
        // ── Cities (Indian Punjab) ──
        addCity("Amritsar",        31.6340, 74.8723);
        addCity("Ludhiana",        30.9010, 75.8573);
        addCity("Jalandhar",       31.3260, 75.5762);
        addCity("Patiala",         30.3398, 76.3869);
        addCity("Bathinda",        30.2110, 74.9455);
        addCity("Mohali",          30.7046, 76.7179);
        addCity("Hoshiarpur",      31.5143, 75.9113);
        addCity("Gurdaspur",       32.0393, 75.4063);
        addCity("Pathankot",       32.2643, 75.6520);
        addCity("Moga",            30.8180, 75.1710);
        addCity("Firozpur",        30.9337, 74.6119);
        addCity("Kapurthala",      31.3800, 75.3800);
        addCity("Rupnagar",        30.9644, 76.5254);
        addCity("Sangrur",         30.2450, 75.8440);
        addCity("Fazilka",         30.4010, 74.0258);
        addCity("Muktsar",         30.4741, 74.5161);
        addCity("Barnala",         30.3785, 75.5480);
        addCity("Nawanshahr",      31.1253, 76.1155);
        addCity("Fatehgarh Sahib", 30.6470, 76.3900);
        addCity("Tarn Taran",      31.4511, 74.9272);
        addCity("Faridkot",        30.6742, 74.7564);
        addCity("Mansa",           29.9906, 75.3972);
        addCity("Malerkotla",      30.5283, 75.8810);
        addCity("Phagwara",        31.2220, 75.7720);
        addCity("Khanna",          30.7059, 76.2193);

        // ── Road connections [cityA, cityB, km, highway] ──
        addRoad("Amritsar",        "Tarn Taran",       25, "NH354");
        addRoad("Amritsar",        "Gurdaspur",        60, "NH354A");
        addRoad("Amritsar",        "Jalandhar",        80, "NH44");
        addRoad("Amritsar",        "Firozpur",        105, "NH54");
        addRoad("Amritsar",        "Kapurthala",       48, "SH");
        addRoad("Tarn Taran",      "Firozpur",         60, "NH54");
        addRoad("Gurdaspur",       "Pathankot",        40, "NH44");
        addRoad("Gurdaspur",       "Jalandhar",        92, "NH44");
        addRoad("Pathankot",       "Hoshiarpur",       90, "NH503");
        addRoad("Jalandhar",       "Kapurthala",       24, "NH703");
        addRoad("Jalandhar",       "Phagwara",         26, "NH44");
        addRoad("Jalandhar",       "Hoshiarpur",       60, "NH503");
        addRoad("Jalandhar",       "Ludhiana",         66, "NH44");
        addRoad("Jalandhar",       "Nawanshahr",       65, "SH");
        addRoad("Phagwara",        "Hoshiarpur",       35, "NH503");
        addRoad("Phagwara",        "Nawanshahr",       40, "SH");
        addRoad("Kapurthala",      "Moga",             55, "SH");
        addRoad("Firozpur",        "Moga",             52, "NH954");
        addRoad("Firozpur",        "Fazilka",          42, "NH54");
        addRoad("Fazilka",         "Muktsar",          55, "NH10");
        addRoad("Muktsar",         "Bathinda",         55, "NH10");
        addRoad("Muktsar",         "Faridkot",         35, "NH10");
        addRoad("Faridkot",        "Moga",             42, "NH10");
        addRoad("Faridkot",        "Bathinda",         60, "NH10");
        addRoad("Moga",            "Ludhiana",         50, "NH44");
        addRoad("Ludhiana",        "Khanna",           35, "NH44");
        addRoad("Ludhiana",        "Fatehgarh Sahib",  45, "NH44");
        addRoad("Ludhiana",        "Rupnagar",         45, "NH21");
        addRoad("Ludhiana",        "Barnala",          65, "SH");
        addRoad("Ludhiana",        "Malerkotla",       55, "SH");
        addRoad("Khanna",          "Fatehgarh Sahib",  30, "NH44");
        addRoad("Khanna",          "Malerkotla",       38, "SH");
        addRoad("Fatehgarh Sahib", "Patiala",          38, "NH44");
        addRoad("Fatehgarh Sahib", "Mohali",           40, "NH44");
        addRoad("Patiala",         "Sangrur",          55, "NH7");
        addRoad("Patiala",         "Mohali",           26, "NH7");
        addRoad("Patiala",         "Barnala",          62, "SH");
        addRoad("Mohali",          "Rupnagar",         30, "NH21");
        addRoad("Nawanshahr",      "Rupnagar",         38, "NH21");
        addRoad("Nawanshahr",      "Hoshiarpur",       38, "SH");
        addRoad("Barnala",         "Sangrur",          30, "SH");
        addRoad("Barnala",         "Bathinda",         65, "SH");
        addRoad("Sangrur",         "Malerkotla",       28, "SH");
        addRoad("Sangrur",         "Mansa",            52, "SH");
        addRoad("Mansa",           "Bathinda",         48, "SH");
        addRoad("Bathinda",        "Faridkot",         60, "NH10");
    }

    private void addCity(String name, double lat, double lng) {
        cities.put(name, new City(name, lat, lng));
        adjacency.put(name, new ArrayList<>());
    }

    private void addRoad(String a, String b, int km, String hw) {
        adjacency.get(a).add(new Road(b, km, hw));
        adjacency.get(b).add(new Road(a, km, hw));
    }

    public Map<String, City>       getCities()   { return cities; }
    public Map<String, List<Road>> getAdjacency(){ return adjacency; }
    public List<String> getCityNames() {
        return cities.keySet().stream().sorted().toList();
    }
    public List<City> getCityDetails() {
        return cities.values().stream()
                .sorted(Comparator.comparing(City::getName))
                .toList();
    }
}
