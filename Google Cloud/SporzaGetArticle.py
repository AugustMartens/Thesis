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
        body = page_soup.find_all('div', class_='vrt-article__intro')
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
        page_soup_div = page_soup.find_all("a", class_="vrt-link--teaser")
        html.close()
    except AttributeError as e:
        return None
    return page_soup_div

def main(request):
    articles = getArticles('https://www.sporza.be/nl/')
    if articles == None:
        return 'Articles could not be found!'
    else:
        link = articles[4]['href']
        return link
        information = getArticle(link)
        if information == None:
            return 'Articles could not be found!'
        else:
            text = information[1].p.text            
            return json.dumps({'success': True, 'data': text}), 200, {'ContentType': 'application/json'}



