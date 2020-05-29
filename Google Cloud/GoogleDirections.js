const axios = require('axios');

exports.main = (req, res) => {
  async function getDirections() {
    var mode = req.query.mode;
    var origin = req.query.origin;
    var destination = req.query.destination;
    var cycling = req.query.cycling;
    var walking = req.query.walking;
    var driving = req.query.driving;
    if (cycling) {
      mode = 'bicycling';
    }
    else if (walking) {
      mode = 'walking';
    } else {
      mode = 'driving';
    }
    var language = 'nl';
    var units = 'metric';
    var url = 'https://maps.googleapis.com/maps/api/directions/json?';

    const response = await axios.get(url, {
      params: {
        mode: mode,
        origin: origin,
        destination: destination,
        language: language,
        units: units,
        key: process.env.KEY
      }
    });
    directions = "De afstand tot uw bestemming " + response.data.routes[0].legs[0].end_address + " is " + response.data.routes[0].legs[0].distance.text +
    " en zal volgens onze berekeningen " + response.data.routes[0].legs[0].duration.text + " duren om er te geraken." + " De routebeschrijving: ";
    for (var i = 0; i < response.data.routes[0].legs[0].steps.length; i++){
      var distance = response.data.routes[0].legs[0].steps[i].distance.text;
      var duration = response.data.routes[0].legs[0].steps[i].duration.text;
      var instruction = response.data.routes[0].legs[0].steps[i].html_instructions;
      instruction = instruction.replace(/(<([^>]+)>)/ig,"");

      if (i != response.data.routes[0].legs[0].steps.length-1) {
        var sentence = instruction + ". Na " + distance + " ";
        directions += sentence;
      } else {
        var sentence = instruction;
        directions += sentence;
      }
    }
    startLatitude = response.data.routes[0].legs[0].start_location.lat;
    startLongitude = response.data.routes[0].legs[0].start_location.lng;
    endLatitude = response.data.routes[0].legs[0].end_location.lat;
    endLongitude = response.data.routes[0].legs[0].end_location.lng;

    res.json({distance: response.data.routes[0].legs[0].distance.text, duration: response.data.routes[0].legs[0].duration.text, directions: directions, 
      startLatitude: startLatitude, startLongitude: startLongitude, endLatitude: endLatitude, endLongitude: endLongitude});
  }
  getDirections();
};


