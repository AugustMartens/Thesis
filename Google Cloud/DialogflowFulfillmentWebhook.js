'use strict';
'esversion: 8';
 
const functions = require('firebase-functions');
const {WebhookClient} = require('dialogflow-fulfillment');
const {Card, Suggestion} = require('dialogflow-fulfillment');
const axios = require('axios');
var SpotifyWebApi = require('spotify-web-api-node');


 
process.env.DEBUG = 'dialogflow:debug'; // enables lib debugging statements
 
exports.dialogflowFulfillment = functions.https.onRequest((request, response) => {
  const agent = new WebhookClient({ request, response });
  console.log('Dialogflow Request headers: ' + JSON.stringify(request.headers));
  console.log('Dialogflow Request body: ' + JSON.stringify(request.body));
 
  function welcome(agent) {
    agent.add(`Welcome to my agent!`);
  }
 
  function fallback(agent) {
    agent.add(`I didn't understand`);
    agent.add(`I'm sorry, can you try again?`);
  }
  
  async function weather(agent) {
    const URL = 'https://europe-west1-thesis-c30c3.cloudfunctions.net/weatherAPI';
    var geoCity = agent.parameters["geo-city"];

    var response = await axios.get(URL, {
      params: {
        geoCity: geoCity
      }
    });

    agent.add(response.data.data_final);
    const context = {'name': 'weather','lifespan': 1, 'parameters': {'intentId': 100, 'city': response.data.data_city, 'temp': response.data.data_temp, 'text': response.data.data_weather, 'icon': response.data.data_icon}};
    agent.setContext(context);
  }

async function wikipedia(agent) {
  var quest = agent.parameters["any"];
  var url = 'https://europe-west1-thesis-c30c3.cloudfunctions.net/ExternalCallsEurope';
  var resWiki = await axios.get(url, 
    {
      params: {
        intent: 'Wikipedia',
        message: quest
      }
  });
  var finalWiki = resWiki.data.data_wikipedia;
  agent.add(finalWiki);
  const context = {'name': 'Wikipedia','lifespan': 1, 'parameters': {'intentId': 112}};
  agent.setContext(context);
}

async function wikipediaExtended(agent) {
  var quest = agent.parameters["any"];
  var url = 'https://europe-west1-thesis-c30c3.cloudfunctions.net/ExternalCallsEurope';
  var resWiki = await axios.get(url, 
    {
      params: {
        intent: 'WikipediaExtended',
        message: quest
      }
  });
  var finalWiki = resWiki.data.data_wikipedia;
  agent.add(finalWiki);
  const context = {'name': 'WikipediaExtended','lifespan': 1, 'parameters': {'intentId': 113}};
  agent.setContext(context);
}

async function moppen(agent) {
  var res_mop = await axios.get('https://europe-west1-thesis-c30c3.cloudfunctions.net/ExternalCallsEurope', 
    {
      params: {
        intent: 'moppen'
      }
  });
  var final_mop = res_mop.data.data_mop;
  agent.add(final_mop);
  const context = {'name': 'Mop','lifespan': 1, 'parameters': {'intentId': 300}};
  agent.setContext(context);
}

async function horoscoop(agent) {
  var res_hor = await axios.get('https://europe-west1-thesis-c30c3.cloudfunctions.net/ExternalCallsEurope', 
    {
      params: {
        intent: 'horoscoop'
      }
  });
  var final_hor = res_hor.data.data_hor;
  agent.add(final_hor);
  const context = {'name': 'horoscoop','lifespan': 1, 'parameters': {'intentId': 301}};
  agent.setContext(context);
}

async function train(agent) {
  var from = agent.parameters["geo-city"];
  var to = agent.parameters["geo-city1"];
  var date = agent.parameters["date-time"].date_time;
  var response = await axios.get('https://europe-west1-thesis-c30c3.cloudfunctions.net/iRailAPI', 
    {
      params: {
        from: from,
        to: to,
        date: date
      }
  });
  var finalTrein = response.data.dataTrein;
  agent.add(finalTrein);
  const context = {'name': 'train','lifespan': 1, 'parameters': {'intentId': 108, 'time': response.data.time, 
  'platform': response.data.platform, 'direction': response.data.direction}};
  agent.setContext(context);
}

async function trainList(agent) {
  var from = agent.parameters["geo-city"];
  var to = agent.parameters["geo-city1"];
  var date = agent.parameters["date-time"].date_time;
  var response = await axios.get('https://europe-west1-thesis-c30c3.cloudfunctions.net/iRailAPILijstTreinen', 
    {
      params: {
        from: from,
        to: to,
        date: date
      }
  });
  var finalTrain = response.data.dataTrein;
  agent.add(finalTrain);
  const context = {'name': 'trainList','lifespan': 1, 'parameters': {'intentId': 109, 'platform': response.data.platform, 
  'direction': response.data.direction, 'stop': response.data.stop, 'delay': response.data.delay}};
  agent.setContext(context);
}

async function trainInfo(agent) {
  var from = agent.parameters["geo-city"];
  var to = agent.parameters["geo-city1"];
  var date = agent.parameters["date-time"].date_time;
  var response = await axios.get('https://us-central1-thesis-c30c3.cloudfunctions.net/iRailAPIInfoTrein', 
    {
      params: {
        from: from,
        to: to,
        date: date
      }
  });
  var finalTrain = response.data.dataTrein;
  agent.add(finalTrain);
  const context = {'name': 'trainInfo','lifespan': 1, 'parameters': {'intentId': 110, 'platform': response.data.platform, 
  'direction': response.data.direction, 'stop': response.data.stop, 'delay': response.data.delay}};
  agent.setContext(context);
}

async function exchange(agent) {
  var currency1 = agent.parameters["currency-name"];
  var currency2 = agent.parameters["currency-name1"];
  var url = 'https://europe-west1-thesis-c30c3.cloudfunctions.net/ExchangeRate?';
  var response = await axios.get(url, 
    {
      params: {
        currency1: currency1,
        currency2: currency2
      }
  });
  var finalCurr = response.data.dataExchange;
  agent.add(finalCurr);
  const context = {'name': 'exchangeRate','lifespan': 1, 'parameters': {'intentId': 103, 'exchangeRate': response.data.exchangeRate}};
  agent.setContext(context);
};

async function exchangeValue(agent) {
  var currency1 = agent.parameters["unit-currency"].currency;
  var amount = agent.parameters["unit-currency"].amount;
  var currency2 = agent.parameters["currency-name"];
  var url = 'https://europe-west1-thesis-c30c3.cloudfunctions.net/ExchangeValue?';
  var response = await axios.get(url, 
    {
      params: {
        currency1: currency1,
        amount: amount,
        currency2: currency2
      }
  });
  var finalCurr = response.data.dataExchange;
  agent.add(finalCurr);
  const context = {'name': 'exchangeValue','lifespan': 1, 'parameters': {'intentId': 104, 'exchangeRate': response.data.exchangeRate, 'amount': response.data.amount}};
  agent.setContext(context);
};

async function verkeer(agent) {
  var res_verkeer = await axios.get('https://europe-west1-thesis-c30c3.cloudfunctions.net/ExternalCallsEurope', 
    {
      params: {
        intent: 'verkeersInformatie'
      }
  });
  var final_verkeer = res_verkeer.data.data_verkeer;
  agent.add(final_verkeer);
  const context = {'name': 'verkeer','lifespan': 1, 'parameters': {'intentId': 302}};
  agent.setContext(context);
}

async function movie(agent) {
  var title = agent.parameters["any"];
  var response = await axios.get('https://europe-west1-thesis-c30c3.cloudfunctions.net/MovieInformation?', 
    {
      params: {
        title: title
      }
  });
  var finalMovie = response.data.dataMovie;
  agent.add(finalMovie);
  const context = {'name': 'movie','lifespan': 1, 'parameters': {'intentId': 111, 'title': response.data.title, 'year': response.data.year, 'actors': response.data.actors,
    'genre': response.data.genre, 'plot': response.data.plot, 'production': response.data.production, 'imdbRating': response.data.imdbRating}};
  agent.setContext(context);
};

async function muziek(agent) {
  var song = agent.parameters["song"];
  var artist = agent.parameters["music-artist"];

  var res_spotify = await axios.get('https://us-central1-thesis-c30c3.cloudfunctions.net/Spotify', 
    {
      params: {
        song: song,
        artist: artist
      }
  });

  console.log(res_spotify.data.final);
  agent.add(res_spotify.data.final);
  const context = {'name': 'muziek','lifespan': 1, 'parameters': {'intentId': 200, 'nummerNaam': res_spotify.data.nummerNaam, 'artiest': res_spotify.data.artiest, 'album': res_spotify.data.album, 'imgUrl': res_spotify.data.imgUrl, 'prevUrl': res_spotify.data.prevUrl}};
  console.log(context);
  agent.setContext(context);

};

async function listEvents(agent) {
  var session = agent.session;
  console.log('session:' + session.split("/").pop());
  var response = await axios.get('https://europe-west1-thesis-c30c3.cloudfunctions.net/GoogleCalendarListEvents', {
    params: {
      speakerId: session.split("/").pop()
    }
  });
  console.log(JSON.stringify(response.data));
  var final = response.data.nextEvent;
  console.log(final);
  agent.add(final);
  const context = {'name': 'listEvents','lifespan': 1, 'parameters': {'intentId': 202}};
  agent.setContext(context);
}

async function addEvent(agent) {
  var session = agent.session;
  console.log('session:' + session.split("/").pop());
  var summary = agent.parameters["any"];
  var location = agent.parameters["geo-city"];
  var description = agent.parameters["any1"];
  var startTime = agent.parameters["time-period"].startTime;
  var endTime = agent.parameters["time-period"].endTime;
  var response = await axios.get('https://europe-west1-thesis-c30c3.cloudfunctions.net/GoogleCalendarAddEvent', {
    params: {
      summary: summary,
      location: location,
      description: description,
      startTime: startTime,
      endTime: endTime,
      speakerId: session.split("/").pop()
    }
  });
  var final = response.data.event;
  agent.add(final);
  const context = {'name': 'addEvent','lifespan': 1, 'parameters': {'intentId': 203, 'summary': summary, 'location': location, 'description': description, 'startTime': startTime, 'endTime': endTime}};
  agent.setContext(context);
}

async function directions(agent) {
  var dirOrigin = agent.parameters["geo-city"];
  var dirDestination = agent.parameters["geo-city1"];
  var walking = agent.parameters["transportWalking"];
  var cycling = agent.parameters["transportCycling"];
  var driving = agent.parameters["transportCar"]
  var response = await axios.get('https://europe-west1-thesis-c30c3.cloudfunctions.net/GoogleDirections?', 
    {
      params: {
        mode: 'driving',
        origin: dirOrigin,
        destination: dirDestination,
        cycling: cycling,
        walking: walking,
        driving: driving
      }
  });
  var finalDirections = response.data.directions;
  var startLat = response.data.startLatitude;
  var startLng = response.data.startLongitude;
  var endLat = response.data.endLatitude;
  var endLng = response.data.endLongitude;
  agent.add(finalDirections);
  const context = {'name': 'directions','lifespan': 1, 'parameters': {'intentId': 105, 'duration': response.data.newConfirmed, 'distance': response.data.distance, 
    'startLat': startLat, 'startLng': startLng, 'endLat': endLat, 'endLng': endLng}};
  agent.setContext(context);
}

async function covid19Belgium(agent) {
  var response = await axios.get('https://europe-west1-thesis-c30c3.cloudfunctions.net/COVID-19Belgium');
  var finalInfoBelgium = response.data.data_covid;
  agent.add(finalInfoBelgium);
  const context = {'name': 'covid19Belgium','lifespan': 1, 'parameters': {'intentId': 101, 'newConfirmed': response.data.newConfirmed, 'totalConfirmed': response.data.totalConfirmed,
   'newDeaths': response.data.newDeaths, 'totalDeaths': response.data.totalDeaths, 'newRecovered': response.data.newRecovered, 'totalRecovered': response.data.totalRecovered}};
  agent.setContext(context);
}

async function covid19Global(agent) {
  var response = await axios.get('https://europe-west1-thesis-c30c3.cloudfunctions.net/COVID-19World');
  var finalInfoGlobal = response.data.data_covid;
  agent.add(finalInfoGlobal);
  const context = {'name': 'covid19Global','lifespan': 1, 'parameters': {'intentId': 102, 'newConfirmed': response.data.newConfirmed, 'totalConfirmed': response.data.totalConfirmed,
   'newDeaths': response.data.newDeaths, 'totalDeaths': response.data.totalDeaths, 'newRecovered': response.data.newRecovered, 'totalRecovered': response.data.totalRecovered}};
  agent.setContext(context);
}

async function googlePlaces(agent) {
  var input = agent.parameters["any"];
  var url = 'https://europe-west1-thesis-c30c3.cloudfunctions.net/GooglePlaces?';
  var response = await axios.get(url, {
    params: {
      input: input
    }
  });
  var finalGP = response.data.final;
  agent.add(finalGP);
  const context = {'name': 'googlePlaces','lifespan': 1, 'parameters': {'intentId': 106, 'formattedAddress': response.data.formattedAddress, 
  'formattedPhoneNumber': response.data.formattedPhoneNumber, 'name': response.data.name, 'rating': response.data.rating}};
  agent.setContext(context);
}

async function googlePlacesOpeningHours(agent) {
  var input = agent.parameters["any"];
  var url = 'https://europe-west1-thesis-c30c3.cloudfunctions.net/GooglePlacesOpeningHours?';
  var response = await axios.get(url, {
    params: {
      input: input
    }
  });
  var finalOH = response.data.openingHours;
  agent.add(finalOH);
  const context = {'name': 'googlePlacesOpeningHours','lifespan': 1, 'parameters': {'intentId': 107, 'openingHours': response.data.openingHours}};
  agent.setContext(context);
}

async function mail(agent) {
  var session = agent.session;
  console.log('session:' + session.split("/").pop());
  var url = 'https://europe-west1-thesis-c30c3.cloudfunctions.net/GMail ';
  var response = await axios.get(url, {
    params: {
      speakerId: session.split("/").pop()
    }
  });
  var final = response.data.email;
  agent.add(final);
  const context = {'name': 'mail','lifespan': 1, 'parameters': {'intentId': 201}};
  agent.setContext(context);
}

async function vRTNewsArticles(agent) {
  var url = 'https://europe-west1-thesis-c30c3.cloudfunctions.net/VRTnewsArticles';
  var response = await axios.get(url);
  var final = response.data.data;
  agent.add(final);
  const context = {'name': 'VRTNewsArticles','lifespan': 1, 'parameters': {'intentId': 114}};
  agent.setContext(context);
}

async function vRTNewsGetArticle(agent) {
  var number = agent.parameters["number-integer"];
  var url = 'https://europe-west1-thesis-c30c3.cloudfunctions.net/VRTNewsGetArticle';
  var response = await axios.get(url,{
    params: {
      number: number
    }
  });
  var final = response.data.data;
  agent.add(final);
  const context = {'name': 'VRTNewsGetArticle','lifespan': 1, 'parameters': {'intentId': 115}};
  agent.setContext(context);
}

async function sporzaNewsArticles(agent) {
  var url = 'https://europe-west1-thesis-c30c3.cloudfunctions.net/SporzaNewsArticles';
  var response = await axios.get(url);
  var final = response.data.data;
  agent.add(final);
  const context = {'name': 'SporzaNewsArticles','lifespan': 1, 'parameters': {'intentId': 116}};
  agent.setContext(context);
}

async function sporzaGetArticle(agent) {
  var number = agent.parameters["number-integer"];
  var url = 'https://europe-west1-thesis-c30c3.cloudfunctions.net/SporzaNewsGetArticle';
  var response = await axios.get(url,{
    params: {
      number: number
    }
  });
  var final = response.data.data;
  agent.add(final);
  const context = {'name': 'SporzaNewsGetArticle','lifespan': 1, 'parameters': {'intentId': 117}};
  agent.setContext(context);
}

async function vrt(agent){
  agent.add("journaal");
  const context = {'name': 'VRT','lifespan': 1, 'parameters': {'intentId': 118}};
  agent.setContext(context);
}

async function quivr(agent) {
  var url = 'https://europe-west1-thesis-c30c3.cloudfunctions.net/Toledo';
  var response = await axios.get(url);
  var final = response.data.schedule;
  agent.add(final);
  const context = {'name': 'Quivr','lifespan': 1, 'parameters': {'intentId': 119}};
  agent.setContext(context);
}


  
  
  // Run the proper function handler based on the matched Dialogflow intent name
  let intentMap = new Map();
  intentMap.set('Default Welcome Intent', welcome);
  intentMap.set('Default Fallback Intent', fallback);
  intentMap.set('Weer', weather);
  intentMap.set('Wikipedia', wikipedia);
  intentMap.set('WikipediaExtended', wikipediaExtended);
  intentMap.set('Mop', moppen);
  intentMap.set('Horoscoop', horoscoop);
  intentMap.set('iRail', train);
  intentMap.set('iRailLijstTreinen', trainList);
  intentMap.set('iRailInfo', trainInfo);
  intentMap.set('Exchange Rate', exchange);
  intentMap.set('ExchangeValue', exchangeValue);
  intentMap.set('VerkeersInformatie', verkeer);
  intentMap.set('Movie', movie);
  intentMap.set('Muziek', muziek);
  intentMap.set('GoogleCalendarList', listEvents);
  intentMap.set('GoogleCalendarAddEvent', addEvent);
  intentMap.set('Directions', directions);
  intentMap.set('COVID-19Belgium', covid19Belgium);
  intentMap.set('COVID-19Global', covid19Global);
  intentMap.set('GooglePlaces', googlePlaces);
  intentMap.set('GooglePlacesOpeninghours', googlePlacesOpeningHours);
  intentMap.set('Gmail', mail);
  intentMap.set('VRTNewsArticles', vRTNewsArticles);
  intentMap.set('VRTNewsGetArticle', vRTNewsGetArticle);
  intentMap.set('SporzaNewsArticles', sporzaNewsArticles);
  intentMap.set('SporzaGetArticle', sporzaGetArticle);
  intentMap.set('VRT', vrt);
  intentMap.set('Quivr', quivr);

  // intentMap.set('your intent name here', yourFunctionHandler);
  // intentMap.set('your intent name here', googleAssistantHandler);
  agent.handleRequest(intentMap);
});


