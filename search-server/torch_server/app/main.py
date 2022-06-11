from fastapi import FastAPI
from typing import Union, List
from pydantic import BaseModel
from sentence_embedding import SentenceModel
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

@app.get("/search/default")
def search_default(query: Query):
    vector = model.sentence_to_vector(query.query)
    # Search for the document.
    index_name = 'game5'

    field_name = 'title_vector'
    index_body = { # Approximate
        "size": 10,
        "query" : {
            "knn": {
                "title_vector": {
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
    return {"res": response}
