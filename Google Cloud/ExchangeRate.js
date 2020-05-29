const axios = require('axios');

exports.main = (req, res) => {
  async function getRate() {
      var currency1 = req.query.currency1;
      var currency2 = req.query.currency2;

      var url = 'https://api.exchangeratesapi.io/latest?' ;
      var response = await axios.get(url, 
        {
          params: {
              base : currency1
            }       
        }).then(response => {
        var final = "De huidige koers is " + response.data.rates[currency2] + ".";
        res.json({dataExchange: final, exchangeRate: response.data.rates[currency2]});
      }).catch (error => {
        var finalError = "Sorry de opgevraagde data kon momenteel niet opgevraagd worden. Probeer het later opnieuw!";
        res.json({dataExchange: finalError});
        console.log(error);
      });
  }
  getRate();
  
};

