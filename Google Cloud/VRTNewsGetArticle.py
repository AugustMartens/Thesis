from bs4 import BeautifulSoup as soup
from urllib.request import urlopen
from urllib.error import HTTPError
import json

def getArticle(url):
    try:
        html = urlopen(url)
    except HTTPError as e:
        return None
    try:
        vrt_html = html.read()
        page_soup = soup(vrt_html, 'html.parser')
        header = page_soup.find('h1', class_='vrt-title')
        body = page_soup.find_all('div', class_='cmp-text')
        information = [header, body]
        html.close()
    except AttributeError as e:
        return None
    return information

def getArticles(url):
    try:
        html = urlopen(url)
    except HTTPError as e:
        return None
    try:
        vrt_html = html.read()
        page_soup = soup(vrt_html, 'html.parser')
        page_soup_div = page_soup.find_all("div", class_="vrt-teaser-wrapper")
        html.close()
    except AttributeError as e:
        return None
    return page_soup_div

def main(request):
    articles = getArticles('https://www.vrt.be/vrtnws/nl/')
    i = int(request.args.get('number'))
    if articles == None:
        print('Articles could not be found!')
    else:
        link = 'https://www.vrt.be' + articles[i].a['href']
        information = getArticle(link)
        if information == None:
            print('Articles could not be found!')
        else:
            text = information[0].text + ' '
            for part in information[1]:
                text += part.p.text                  
            return json.dumps({'success': True, 'data': text}), 200, {'ContentType': 'application/json'}




