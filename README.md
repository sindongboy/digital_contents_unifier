
# -------------------- #
# TSV field
# -------------------- #
1. cid
2. pid
3. title
4. rep synopsis
5. date
6. rate
7. genre
8. genre bigram
9. directors
10. actors
11. national
12. keyword
13. score
14. scoreCount
15. purchase
16. m2k add
17. m2k rem

# --------- #
# CODE 정의
# --------- #

0. Rate
	00 : 전체상영가 
	99 : 제한상영가 
	12 : 12세이상관람가
	15 : 15세이상관람가
	18 : 18세이상관람가

1. Country

# --------- # 
# 통합 목록 
# --------- # 

0. Configuration
	설정 파일 : ${PROJECT}/config/unification.conf
	설정 방법 :
		- 각 카테고리별로 통합의 대상 목록을 'set' 항목에 추가한다.
		- 각 대상의 naming convention 은 다음과 같다.
			[service]-[category]-[source]

1. Movie (MV) Category
	: ${category} 가 Movie 인 모든 Meta 가 대상이다. 
		// hoppin meta for hoppin service
	-> hoppin-movie.meta-${version}
		// tstore meta for tstore service
	-> tstore-movie.meta-${version}
		// naver meta for naver service
	-> hoppin-movie-naver.meta-${version}
		// naver meta for tstore service 
	-> tstore-movie-naver.meta-${version}
		// kmdb meta for hoppin service
	-> hoppin-movie-kmdb.meta-${version}
		// kmdb meta for tstore service
	-> tstore-movie-kmdb.meta-${version}
		// maxmovie meta for xlife service
	-> xlife-movie-maxmovie.meta-${version}

2. Animation (AN), Dramak (DK), Dramaf (DF)
	: ${category} 가 animation/dramak/dramaf 인 모든 Meta 가 대상이다.

	TV="animation,dramak, dramaf"
		// hoppin meta for hoppin service
	-> hoppin-${TV}.meta-${version}
		// naver meta for hoppin service
	-> hoppin-${TV}-naver.meta-${version}
		// daum meta for hoppin service
	-> hoppin-${TV}-daum.meta-${version}
