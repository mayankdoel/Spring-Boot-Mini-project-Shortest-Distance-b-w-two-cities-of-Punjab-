let routeMap;
let cityMarkersLayer;
let routeLineLayer;
let routeMarkersLayer;

const citySelect1 = document.getElementById('city1');
const citySelect2 = document.getElementById('city2');
const errMsg = document.getElementById('errMsg');
const resultBox = document.getElementById('resultBox');
const mapStatus = document.getElementById('mapStatus');
const roadsList = document.getElementById('roadsList');

window.addEventListener('load', async () => {
    try {
        const [cities, details] = await Promise.all([
            fetchJson('/api/cities'),
            fetchJson('/api/cities/details')
        ]);

        populateSelects(cities);
        populateChips(cities);
        initializeMap(details);
    } catch (error) {
        errMsg.textContent = 'Could not connect to the server. Is Spring Boot running?';
        errMsg.style.display = 'block';
    }
});

async function fetchJson(url) {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`Request failed: ${response.status}`);
    }
    return response.json();
}

function populateSelects(cities) {
    cities.forEach(name => {
        citySelect1.innerHTML += `<option value="${name}">${name}</option>`;
        citySelect2.innerHTML += `<option value="${name}">${name}</option>`;
    });
}

function populateChips(cities) {
    const chips = document.getElementById('chips');
    cities.forEach(name => {
        chips.innerHTML += `<div class="chip"><div class="chip-dot"></div>${name}</div>`;
    });
}

function initializeMap(cities) {
    if (typeof L === 'undefined') {
        mapStatus.textContent = 'Map library could not be loaded.';
        return;
    }

    routeMap = L.map('routeMap', {
        zoomControl: true,
        scrollWheelZoom: false
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(routeMap);

    cityMarkersLayer = L.layerGroup().addTo(routeMap);
    routeLineLayer = L.layerGroup().addTo(routeMap);
    routeMarkersLayer = L.layerGroup().addTo(routeMap);

    renderCityMarkers(cities);

    const bounds = L.latLngBounds(cities.map(city => [city.lat, city.lng]));
    routeMap.fitBounds(bounds.pad(0.15));
}

function renderCityMarkers(cities) {
    cityMarkersLayer.clearLayers();

    cities.forEach(city => {
        L.circleMarker([city.lat, city.lng], {
            radius: 6,
            weight: 2,
            color: '#ffffff',
            fillColor: '#f97316',
            fillOpacity: 0.92
        })
            .bindPopup(createCityPopup(city, 'Punjab city'))
            .addTo(cityMarkersLayer);
    });
}

function createCityPopup(city, subtitle) {
    return `
        <div class="city-popup">
            <strong>${city.name}</strong>
            <span>${subtitle}</span>
        </div>
    `;
}

function swapCities() {
    const temp = citySelect1.value;
    citySelect1.value = citySelect2.value;
    citySelect2.value = temp;
}

function findRoute() {
    const n1 = citySelect1.value;
    const n2 = citySelect2.value;

    if (!n1 || !n2 || n1 === n2) {
        errMsg.textContent = 'Please select two different cities.';
        errMsg.style.display = 'block';
        resultBox.style.display = 'none';
        return;
    }

    errMsg.style.display = 'none';

    fetch(`/api/route?from=${encodeURIComponent(n1)}&to=${encodeURIComponent(n2)}`)
        .then(response => response.json())
        .then(data => {
            if (data.error) {
                errMsg.textContent = data.error;
                errMsg.style.display = 'block';
                resultBox.style.display = 'none';
                return;
            }

            renderRoute(data);
            renderMapRoute(data);
        })
        .catch(() => {
            errMsg.textContent = 'Server error. Make sure Spring Boot is running on port 5001.';
            errMsg.style.display = 'block';
            resultBox.style.display = 'none';
        });
}

function renderRoute(data) {
    const stops = data.numberOfStops;
    const roadsUsed = [...new Set((data.segments || []).map(segment => segment.highway))];

    document.getElementById('summaryCols').innerHTML = `
    <div class="sum-circle">
      <div class="s-icon">KM</div>
      <div class="s-val">${data.totalDistanceKm} km</div>
      <div class="s-lbl">Road Dist.</div>
    </div>
    <div class="sum-circle">
      <div class="s-icon">ETA</div>
      <div class="s-val">${data.totalEstimatedTime}</div>
      <div class="s-lbl">Drive Time</div>
    </div>
    <div class="sum-circle">
      <div class="s-icon">STP</div>
      <div class="s-val">${stops < 1 ? 'Direct' : stops}</div>
      <div class="s-lbl">${stops < 1 ? 'Route' : 'Via Cities'}</div>
    </div>`;

    roadsList.innerHTML = roadsUsed.length
        ? roadsUsed.map(road => `<span class="road-pill">${road}</span>`).join('')
        : '<span class="road-pill">Direct local road</span>';

    const path = data.cityPath;
    const segments = data.segments;
    let html = '';

    path.forEach((city, i) => {
        const isFirst = i === 0;
        const isLast = i === path.length - 1;
        const dotClass = isFirst ? 'start' : isLast ? 'end' : 'mid';
        const dotLabel = isFirst ? 'A' : isLast ? 'B' : i;
        const cardClass = isFirst || isLast ? 'highlight' : '';

        const cityLabel = isFirst ? `Start: ${city}` : isLast ? `End: ${city}` : city;
        const roleLabel = isFirst ? 'Starting Point' : isLast ? 'Destination' : 'Via Point';

        let segInfo = '';
        if (i < path.length - 1 && segments[i]) {
            const seg = segments[i];
            segInfo = `
        <div class="arrow-down">
          <div class="arrow-line"></div>
          <span style="color:#ff6b35;font-weight:700;">${seg.distanceKm} km</span>
          <span class="hwy-badge">${seg.highway}</span>
          <span style="color:#94a3b8;">${seg.estimatedTime}</span>
          <div class="arrow-line"></div>
        </div>`;
        }

        html += `
      <div class="t-step">
        <div class="t-dot ${dotClass}">${dotLabel}</div>
        <div class="t-card ${cardClass}">
          <div class="city-name">${cityLabel}</div>
          <div class="seg-info"><span>${roleLabel}</span></div>
        </div>
        ${segInfo}
      </div>`;
    });

    document.getElementById('timeline').innerHTML = html;
    resultBox.style.display = 'block';
    resultBox.style.animation = 'none';
    void resultBox.offsetWidth;
    resultBox.style.animation = 'slideUp .4s ease';
}

function renderMapRoute(data) {
    if (!routeMap || !routeLineLayer || !routeMarkersLayer) {
        return;
    }

    const routeCities = data.routeCities || [];
    if (!routeCities.length) {
        mapStatus.textContent = 'Route coordinates are not available.';
        return;
    }

    const latLngs = routeCities.map(city => [city.lat, city.lng]);

    routeLineLayer.clearLayers();
    routeMarkersLayer.clearLayers();
    cityMarkersLayer.clearLayers();

    L.polyline(latLngs, {
        color: '#2563eb',
        weight: 5,
        opacity: 0.85
    }).addTo(routeLineLayer);

    routeCities.forEach((city, index) => {
        const subtitle =
            index === 0 ? 'Start' :
            index === routeCities.length - 1 ? 'Destination' :
            `Stop ${index}`;

        L.marker([city.lat, city.lng])
            .bindPopup(createCityPopup(city, subtitle))
            .addTo(routeMarkersLayer);
    });

    const roadSummary = [...new Set((data.segments || []).map(segment => segment.highway))].join(', ');
    mapStatus.textContent = roadSummary
        ? `${data.source} to ${data.destination} through ${roadSummary}.`
        : `${data.source} to ${data.destination} with ${Math.max(data.numberOfStops, 0)} stop(s).`;

    setTimeout(() => {
        routeMap.invalidateSize();
        routeMap.flyToBounds(L.latLngBounds(latLngs).pad(0.35), {
            padding: [24, 24],
            duration: 0.8
        });
    }, 120);
}
