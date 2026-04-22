// ── City Names (fetched from Spring Boot API on load) ──
const citySelect1 = document.getElementById('city1');
const citySelect2 = document.getElementById('city2');
const errMsg      = document.getElementById('errMsg');
const resultBox   = document.getElementById('resultBox');

// ── On Page Load: fetch city list from backend ──
window.addEventListener('load', () => {
    fetch('/api/cities')
        .then(r => r.json())
        .then(cities => {
            populateSelects(cities);
            populateChips(cities);
        })
        .catch(() => {
            errMsg.textContent = '⚠️ Could not connect to server. Is Spring Boot running?';
            errMsg.style.display = 'block';
        });
});

// ── Populate city dropdowns ──
function populateSelects(cities) {
    cities.forEach(name => {
        citySelect1.innerHTML += `<option value="${name}">${name}</option>`;
        citySelect2.innerHTML += `<option value="${name}">${name}</option>`;
    });
}

// ── Populate city chips at bottom ──
function populateChips(cities) {
    const chips = document.getElementById('chips');
    cities.forEach(name => {
        chips.innerHTML += `<div class="chip"><div class="chip-dot"></div>${name}</div>`;
    });
}

// ── Swap the two selected cities ──
function swapCities() {
    const temp = citySelect1.value;
    citySelect1.value = citySelect2.value;
    citySelect2.value = temp;
}

// ── Find Route: calls Spring Boot API ──
function findRoute() {
    const n1 = citySelect1.value;
    const n2 = citySelect2.value;

    // Validate selection
    if (!n1 || !n2 || n1 === n2) {
        errMsg.textContent = '⚠️ Please select two different cities.';
        errMsg.style.display = 'block';
        resultBox.style.display = 'none';
        return;
    }
    errMsg.style.display = 'none';

    // Call Spring Boot REST API
    fetch(`/api/route?from=${encodeURIComponent(n1)}&to=${encodeURIComponent(n2)}`)
        .then(r => r.json())
        .then(data => {
            if (data.error) {
                errMsg.textContent = '⚠️ ' + data.error;
                errMsg.style.display = 'block';
                resultBox.style.display = 'none';
            } else {
                renderRoute(data);
            }
        })
        .catch(() => {
            errMsg.textContent = '⚠️ Server error. Make sure Spring Boot is running on port 5001.';
            errMsg.style.display = 'block';
            resultBox.style.display = 'none';
        });
}

// ── Render the route result ──
function renderRoute(data) {
    // Summary circles
    const stops = data.numberOfStops;
    document.getElementById('summaryCols').innerHTML = `
    <div class="sum-circle">
      <div class="s-icon">📏</div>
      <div class="s-val">${data.totalDistanceKm} km</div>
      <div class="s-lbl">Road Dist.</div>
    </div>
    <div class="sum-circle">
      <div class="s-icon">🚗</div>
      <div class="s-val">${data.totalEstimatedTime}</div>
      <div class="s-lbl">Drive Time</div>
    </div>
    <div class="sum-circle">
      <div class="s-icon">🛑</div>
      <div class="s-val">${stops < 1 ? 'Direct' : stops}</div>
      <div class="s-lbl">${stops < 1 ? 'Route' : 'Via Cities'}</div>
    </div>`;

    // Build timeline from cityPath + segments
    const path     = data.cityPath;
    const segments = data.segments;
    let html = '';

    path.forEach((city, i) => {
        const isFirst = i === 0;
        const isLast  = i === path.length - 1;
        const dotClass  = isFirst ? 'start' : isLast ? 'end' : 'mid';
        const dotLabel  = isFirst ? 'A' : isLast ? 'B' : i;
        const cardClass = (isFirst || isLast) ? 'highlight' : '';

        const cityLabel = isFirst ? '🟠 ' + city : isLast ? '🔵 ' + city : city;
        const roleLabel = isFirst ? '📍 Starting Point' : isLast ? '🏁 Destination' : '🔄 Via Point';

        // Segment arrow between this city and next
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

    // Animate result box
    resultBox.style.display = 'block';
    resultBox.style.animation = 'none';
    void resultBox.offsetWidth;
    resultBox.style.animation = 'slideUp .4s ease';
}
