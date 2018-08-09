#!/bin/bash

source /Users/sindongboy/Dropbox/Documents/resource/digital-contents-unifier/bin/env.sh

function usage() {
	echo "usage: $0 [options]"
	echo "-h	help"
	echo "-v	version"
	exit 1
}


while test $# -gt 0; 
do
	case "$1" in
		-h)
			usage
			;;
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

# animation
echo "extract id mapping for animation"
echo "to naver"
${PROJECT_BASE}/scripts/idMap.sh -c animation -v ${version} -u naver -o ${RESOURCE_BASE}/map/animation-naver.map-${version} 
echo "to daum"
${PROJECT_BASE}/scripts/idMap.sh -c animation -v ${version} -u daum -o ${RESOURCE_BASE}/map/animation-daum.map-${version} 

echo "extract id mapping for dramak"
echo "to naver"
${PROJECT_BASE}/scripts/idMap.sh -c dramak -v ${version} -u naver -o ${RESOURCE_BASE}/map/dramak-naver.map-${version} 
echo "to daum"
${PROJECT_BASE}/scripts/idMap.sh -c dramak -v ${version} -u daum -o ${RESOURCE_BASE}/map/dramak-daum.map-${version} 

echo "extract id mapping for dramaf"
echo "to naver"
${PROJECT_BASE}/scripts/idMap.sh -c dramaf -v ${version} -u naver -o ${RESOURCE_BASE}/map/dramaf-naver.map-${version} 
echo "to daum"
${PROJECT_BASE}/scripts/idMap.sh -c dramaf -v ${version} -u daum -o ${RESOURCE_BASE}/map/dramaf-daum.map-${version} 

