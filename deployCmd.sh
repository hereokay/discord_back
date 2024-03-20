#!/bin/bash

# 사용자 개인 키 경로
private_key="/Users/ijinjung/Documents/pw/my-key-pair.pem"

# 원본 파일 경로
source_file="/Users/ijinjung/Documents/maples/mapleland-util-back/build/libs/mapleland-util-back-0.0.2.jar"

# 원격 서버의 홈 디렉토리
remote_home="ubuntu@3.38.25.218:~"

# scp를 사용하여 파일 전송
scp -i "$private_key" "$source_file" "$remote_home"
