# NLP, Pororo Setup wit Docker

## Environment Setup 

0. step 0 : 도커 설치

1. step 1 : pull

```bash
$ docker pull youngseon/pororo 
```

2. step 2 : run (생성)

```bash
$ docker run -it --name pororo-dockerrr -p 3000:3000 -p 8888:8888 -v ~/workspace/work:/app/work youngseon/pororo
# 참고: 로컬 주소(~/workspace/pororo-work) : 도커에서의주소(app/workspace)
# 주소는 자율적으로 설정 (상대 주소, 절대 주소 둘다 가능)

# 위의 쉘 커맨드를 입력하면 도커 컨테이너 쉘로 들어간다.
root@123112:/app $ 
```

- 재 접속 시 다시 들어가는 명령어(attach)
```bash
$ docker start pororo-dockerrrr
$ docker attach pororo-dockerrr
```

## Additional Setup

```bash
# 현재 쉘 위치: 도커 컨테이너

# 1. pororo update 해줘야 자잘한 오류안남
root@123112:/app/work$ pip install --upgrade pororo

# 2. jupyter notebook 설치
root@123112:/app/work$ pip install jupyter
```

## Jupyter notebook 실행 

```bash
root@123112:/app/work$ jupyter notebook --ip='0.0.0.0' --allow-root
```

## See also

- [pororo-demo.ipynb](./pororo-demo.ipynb)
