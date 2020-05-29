const co = require('co');
const mongodb = require('mongodb');

const uri = process.env.URI;

exports.getGoogleATCalendar = (req, res) => {
  idGiven = req.query.speakerId;
  console.log(idGiven);
  co(function*() {
    const client = yield mongodb.MongoClient.connect(uri);

    const docs = yield client.db('Thesis_db').collection('site_login').find({ SpeakerId: req.query.speakerId}).toArray();
    res.json({access_token: docs[0].gcAT, refresh_token: docs[0].gcRT});
    console.log(res);
  }).catch(error => {
    res.json({city: null});
  });
};
