#!/bin/bash

source /Users/sindongboy/Dropbox/Documents/resource/digital-contents-unifier/bin/env.sh

# env
CONFIG="${PROJECT_BASE}/config"
RESOURCE_CRAWL_PATH=`find ${CRAWL_BASE}/crawl -type d -name "*" | awk '{printf("%s:", $0)}' | sed 's/:$//g'`
RESOURCE_SERVICE_PATH=`find ${RESOURCE_BASE} -type d -name "*" | awk '{printf("%s:", $0)}' | sed 's/:$//g'`
HADOOP_CONFIG1="/usr/local/Cellar/hadoop/2.6.0/libexec/etc/hadoop"
HADOOP_CONFIG2="/usr/local/Cellar/hadoop/2.6.0/libexec/libexec"

# lib
CLI="/Users/sindongboy/.m2/repository/com/skplanet/nlp/cli/1.0.1-SNAPSHOT/cli-1.0.1-SNAPSHOT.jar"
CLICOM="/Users/sindongboy/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar"
LOG="/Users/sindongboy/.m2/repository/log4j/log4j/1.2.7/log4j-1.2.7.jar"
TYPESAFE="/Users/sindongboy/.m2/repository/com/typesafe/config/1.2.1/config-1.2.1.jar"
COLLECTION="/Users/sindongboy/.m2/repository/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar"
OMPCONFIG="/Users/sindongboy/.m2/repository/com/skplanet/nlp/omp-config/1.0.6-SNAPSHOT/omp-config-1.0.6-SNAPSHOT.jar"
HADOOP_COMMON="/Users/sindongboy/.m2/repository/org/apache/hadoop/hadoop-common/2.2.0/hadoop-common-2.2.0.jar"
HADOOP_HDFS="/Users/sindongboy/.m2/repository/org/apache/hadoop/hadoop-hdfs/2.2.0/hadoop-hdfs-2.2.0.jar"
COMMON_LOGGING="/Users/sindongboy/.m2/repository/commons-logging/commons-logging/1.1.3/commons-logging-1.1.3.jar"
COMMON_LANG="/Users/sindongboy/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar"
APACHE_CONFIG="/Users/sindongboy/.m2/repository/org/apache/commons/commons-lang3/3.3.2/commons-lang3-3.3.2.jar"
GUAVA="/Users/sindongboy/.m2/repository/com/google/guava/guava/18.0/guava-18.0.jar"
COMMON_CONFIG="/Users/sindongboy/.m2/repository/commons-configuration/commons-configuration/1.9/commons-configuration-1.9.jar"
HADOOP_AUTH="/Users/sindongboy/.m2/repository/org/apache/hadoop/hadoop-auth/2.2.0/hadoop-auth-2.2.0.jar"
SL4J_SIMPLE="/Users/sindongboy/.m2/repository/org/slf4j/slf4j-simple/1.7.7/slf4j-simple-1.7.7.jar"
SL4J_API="/Users/sindongboy/.m2/repository/org/slf4j/slf4j-api/1.7.7/slf4j-api-1.7.7.jar"
PROTOBUF="/Users/sindongboy/.m2/repository/com/google/protobuf/protobuf-java/2.5.0/protobuf-java-2.5.0.jar"
COMMON_IO="/Users/sindongboy/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar"

# Target
TARGET="${PROJECT_BASE}/target/digital-contents-unifier-1.0.0-SNAPSHOT.jar"

CP="${CONFIG}:${DICT}:${RESOURCE_CRAWL_PATH}:${RESOURCE_SERVICE_PATH}:${HADOOP_CONFIG1}:${HADOOP_CONFIG2}:${COMMON_LOGGING}:${CLI}:${CLICOM}:${LOG}:${TYPESAFE}:${COLLECTION}:${OMPCONFIG}:${HADOOP_HDFS}:${HADOOP_COMMON}:${TARGET}:${COMMON_LANG}:${GOOGLE_COMMON}:${APACHE_CONFIG}:${GUAVA}:${COMMON_CONFIG}:${HADOOP_AUTH}:${SL4J_API}:${SL4J_SIMPLE}:${PROTOBUF}:${COMMON_IO}"

# unify
java -Xmx4G -cp ${CP} com.skplanet.nlp.unifier.dc.driver.Hocon2Tsv $1
