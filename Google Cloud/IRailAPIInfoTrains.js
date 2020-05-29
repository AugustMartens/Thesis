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
      var response = await axios.get('https://api.irail.be//connections/', {
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
      // Format the data to sent back to Dialogflow
      var tekst = 'De trein met richting ' + req.query.to + ' kunt u nemen op volgende tijdstippen: ';
      for (var i = 0; i < response.data.connection.length; i++){
        tekst += getTime(response.data.connection[i].departure.time);
        if (i != response.data.connection.lengt-1) {
          tekst += ", ";
        }
      }
      res.json({dataTrein: tekst});
    } catch(e) {
      // Check if an error occured
      console.log(e);
      res.json({dataTrein: "Sorry er ging iets mis. Probeer het later opnieuw!"})
    }
  }
  function getTime(date) {
    var date = new Date(date*1000+ 2*60*60*1000);
    var hours = date.getHours();
    var minutes = "0" + date.getMinutes();
    var b = hours + ':' + minutes.substr(-2);
    return b;
  }
  run();
};

