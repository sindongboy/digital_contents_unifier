#!/bin/bash


function usage() {
	echo "usage: $0 [option]"
	echo "-h	help"
	echo "-v	version"
	exit 1
}

if [[ $# == 0 ]]; then 
	usage
fi

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

RESOURCE_DIR="/Users/sindongboy/Dropbox/Documents/resource/digital-contents-unifier"
PROJECT_DIR="/Users/sindongboy/Dropbox/Documents/workspace/digital-contents-unifier"

# -------------------- #
# 1. source to hocon
# -------------------- #
# hoppin movie 
echo "# 1. source to hocon ==> hoppin movie"
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u hoppin -c movie -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-movie-hoppin-meta-${version}.conf

# hoppin dramak 
echo "# 1. source to hocon ==> hoppin dramak"
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u hoppin -c dramak -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-dramak-hoppin-meta-${version}.conf
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u hoppin -c dramak -t episode -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-dramak-hoppin-episode-${version}.conf

# hoppin dramaf 
echo "# 1. source to hocon ==> hoppin dramaf"
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u hoppin -c dramaf -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-dramaf-hoppin-meta-${version}.conf
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u hoppin -c dramaf -t episode -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-dramaf-hoppin-episode-${version}.conf

# hoppin animation 
echo "# 1. source to hocon ==> hoppin animation"
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u hoppin -c animation -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-animation-hoppin-meta-${version}.conf
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u hoppin -c animation -t episode -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-animation-hoppin-episode-${version}.conf

# tstore movie 
echo "# 1. source to hocon ==> tstore movie"
${PROJECT_DIR}/scripts/tsv2hocon.sh -s tstore -u tstore -c movie -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/tstore-movie-tstore-meta-${version}.conf

# -------------------- #
# 2. crawl request 
# -------------------- #
echo "# 2. crawl request ==> movie" 
${PROJECT_DIR}/scripts/crawl-request.sh -s hoppin^tstore -c movie -v ${version} -o ${RESOURCE_DIR}/rsync/request/movie.request-${version}
echo "# 2. crawl request ==> dramak" 
${PROJECT_DIR}/scripts/crawl-request.sh -s hoppin -c dramak -v ${version} -o ${RESOURCE_DIR}/rsync/request/dramak.request-${version}
echo "# 2. crawl request ==> dramaf" 
${PROJECT_DIR}/scripts/crawl-request.sh -s hoppin -c dramaf -v ${version} -o ${RESOURCE_DIR}/rsync/request/dramaf.request-${version}
echo "# 2. crawl request ==> animation" 
${PROJECT_DIR}/scripts/crawl-request.sh -s hoppin -c animation -v ${version} -o ${RESOURCE_DIR}/rsync/request/animation.request-${version}

# -------------------- #
# 3. crawl split
# -------------------- #
echo "# 3. crawl split ==> movie"
${PROJECT_DIR}/scripts/crawlSplit.sh -c movie -v ${version} -o ${RESOURCE_DIR}/rsync/crawl
echo "# 3. crawl split ==> dramak"
${PROJECT_DIR}/scripts/crawlSplit.sh -c dramak -v ${version} -o ${RESOURCE_DIR}/rsync/crawl
echo "# 3. crawl split ==> dramaf"
${PROJECT_DIR}/scripts/crawlSplit.sh -c dramaf -v ${version} -o ${RESOURCE_DIR}/rsync/crawl
echo "# 3. crawl split ==> animation"
${PROJECT_DIR}/scripts/crawlSplit.sh -c animation -v ${version} -o ${RESOURCE_DIR}/rsync/crawl

# -------------------- #
# 4. crawl to hocon 
# -------------------- #
echo "# 4. hoppin crawl to hocon ==> movie naver" 
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u naver -c movie -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-movie-naver-meta-${version}.conf
echo "# 4. hoppin crawl to hocon ==> movie kmdb" 
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u kmdb -c movie -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-movie-kmdb-meta-${version}.conf

echo "# 4. hoppin crawl to hocon ==> dramak naver" 
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u naver -c dramak -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-dramak-naver-meta-${version}.conf
echo "# 4. hoppin crawl to hocon ==> dramak daum" 
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u daum -c dramak -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-dramak-daum-meta-${version}.conf
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u daum -c dramak -t episode -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-dramak-daum-episode-${version}.conf

echo "# 4. hoppin crawl to hocon ==> dramaf naver" 
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u naver -c dramaf -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-dramaf-naver-meta-${version}.conf
echo "# 4. hoppin crawl to hocon ==> dramaf daum" 
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u daum -c dramaf -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-dramaf-daum-meta-${version}.conf
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u daum -c dramaf -t episode -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-dramaf-daum-episode-${version}.conf

echo "# 4. hoppin crawl to hocon ==> animation naver" 
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u naver -c animation -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-animation-naver-meta-${version}.conf
echo "# 4. hoppin crawl to hocon ==> animation daum" 
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u daum -c animation -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-animation-daum-meta-${version}.conf
${PROJECT_DIR}/scripts/tsv2hocon.sh -s hoppin -u daum -c animation -t episode -v ${version} > ${RESOURCE_DIR}/resource/hocon/hoppin-animation-daum-episode-${version}.conf

echo "# 4. tstore crawl to hocon ==> movie naver" 
${PROJECT_DIR}/scripts/tsv2hocon.sh -s tstore -u naver -c movie -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/tstore-movie-naver-meta-${version}.conf
echo "# 4. tstore crawl to hocon ==> movie kmdb" 
${PROJECT_DIR}/scripts/tsv2hocon.sh -s tstore -u kmdb -c movie -t meta -v ${version} > ${RESOURCE_DIR}/resource/hocon/tstore-movie-kmdb-meta-${version}.conf


