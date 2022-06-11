from datas import datas
from sentence_embedding import SentenceModel, get_ko_sentence

import pandas as pd
pd.options.mode.chained_assignment = None 
import numpy as np
import re
import nltk
from sklearn.manifold import TSNE

import matplotlib.pyplot as plt
plt.rc('font', family='NamumBarunGothic')

import matplotlib.font_manager as fm  
# 'NanumBarunGothic' in [font.name for font in fm.fontManager.ttflist]

plt.rcParams['axes.unicode_minus'] = False
plt.rcParams['font.family'] = 'NanumBarunGothic'

model = SentenceModel()

titles = []
title_vectors = []
genres = []
for data in datas:
  titles.append(get_ko_sentence(data['title']))
  title_vectors.append(model.sentence_to_vector(titles[-1]))
  genres.append(data['genre'])

import dash
from dash.dependencies import Input, Output
import plotly.express as px
import dash_core_components as dcc
import dash_html_components as html_comp
import plotly.graph_objs as go

# from pandas_datareader import data as dt 
app = dash.Dash('Hello')

df = pd.DataFrame({"title": titles, 'genre': genres, "title_vector": title_vectors})

def tsne_plot(df, genres):
  tsne = TSNE(perplexity=50, n_components=2, init='pca', n_iter=1000, random_state=23)
  projections = tsne.fit_transform(df['title_vector'].tolist())

  fig = px.scatter(projections, x=0, y=1, color=df.genre, labels={'color': 'genre'}, hover_name=df.title)
  
  app.layout = html_comp.Div(children=[
    html_comp.H1(children='Hello'),
    html_comp.Div(children='Game data'),
    dcc.Graph(id='graph', figure=fig)
  ])

  app.run_server(debug=True, host='0.0.0.0', port=8000)

tsne_plot(df, genres)