import pandas as pd
from pandas import DataFrame
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import time
import os
from bs4 import BeautifulSoup
import json
#from pororo import Pororo
import datetime

def login(driver):
	driver.get("https://member.onstove.com/auth/login?redirect_url=https%3A%2F%2Findie.onstove.com%2Fko%2Fstore%2Fsearch")
	driver.find_element_by_id('user_id').send_keys("")
	driver.find_element_by_id('user_pwd').send_keys('')
	driver.find_element_by_xpath("//*[@id='idLogin']/div[4]/button/span").click()
	print("login success")
	time.sleep(3)

def get_name(soup):
	names = []
	name_tags = soup.find_all("span","writer-title grade_2")
	for name_tag in name_tags:
		names.append(name_tag.getText())

	return names

def get_rec(soup):
	ev = soup.find("section","igd-section igd-section-evaluation")
	spans = ev.find_all("span")
	for span in spans:
		if "명의 유저가 추천합니다." in span.getText():
			return span.getText().replace("명의 유저가 추천합니다.","")

def get_genre(soup):
	game_info = soup.find("div","game-info")
	table = game_info.find("table")
	genre = table.find("td")
	return genre.getText()

def remove_blank(s):
	s = s[1:].strip()
	return s

def get_topic(doc):

	zs = Pororo(task = 'zero-topic')
	number = len(doc)//200
	rst = {
		"귀여운":0,
		"감동적인":0,
		"활기찬":0,
		"우울한":0,
		"어두운":0,
		"슬픈":0
	}

	for i in range(number):
		zz = zs(doc[i*200:(i+1)*200],["귀여운","감동적인","활기찬","우울한","어두운","슬픈"])
		for key,value in zz.items():
			rst[key] += value

	zz = zs(doc[number*200:],["귀여운","감동적인","활기찬","우울한","어두운","슬픈"])
	for key,value in zz.items():
		rst[key] += value
		rst[key] /= (number+1)
	
	return rst


def check(title):
	file_name = './data.json'
	
	with open(file_name) as f:
		data_list = json.load(f)

	for review_dict in data_list :
		if title == review_dict['title'] :
			print("already have one")
			return True

	return False

def convert_to_json(title,desc,sentences,tag_names,replys,names,genre,rec,link,article_html):
	file_name = './data.json'

	with open(file_name) as f:
		data_list = json.load(f)

	data_list.append({
		"title":title,
		"desc":desc,
		"contents":sentences,
		"tag":tag_names,
		"reply":replys,
		"reply_writer":names,
		"genre":genre,
		"rec":rec,
		"link":link,
		"article_html":article_html
	})

	with open(file_name,"w") as f:
		json.dump(data_list,f,indent = 4,separators=(',',': '),ensure_ascii=False)

	print("converting to json done successfully.")


links = []

opts = Options()
opts.add_argument("--headless")
opts.add_argument("window-size=1920x1080")
opts.add_argument('--no-sandbox')
opts.add_argument('--disable-dev-shm-usage')
driver_path = os.getcwd() + '/chromedriver'
driver = webdriver.Chrome(executable_path = driver_path,options = opts)

login(driver)

print("crawling links...")

#crawling links
for page_number in range(1,10):
	url = "https://indie.onstove.com/ko/store/search?page=" + str(page_number)
	driver.get(url)
	time.sleep(2)
	soup_file = driver.page_source
	soup = BeautifulSoup(soup_file,features="html.parser")
	game_menu = soup.find("div","module-card-list")
	game_list = game_menu.findChildren("a")
	for idx in range(len(game_list)):
		links.append(game_list[idx]["href"])
	print("crawling link #"+str(page_number)+"done")

print("crawling links done")
print("crawling documents...")

#for each links
for idx,link in enumerate(links):
	link = "https://indie.onstove.com" + link

	driver.get(link)
	print(link)
	driver.maximize_window()
	time.sleep(2)
	driver.execute_script('window.scrollTo(0,600);')
	time.sleep(2)
	btns = driver.find_elements_by_class_name("btn-txt")
	for btn in btns:
		if btn.text == "더보기":
			btn.click()
			break
	time.sleep(2)
	soup_file = driver.page_source
	soup = BeautifulSoup(soup_file,features="html.parser")

	#get title
	title = soup.find("p","tit-txt").getText()
	title = remove_blank(title)
	if check(title) == True:
		continue

	#get genre
	genre = get_genre(soup)

	#get tag
	tag_names = []
	tags = soup.find_all("span","tag")
	for tag in tags:
		tag_names.append(tag.getText().strip()[1:])

	#get description
	desc = soup.find("p","game-desc").getText()

	#get article
	article = soup.find("article","article-item fr-view")
	sentences = ""
	sentence_list = article.findChildren("p")
	for sentence in sentence_list:
		sentences += sentence.getText()
	article_html = str(article)
	#print(sentences)

	driver.execute_script('window.scrollTo(0,3000)')
	time.sleep(1)

	#get reply
	soup_file = driver.page_source
	soup = BeautifulSoup(soup_file,features="html.parser")
	replys = []
	rs = soup.find_all("div","reply-txt")
	for r in rs:
		replys.append(r.find("p").getText())

	#get name
	names = get_name(soup)

	#get topic(tag)
	#topic = get_topic(desc)
	#topic = []

	#get rec
	rec = get_rec(soup)

	#make json file
	convert_to_json(title,desc,sentences,tag_names,replys,names,genre,rec,link,article_html)
	print('crawling document #'+str(idx)+'done')

print("crawling documents done")

