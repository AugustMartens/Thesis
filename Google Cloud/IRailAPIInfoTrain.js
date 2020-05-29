const axios = require('axios');
const stringify = require('json-stringify');

exports.main = (req, res) => {
  async function run() {
    try {
      var givenDate = req.query.date;
      console.log('GivenDate:' + givenDate);
      // Start with creating a date and time variable in the appropriate format for the API
      if (!givenDate) {
        var today = new Date();
        today.setHours(today.getHours() + 2);
        var dd = String(today.getDate()).padStart(2, '0');
        var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
        var yyyy = String(today.getFullYear()).substr(2, 3);
        var ddmmyy = dd+mm+yyyy;
        console.log('Dialogflow Date format: ' + JSON.stringify(ddmmyy));
        var hours = String(today.getHours());
        var minutes = "0" + today.getMinutes();
        minutes = String(minutes);
        var hhmm = hours + minutes.substr(-2);
        console.log('Dialogflow Date format: ' + JSON.stringify(hhmm));
      } else {
        var dd = givenDate.substr(8,2);
        var mm = givenDate.substr(5,2);
        var yyyy = givenDate.substr(0,4);
        var ddmmyy = dd + mm + yyyy;
        var hh = givenDate.substr(11,2);
        var mm = givenDate.substr(14,2);
        var hhmm = hh + mm;
        console.log('Date format: ' + ddmmyy);
        console.log('Date format: ' + hhmm);
      }      

      // Make a request with the given parameters
      var a = await axios.get('https://api.irail.be//connections/', {
        params: {
          from: req.query.from,
          to: req.query.to,
          date: ddmmyy,
          time: hhmm,
          lang: 'nl',
          format: 'json',
          typeOfTransport: 'automatic'
        }
      });
      var date = new Date(a.data.connection[0].departure.time*1000+ 2*60*60*1000);
      var hours = date.getHours();
      var minutes = "0" + date.getMinutes();
      var b = hours + ':' + minutes.substr(-2);
      // Format the data to sent back to Dialogflow
      if (a.data.connection[0].departure.canceled === 1) {
        res.json({dataTrein: "Deze trein is jammer genoeg gecanceled."});
      }
      var tekst = 'De trein vertrekt om ' + b + ' vanaf perron ' 
      + a.data.connection[0].departure.platform + ' met richting ' 
      + a.data.connection[0].departure.direction.name + '. '
      + 'De trein heeft momenteel een vertraging van ' 
      + a.data.connection[0].departure.delay + ' minuten. '
      + 'Deze trein maakt ' + a.data.connection[0].departure.stops.number + ' tussenstops. ' 
      + 'De trein stopt in volgende stations: ';
      for (var i = 0; i < a.data.connection[0].departure.stops.stop.length; i++) {
        tekst += a.data.connection[0].departure.stops.stop[i].station;
        if (i != a.data.connection[0].departure.stops.stop.length-1) {
          tekst += ", ";
        }
      }
      tekst += ".";

      res.json({dataTrein: tekst, platform: a.data.connection[0].departure.platform, direction: a.data.connection[0].departure.direction.name, stops: a.data.connection[0].departure.stops.number, delay: a.data.connection[0].departure.delay});
    } catch(e) {
      // Check if an error occured
      console.log(e);
      res.json({dataTrein: "Sorry er ging iets mis. Probeer het later opnieuw!"})
    }
  }
  run();
};

