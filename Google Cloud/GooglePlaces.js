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
      var formattedAddress = response.data.result.formatted_address;
      var formattedPhoneNumber = response.data.result.formatted_phone_number;
      var name = response.data.result.name;
      var rating = response.data.result.rating;
      var final = "Het adres van  " + name +": " + formattedAddress + ". "
      + "Telefoonnummer: " + formattedPhoneNumber + ". "
      + "Deze plaats kreeg een rating van " + rating + " op Google Places.";
      res.json({formattedAddress: formattedAddress, formattedPhoneNumber: formattedPhoneNumber, name: name, rating: rating, final: final});
    } catch (e) {
      console.log(e);
      res.json({finalPlaces: "Sorry, er ging iets mis. Probeer het later opnieuw!"});
    }
  }
  places();
};

