import wikipedia
from bs4 import BeautifulSoup as soup
from urllib.request import urlopen
from urllib.error import HTTPError
import json
import random

def selectIntent(request):
    if request.args and 'intent' in request.args:
        if request.args.get('intent')=='Wikipedia':
            return wikipediaAPI(request.args.get('message'))
        if request.args.get('intent')=='WikipediaExtended':
            return wikipediaAPIExtended(request.args.get('message'))
        if request.args.get('intent')=='nieuws':
            return nieuwsbladSCRAP('o')
        if request.args.get('intent')=='moppen':
            return moppenSCRAP()
        if request.args.get('intent')=='horoscoop':
            return horSCRAP()
        if request.args.get('intent')=='verkeersInformatie':
            return verkeer()
    elif request_json and 'message' in request_json:
        return request_json['message']
    else:
        return f'No intent Detected'

def wikipediaAPI(messageGiven):
    wikipedia.set_lang("nl")
    return json.dumps({'success': True, 'data_wikipedia': wikipedia.summary(messageGiven, sentences=1)}), 200, {'ContentType': 'application/json'}

def wikipediaAPIExtended(messageGiven):
    wikipedia.set_lang("nl")
    return json.dumps({'success': True, 'data_wikipedia': wikipedia.summary(messageGiven, sentences=7)}), 200, {'ContentType': 'application/json'}


def nieuwsbladSCRAP(messageGiv):
    moppen_url = 'https://www.nieuwsblad.be'
    moppen_data = urlopen(moppen_url)
    moppen_html = moppen_data.read()
    moppen_data.close()

    page_soup = soup(moppen_html, 'html.parser')
    page_soup_div = page_soup.find_all("article")
    
    for article in page_soup_div:
        return json.dumps({'success': True, 'data': article.text}), 200, {'ContentType': 'application/json'}

def moppenSCRAP():
    try: 
        moppen_url = 'https://www.seniorennet.be/Pages/Moppen/mop.php'
        moppen_data = urlopen(moppen_url)
        moppen_html = moppen_data.read()
        moppen_data.close()

        page_soup = soup(moppen_html, 'html.parser')

        page_soup_div = page_soup.find_all("div", class_="weetje-msg");
        rand = random.randint(0,2)
        return json.dumps({'success': True, 'data_mop': page_soup_div[rand].text}), 200, {'ContentType': 'application/json'}
    except:
        return json.dumps({'success': False}), 400, {'ContentType': 'application/json'}

def horSCRAP():
    horoscoop_url = 'https://www.mediumchat.be/daghoroscoop/waterman'
    horoscoop_data = urlopen(horoscoop_url)
    horoscoop_html = horoscoop_data.read()
    horoscoop_data.close()

    page_soup = soup(horoscoop_html, 'html.parser')
    page_soup_div = page_soup.find_all("div", class_="col-xs-12 col-sm-10")

    for story in page_soup_div:
        return json.dumps({'success': True, 'data_hor': story.text}), 200, {'ContentType': 'application/json'}

def getArticles(url):
    try:
        html = urlopen(url)
    except HTTPError as e:
        return None
    try:
        metro_html = html.read()
        html.close()
        page_soup = soup(metro_html, 'html.parser')
        page_soup_div = page_soup.find_all("div", class_="card-body")
    except AttributeError as e:
        return None
    return page_soup_div

def verkeer():
    articles = getArticles('https://www.verkeerscentrum.be/tekstuele-verkeerssituatie?types%5B580%5D=580&types%5B578%2C586%2C579%2C584%2C583%2C577%2C585%2C587%5D=578%2C586%2C579%2C584%2C583%2C577%2C585%2C587')
    if articles == None:
        return json.dumps({'success': True, 'data_verkeer': 'Verkeersdata is momenteel niet beschikbaar.'}), 200, {'ContentType': 'application/json'}
    else:
        items = "De huidige verkeersinformatie: Er is een "
        for article in articles:
            items += article.select('h2')[0].get_text()
            items += "."

        return json.dumps({'success': True, 'data_verkeer': items}), 200, {'ContentType': 'application/json'}
