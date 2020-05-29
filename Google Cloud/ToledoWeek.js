const puppeteer = require('puppeteer');

exports.main = (req, res) => {
  
function run () {
    return new Promise(async (resolve, reject) => {
        try {
            const browser = await puppeteer.launch({env: {TZ: 'Belgium/Europe'}, args: ['--no-sandbox']});
            const page = await browser.newPage();
            await page.goto("https://app.quivr.be/login");
            await page.type('#email', process.env.EMAIL);
            await page.type('#password', process.env.KEY);
            await page.click('#submit');
            await page.waitForSelector('a[href="/schedule"]' , {
                timeout: 5000
            });
            await page.click('a[href="/schedule"]');
            await page.waitForSelector('div[class="event-content"]' , {
                timeout: 5000
            });
            let schedule = await page.evaluate(() => {
                let item = [...document.querySelectorAll('div[class="event-content"]')].map(elem => elem.innerText).join(' ');
                return item;
            });
            browser.close();
            resolve(schedule);
        } catch (e) {
            return reject(e);
        }
    })
}

run()
.then(nextItem => {
  console.log(nextItem);
  nexItem = nextItem.replace("\n", "");
  res.status(200).json({schedule: nextItem});
})
.catch(err => {
    console.error(err);
    res.status(500).send("An Error occured" + err);  
  })
  
};
