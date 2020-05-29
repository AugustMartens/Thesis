const { google } = require('googleapis')
const { OAuth2 } = google.auth
const axios = require('axios');
const stringify = require('json-stringify');

exports.main = (req, resp) => {

  async function getMessages() {
    try {
      var credentials = await axios.get('https://europe-west1-thesis-c30c3.cloudfunctions.net/GoogleAT', {
          params: {
            speakerId: req.query.speakerId
          }
      });

      if (!credentials.data.access_token) {
        resp.json({dataEvents: 'Uw account heeft geen toegang tot uw Google Calendar.'});
      }

      auth = new google.auth.OAuth2(
        process.env.clientID,
        process.env.clientSecret
      );
      
      auth.setCredentials({
        refresh_token: credentials.data.refresh_token
      });
      const accessToken = await auth.getAccessToken();
      console.log("accessToken: " + JSON.stringify(accessToken));
      auth.setCredentials({
        access_token: accessToken.token
      });

      const gmail = google.gmail({version: 'v1', auth});
      var request = await gmail.users.messages.list({
        userId: 'me',
        labelIds: 'INBOX',
        maxResults: 1,
        q: 'is:unread'
      });
      let id = request.data.messages[0].id;
      console.log(id);
      var request2 = await gmail.users.messages.get({
        userId: 'me',
        id: id
      });
      console.log('request2:' + JSON.stringify(request2.data));
      let msg = 'De laatste mail die u ontving: ' + request2.data.snippet;
      await gmail.users.messages.modify({
        'userId':'me',
        'id':id,
        'resource': {
            'addLabelIds':[],
            'removeLabelIds': ['UNREAD']
        }
      });
      resp.json({email: msg});
    } catch (e) {
      console.log(e);
      resp.json({email: e});
    }
  }


  getMessages();

};

