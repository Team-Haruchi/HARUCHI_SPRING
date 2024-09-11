# HARUCHI (하루치) Spring

![0  썸네일](https://github.com/user-attachments/assets/25f09e8f-04e4-4852-abd7-f16a1a648926)

## 1. 아이디어 소개

> **지출통제 도우미.<br><br>
>하루 예산을 기준으로 유동적으로 소비하세요.<br>
지정한 한도 내에서 소비할 수 있도록 도와드립니다.**<br><br>
예산을 설정하시면 하루에 얼마나 쓸 수 있는지 알려드려요.
당일의 지출에 따라 그날그날의 예산을 재분배할 수도 있어요.
> 
<br>

**이번 달 생활비가 부족해… 긴축에 들어갈 때**

- 아 오늘 외식하고 싶은데…지금 이거 먹으면 앞으로 얼마나 써야 하지?
- 이거 너무 사고 싶다…어차피 이번달 월급 이미 많이 쓴 것 같은데,
다음달 월급 들어오면 진짜 가계부 써야지!
- (잔액부족) 분명히 150만원이 있었는데…? 언제 다 썼지?<br>

>📌 **가계부 앱의 문제점**<br>
>- ✅ **가계부를 사용하는 목적:**<br>
지난 소비를 돌아보고 향후 지출을 적당한 수준으로 유지하기 위함임. 그러나,
<br>

1️⃣ **지출 통제가 아닌 기록에 더 초점이 맞춰져 있다.**

항상 사후적인 분석을 거쳐 이뤄지기 때문에 (즉, **회고적이기 때문에**)

매 긴축사이클/상황에 맞춘 계획적인 지출 통제란 불가능함

- 예산 설정 기능이 있는 경우도 있지만 복잡하거나 번거롭다.
- 당장 지출을 줄여야 할 때, 지금까지 얼마나 썼는지를 알아서는 부족하다.
- 월별 통계가 나오고 나서야 사후적으로, 그리고 간접적으로 지출을 관리할 수 있다.

**∴ 목적에 부합하는 기능을 제공하지 않는다**

2️⃣ **통계만을 보여준다**

- **분석**(어디서 줄여야 하고)과 **대응**(어떻게 줄일 건지)은 **이용자의 몫**

→ 지출 통제는 사후적이 아닌, 소비와 동시적으로 이루어져야 한다.
    (예측까지 가능하다면 더욱 바람직)

3️⃣ **하루에 얼마나 써야 할지 몰라서 지출의 기준을 세우기 어렵다.**

- 그래서 총예산을 단순 일할 계산하면?

       → 하루 예산을 알더라도 매일 씀씀이가 다른데…

오늘 이렇게 썼을 때 앞으로

- 하루당 얼마나 덜/더 써야 하는지
- 매일 다른 일정마다 예산을 어떻게 배분해야 할지 알기 어렵다.<br>

>▫️ ✅ **결론:**<br>
돈을 아껴야 하는 사람은 <br>
~~얼마나 썼는지~~가 중요한 게 아니라<br>
앞으로 얼마를 쓸 수 있는지(한도 내 소비를 위한 미래의 지출기준)가 더 중요하다.<br><br>
⇒ 이를 위한 기준을 제시하고자 하는 것
<br>

🚧 그래서,

- **직관적인 소비 기준이 제시**되고
- **하루에 얼마나 써야 할지 한눈에 보여** 지출을 관리할 수 있으면서도
- **총예산 내에서 그날그날 유동적으로 소비**할 수 있도록 돕는 앱을 기획했습니다.

- 오늘 과소비했더라도, 예산 재배치를 통해 긴축 목표를 잃지 않게 해줄 수 있습니다.

→ **목표**: 하루치 예산을 가이드라인으로 하여 소비하되, 덜 쓰든 더 쓰든 예산 재분배를 통해 총예산 내에서 유동적인 소비가 가능하도록

**총예산 한도 내에서 소비와 예산 계획에 따라, 매일의 지출 가이드라인이 적응적으로 새롭게 제시됩니다.**
<br><br><br>

## **2. 개발 기간**

전체 개발 기간 : 24.07.13 - 24.08.21

기능 구현 : 24.07.18 - 24.08.21
<br><br><br>

## 3. 팀원 소개

| 임지민/제아 | 정명서/루베 | 김은지/리버 | 송민아/앤드 |
| --- | --- | --- | --- |
| [@jimmy0524](https://github.com/jimmy0524) | [@ohu-star](https://github.com/ohu-star) | [@hcg0127](https://github.com/hcg0127) | [@ssongmina](https://github.com/ssongmina) |
| 서버 배포 및 AWS DB 연결, 당겨쓰기 및 넘겨쓰기 관련 기능, 지출 마감 관련 기능 | 초기세팅, 한달 예산 관련 기능, 한주 예산 및 남은 일정 및 금액 조회 기능 | API 명세서, ERD 설계, 회원가입 및 로그인, 이메일 인증, 회원 관련 기능 | API 명세서, ERD 설계, 하루 및 날짜별 예산 조회, 수입/지출 등록 및 삭제 기능 |

<br><br><br>

## 4. 개발 환경

- Java 17
- JDK 17.0.6
- **IDE** : IntelliJ IDEA 2024.1
- **Framework** : Springboot(3.3.1)
- **Database** : MySQL(AWS RDS), redis(AWS ElastiCache)
- **ORM** : Spring Data JPA
- **배포** : GitHub Actions를 사용하여 AWS Elastic Beanstalk에 개발용 서버 배포
- **API 툴** : Swagger
<br><br><br>

## 5. 브랜치 전략 및 협업 규칙

- main, develop, feat 브랜치로 나누어 개발을 진행하였습니다.
    - main 브랜치는 최종 개발 완료된 develop 브랜치의 내용을 merge하는 브랜치입니다.
    - develop 브랜치는 개발 단계에서 모든 기능이 이곳에 merge되는 브랜치입니다.
    - feat 브랜치는 각 기능별로 이슈를 생성하고, 해당 이슈에 대해 개발을 진행한 후, 완료된 기능을 develop 브랜치에 Pull Request(PR)하여 merge하는 과정으로 운영되었습니다.
- Pull Request(PR)은 최소 2명이 수락해야 merge할 수 있도록 제한하였습니다.
<br><br><br>

## 6. 프로젝트 구조

```xml
src
├─main
│  ├─java
│  │  └─umc
│  │      └─haruchi
│  │          │  HaruchiApplication.java
│  │          │
│  │          ├─apiPayload
│  │          │  │  ApiResponse.java
│  │          │  │
│  │          │  ├─code
│  │          │  │  │  BaseCode.java
│  │          │  │  │  BaseErrorCode.java
│  │          │  │  │  ErrorReasonDTO.java
│  │          │  │  │  ReasonDTO.java
│  │          │  │  │
│  │          │  │  └─status
│  │          │  │          ErrorStatus.java
│  │          │  │          SuccessStatus.java
│  │          │  │
│  │          │  └─exception
│  │          │      │  ExceptionAdvice.java
│  │          │      │  GeneralException.java
│  │          │      │
│  │          │      └─handler
│  │          │              BudgetRedistributionHandler.java
│  │          │              DayBudgetHandler.java
│  │          │              ExpenditureHandler.java
│  │          │              IncomeHandler.java
│  │          │              JwtExceptionHandler.java
│  │          │              MemberHandler.java
│  │          │              MonthBudgetHandler.java
│  │          │
│  │          ├─config
│  │          │  │  EmailConfig.java
│  │          │  │  SwaggerConfig.java
│  │          │  │
│  │          │  ├─login
│  │          │  │  │  SecurityConfig.java
│  │          │  │  │
│  │          │  │  ├─auth
│  │          │  │  │      MemberDetail.java
│  │          │  │  │      MemberDetailService.java
│  │          │  │  │
│  │          │  │  └─jwt
│  │          │  │          JwtAccessDeniedHandler.java
│  │          │  │          JwtAuthenticationEntryPoint.java
│  │          │  │          JwtAuthenticationFilter.java
│  │          │  │          JwtExceptionHandlerFilter.java
│  │          │  │          JwtUtil.java
│  │          │  │
│  │          │  └─redis
│  │          │          RedisConfig.java
│  │          │
│  │          ├─converter
│  │          │      BudgetRedistributionConverter.java
│  │          │      DayBudgetConverter.java
│  │          │      MemberConverter.java
│  │          │      MonthBudgetConverter.java
│  │          │
│  │          ├─domain
│  │          │  │  DayBudget.java
│  │          │  │  Expenditure.java
│  │          │  │  Income.java
│  │          │  │  Member.java
│  │          │  │  MonthBudget.java
│  │          │  │  PullMinusClosing.java
│  │          │  │  PushPlusClosing.java
│  │          │  │  Withdrawer.java
│  │          │  │
│  │          │  ├─common
│  │          │  │      BaseEntity.java
│  │          │  │
│  │          │  └─enums
│  │          │          ClosingStatus.java
│  │          │          DayBudgetStatus.java
│  │          │          ExpenditureCategory.java
│  │          │          IncomeCategory.java
│  │          │          RedistributionOption.java
│  │          │
│  │          ├─repository
│  │          │      DayBudgetRepository.java
│  │          │      ExpenditureRepository.java
│  │          │      IncomeRepository.java
│  │          │      MemberRepository.java
│  │          │      MonthBudgetRepository.java
│  │          │      PullMinusClosingRepository.java
│  │          │      PushPlusClosingRepository.java
│  │          │      WithdrawerRepository.java
│  │          │
│  │          ├─service
│  │          │      BudgetRedistributionService.java
│  │          │      DayBudgetService.java
│  │          │      MemberService.java
│  │          │      MonthBudgetService.java
│  │          │
│  │          └─web
│  │              ├─controller
│  │              │      BudgetRedistributionController.java
│  │              │      DayBudgetController.java
│  │              │      MemberApiController.java
│  │              │      MonthBudgetController.java
│  │              │      RootController.java
│  │              │
│  │              └─dto
│  │                      BudgetRedistributionRequestDTO.java
│  │                      BudgetRedistributionResponseDTO.java
│  │                      DayBudgetRequestDTO.java
│  │                      DayBudgetResponseDTO.java
│  │                      MemberRequestDTO.java
│  │                      MemberResponseDTO.java
│  │                      MonthBudgetRequestDTO.java
│  │                      MonthBudgetResponseDTO.java
│  │
│  └─resources
│          application-secret.yml
│          application.yml
│
└─test
└─java
└─umc
└─haruchi
HaruchiApplicationTests.java
```
<br><br><br>

## 7. API 명세서

![하루치api](https://github.com/user-attachments/assets/04033203-76a9-4bd9-b47e-9ae840baca09)
<br><br><br>

## 8. 세부 기능(Member, MonthBudget, DayBudget, BudgetRedistribution)

![하루치3번째](https://github.com/user-attachments/assets/f495d598-9b40-483b-8968-2b7fceea8646)

### Member

- 로그인(일반) : Spring Security와 JWT를 이용해 사용자 인가/인증 기능 구현
- 이메일 인증 기반 회원가입 : Gmail SMTP를 사용해 Spring Boot에서 인증 코드를 메일로 보내고 Redis에 캐싱하는 방식을 사용해 구현
- 로그아웃 및 회원탈퇴: 발급된 토큰을 블랙리스트화하는 방식을 사용해 구현
- 회원 정보 조회: 토큰에서 뽑아낸 유저에 대한 정보 조회 기능 구현

### MonthBudget

- 한 달 예산 수정: 한 달 예산을 수정하고 하루 예산을 재분배하는 기능 구현
    - 하루 예산 재분배: 한 달 중 남은 일자에 예산을 고르게 분배, 나누어지지 않는 금액은 세이프 박스에 저장하도록 구현
- 한 달 예산 금액 조회: 이번 달의 예산 금액을 반환하도록 구현
- 한 달 지출률 조회: 이번 달의 지출률(한 달 예산 대비 사용한 금액)을 반환하도록 구현
- 한 주 예산 금액 조회: 이번 주의 하루 예산 리스트, 현재 달과 주차를 DTO로 변환하여 데이터 전달
- 남은 일정 및 금액 조회: 오늘부터 말일까지의 남은 일수와 남은 예산을 DTO로 변환하여 데이터 전달

### DayBudget

- 하루 예산 금액 조회 : 오늘 사용 가능한 금액을 업데이트하여 반환하도록 구현
- 날짜별 예산 금액 조회 : 오늘부터 말일까지의 예산 금액 리스트DTO를 생성하여 데이터 전달
- 수입 등록 / 삭제 : 오늘의 수입을 등록하고 삭제하는 로직 구현
- 지출 등록 / 삭제 : 오늘의 지출을 등록하고 삭제하는 로직 구현

### BudgetRedistribution

- 당겨쓰기 및 넘겨쓰기 : 고르게, 세이프박스, 특정일의 재분배 옵션을 ENUM으로 구분하여 각 로직 구현 (모든 과정에는 10의 자리 이하 절사 로직 포함)
- 지출 마감하기 : 하루 지출(DayBudget)의 양수/음수/0 판단하여
    - 양수인 경우 : 고르게 분배 or 세이프박스 ENUM으로 구분하여 로직 구현
    - 음수인 경우 : 고르게 가져오기 or 세이프박스 ENUM으로 구분하여 로직 구현
    - 0인 경우 : 재분배 옵션을 ZERO로 지정 후 바로 지출 마감
        
        (모든 과정에는 10의 자리 이하 절사 로직 포함)
        
- 지출 영수증 조회 : 수입, 지출, 당겨쓰기 및 넘겨쓰기의 각 리스트를 DTO로 변환하여 최종적으로 영수증 리스트 DTO를 생성하여 데이터 전달
<br><br><br>

## 9. ERD

![하루치 (HARUCHI) erd](https://github.com/user-attachments/assets/47d43e39-f9ee-4cb5-b87d-37a8a5560362)
<br><br><br>

## 10. 프로젝트 후기

### 🙊임지민/제아

프로젝트를 진행하는 동안 정말 많은 것을 배울 수 있었습니다. 협업 규칙을 체계적으로 정하고 진행한 프로젝트는 거의 처음이라, 템플릿 작성부터 함께 의논하여 완성하는 과정이 정말 소중했습니다. 특히 PR을 통해 서로 코드 리뷰를 진행하여 더 좋은 코드를 작성할 수 있었던 것 같습니다. UMC 스터디에서 배운 내용을 실제로 많이 적용해 볼 수 있어서 좋았고, 수상까지 하게 되어 저에게는 정말 뜻깊은 프로젝트로 기억될 것 같습니다. 우리 자랑스러운 팀원들 정말 고생 많았습니다! 🎉

### 🍇정명서/루베

프로젝트를 진행하며 UMC 스터디에서 배운 지식들을 실제로 적용해볼 수 있었고, 이를 통해 더 견고하게 익힐 수 있었습니다. 또한 나의 부족한 부분이 무엇인지 알게 되었고, 이를 바탕으로 어떤 것을 추가적으로 공부해야 하는 지 파악하여 더욱 성장할 수 있는 계기가 되었습니다. 깃허브 PR을 통해서 제대로 된 코드 리뷰를 진행한 프로젝트가 거의 처음이었기 때문에 처음엔 낯설었지만, 코드 리뷰 과정에서 내가 작성한 코드를 다시 돌아보고 개선할 수 있었고, 팀원들의 코드를 리뷰하며 서로 배우고 함께 성장할 수 있었습니다. 하루치 프로젝트는 훌륭한 팀원들과 협업하며 많은 것을 얻어갈 수 있었던 값진 경험이었습니다. 끝까지 함께 고생하며 프로젝트 완료까지 달려온 팀원들에게 진심으로 감사의 인사를 전합니다!

### 🐶김은지/리버

커밋 컨벤션부터 이슈와 PR 관리까지 협업의 모든 과정을 제대로 경험한 첫 프로젝트였습니다! 특히  Spring Security를 활용한 사용자 인증/인가 구현을 처음으로 구현해 볼 수 있었고, 팀원들의 도움으로 제대로 해낼 수 있었던 것 같습니다. 시간에 쫓기며 진행된 부분도 있었지만, 그만큼 어떤 부분이 부족한지 알 수 있었던 기회였다고 생각합니다. 함께 고생한 팀원들 모두에게 감사의 인사를 전하고 싶습니다. 앞으로도 이 경험을 바탕으로 더 체계적이고 효율적으로 개발하는, 더 나은 개발자로 성장해 나가고 싶습니다!

### 🍀송민아/앤드

 한 달 반동안 팀 프로젝트를 진행하면서 많은 점들을 느낄 수 있었습니다. 개인적으로는 UMC 스터디를 하면서 배웠던 지식을 기반으로 프로젝트 설계부터 코드 작성까지 전 과정들을 직접 시행착오를 거치면서 구현해 냈다는 점에서 뿌듯하고 즐거웠습니다. 또한 PR을 통해 코드 리뷰를 작성하여 클린한 코드들을 작성할 수 있어서 너무나 좋았습니다. 첫 팀 프로젝트였기 때문에 부족한 점도 많았지만 팀원들 덕분에 성공적으로 마무리할 수 있어서 감사하고, 좋은 기억으로 남을 것 같습니다! 모두들 수고하셨고, 앞으로도 화이팅입니다!!
