const axios = require('axios');
const projectId = process.env.PROJECTID;
const {Translate} = require('@google-cloud/translate').v2;

exports.main = (req, res) => {

  async function translate(text) {
    const translate = new Translate({projectId});
    const target = 'nl';
    const [translation] = await translate.translate(text, target);
    console.log(`Text: ${text}`);
    console.log(`Translation: ${translation}`);
    return translation;
  }

  async function movie() {
    try {
      var title = req.query.title;
      var url = 'http://www.omdbapi.com/?';

      var response = await axios.get(url, {
        params: {
          t: title,
          i: process.env.ID,
          apikey: process.env.KEY
        }       
      });
      var title = response.data.Title;
      var year = response.data.Year;
      var director = response.data.Director;
      var actors = response.data.Actors;
      var genre = response.data.Genre;
      var plot = await translate(response.data.Plot);
      var imdbRating = response.data.imdbRating;
      var production = response.data.Production;
      var final = title + " is een film uit het jaar: " + year +  
      " en werd geregisseerd door " + director + ". De cast van de film bestaat uit: " +  
      actors + ". Het genre van de film is: " + genre +
      ". Het verhaal van de film gaat als volgt: " + plot + " "
      + "Op IMDB kreeg de film een rating van " + imdbRating + ". "
      + "De productie studio van de film is " + production + ".";
      res.json({dataMovie: final, title: title, year:year, director: director, actors: actors, genre: genre, plot: plot, imdbRating: imdbRating, production: production});
    } catch(e) {
      var finalWrong = "Sorry er ging iets mis! Probeer het later opnieuw.";
      console.log(error);
      res.json({dataMovie: final});
    }
  }

  movie();
};

