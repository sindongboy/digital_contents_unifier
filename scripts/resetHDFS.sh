#!/bin/bash

function usage() {
	echo "usage: $0 [version]"
	exit 1
}


if [[ $# -ne 1 ]]; then
	usage
fi

version=$1
hput="hadoop fs -put"
hrm="hadoop fs -rm -r"
hcat="hadoop fs -cat"

echo "removie previous umeta in hdfs"
$hrm /meta/dc/an/*
$hrm /meta/dc/df/*
$hrm /meta/dc/dk/*
$hrm /meta/dc/mv/*


echo "create empty umeta"
touch animation-umeta-${version}
touch dramak-umeta-${version}
touch dramaf-umeta-${version}
touch movie-umeta-${version}

echo "category.name = \"animation\"" > animation-umeta-${version}
echo "" >> animation-umeta-${version}
echo "umeta = [" >> animation-umeta-${version}
echo "]" >> animation-umeta-${version}

echo "category.name = \"dramak\"" > dramak-umeta-${version}
echo "" >> dramak-umeta-${version}
echo "umeta = [" >> dramak-umeta-${version}
echo "]" >> dramak-umeta-${version}

echo "category.name = \"dramaf\"" > dramaf-umeta-${version}
echo "" >> dramaf-umeta-${version}
echo "umeta = [" >> dramaf-umeta-${version}
echo "]" >> dramaf-umeta-${version}

echo "category.name = \"movie\"" > movie-umeta-${version}
echo "" >> movie-umeta-${version}
echo "umeta = [" >> movie-umeta-${version}
echo "]" >> movie-umeta-${version}

echo "put newly created umeta into hdfs"
$hput animation-umeta-${version} /meta/dc/an/
$hput dramak-umeta-${version} /meta/dc/dk/
$hput dramaf-umeta-${version} /meta/dc/df/
$hput movie-umeta-${version} /meta/dc/mv/

echo "cat all meta just put"
$hcat /meta/dc/an/animation-umeta-${version}
$hcat /meta/dc/dk/dramak-umeta-${version}
$hcat /meta/dc/df/dramaf-umeta-${version}
$hcat /meta/dc/mv/movie-umeta-${version}

rm -f *0505
echo "all done!"
