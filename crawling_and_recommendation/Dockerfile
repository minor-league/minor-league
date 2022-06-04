FROM youngseon/pororo
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys A4B469963BF863CC
RUN apt-get update
RUN pip install flask && pip install pandas
RUN pip install --upgrade requests && pip install urllib3==1.26
RUN pip install apscheduler && pip install selenium==3.14.0 && pip install bs4
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN apt install -y ./google-chrome-stable_current_amd64.deb
