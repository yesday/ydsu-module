FROM archlinux/base
MAINTAINER YesDay <yesdayuk@gmail.com>

# add our user and group first to make sure their IDs get assigned consistently, regardless of whatever dependencies get added
RUN useradd -m ydsu && usermod -aG wheel,adm ydsu

RUN set -x \
	&& pacman -Syyu --needed --noconfirm \
    && pacman -S --needed --noconfirm which awk zip unzip git curl ansible \
       jdk-openjdk groovy gradle maven docker nano ranger

USER ydsu
RUN set -x \
    && git config --global user.email 'yesdayuk@gmail.com' \
    && git config --global user.name 'YesDay'
ENV HOME /home/ydsu

RUN mkdir /home/ydsu/.gradle /home/ydsu/.m2

VOLUME /source /home/ydsu/.gradle /home/ydsu/.m2

WORKDIR /source

CMD /usr/bin/bash
