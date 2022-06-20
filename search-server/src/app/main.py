from fastapi import FastAPI
from typing import Union, List
from pydantic import BaseModel
from .sentence_embedding import SentenceModel
from opensearchpy import OpenSearch

client = OpenSearch(
    hosts = [{'host': 'opensearch-node1', 'port': 9200}],
    http_compress = True, # enables gzip compression
    http_auth = ('admin', 'admin'),
    ssl_show_warn = True
)

model = SentenceModel()
app = FastAPI()


class Query(BaseModel):
    query: str

@app.post("/search/default")
def search_default(query: Query):
    vector = model.sentence_to_vector(query.query)
    # Search for the document.
    index_name = 'stove-game'

    field_name = 'title_vector'
    index_body = { # Approximate
        "size": 10,
        "query" : {
            "knn": {
                "ko_title_vector": {
                    "vector": vector,
                    "k": 10
                }
            }
        }
    }
    response = client.search(
        body = index_body,
        index = index_name
    )
    games = []
    for game in response['hits']['hits']:
        games.append({'_id': game['_id'], 'title': game['_source']['title']})
    
    return {"games": games}

@app.post("/search/sentence")
def search_default(query: Query):
    vector = model.sentence_to_vector(query.query)
    # Search for the document.
    index_name = 'stove-game-sentence'

    # field_name = 'title_vector'
    index_body = { # Approximate
        "size": 10,
        "query" : {
            "knn": {
                "sentence_vector": {
                    "vector": vector,
                    "k": 10
                }
            }
        }
    }
    response = client.search(
        body = index_body,
        index = index_name
    )
    
    games = []
    for game in response['hits']['hits']:
        
        title = client.get(index = 'stove-game', id=game['_source']['game_id'])['_source']['title']
        
        games.append({'games_id': game['_source']['game_id'], 'game_title': title ,'sentence': game['_source']['sentence']})
    
    return {"games": games}
