#!/bin/bash
set -eu -o pipefail

[[ -z "${JOB_NAME}" ]] && { echo "[ERROR] JOB_NAME is not set! exit."; exit 1; }

#JOB_NAME=PRODUCT__FROM-to-TO__COMPONENT
declare -xr PRODUCT_NAME="$(echo "${JOB_NAME}" | awk -F'__' '{ print $1; }')"
declare -xr COMPONENT_NAME="$(echo "${JOB_NAME}" | awk -F'__' '{ print $3; }')"
declare -xr FROM_TO="$(echo "${JOB_NAME}" | awk -F'__' '{ print $2; }')"
declare -xr FROM=${FROM_TO%%-to*}
declare -xr TO=${FROM_TO#*to-}

echo "-------------------------------------------------------------------"
echo ""
echo "PRODUCT_NAME=${PRODUCT_NAME}"
echo "COMPONENT_NAME=${COMPONENT_NAME}"
echo "FROM=${FROM}"
echo "TO=${TO}"
echo ""

declare -xr START_SCRIPT_DIRS=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
#echo "START_SCRIPT_DIRS: ${START_SCRIPT_DIRS}"

echo "-------------------------------------------------------------------"
echo "Load conf for TO: ${TO}"
echo "                       rundeck server_url:"
echo "                       rundeck username/password"
echo ""
echo ""
echo "-------------------------------------------------------------------"
echo "if FROM=BUILD"
echo "    load build functions"
echo "           common  build function"
echo "           product build function if product has custom function"
echo "    build src -> package"
echo "-------------------------------------------------------------------"
echo ""
echo "Upload Package from ${FROM} to ${TO}"
echo ""
echo "-------------------------------------------------------------------"
echo ""
echo "Deploy to: ${TO}"
echo ""
echo "-------------------------------------------------------------------"
echo ""
echo "DONE"
echo ""
echo "-------------------------------------------------------------------"

