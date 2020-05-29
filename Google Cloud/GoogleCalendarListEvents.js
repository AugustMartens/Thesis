const { google } = require('googleapis');
const { OAuth2 } = google.auth;
const axios = require('axios');

exports.main = (req, resp) => {

  async function listEvents(auth) {
    try {
      var at = await axios.get('https://us-central1-thesis-c30c3.cloudfunctions.net/GoogleATCalendar', {
          params: {
            speakerId: req.query.speakerId
          }
        });

      auth = new google.auth.OAuth2(
        process.env.clientID,
        process.env.clientSecret
      );
      
      auth.setCredentials({
        refresh_token: at.data.refresh_token
      });
      const accessToken = await auth.getAccessToken();
      console.log("accessToken: " + JSON.stringify(accessToken));
      auth.setCredentials({
        access_token: accessToken.token
      });

      const calendar = google.calendar({version: 'v3', auth});

      var date = (new Date()).toISOString();

      calendar.events.list({
        calendarId: 'primary',
        timeMin: date,
        maxResults: 10,
        singleEvents: true,
        orderBy: 'startTime',
      }, (err, res) => {
        if (err) return console.log('The API returned an error: ' + err);
        const events = res.data.items;
        if (events.length) {
          console.log('Upcoming 10 events:');
          events.map((event, i) => {
            const start = event.start.dateTime || event.start.date;
            var dataReturn = `${start} - ${event.summary}`;
            console.log(`${start} - ${event.summary}`);
            resp.json({nextEvent: dataReturn});
          });
        } else {
          resp.json({nextEvent: 'Er konden geen opkomende afspraken gevonden worden in uw agenda.'});
        }
      });
    } catch (e) {
      console.log(e);
      resp.json({nextEvent: "Sorry, er ging iets mis. Probeer het later opnieuw!"});
    }
  }

  listEvents();
};
