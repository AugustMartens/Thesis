// jshint esversion:8

const functions = require('firebase-functions');
const admin = require('firebase-admin');
const cors = require('cors')({ origin: false});

//const serviceAccount = require('./service-account.json');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions

//ADMIN
//admin.initializeApp({
//  credential: admin.credential.cert(serviceAccount)
//});

const { SessionsClient } = require('dialogflow');

// dialogflowGateway
exports.dialogflowGateway = functions
  .region('europe-west1')
  .https.onRequest((request, response) => {
  cors(request, response, async () => {
    console.log("******* NEW REQUEST ******");
    console.log('1 Request from Sepaker: ' + JSON.stringify(request.body));


    //const { queryInput, sessionId } = request.body;
    //console.log(queryInput);
    //console.log(sessionId);
    const sessionId = request.body.id;
    const sessionClient = new SessionsClient();
    const session = sessionClient.sessionPath('thesis-c30c3', sessionId);
    const outputFile = './resources/myOutput.wav';

    const aanv = {
    session: session,
    queryInput: {
      audioConfig: {
        languageCode: 'nl-NL'
        }
      },
      inputAudio: request.body.text ,
      outputAudioConfig: {
        audioEncoding: 'OUTPUT_AUDIO_ENCODING_MP3',
        synthesizeSpeechConfig: {
            "speakingRate": 0.95,
            "pitch": -4,
            "voice": {
              "name": "nl-NL-Wavenet-D"
            }
        }
      },
    };
    console.log('2 Request to Dialogflow: ' + JSON.stringify(aanv));

    const responses = await sessionClient.detectIntent(aanv);
    console.log('3 Resonse from Dialogflow ' + JSON.stringify(responses));

    const result = responses[0].queryResult; //type:
    const audioFile = responses[0].outputAudio; //type: buffer
    const base64data = audioFile.toString('base64'); //type: string

    console.log('4 QueryResonse from Dialogflow ' + JSON.stringify(result));
    console.log('5 ResponseFullfillmentText from Dialogflow ' + JSON.stringify(result.fulfillmentText));

   try {
    console.log(`6 OK Intent: ${result.intent.displayName}`);
    var final = {
    "responseId": result.responseId,
    "sessionId": sessionId,
    "keepAlive": true,
    "intentId": result.outputContexts[0].parameters.fields.intentId.numberValue,
    "intentName": result.intent.displayName,
    "text": result.fulfillmentText,
    "audio": base64data,
    "parameters": result.outputContexts[0].parameters.fields
  }      
    } catch(err) {
    var final = {
    "responseId": result.responseId,
    "sessionId": sessionId,
    "keepAlive": false,
    "intentId": 0000,
    "intentName": 'ERROR',
    "text": `Error ${err.message}`,
    "audio": '',
    "parameters": {},
  }
    }
    
    console.log('7 Final send to Speaker ' + JSON.stringify(final));
    response.send(final);
    
    });
});



