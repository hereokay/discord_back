#!/bin/bash

# 이미지 빌드
docker build -t discord-back .
if [ $? -ne 0 ]; then
    echo "Docker build failed"
    exit 1
fi

# 이미지 태그
docker tag discord-back hereokay/discord-back
if [ $? -ne 0 ]; then
    echo "Docker tag failed"
    exit 1
fi

# Docker Hub에 푸시
docker push hereokay/discord-back
if [ $? -ne 0 ]; then
    echo "Docker push failed"
    exit 1
fi

echo "Docker operations completed successfully"
