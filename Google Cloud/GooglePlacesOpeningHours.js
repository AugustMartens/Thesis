const axios = require('axios');
const stringify = require('json-stringify');

exports.main = (req, res) => {
  async function places() {
    try {
      var input = req.query.input;
      var url = 'https://maps.googleapis.com/maps/api/place/findplacefromtext/json?';
      var response = await axios.get(url, {
        params: {
          key: process.env.KEY,
          input: input,
          inputtype: 'textquery',
          language: 'nl'
        }
      });
      console.log(JSON.stringify(response.data));
      var id = response.data.candidates[0].place_id;
      console.log('id:' + id);
      url = 'https://maps.googleapis.com/maps/api/place/details/json?';
      response = await axios.get(url, {
        params: {
          place_id: id,
          key: process.env.KEY,
          language: 'nl'
        }
      });
      console.log(JSON.stringify(response.data));
      var openingHours = 'De openingsuren zijn als volgt: ';
      for(var i = 0; i < response.data.result.opening_hours.weekday_text.length; i++) {
        openingHours += response.data.result.opening_hours.weekday_text[i];
        if (i !== response.data.result.opening_hours.weekday_text.length-1) {
          openingHours += ',';
        }
      }
      openingHours += '.';
      res.json({openingHours: openingHours});
    } catch (e) {
      console.log(e);
      res.json({openingHours: 'Sorry, de openingsuren konden niet gevonden worden.'});
    }
  }
  places();
};

