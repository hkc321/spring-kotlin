# spring-kotlin
[![CI-CD](https://github.com/hunkicho/spring-kotlin/actions/workflows/main.yml/badge.svg)](https://github.com/hunkicho/spring-kotlin/actions/workflows/main.yml)
[![codecov](https://codecov.io/gh/hunkicho/spring-kotlin/branch/master/graph/badge.svg?token=JOH259X9H2)](https://codecov.io/gh/hunkicho/spring-kotlin)
- Practice spring-boot with kotlin</br>
- Practice hexagonal architecture

## Environment
[![](https://img.shields.io/badge/koltin-1.9.0-green?logo=kotlin)](https://kotlinlang.org/docs/whatsnew19.html)
[![](https://img.shields.io/badge/spring%20boot-3.1.0-green?logo=springboot) ](https://spring.io/blog/2023/05/18/spring-boot-3-1-0-available-now)
[![](https://img.shields.io/badge/spring_data_jpa-3.1.0-green) ](https://jakarta.ee/specifications/persistence/3.1/)
[![](https://img.shields.io/badge/Spring%20Security-6.1.0-green?logo=springsecurity)](https://kotlinlang.org/docs/whatsnew19.html)

[![](https://img.shields.io/badge/hibernate-6.2.2.Final-blue?logo=hibernate)](https://hibernate.org/orm/releases/6.2/)
[![](https://img.shields.io/badge/kotlin_jdsl-2.2.1.RELEASE-blue?link=https://github.com/line/kotlin-jdsl)](https://github.com/line/kotlin-jdsl/releases/tag/2.2.1.RELEASE)
[![](https://img.shields.io/badge/mysql-8.0.32-blue?logo=mysql)](https://dev.mysql.com/doc/relnotes/mysql/8.0/en/news-8-0-23.html)
[![](https://img.shields.io/badge/redis-7.0.12-blue?logo=redis)](https://github.com/redis/redis/releases/tag/7.0.12)

[![](https://img.shields.io/badge/junit5-5.8.1-none?logo=junit5) ](https://junit.org/junit5/)
[![](https://img.shields.io/badge/kotest-5.6.2-none?logo=kotest) ](https://kotest.io/)

[![](https://img.shields.io/badge/swagger-none?logo=swagger) ](https://swagger.io/)

[![](https://img.shields.io/badge/ubuntu-20.04-white?logo=ubuntu)](https://ubuntu.com/)
[![](https://img.shields.io/badge/docker-20.10.24-white?logo=docker) ](https://www.docker.com/)
[![](https://img.shields.io/badge/nginx-1.25.1-white?logo=nginx) ](https://www.nginx.com/)
[![](https://img.shields.io/badge/github_actions-white?logo=githubactions) ](https://docs.github.com/ko/actions)

[![](https://img.shields.io/badge/slack-purple?logo=slack)](https://slack.com/intl/ko-kr/)
[![](https://img.shields.io/badge/sentry-purple?logo=sentry)](https://sentry.io/welcome/)

## Swagger
https://jgchktestdns.shop/swagger<br/>
포트폴리오 링크가 잘못 기입되었습니다. 위 링크로 접속 부탁드립니다.


<br/><br/>

## 설명
kotlin과 srping boot를 이용한 회원 및 게시판 관련 api<br/>
- 회원가입 및 로그인 및 로그아웃
- 회원 CRUD, 권한 변경
- 게시판/게시글/댓글 CRUD 및 페이징
- 게시글/댓글 좋아요

## ERD
![erd](https://github.com/hunkicho/spring-kotlin/assets/115965829/469d6edc-1327-4d6f-a85a-cec5822c815a)


## 시스템 구조
![systemimg](https://github.com/hunkicho/spring-kotlin/assets/115965829/62b1c8c1-a222-48fe-b116-6428d0ca1e7f)


## 주요내용

### hexagonal architecture를 통해 외부로부터 주요 로직을 격리

- hexagonal architecture를 구현해 각 도메인 별 주요로직을 외부요소(db)로 부터 격리하여 유지보수가 쉽도록 하였습니다.
- [관련 블로그 게시물](https://medium.com/@jgchk4814/hexagonal-architecture-3729e9a9200b)

### spring security 및 jwt를 통한 인증 및 인가 구현

- custom filter들을 등록하여 spring security를 사용하였습니다.
- 인증 및 인가 시 jwt를 사용하도록 하였습니다.
    - 인증 시 custom filter에서 jwt를 검사하여 유효성을 검사합니다.
    - 성공 시 access token과 refresh token을 발행합니다.
    - access token 만료 시 header에 refresh token을 포함하여 새로운 access token을 발급받도록 하였습니다.

### OAS를 이용하여 테스트 코드 기반 swagger api 문서 작성

- restdocs-api-spec 오픈소스를 사용하여 swagger api 문서를 작성
    - spring rest doc과 swagger의 장점을 이용할 수 있습니다.
    - test code를 작성해야만 문서를 작성할 수 있기 때문에 문서의 신뢰도가 상승합니다.
    - test code를 통해  OAS파일을 만들고 해당 파일을 swagger로  띄웁니다.

### Codecove를 통한 코드 커버리지 측정

- 툴을 통해 코드 커버리지를 측정하여 테스트 코드의 범위를 파악

### 필요한 곳에 redis 사용
 redis의 특성을 이용하여 필요한 곳에 사용
- JWT 구현
    - 만료 시간이 있는 특성 사용
    - 로그인 시 refresh토큰을 redis에 유효 기간을 지정하여 저장
    - user가 refresh token을 통해 access token 발급 요청 시 유효 기간을 확인하여 처리
    - access token이 만료되기 전 로그아웃 시, 해당 토큰을 남은 유효 기간 만큼 redis에 저장한다.
        - 이후 로그아웃된 access token으로 로그인을 시도해도 불가능하게 하였습니다.
- 게시글 및 댓글 좋아요
    - 중복이 불가능한 set 자료구조 특성 사용
    - 좋아요 클릭 시 user email을 set 자료구조에 저장하여 중복 클릭이 불가능하도록 설정

### advice를 통한 일관적인 예외 처리

- 상황별로 예외처리를 하였습니다.
- controller advice를 통해 각종 예외 및 에러들을 일관적인 형태로 알려주도록 하였습니다.

### nginx를 활용한 무중단 배포 구현
- nginx를 이용하여 blue green 방식의 무중단 배포를 구현하였습니다.
- [관련 블로그 게시물](https://medium.com/@jgchk4814/nginx%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EB%AC%B4%EC%A4%91%EB%8B%A8-%EB%B0%B0%ED%8F%AC-feat-docker-ec85d93623d5)에서 자세한 내용을 볼 수 있습니다

### slack과 sentry를 통한 에러 추적
- 배포작업이 완료되면 slack에 알림을 보낼 수 있도록 하였습니다.
- sentry를 통해 어플리케이션을 모니터링 하며 에러로그를 기록할 수 있도록 하였습니다.
- 무료버전을 이용중이기 때문에 에러로그 캡쳐 시 slack에도 메시지를 보내도록 하였습니다.

## 완료 후 느낀점
- 크고 작은 새로운 기술들을 접하며 어플리케이션의 시작부터 배포까지의 경험을 할 수 있었습니다.
- 안해본 것을 도전하기 위해 처음보는 기술들을 대거 사용하여 어려움이 있었는데 포기하지 않고 완성하여 뿌듯함이 있습니다.
- 새로운 기술을 접하며 알아가는 과정이 고통스러웠지만 적용하고 나면 기분이 굉장히 좋았습니다.
  - 한편 또다른 느낀점은 실무에서는 무조건 새롭고 좋은 기술만이 정답이 아니라는 생각도 들었습니다.
  - 비교적 작은 api 임에도 불구하고 새로운 기술들을 적용하느라 생각보다 시간이 오래 걸렸습니다.
  - 해당 기술에 대한 이해도가 낮은 상태에서 레퍼런스가 적은 오픈소스를 사용하였기 때문이라고 생각합니다.
  - 만약 실무에서 특정 기술을 고집하느라 일정을 맞추지 못한다면 문제가 있을거라고 생각합니다.
  - 상황에 맞는 기술을 선택하는 것 또한 개발자의 능력이라고 느꼈습니다.
- 개발 중 다양한 문제들을 마주치며 저의 부족한점을 깨달았고 끊임없는 공부가 필요하다고 느꼈습니다.

## 프로젝트 관련 추가 블로그 게시글
- [Kotlin JDSL 사용해보기](https://medium.com/@jgchk4814/kotlin-jdsl-%EC%82%AC%EC%9A%A9%ED%95%B4%EB%B3%B4%EA%B8%B0-92fb77dcd10f)
- [Redis 비밀번호에 $가 있을 때 나타나는 에러?](https://medium.com/@jgchk4814/redis-%EB%B9%84%EB%B0%80%EB%B2%88%ED%98%B8%EC%97%90-%EA%B0%80-%EC%9E%88%EC%9D%84-%EB%95%8C-%EB%82%98%ED%83%80%EB%82%98%EB%8A%94-%EC%97%90%EB%9F%AC-b98234e90002)
- [DataIntegrityViolationException 잡기](https://medium.com/@jgchk4814/dataintegrityviolationexception-%EC%9E%A1%EA%B8%B0-5610a594cd3c)
