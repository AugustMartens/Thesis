const projectId = 'AIzaSyDTbqDP5SUreJ9juL5x84H48RgWQTKtbgw';
const {Translate} = require('@google-cloud/translate').v2;


exports.main = (req, res) => {
  // Instantiates a client
  const translate = new Translate({projectId});

  async function quickStart() {
    // The text to translate
    const text = 'Hello, world!';

    // The target language
    const target = 'nl';

    // Translates some text into Russian
    const [translation] = await translate.translate(text, target);
    console.log(`Text: ${text}`);
    console.log(`Translation: ${translation}`);
    res.send(translation);
  }

  quickStart();
};

