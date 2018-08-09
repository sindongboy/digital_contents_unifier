#!/bin/bash

source /Users/sindongboy/Dropbox/Documents/resource/digital-contents-unifier/bin/env.sh

function usage() {
	echo "usage: $0 [version]"
	exit 1
}

if [[ $# -ne 1 ]]; then 
	usage
fi

version=$1

${PROJECT_BASE}/scripts/hocon2tsv.sh -c animation -v ${version} > ${RESOURCE_BASE}/union/meta/animation/hoppin-meta.uni.animation-${version}
${PROJECT_BASE}/scripts/hocon2tsv.sh -c dramak -v ${version} > ${RESOURCE_BASE}/union/meta/dramak/hoppin-meta.uni.dramak-${version}
${PROJECT_BASE}/scripts/hocon2tsv.sh -c dramaf -v ${version} > ${RESOURCE_BASE}/union/meta/dramaf/hoppin-meta.uni.dramaf-${version}
