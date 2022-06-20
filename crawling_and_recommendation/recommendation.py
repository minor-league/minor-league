import pandas as pd
import math
from pororo import Pororo
from sklearn.feature_extraction.text import TfidfVectorizer
from konlpy.tag import Hannanum
from sklearn.metrics.pairwise import linear_kernel

def get_similarity(docs,inputs):

	tokenizer = Hannanum()
	tokenized = []

	for doc in docs:
		token = tokenizer.nouns(doc)
		tokenized.append(" ".join(token))

	tfidf = TfidfVectorizer()
	matrix = tfidf.fit_transform(tokenized)

	similarity = linear_kernel(matrix,matrix)

	sim = [0 for x in range(0,len(docs))]

	for input in inputs:
		for x in range(0,len(docs)):
			sim[x] += similarity[input][x]

	for x in range(0,len(docs)):
		sim[x] /= len(inputs)

	return sim


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
	
	for r in range(size):
		s2 = set(tags[r])
		inter = len(s1&s2)
		l.append([inter,r])

	l = sorted(l,key = lambda x : x[0],reverse = True)

	return [x[0] for x in l]

def get_sentiment_score(reply):

	sa = Pororo(task="sentiment", model="brainbert.base.ko.nsmc", lang="ko")
	score = 0
	
	for r in reply:
		np = sa(r, show_probs=True)
		score += np['positive']
		score -= 0.5
	
	if len(reply) != 0:
		score /= len(reply)

	return score

def filter_rec_reply(recs,replys,rst,sim,tag_intersection):
	
	l = []
	score = 0
	for r in rst:
		if math.isnan(recs[r]) == False:
			score = get_sentiment_score(replys[r])
			similarity = sim[r]
			tag_number = tag_intersection[r]
			l.append([int(recs[r])/50 + score + similarity + tag_number/5,r,recs[r],score,similarity,tag_number])
	
	l = sorted(l,key = lambda x : x[0], reverse = True)	

	for x in range(5):
		print(l[x][2:])

	return [x[1] for x in l]

def recommend(inputs):

	df = pd.read_json('./crawling/data.json')

	rst = []
	titles = df['title']
	tags = df['tag']
	genres = df['genre']
	recs = df['rec']
	replys = df['reply']
	docs = df['contents']

	for input in inputs:
		print(titles[input],"["+genres[input]+"]")

	filter_genre(genres,inputs,rst)
	tag_intersection = filter_tag(tags,inputs,rst)
	sim = get_similarity(docs,inputs)
	rst = filter_rec_reply(recs,replys,rst,sim,tag_intersection)

	rst_string = ""
	for i in range(len(rst)):
		print(titles[rst[i]])
		rst_string += titles[rst[i]] + ", "

	return rst
