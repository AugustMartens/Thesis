const co = require('co');
const mongodb = require('mongodb');

const uri = 'mongodb+srv://Lennert:5YYRI2L2x3Gtlcu2@thesiscluster-zwbbo.gcp.mongodb.net/test?retryWrites=true&w=majority';

exports.getPlaatsnaam = (req, res) => {
  idGiven = req.query.speakerId;
  console.log(idGiven);
  co(function*() {
    const client = yield mongodb.MongoClient.connect(uri);

    const docs = yield client.db('Thesis_db').collection('site_login').find({ SpeakerId: 'b489e7c3f459e33a'}).toArray();
    res.json({stad: docs[0].Plaatsnaam});
    console.log(res);
  }).catch(error => {
    res.json({city: null});
  });
};
