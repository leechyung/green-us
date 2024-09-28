<h2 align="center">GREEN-US, 친환경적 습관 형성을 위한 앱</h2>

<p align="center">
  <img src="https://github.com/green-us-2024/green-us/blob/main/backend/src/main/resources/static/images/login-image.jpg" alt="green-us main logo" width="400px" height="400px"/>
</p>


**프로젝트 주제** 친환경 습관 형성을 위한 앱 개발 (친환경 챌린지에 참여 후 완료하면 포인트를 통해 제테크를 하는 앱)

## 기술 스택 :pushpin:

| Category  | Technology        |
|-----------|-------------------|
| **Frontend**  | ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)  ![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white) ![Firebase](https://img.shields.io/badge/Firebase-039BE5?style=for-the-badge&logo=Firebase&logoColor=white)  ![html5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)  ![CSS](https://img.shields.io/badge/CSS-239120?&style=for-the-badge&logo=css3&logoColor=white)|
| **Backend**   | ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)  ![SpringBoot](https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springBoot&logoColor=white)  ![hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)|
| **Database**  | ![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)  ![Firebase](https://img.shields.io/badge/Firebase-039BE5?style=for-the-badge&logo=Firebase&logoColor=white) |
| **IDE**  | ![AndroidStudio](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)  ![Intellij](https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)|

*MySQL 8.xx 버전 사용
*Android Studio Koala 사용


## 개발 환경 세팅 :pencil:
❗ Frontend
*RetrofitManager -> 본이 pc의 ip주소 및 포트 사용 필요 (로컬 환경에서 실행)
 ```xml
val retrofit = Retrofit.Builder()
            .baseUrl("본인 pc ip 주소")
```

*AddressDialogFragment -> 본이 pc의 ip주소 및 포트 사용 필요 (로컬 환경에서 실행)
 ```xml
val view = inflater.inflate(R.layout.fragment_address_search, container, false)
        webView = view.findViewById(R.id.webView)
        setupWebView()
        webView.loadUrl("본인 pc ip주소/address")
```

❗ Backend
 ```xml
spring.datasource.password=????
```
본인 DB 비밀번호 사용

