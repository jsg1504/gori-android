# gori-android

## 고리는 사용자간 사진을 공유하는 서비스입니다.
### Play Store : https://play.google.com/store/apps/details?id=com.mozible.gori
<img src="https://github.com/jhlee789/gori-android/blob/master/graphic_image.png" width="90%">
<img src="https://github.com/jhlee789/gori-android/blob/master/screen1.png" width="30%">
<img src="https://github.com/jhlee789/gori-android/blob/master/screen2.png" width="30%">
<img src="https://github.com/jhlee789/gori-android/blob/master/screen3.png" width="30%">
### 고리에서 구현된 기능은 다음과 같습니다.

> 로그인 페이지

>> 고리 서버로 회원가입 요청

>> 고리 서버로 로그인 요청

>> 팔로잉 하는 사람들을 DB에 저장

> 뉴스피드 페이지

>> 팔로잉 하고있는 사람들의 정보를 시간순서대로 받아와서 리스트뷰에 출력

>>> 리스트 뷰에는 프로필 사진, 유저네임, 컨텐츠 사진, 설명이 출력

>>> 리스트뷰에서 유저 프로필 클릭하면 유저 프로필 상세로 이동

> Allfeed 페이지

>> 모든 사용자의 컨텐츠를 시간순으로 갖고와서 출력

>>> 리스트 뷰에는 프로필 사진, 유저네임, 컨텐츠 사진, 설명이 출력

>>> 리스트뷰에서 유저 프로필 클릭하면 유저 프로필 상세로 이동

> Photo 페이지

>> 사진 업로드 기능

>> 총 3가지 단계로 구성

>>> Content Upload한다고 서버에 알린 후 id 받아오기

>>> 받아온 id를 토대로 url 구성 후 서버에 content image upload

>>> description 작성 후 서버에 content description upload

> Profile 페이지

>> 특정 유저 또는 자신에 대한 정보 출력

>> 만약 자신의 프로필 페이지일 경우, 프로필 편집과 logout 버튼이 생성

>> 만약 다른 사람의 프로필 페이지일 경우, 팔로우/언팔로우 버튼이 생성

> ETC

>> GoogleAnalytics 연동