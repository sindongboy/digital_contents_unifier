#!/bin/bash

source /Users/sindongboy/Dropbox/Documents/resource/digital-contents-unifier/env.sh

function usage() {
	echo "usage: $0 [options]"
	echo "-h	help"
	echo "-c	category"
	echo "-v	version"
	exit 1
}


while test $# -gt 0; 
do
	case "$1" in
		-h)
			usage
			;;
		-c)
			shift
			category=$1
			shift ;;
		-v)
			shift
			version=$1
			shift ;;
		*)
			break
			;;
	esac
done

if [[ -z ${version} ]]; then 
	usage
fi


# tstore tstore meta
echo "tstore : tstore : ${category} : ${version}"
./tsv2hocon.sh -s tstore -u tstore -c ${category} -t meta -v ${version} > ${UM_RESOURCE}/hocon/tstore-${category}-tstore-meta-${version}.conf
# tstore naver meta
echo "tstore : naver : ${category} : ${version}"
./tsv2hocon.sh -s tstore -u naver -c ${category} -t meta -v ${version} > ${UM_RESOURCE}/hocon/tstore-${category}-naver-meta-${version}.conf
# tstore daum meta
echo "tstore : daum : ${category} : ${version}"
./tsv2hocon.sh -s tstore -u daum -c ${category} -t meta -v ${version} > ${UM_RESOURCE}/hocon/tstore-${category}-daum-meta-${version}.conf
# tstore kmdb  meta
echo "tstore : kmdb : ${category} : ${version}"
./tsv2hocon.sh -s tstore -u kmdb -c ${category} -t meta -v ${version} > ${UM_RESOURCE}/hocon/tstore-${category}-kmdb-meta-${version}.conf


# hoppin hoppin meta
echo "hoppin : hoppin : ${category} : ${version}"
./tsv2hocon.sh -s hoppin -u hoppin -c ${category} -t meta -v ${version} > ${UM_RESOURCE}/hocon/hoppin-${category}-hoppin-meta-${version}.conf
# hoppin naver meta
echo "hoppin : naver : ${category} : ${version}"
./tsv2hocon.sh -s hoppin -u naver -c ${category} -t meta -v ${version} > ${UM_RESOURCE}/hocon/hoppin-${category}-naver-meta-${version}.conf
# hoppin daum  meta
echo "hoppin : naver : ${category} : ${version}"
./tsv2hocon.sh -s hoppin -u daum -c ${category} -t meta -v ${version} > ${UM_RESOURCE}/hocon/hoppin-${category}-daum-meta-${version}.conf
# hoppin kmdb  meta
echo "hoppin : kmdb : ${category} : ${version}"
./tsv2hocon.sh -s hoppin -u kmdb -c ${category} -t meta -v ${version} > ${UM_RESOURCE}/hocon/hoppin-${category}-kmdb-meta-${version}.conf

for newFiles in `find ${UM_RESOURCE}/hocon -type f -name "*${version}.conf"`
do
	if [[ ! -s ${newFiles} ]]; then
		echo -n "size zero, thus rm -f --> "
		rm -vf ${newFiles}
	fi
done
