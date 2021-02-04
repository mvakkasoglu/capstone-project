FROM jenkins/jenkins
USER root
RUN apt-get update
RUN apt-get install python3-pip -y
RUN pip3 install ansible --upgrade
RUN apt-get upgrade -y
RUN apt-get update
USER jenkins 