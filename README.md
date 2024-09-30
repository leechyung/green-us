<h2 align="center">GREEN-US, 친환경적 습관 형성을 위한 앱</h2>

<p align="center">
  <img src="https://github.com/green-us-2024/green-us/blob/main/backend/src/main/resources/static/images/login-image.jpg" alt="green-us main logo" width="400px" height="400px"/>
</p>


**프로젝트 주제** 친환경 습관 형성을 위한 앱 개발 (친환경 챌린지에 참여 후 완료하면 포인트를 통해 재테크를 하는 앱)

## 기술 스택 :pushpin:

| Category  | Technology        |
|-----------|-------------------|
| **Frontend**  | ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)  ![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white) ![Firebase](https://img.shields.io/badge/Firebase-DD2C00?style=for-the-badge&logo=Firebase&logoColor=white)  ![html5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)  ![CSS](https://img.shields.io/badge/CSS-239120?&style=for-the-badge&logo=css3&logoColor=white)|
| **Backend**   | ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)  ![SpringBoot](https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springBoot&logoColor=white)  ![hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)|
| **Database**  | ![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)  ![Firebase](https://img.shields.io/badge/Firebase-DD2C00?style=for-the-badge&logo=Firebase&logoColor=white) |
| **IDE**  | ![AndroidStudio](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)  ![Intellij](https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)|

* MySQL 8.xx 버전 사용<br>
* Android Studio Koala 사용


## 개발 환경 세팅 ✏️
❗ Frontend
* RetrofitManager -> 본이 pc의 ip주소 및 포트 사용 필요 (로컬 환경에서 실행)
 ```xml
val retrofit = Retrofit.Builder()
            .baseUrl("본인 pc ip 주소")
```

* AddressDialogFragment -> 본이 pc의 ip주소 및 포트 사용 필요 (로컬 환경에서 실행)
 ```xml
val view = inflater.inflate(R.layout.fragment_address_search, container, false)
        webView = view.findViewById(R.id.webView)
        setupWebView()
        webView.loadUrl("본인 pc ip주소/address")
```

❗ Backend
* application.properties -> 본인 DB 비밀번호 사용
 ```xml
spring.datasource.password=????
```
+ SQL을 직접 실행해도 되지만, 서버를 실행시키면 자동으로 생성 됌.

## 주요 기능 ✔️
* 환경부 정책 정보 나눔
```
가장 상단 배너에 사용자에게 유용할 환경부 정책을 홍보합니다. 클릭시 해당 게시글이 업로드된 링크로 이동합니다.
```
![Vector](https://github.com/user-attachments/assets/91efbb64-cc54-416d-a6e3-068557cdce8a)
![Vector-1](https://github.com/user-attachments/assets/c508addd-e537-4044-bb0b-b42fd0d7a7d9)
* 내주변
```
사용자 위치 기반 주변 5개의 친환경 마켓의 정보를 제공함으로써 친환경 제품 소비를 장려합니다. 아이템 클릭시 해당 상점의 sns 링크로 이동합니다.
```
![Vector](https://github.com/user-attachments/assets/a43dacf4-3a58-45f9-91a5-647dd32bb5d2)
![Vector-1](https://github.com/user-attachments/assets/7b94effe-53f4-4d9a-8736-a3ce2204827d)
* 만보기
```
사용자를 한 번이라도 더 앱에 진입시키기 위한 기능입니다.
```
![Vector-1](https://github.com/user-attachments/assets/81a0c910-9fe9-436e-8bc2-61afbc3bce7c)
![Vector](https://github.com/user-attachments/assets/2b8cec93-43b3-400f-bc39-3290b4beaeed)

* 그리닝 개설하기
```
사용자는 친환경 그리닝(미션)을 만들어 다른 사용자들과 함께 습관형성에 도전할 수 있습니다.
```
![Vector](https://github.com/user-attachments/assets/835d0d8a-3250-4b2c-b229-b5959cac8b44)
![Vector-1](https://github.com/user-attachments/assets/2ca760cf-f9b8-4387-8217-c54a9bc172b0)

* 그리닝 참여하기
```
사용자는 원하는 그리닝의 예치금을 카드로 결제하여 도전을 신청합니다. 카드결제는 부트페이 API를 사용하였습니다.
```
![Vector](https://github.com/user-attachments/assets/aa5a8d3e-7008-47d7-82b4-3151d7e98e0b)
![Vector](https://github.com/user-attachments/assets/645f6966-0342-4cf5-a386-faa254ed6266)
![Vector](https://github.com/user-attachments/assets/cc0eb9ef-6ff4-4ce7-861a-2962eb8fa425)
* 그리닝 인증하기
```
사용자는 신청한 그리닝을 정해진 빈도와 기간동안 사진으로 인증하여 달성해나갑니다.
```
![Vector](https://github.com/user-attachments/assets/1c7d3914-bbb4-4b18-83c5-fdfcfbba3e08)
![Vector-1](https://github.com/user-attachments/assets/d978bd53-b02a-45f9-bd0a-bd4f91f79459)
* 그리닝 리뷰
```
사용자는 완수한 그리닝에 대해 리뷰를 남길 수 있고, 다른 사용자에 리뷰도 확인할 수 있습니다. 이를 통해 성취감을 공유할 수 있습니다.
```
![Vector](https://github.com/user-attachments/assets/631ce798-591e-4a4c-b687-e042fff0e7e9)
![Vector-1](https://github.com/user-attachments/assets/2252cdc0-5670-4f17-a06d-21fd8deffc4a)
* 포인트
```
사용자는 100% 완수시 앞서 결제하였던 예치금과 상금을 포인트로 지급받습니다.
```
![Vector-1](https://github.com/user-attachments/assets/95745a58-0864-478e-8dae-30dcf7fbe4fd)
![Vector](https://github.com/user-attachments/assets/237163a8-b841-4342-8e90-f06df53b7944)

## ERD 📝
![ERD](https://github.com/user-attachments/assets/78dd3c0a-79e7-4625-b478-2067fb9ca431)



