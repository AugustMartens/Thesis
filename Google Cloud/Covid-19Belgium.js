const axios = require('axios');

exports.main = (req, res) => {
  async function getData() {
    try {
    const url = 'https://api.covid19api.com/summary';
    var response = await axios.get(url);
    var belgium = response.data.Countries[16];
    var newConfirmed = belgium.NewConfirmed;
    var totalConfirmed = belgium.TotalConfirmed;
    var newDeaths = belgium.NewDeaths;
    var totalDeaths = belgium.TotalDeaths;
    var newRecovered = belgium.NewRecovered;
    var totalRecovered = belgium.TotalRecovered;
    var final = "Vandaag werden er " + newConfirmed + " nieuwe besmettingen vastgesteld van COVID-19. "
    + "Dit brengt het totaal aantal besmettingen op " + totalConfirmed + ". " 
    + "Het aantal overlijdens vandaag bedraagt: " + newDeaths + ". "
    + "Dit brengt het totaal aantal overlijdens in België op " + totalDeaths + ".";
    res.json({data_covid: final, newConfirmed: newConfirmed, totalConfirmed: totalConfirmed, 
      newDeaths: newDeaths, totalDeaths: totalDeaths, newRecovered: newRecovered, totalRecovered: totalRecovered});
    } catch(e) {
      console.log(e);
      res.json({data_covid: "De data kon niet gevonden worden. Probeer het later opnieuw."});
    }
  }

  getData();
  
};

