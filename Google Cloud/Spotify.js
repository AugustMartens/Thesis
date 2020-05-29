var SpotifyWebApi = require('spotify-web-api-node');

exports.main = (req, res) => {
  async function music() {
    try {
      var song = req.query.song;
      var artist = req.query.artist;
      var query;

      // Check which of the song and artist parameters are passed
      if(!artist){
        if(!song){
          console.log("Geen artist, geen song");
        }else{
          console.log(`Geen artist, song: ${song}`);
          query = `${song}`;
        }
      }else{
        if(!song){
          console.log(`artist: ${artist}, geen song`);
          query = `${artist}`;
        }else{
          console.log(`artist: ${artist}, song: ${song}`);
          query = `${song} ${artist}`;
        }
      }

      // Client id and secret
      var clientId = 'cd31c3cb01904210bf23e412d10f2cdc',
      clientSecret = '6da641311a5a4cacb7a55564a5f55250';

      // Create the api object with the credentials
      var spotifyApi = new SpotifyWebApi({
        clientId: clientId,
        clientSecret: clientSecret
      });

      // Get an access token from the spotify API
      var cred = await spotifyApi.clientCredentialsGrant();
      console.log('The access token expires in ' + cred.body['expires_in']);
      console.log('The access token is ' + cred.body['access_token']);
      // Save the access token so that it's used in future calls
      spotifyApi.setAccessToken(cred.body['access_token']);
        
      // Search for the requested song or Artist
      var data = await spotifyApi.searchTracks(query,{ limit:1 , market: 'BE'});
      console.log(data.body.tracks.items[0]);
      // Get the necessary parameters
      let nummerNaam = data.body.tracks.items[0].name;
      let artiest = data.body.tracks.items[0].artists[0].name;
      let album = data.body.tracks.items[0].album.name;
      let imgUrl = data.body.tracks.items[0].album.images[1].url;
      let prevUrl =data.body.tracks.items[0].preview_url

      // Check if a certain song can be played and create the sentence for the Dialogflow API
      if(prevUrl == null){
      var final = `Ik wil graag het nummer ${nummerNaam} van ${artiest} voor je afspelen, maar door een rechtenbeperking in jouw land is dit niet mogelijk.`;
      }else{
      var final = `Ok, ik speel het nummer ${nummerNaam} van ${artiest} af.`;
      }
      // Sent the data back to the fullfillment webhook
      res.json({'final': final, 'nummerNaam': nummerNaam, 'artiest': artiest, 'album': album, 'imgUrl': imgUrl, 'prevUrl': prevUrl});
    } catch(e) {
      // Check if an error occured
      console.log(e);
      res.send(e);
    }
  }
  
  music();
};

