#!/bin/bash

source /Users/sindongboy/Dropbox/Documents/resource/digital-contents-unifier/env.sh

function usage() {
	echo "usage: $0 [options]"
	echo "-h	help"
	echo "-s	service name list, ex) hoppin^tstore^xlife"
	echo "-c	category, ex) movie, dramak, dramaf, animation"
	echo "-v	version, ex) 20140303"
	echo "-o	output file"
	exit 1
}

if [[ $# -eq 0 ]]; then
	usage
fi

while test $# -gt 0; do
	case "$1" in
		-h)
			usage
			;;
		-s)
			shift 
			SVC_LIST=$1
			shift ;;
		-c)
			shift
			CATEGORY=$1
			shift ;;
		-v)
			shift
			VERSION=$1
			shift ;;
		-o)
			shift
			OUTPUT=$1
			shift ;;
		*)
			break
			;;
	esac
done

# env
CONFIG="/Users/sindongboy/Dropbox/Documents/workspace/digital-contents-unifier/config"
RESOURCE_CRAWL_PATH=`find ${UM_CRAWL} -type d -name "*" | awk '{printf("%s:", $0);}' | sed 's/:$//g'`
RESOURCE_SERVICE_PATH=`find ${UM_RESOURCE} -type d -name "*" | awk '{printf("%s:", $0);}' | sed 's/:$//g'`

# libs
CLI="/Users/sindongboy/.m2/repository/com/skplanet/nlp/cli/1.0.1-SNAPSHOT/cli-1.0.1-SNAPSHOT.jar"
CLICOM="/Users/sindongboy/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar"
LOG="/Users/sindongboy/.m2/repository/log4j/log4j/1.2.7/log4j-1.2.7.jar"
TYPESAFE="/Users/sindongboy/.m2/repository/com/typesafe/config/1.2.1/config-1.2.1.jar"
COLLECTION="/Users/sindongboy/.m2/repository/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar"

# Target
TARGET="/Users/sindongboy/Dropbox/Documents/workspace/digital-contents-unifier/target/digital-contents-unifier-1.0.0-SNAPSHOT.jar"

CP="${CONFIG}:${DICT}:${RESOURCE}:${CLI}:${CLICOM}:${LOG}:${TYPESAFE}:${COLLECTION}:${RESOURCE_CRAWL_PATH}:${RESOURCE_SERVICE_PATH}:${TARGET}"

java -Xmx4G -Dfile.encoding=UTF-8 -cp ${CP} com.skplanet.nlp.unifier.dc.driver.CrawlRequester -s ${SVC_LIST} -c ${CATEGORY} -v ${VERSION} -o ${OUTPUT}
