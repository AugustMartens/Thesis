const co = require('co');
const mongodb = require('mongodb');

const uri = 'mongodb+srv://Lennert:5YYRI2L2x3Gtlcu2@thesiscluster-zwbbo.gcp.mongodb.net/test?retryWrites=true&w=majority';

exports.checkReg = (req, res) => {
  idGiven = req.body.speakerId;
  console.log(idGiven);
  co(function*() {
    const client = yield mongodb.MongoClient.connect(uri);

    const docs = yield client.db('Thesis_db').collection('site_login').find({ SpeakerId: idGiven}).toArray();
    res.json({speakerId: docs[0].SpeakerId});
    console.log(res);
  }).catch(error => {
    res.json({speakerId: null});
  });
};