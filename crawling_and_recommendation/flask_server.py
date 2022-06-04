from flask import Flask
from flask import request
import recommendation

app = Flask(__name__)

@app.route('/')
def rec():
	inputs = request.args.get('inputs')
	input_list = inputs.split(',')
	input_list = [int(x) for x in input_list]
	rs = recommendation.recommend(input_list)
	s = ""
	for r in rs:
		s += str(r)
		s += " "
	print(s)	
	return s

if __name__ == '__main__':

	app.run(host = '0.0.0.0',debug = True,port = 3000)
