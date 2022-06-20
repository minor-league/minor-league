FROM limdongjin/pororo:latest

# WORKDIR /app/workspace
RUN pip install jupyter 
RUN pip install opensearch-py

WORKDIR /app/code
COPY ./requirements.txt /app/code/requirements.txt
RUN pip install --no-cache-dir --upgrade -r /app/code/requirements.txt
COPY . /app/code/

CMD ["uvicorn", "app.main:app", "--reload", "--host", "0.0.0.0", "--port", "8000"]
