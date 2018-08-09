#!/bin/bash

source /Users/sindongboy/Dropbox/Documents/resource/digital-contents-unifier/env.sh

function usage() {
	echo "usage: $0 [version]"
	exit 1
}

if [[ $# -eq 0 ]]; then
	usage
fi

version=$1
echo "movie"
./crawl-request.sh -s hoppin^tstore -c movie -v ${version} -o ${UM_RSYNC}/request/movie.request-${version}
echo "animation"
./crawl-request.sh -s hoppin -c animation -v ${version} -o ${UM_RSYNC}/request/animation.request-${version}
echo "dramak"
./crawl-request.sh -s hoppin -c dramak -v ${version} -o ${UM_RSYNC}/request/dramak.request-${version}
echo "dramaf"
./crawl-request.sh -s hoppin -c dramaf -v ${version} -o ${UM_RSYNC}/request/dramaf.request-${version}

