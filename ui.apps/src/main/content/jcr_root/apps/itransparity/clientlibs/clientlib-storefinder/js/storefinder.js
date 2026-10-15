(function () {
  'use strict';

  var MAX_KM = 150;
  var IP_LOOKUP = 'https://ipwho.is/';

  function toRad(d) { return d * Math.PI / 180; }

  function haversineKm(lat1, lon1, lat2, lon2) {
    var dLat = toRad(lat2 - lat1);
    var dLon = toRad(lon2 - lon1);
    var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    return 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  }

  function init(root) {
    var endpoint = root.getAttribute('data-endpoint');
    var regions = JSON.parse(root.getAttribute('data-regions') || '[]');
    var select = root.querySelector('.store-finder__select');
    var list = root.querySelector('.store-finder__list');
    var empty = root.querySelector('.store-finder__empty');
    var status = root.querySelector('.store-finder__status');
    var detectBtn = root.querySelector('.store-finder__detect');

    var manual = false;
    var current = null;
    var requestId = 0;

    function labelFor(key) {
      for (var i = 0; i < regions.length; i++) {
        if (regions[i].key === key) { return regions[i].label; }
      }
      return key;
    }

    function nearestRegion(lat, lon) {
      var best = 'other';
      var bestKm = Infinity;
      for (var i = 0; i < regions.length; i++) {
        var r = regions[i];
        if (typeof r.lat !== 'number') { continue; }
        var km = haversineKm(lat, lon, r.lat, r.lon);
        if (km < bestKm) { best = r.key; bestKm = km; }
      }
      return bestKm <= MAX_KM ? best : 'other';
    }

    function render(data) {
      list.innerHTML = '';
      if (!data.stores || !data.stores.length) {
        empty.hidden = false;
        return;
      }
      empty.hidden = true;
      data.stores.forEach(function (s) {
        var li = document.createElement('li');
        li.className = 'store-finder__item';
        var name = document.createElement('strong');
        name.textContent = s.name;
        li.appendChild(name);
        li.appendChild(document.createTextNode(s.address));
        list.appendChild(li);
      });
    }

    function load(key, detected) {
      current = key;
      select.value = key;
      var id = ++requestId;
      status.textContent = (detected ? 'Detected location: ' : 'Showing dealers for: ') + labelFor(key);
      fetch(endpoint + '.stores.' + encodeURIComponent(key) + '.json', { credentials: 'same-origin' })
        .then(function (res) {
          if (!res.ok) { throw new Error('HTTP ' + res.status); }
          return res.json();
        })
        .then(function (data) { if (id === requestId) { render(data); } })
        .catch(function () {
          if (id === requestId) {
            list.innerHTML = '';
            empty.hidden = true;
            status.textContent = 'Dealers could not be loaded. Please try again.';
          }
        });
    }

    function lookupByIp() {
      return fetch(IP_LOOKUP)
        .then(function (res) { return res.json(); })
        .then(function (j) { return j && j.success ? nearestRegion(j.latitude, j.longitude) : null; })
        .catch(function () { return null; });
    }

  function lookupByBrowser() {
      return new Promise(function (resolve) {
          if (!navigator.geolocation) {
              resolve(null);
              return;
          }

          navigator.geolocation.getCurrentPosition(
              function (position) {
                  resolve({
                      latitude: position.coords.latitude,
                      longitude: position.coords.longitude
                  });
              },
              function (error) {
                  console.error('Location error:', error);
                  resolve(null);
              },
              {
                  enableHighAccuracy: true,
                  timeout: 10000,
                  maximumAge: 0
              }
          );
      });
  }

    function detect() {
      return lookupByIp()
        .then(function (key) { return key || lookupByBrowser(); })
        .then(function (key) { return key || 'other'; });
    }

    function autoDetect() {
      detect().then(function (key) {
        if (!manual && key !== current) { load(key, true); }
      });
    }

    select.addEventListener('change', function () {
      manual = true;
      load(select.value, false);
    });

    detectBtn.addEventListener('click', function () {
      manual = false;
      status.textContent = 'Detecting your location...';
      detect().then(function (key) { load(key, true); });
    });

    document.addEventListener('visibilitychange', function () {
      if (!document.hidden && !manual) { autoDetect(); }
    });
    window.addEventListener('focus', function () {
      if (!manual) { autoDetect(); }
    });

    status.textContent = 'Detecting your location...';
    autoDetect();
  }

  function ready() {
    var nodes = document.querySelectorAll('.store-finder');
    for (var i = 0; i < nodes.length; i++) { init(nodes[i]); }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', ready);
  } else {
    ready();
  }
}());