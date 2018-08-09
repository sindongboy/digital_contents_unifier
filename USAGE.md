
# ----------------- #
# Usage and Example
# ----------------- #

1. 수집 요청
	bin: ${PROJECT}/scripts/crawl-request.sh
	options:
		-h	help"
		-s	service name list, ex) hoppin^tstore^xlife"
		-c	category, ex) movie, dramak, dramaf, animation"
		-v	version, ex) 20140303"
		-o	output file"
	example:
		${PROJECT}/scripts/crawl-request.sh -s hoppin^tstore -c movie -v 20150409 -o ./movie.request-20150409

2. 제목 Stopword Generation
	: 제목 Stopword 의 후보를 생성 해준다. 
	*** 주의 ***
	- 우선, maxmovie 데이타에 대하여 적용하는 것이 좋음
	- 이유는, maxmovie 의 경우, 제목 대비 stopword 비율이 상당히 높음
	- 다른 Service, Source 데이타의 경우, 부작용이 있음
	
	bin: ${PROJECT}/scripts/stopword-gen.sh
	options: 
		-h	help
		-s	service name
		-c	category name
		-u	source name
		-v	version
		-o	output file name
	example:
		${PROJECT}/scripts/stopword-gen.sh -s xlife -c movie -u maxmovie -v 20150409 -o ./title-stopwords.list-20150409


