# Minor League

## Installation && Init

### 1. Init Network

```bash
docker network create minor-service-net
```

### 2. Start minor-server (spring webflux)

url: http://localhost:8080

```bash
cd minor-server # minor-league/minor-server
# ./gradlew clean # (optional) because, build folder is committed
# ./gradlew build # (optional) because, build folder is committed
docker-compose build
docker-compose up -d
```

### 3. Start opensearch

url: http://localhost:9200

```bash
cd opensearch # minor-league/opensearch
docker-compose up -d
``` 

### 4. Start search-server (fastapi+bert)

url: http://localhost:8000

```bash
cd search-server # minor-league/search-server
docker-compose build
docker-compose up -d
```

## 5. Start rec-server (flask+bert)

url: http://localhost:5000

```bash
cd rec-scrap-server # minor-league/rec-scrap-server
docker-compose build
docker-compose up -d
```

## 6. Init Data

```bash
docker exec search-server python app/add_game_to_opensearch.py
```

## API document

- [API document](https://documenter.getpostman.com/view/4142881/UzBmNT3G#24f5f9a4-476a-45f2-b6b8-4b38c999d574)

