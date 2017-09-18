#!/bin/bash
set -e

CONTAINER_NAME="jam_jdk8"
PORT="30036"

BUILD_SCRIPT_DIR="/opt/cicd"
BASE_DIR="/var/data/containers"
M2="${BASE_DIR}/${CONTAINER_NAME}/m2"
WS="${BASE_DIR}/${CONTAINER_NAME}/WS"
BUILD_DATA="${BASE_DIR}/${CONTAINER_NAME}/var/data"

f_create()
{
mkdir -p ${BASE_DIR}/${CONTAINER_NAME}
mkdir -p ${M2}
mkdir -p ${WS}
mkdir -p ${BUILD_DATA}
docker run --name "${CONTAINER_NAME}" \
                 -p ${PORT}:22 \
                 -v "${BUILD_SCRIPT_DIR}":"${BUILD_SCRIPT_DIR}" \
                 -v "${BUILD_DATA}:/var/data" \
                 -v "${M2}:/home/jenkins/.m2" \
                 -v "${WS}:/home/jenkins/workspace" \
                 -d "harbor.rd.company.com/devops/jenkins_slave_node8_jdk8:v0.2"
}

docker exec -it "${CONTAINER_NAME}" bash -c "chpasswd </opt/cicd/com/company/slaves/auth.txt"
docker exec -it "${CONTAINER_NAME}" bash -c "chown -R jenkins:jenkins ${BUILD_SCRIPT_DIR}"
docker exec -it "${CONTAINER_NAME}" bash -c "chown -R jenkins:jenkins /home/jenkins/workspace/"
docker exec -it "${CONTAINER_NAME}" bash -c "chown -R jenkins:jenkins /home/jenkins/.m2/"
docker exec -it "${CONTAINER_NAME}" bash -c "chown -R jenkins:jenkins /var/data"



