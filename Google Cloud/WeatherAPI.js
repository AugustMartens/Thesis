const axios = require('axios');

exports.main = (req, res) => {
  async function getWeather() {
    try {
      var city = req.query.geoCity;
      console.log('city:' + city);
      //check if the geoCity parameter has been specified
      if (!city) {
        //if not: retrieve the city name from the database
        var res_stad = await axios.get('https://us-central1-thesis-c30c3.cloudfunctions.net/Plaatsnaam');
        city = res_stad.data.stad;
      }
      console.log('Parameter Geo-city: ' + city);

      const apikey = 'QjGjP67ZwpuSQ9kLFm2V4lsseH45PASE';

      //axios request to get the appropriate code for the city
      var response1 = await axios.get('http://dataservice.accuweather.com/locations/v1/cities/search', {
        params: {
          apikey: apikey,
          q: city,
          language: 'nl-be'
        }
      });

      var citycode = response1.data[0].Key;
      console.log(citycode);

      //make a request with the appropriate city code to retrieve weather data
      const URL = 'http://dataservice.accuweather.com/currentconditions/v1/' + citycode;

      var response2 = await axios.get(URL, {
        params: {
          apikey: apikey,
          language: 'nl-be'
        }
      });

      console.log(response2.data[0]);
      //set retrieved variables
      var weather = response2.data[0].WeatherText.toLowerCase();
      var temp = response2.data[0].Temperature.Metric.Value;
      var icon = response2.data[0].WeatherIcon;

      //create the sentence that the smart speaker has to say
      var final = `Het weer in ${city} is ${weather} met een temperatuur van ${temp} graden.`;

      res.json({data_final: final, data_city: city, data_weather: weather, data_temp:  temp, data_icon: icon});
    } catch(e) {
      //if an error occured log the error in the logfiles and create an error sentecne for the smart speaker
      console.log("Error occured calling weather function: " + e);
      res.json({final: "De data over het weer kan momenteel niet opgehaald worden", data_city: "/", data_weather: "/", data_temp: "/", data_icon: "/"});
    } 
  }

  getWeather();
};

