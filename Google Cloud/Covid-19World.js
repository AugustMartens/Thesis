const axios = require('axios');

exports.main = (req, res) => {
  async function getData() {
    try {
    const url = 'https://api.covid19api.com/summary';
    var response = await axios.get(url);
    var global = response.data.Global;
    var newConfirmed = global.NewConfirmed;
    var totalConfirmed = global.TotalConfirmed;
    var newDeaths = global.NewDeaths;
    var totalDeaths = global.TotalDeaths;
    var newRecovered = global.NewRecovered;
    var totalRecovered = global.TotalRecovered;
    var final = "Vandaag werden er wereldwijd " + newConfirmed + " nieuwe besmettingen vastgesteld van COVID-19. "
    + "Dit brengt het totaal aantal besmettingen op " + totalConfirmed + ". " 
    + "Het aantal overlijdens vandaag bedraagt: " + newDeaths + ". "
    + "Dit brengt het totaal aantal overlijdens globaal op " + totalDeaths + ".";
    res.json({data_covid: final, newConfirmed: newConfirmed, totalConfirmed: totalConfirmed, 
      newDeaths: newDeaths, totalDeaths: totalDeaths, newRecovered: newRecovered, totalRecovered: totalRecovered});
    } catch(e) {
      console.log(e);
      res.json({data_covid: "De data kon niet gevonden worden. Probeer het later opnieuw."});
    }
  }

  getData();
  
};

