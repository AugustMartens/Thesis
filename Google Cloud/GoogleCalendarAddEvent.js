const { google } = require('googleapis')
const { OAuth2 } = google.auth
const axios = require('axios');

exports.main = (req, resp) => {
  async function addEvent() {
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

      var location = req.query.location;
      var description = req.query.description;
      if (!location){
        location = "niet gespecificeerd";
      }
      if (!description) {
        description = "niet gespecificeerd";
      }
      const event = {
        summary: req.query.summary,
        location: location,
        description: description,
        colorId: 1,
        start: {
          dateTime: req.query.startTime,
          timeZone: 'Etc/GMT+2',
        },
        end: {
          dateTime: req.query.endTime,
          timeZone: 'Etc/GMT+2',
        },
      }

    calendar.freebusy.query(
      {
        resource: {
          timeMin: req.query.startTime,
          timeMax: req.query.endTime,
          timeZone: 'Etc/GMT+2',
          items: [{ id: 'primary' }],
        },
      },
      (err, res) => {
        if (err){
          return console.error('Free Busy Query Error: ', err)
          resp.json({event: 'Sorry, er ging iets mis. Probeer het later opnieuw!'});
          }
        const eventArr = res.data.calendars.primary.busy;

        if (eventArr.length === 0)
          return calendar.events.insert(
            { calendarId: 'primary', resource: event },
            err => {
              if (err) return console.error('Error Creating Calender Event:', err)
              console.log('Calendar event successfully created.')
              resp.json({event: 'De afspraak is succesvol toegevoegd aan je agenda!'});
            }
          )
        console.log(`Sorry je hebt dan al reeds iets gepland.`)
        resp.json({event: `Sorry we konden het niet aan je agenda toevoegen doordat je al reeds iets op dat moment ingepland hebt.`});
      })
    } catch (e) {
      console.log(e);
      resp.json({event: 'Sorry, er ging iets mis. Probeer het later opnieuw!'});
    }
  }
  addEvent();
};

