from opensearchpy import OpenSearch
import requests

vector_field_info = {
    "type": "knn_vector",
    "dimension": 768,
    "method": {
        "name": "hnsw",
        "space_type": "l2", 
        "engine": "nmslib",
        "parameters": {
            "ef_construction": 512,
            "m": 16
        }
    }
}
GAME = 'stove-game'
GAME_SENTENCE = 'stove-game-sentence'

class OpenSearchService:
    pass

def createGameAndGameSentence(client):
    games_index_name = GAME
    _settings = {
        'index': {
            'knn': True,
            "knn.algo_param.ef_search": 100
        }
    }
    vector_field_info = {
        "type": "knn_vector",
        "dimension": 768,
        "method": {
            "name": "hnsw",
            "space_type": "l2", 
            "engine": "nmslib",
            "parameters": {
                "ef_construction": 128,
                "m": 24
            }
        }
    }
    text_field_info = {
        "type": "text"
    }
    _mappings = {
        "properties": {
            "id_stove": { "type": "integer" },
            "title": text_field_info,
            "title_vector": vector_field_info,
            "desc": text_field_info,
            "desc_vector": vector_field_info,
            "content": text_field_info,
            "urls": {
                "type": "keyword",
                "fields": {
                    "url": {
                        "type": "keyword"
                    }
                }
            },
            "ko_title": text_field_info,
            "ko_title_vector": vector_field_info,
            "en_title": { "type": "text" },
            "en_title_vector": vector_field_info,
            "translated": text_field_info,
            "translated_vector": vector_field_info,
            "genre": { "type": "keyword" },
            "tags": {
                "type": "keyword",
                "fields": {
                    "tag": {
                        "type": "keyword"
                    }
                }
            },
            "link": { "type": "keyword" },
            "timestamp": {
                "type": "date",
                "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
            }
        }
    }

    index_body = {
        'settings': _settings,
        "mappings": _mappings
    }
    response = client.indices.create(games_index_name, body=index_body)

    print('\nCreating index:')
    print(response)

    index_name = GAME_SENTENCE

    _mappings = {
        "properties": {
            "sentence": text_field_info,
            "sentence_vector": vector_field_info,
            "games_id": { "type": "integer" }, # FK
            "order": { "type": "integer" }
        }
    }

    index_body = {
        'settings': _settings,
        'mappings': _mappings
    }
    response = client.indices.create(index_name, body=index_body)
    print(response)

def getOpenSearch():
    host = 'opensearch-node1'
    port = 9200
    auth = ('admin', 'admin') # For testing only. Don't store credentials in code.

    # folder_path = '/app/workspace/keys/'
    # ca_certs_path    = folder_path + 'root-ca.pem' # Provide a CA bundle if you use intermediate CAs with your root CA.
    # client_cert_path = folder_path + 'client.pem'
    # client_key_path  = folder_path + 'client-key.pem'

    return OpenSearch(
        hosts = [{'host': host, 'port': port}],
        http_compress = True, # enables gzip compression
        http_auth = auth,
        ssl_show_warn = True
    )

def main():
    client = getOpenSearch()
    
    #client.indices.delete(GAME)
    #client.indices.delete(GAME_SENTENCE)
    createGameAndGameSentence(client)
    
    from datas import datas
    from sentence_embedding import SentenceModel, get_en_sentence, get_ko_sentence, translate_to_ko, sentences_to_sentence
    model = SentenceModel()

    for data in datas:
        document = {
            'title': data['title'],
            'title_vector': model.sentence_to_vector(data['title']),
            'ko_title': get_ko_sentence(data['title']),
            # 'translated_title': translate_to_ko(data['title']),
            'en_title': get_en_sentence(data['title']),
            'content': data['contents'],
            'desc': data['desc'],
            'desc_vector': model.sentence_to_vector(data['desc']),
            'tags': data['tag'],
            'genre': data['genre'],
            'link': data['link']
        }
        document['ko_title_vector'] = model.sentence_to_vector(document['ko_title'])
        document['en_title_vector'] = model.sentence_to_vector(document['en_title'])
        # document['translated_title_vector'] = model.sentence_to_vector(document['translated_title'])
        
        response = client.index(
            index = GAME,
            body = document,
            refresh = True
        )
        print('\nAdding Game:')
        print(response)

        game_id = response['_id']
        order = 0
        # sentences_to_sentence(game_tata)

        for sentence in sentences_to_sentence(data['contents']):
            body = {
                'sentence': sentence,
                'sentence_vector': model.sentence_to_vector(sentence),
                'order': order,
                'game_id': game_id
            }
            client.index(index=GAME_SENTENCE, body=body)
            order += 1

main()
