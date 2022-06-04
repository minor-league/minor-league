import pandas as pd
import math
from pororo import Pororo

def filter_genre(genres,inputs,rst):
	size = len(genres)
	
	for input in inputs:
		for i in range(size):
			if i == input:
				continue
			if genres[i] == genres[input]:
				rst.append(i)

def filter_tag(tags,inputs,rst):
	size = len(tags)
	
	l = []
	s1 = set()

	for input in inputs:
		s1 |= set(tags[input])
	
	for r in rst:
		s2 = set(tags[r])
		inter = len(s1&s2)
		l.append([inter,r])

	l = sorted(l,key = lambda x : x[0],reverse = True)

	return [x[1] for x in l][:3]

def get_sentiment_score(reply):

	sa = Pororo(task="sentiment", model="brainbert.base.ko.nsmc", lang="ko")
	score = 0
	
	for r in reply:
		np = sa(r, show_probs=True)
		score += np['positive']
		score -= 0.5
	
	if len(reply) != 0:
		score /= len(reply)

	print('score is',score)
	return score

def filter_rec_reply(recs,replys,rst):
	
	l = []
	score = 0
	for r in rst:
		if math.isnan(recs[r]) == False:
			score = get_sentiment_score(replys[r])
			l.append([int(recs[r])/50 + score,r])
	
	l = sorted(l,key = lambda x : x[0], reverse = True)
	return [x[1] for x in l]

	return rst

def recommend(inputs):

	df = pd.read_json('./crawling/data.json')

	rst = []
	titles = df['title']
	tags = df['tag']
	genres = df['genre']
	recs = df['rec']
	replys = df['reply']

	filter_genre(genres,inputs,rst)
	print(rst)
	rst = filter_tag(tags,inputs,rst)
	rst = filter_rec_reply(recs,replys,rst)

	rst_string = ""
	for i in range(len(rst)):
		rst_string += titles[rst[i]] + ", "

	print(rst_string)

	return rst