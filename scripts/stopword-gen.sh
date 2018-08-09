#!/bin/bash

function usage() {
	echo "usage: $0 [options]"
	echo "-h	help"
	echo "-s	service name"
	echo "-c	category name"
	echo "-u	source name"
	echo "-v	version"
	echo "-o	output file name"
	exit 1
}

while test $# -gt 0; 
do
	case "$1" in
		-h)
			usage
			;;
		-s)
			shift
			service=$1
			shift ;; 
		-c)
			shift
			category=$1
			shift ;;
		-u)
			shift
			data_source=$1
			shift ;;
		-v)
			shift
			version=$1
			shift ;;
		-o)
			shift
			output=$1
			shift ;;
		*)
			break
			;;
	esac
done 

# env
CONFIG="/Users/sindongboy/Dropbox/Documents/workspace/digital-contents-unifier/config"
RESOURCE="/Users/sindongboy/Dropbox/Documents/workspace/digital-contents-unifier/resource"
DICT="/Users/sindongboy/Dropbox/Documents/workspace/digital-contents-unifier/dict"

# dependencies
CLI="/Users/sindongboy/.m2/repository/com/skplanet/nlp/cli/1.0.1-SNAPSHOT/cli-1.0.1-SNAPSHOT.jar"
COMCLI="/Users/sindongboy/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar"
LOG4J="/Users/sindongboy/.m2/repository/log4j/log4j/1.2.12/log4j-1.2.12.jar"
TYPESAFE="/Users/sindongboy/.m2/repository/com/typesafe/config/1.2.1/config-1.2.1.jar"
COLLECTION="/Users/sindongboy/.m2/repository/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar"
HADOOPCOMMON="/Users/sindongboy/.m2/repository/org/apache/hadoop/hadoop-common/2.2.0/hadoop-common-2.2.0.jar"
HDFS="/Users/sindongboy/.m2/repository/org/apache/hadoop/hadoop-hdfs/2.2.0/hadoop-hdfs-2.2.0.jar"

TARGET="../target/digital-contents-unifier-0.9.0-SNAPSHOT.jar"
CP="${CONFIG}:${RESOURCE}:${DICT}:${CLI}:${COMCLI}:${LOG4J}:${TYPESAFE}:${COLLECTION}:${HADOOPCOMMON}:${HDFS}:${TARGET}"
DRIVER="com.skplanet.nlp.unifier.dc.driver.StopwordGeneratorDriver"

if [[ -z ${service} ]] || [[ -z ${category} ]] || [[ -z ${data_source} ]] || [[ -z ${version} ]]; then
	usage
fi

# for printing result to the screen
if [[ -z ${output} ]]; then
	java -Xmx4G -Dfile.encoding=UTF-8 -cp ${CP} ${DRIVER} -s ${service} -c ${category} -u ${data_source} -v ${version}
else 
# for writing result to a file
	java -Xmx4G -Dfile.encoding=UTF-8 -cp ${CP} ${DRIVER} -s ${service} -c ${category} -u ${data_source} -v ${version} -o ${output}
fi
