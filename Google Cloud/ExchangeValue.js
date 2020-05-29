const axios = require('axios');

exports.main = (req, res) => {
  async function getRate() {
      var currency1 = req.query.currency1;
      var amount = req.query.amount;
      var currency2 = req.query.currency2;

      var url = 'https://api.exchangeratesapi.io/latest?' ;
      var response = await axios.get(url, 
        {
          params: {
              base : currency1
            }       
        }).then(response => {

        var exchange_rate = response.data.rates[currency2];
        var value = exchange_rate*amount;
        var final = "Deze hoeveelheid is omgerekend " + value + ".";
        res.json({dataExchange: final, exchangeRate: response.data.rates[currency2], amount: value})
      }).catch (error => {
        var finalError = "Sorry de opgevraagde data kon momenteel niet opgevraagd worden. Probeer het later opnieuw!";
        res.json({dataExchange: finalError});
        console.log(error);
      });
  }
  getRate();
  
};

