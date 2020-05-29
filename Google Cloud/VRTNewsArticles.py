from bs4 import BeautifulSoup as soup
from urllib.request import urlopen
from urllib.error import HTTPError
import json

def getArticles(url):
    try:
        html = urlopen(url)
    except HTTPError as e:
        return None
    try:
        vrt_html = html.read()
        page_soup = soup(vrt_html, 'html.parser')
        page_soup_div = page_soup.find_all("span", class_="vrt-teaser__title-text")
        html.close()
    except AttributeError as e:
        return None
    return page_soup_div

def main(request):
    totalArticles = 'De laatste nieuwsberichten: '
    articles = getArticles('https://www.vrt.be/vrtnws/nl/')
    if articles == None:
        return json.dumps({'success': False, 'data': 'De artikels konden momenteel niet opgevraagd worden! probeer het later opnieuw'}), 200, {'ContentType': 'application/json'}
    else:
        i = 0
        for article in articles:
            i+=1
            totalArticles += article.text
            if i>15:
                break

        return json.dumps({'success': True, 'data': totalArticles}), 200, {'ContentType': 'application/json'}

