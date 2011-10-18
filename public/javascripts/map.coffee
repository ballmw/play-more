# Geolocation with HTML 5 and Google Maps API based on example from maxheapsize: 
# http://maxheapsize.com/2009/04/11/getting-the-browsers-geolocation-with-html-5/
# This script is by Merge Database and Design, http://merged.ca/ -- if you use some, all, or any of this code, please offer a return link.

map = null
mapCenter = null
geocoder = null
latlng = null
geolocationOptions = { maximumAge: 3000, timeout: 5000, enableHighAccuracy: true }

initialize = ->	
  if navigator.geolocation
    navigator.geolocation.getCurrentPosition showMap, geolocationError, geolocationOptions

showMap = (position) ->
  latitude = position.coords.latitude
  longitude = position.coords.longitude
  mapOptions = {
    zoom: 15,
    mapTypeId: google.maps.MapTypeId.ROADMAP
  }
  map = new google.maps.Map(document.getElementById("map"), mapOptions)
  latlng = new google.maps.LatLng(latitude, longitude)
  map.setCenter(latlng)
  Odometer.start({position: position, callback: drawMap, map: map})

  geocoder = new google.maps.Geocoder()
  geocoder.geocode({'latLng': latlng}, addAddressToMap)

addAddressToMap = (results, status) ->
  if (status == google.maps.GeocoderStatus.OK) 
    if (results[1]) 
      marker = new google.maps.Marker({
          position: latlng,
          map: map
      })
      $('#location').html('Your location: ' + results[0].formatted_address)
  else 
    alert "Sorry, we were unable to geocode that address."

tripCoordinates = []
drawMap = (lastPos, newPos, map) ->
  if console.log
    console.log 'last lat: ' + lastPos.coords.latitude
    console.log 'last long: ' + lastPos.coords.longitude
    console.log 'new lat: ' + newPos.coords.latitude
    console.log 'new long: ' + newPos.coords.longitude

  tripCoordinates.push new google.maps.LatLng(lastPos.coords.latitude, lastPos.coords.longitude)
  tripCoordinates.push new google.maps.LatLng(newPos.coords.latitude, newPos.coords.longitude)
  tripPath = new google.maps.Polyline({
    path: tripCoordinates,
    strokeColor: "#FF0000",
    strokeOpacity: 1.0,
    strokeWeight: 2
  })
  tripPath.setMap map

geolocationError = (error) ->
  msg = 'Unable to locate position. '
  switch error.code
    when error.TIMEOUT then msg += 'Timeout.'
    when error.POSITION_UNAVAILABLE then msg += 'Position unavailable.'
    when error.PERMISSION_DENIED then msg += 'Please turn on location services.'
    when error.UNKNOWN_ERROR then msg += error.code
  alert msg

start = ->
  setTimeout initialize, 500

reset = ->
  map = null
  tripCoordinates = []
  $('#location').html('')
  Odometer.reset()

@Map = {
  start: start
  reset: reset
}