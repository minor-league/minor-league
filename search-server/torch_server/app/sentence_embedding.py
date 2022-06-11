# import kss
import torch
import re
from sentence_transformers import SentenceTransformer
from pororo.tasks.utils.download_utils import download_or_load
from os import listdir

class SentenceModel:
    def __init__(self):
        self.transformer = self._load_model()
        self.tokenizer = self.transformer.tokenizer

    def sentence_to_vector(self, sent):
        # self.transformer.eval()
        # with torch.no_grad():
        self.transformer.eval()
        return self.transformer.encode([sent])[0].tolist()

    def sentence_to_tokens_with_vectors(self, sent):
        # self.transformer.eval()
        # with torch.no_grad():
        model_output = self.transformer.encode([sent],
                                                output_value='token_embeddings',convert_to_tensor=False)

        tokens = self.tokenizer.tokenize(sent)

        encoded_tokens = []
        for idx, encoded in enumerate(model_output[0][1:-2]):
            encoded_tokens.append((tokens[idx], encoded))

        return encoded_tokens

    def _load_model(self):
        model_name = 'brainsbert.base.ko.kornli.korsts'
        model_path = f"./model/pytorch_model.bin"
        try:
            transformer = torch.load(model_path)
        except FileNotFoundError:
            print("downloading....")

            base_path = '/root/.pororo'
            path = f'{base_path}/sbert/{model_name}'
            try:
                listdir(path)
            except FileNotFoundError:
                downloaded = download_or_load(
                    f"sbert/{model_name}",
                    'ko'
                )
                listdir(downloaded)
                assert path == downloaded
            
            transformer = SentenceTransformer(path).eval().to('cpu')
            torch.save(transformer, model_path)
            
            transformer = torch.load(model_path)
        print('load ok')
        return transformer

def sentences_to_sentence(sentences):
    from nltk import sent_tokenize
    return sent_tokenize(sentences)
    # return kss.split_sentences(sentences)

def mean_pooling(model_output, attention_mask):
    token_embeddings = model_output #First element of model_output contains all token embeddings
    input_mask_expanded = attention_mask.unsqueeze(-1).expand(token_embeddings.size()).float()
    sum_embeddings = torch.sum(token_embeddings * input_mask_expanded, 1)
    sum_mask = torch.clamp(input_mask_expanded.sum(1), min=1e-9)
    return sum_embeddings / sum_mask

# mt = Pororo(task="translation", lang="multi")
def get_ko_sentence(original):
    # original = '원혼 : 시작 - Wonhon: The beginning'
    ko_sent = re.sub('[^ 가-힣+]', '', original).strip()
    return ko_sent

def get_en_sentence(original):
    en_sent = re.sub('[^ A-Za-z+]', '', original).strip()
    if en_sent.isupper():
        en_sent = en_sent.title()
    return en_sent

def translate_to_ko(en_sent):
    from pororo import Pororo
    mt = Pororo(task="translation", lang="multi", model="transformer.large.multi.fast.mtpg")
    return mt(en_sent, src="en", tgt="ko")