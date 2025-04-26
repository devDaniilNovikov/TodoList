FROM ubuntu:latest
LABEL authors="danilanovikov"

ENTRYPOINT ["top", "-b"]