# 포인트 토이 프로젝트

---

## 정의한 요구사항

---

### 개발 필수요건
- API에서 회원정보는 '회원번호(회원에게 부여된 유니크한 번호)' 이외의 다른 정보는 전달받지 않음
- 각 API 에서 ‘회원번호’ 이외에 request, response는 자유롭게 구성
- 회원 별 적립금 합계는 마이너스가 될 수 없음
- 개발해야 할 API는 다음과 같습니다.
  - 회원별 적립금 합계 조회
  - 회원별 적립금 적립/사용 내역 조회
    - 페이징 처리
  - 회원별 적립금 적립
  - 회원별 적립금 사용
    - 적립금 사용시 우선순위는 먼저 적립된 순서로 사용(FIFO)
- ORM 사용 (ex: JPA / typeorm 등) 

### 개발 추가요건
- 적립금의 유효기간 구현 (1년)
- 회원별 적립금 사용취소 API 개발
  - 적립금 사용 API 호출하는 쪽에서 Rollback 처리를 위한 용도
- 트래픽이 많고, 저장되어 있는 데이터가 많음을 염두에 둔 개발
- 동시성 이슈가 발생할 수 있는 부분을 염두에 둔 개발

---
### 사용기술
- SpringBoot 3.0, JDK 17
- JPA, QueryDsl
- CaffeineCache
- H2 DB
- Gradle

--- 
### 실행
 - ./gradlew build
 - java -jar build/libs/voucher_point-0.0.1.jar

---

### API

---
#### 회원 API

- 회원가입
  - Api Uri : http://localhost:1017/api/v1/members/sign-up
  - ##### Parameters

    Name | Type       | Description | Notes
    ------------- |------------|-------------| -------------
     **memberName** | **String** | 멤버이름    | 옵
    ```json
    request :
    curl --location --request POST 'http://localhost:1017/api/v1/members/sign-up' \
    --header 'Content-Type: application/json' \
    --header 'Cookie: JSESSIONID=536B6ABB74D28AEF68234547127648F4' \
    --data-raw '{
        "memberName" : "포인트가진사람1"
    }'
    
    response : "성공"
    ```
- 회원 목록 조회
  - Api Uri : http://localhost:1017/api/v1/members

    ```json
    request :
    curl --location --request GET 'http://localhost:1017/api/v1/members' \
    --header 'Content-Type: application/json' \
    --header 'Cookie: JSESSIONID=536B6ABB74D28AEF68234547127648F4' \
    --data-raw '{
        "memberName" : "포인트매니아"
    }'
    response : 
    [
        {
            "memberId": 1,
            "memberName": "포인트가진사람1"
        }
    ]
    ```
---
#### 포인트 API
- 회원별 적립금 적립/사용
  - Api Uri : http://localhost:1017/api/v1/point/:memberId/transaction
   - ##### Parameters
      Name | Type                                               | Description | Notes
      ------------- |----------------------------------------------------|-------------| -------------
      **memberId** | **Long**                 | 멤버아이디       | 필수
      **pointTransactionType** | [**PointTransactionType**](PointTransactionType.md) | 포인트 거래 유형   | 필수
      **pointAmount** | **Bigdecimal**                                     | 거래 포인트(양수)  | 필수

  - ##### Request/Response
    ```json
    request :
    curl --location --request POST 'http://localhost:1017/api/v1/point/1/transaction' \
    --header 'Content-Type: application/json' \
    --header 'Cookie: JSESSIONID=536B6ABB74D28AEF68234547127648F4' \
    --data-raw '{
        "pointTransactionType" : "EARN_POINT",
        "pointAmount" : "341"
    }'
    
    response : 
    {
        "result": null,
        "code": "POINT_S_001",
        "status": 201,
        "resultMsg": "포인트 적립이 성공하였습니다."
    }
    ```
    
- 회원별 적립금 합계 조회
  - Api Uri : http://localhost:1017/api/v1/point/:memberId/balance
  - ##### Parameters
    Name | Type                                                | Description | Notes
    ------------- |-----------------------------------------------------|-------------| -------------
    **memberId** | **Long**                                            | 멤버아이디    | 필수
  - ##### Request/Response
    ```json
    request :
    curl --location --request GET 'http://localhost:1017/api/v1/point/1/balance' \
    --header 'Cookie: JSESSIONID=536B6ABB74D28AEF68234547127648F4' \
    --data-raw ''
    
    response :
    {
    "result": {
    "memberId": 1,
    "pointBalanceTotal": 1023.00
    },
    "code": "POINT_S_005",
    "status": 200,
    "resultMsg": "포인트 잔액 조회가 성공하였습니다."
    }
    ```
    
- 회원별 적립금 적립/사용/사용취소 조회
  - Api Uri : http://localhost:1017/api/v1/point/:memberId/transaction?direction=DESC&size=50&page=1
  - ##### Parameters
    Name | Type          | Description             | Notes
    ------------- |---------------|-------------------------| -------------
    **memberId** | **Long**      | 멤버아이디                   | 필수
    **direction** | **Direction** | DESC, ASC               | 필수
    **size** | **Integer**   | 한 페이지의 목록 갯수 MAX:50, 양수 | 필수
    **page** | **Integer**      | 페이지 번호, 양수                  | 필수
  - ##### Request/Response
      - ###### Response Type
        Name | Type                                                | Description | Notes
        ------------- |-----------------------------------------------------|-------------| -------------
        **pointTransactionType** | [**PointTransactionType**](PointTransactionType.md) | 포인트 거래 유형   | 
        **pointStatusType** | [**PointStatusType**](PointStatusType.md)           | 포인트 거래 후 상태 | 
    
  ```json
    
  response :
  {
  "result": {
  "content": [
  {
  "pointLedgerId": 3,
  "memberId": 1,
  "pointTransactionType": "REDEEM_POINT",
  "pointStatusType": "REDEEMED",
  "pointAmount": 150.00,
  "remainPointAmount": 0.00,
  "resultBalanceTotal": 50.00,
  "pointExpireDate": null,
  "createdDate": "2023-01-06 11:31:39",
  "modifiedDate": "2023-01-06 11:31:39"
  },
  {
  "pointLedgerId": 2,
  "memberId": 1,
  "pointTransactionType": "EARN_POINT",
  "pointStatusType": "AVAILABLE",
  "pointAmount": 100.00,
  "remainPointAmount": 50.00,
  "resultBalanceTotal": 200.00,
  "pointExpireDate": "2024-01-06 11:31:23",
  "createdDate": "2023-01-06 11:31:23",
  "modifiedDate": "2023-01-06 11:31:39"
  },
  {
  "pointLedgerId": 1,
  "memberId": 1,
  "pointTransactionType": "EARN_POINT",
  "pointStatusType": "USED",
  "pointAmount": 100.00,
  "remainPointAmount": 0.00,
  "resultBalanceTotal": 100.00,
  "pointExpireDate": "2024-01-06 11:31:19",
  "createdDate": "2023-01-06 11:31:19",
  "modifiedDate": "2023-01-06 11:31:39"
  }
  ],
  "pageable": {
  "sort": {
  "empty": true,
  "unsorted": true,
  "sorted": false
  },
  "offset": 0,
  "pageNumber": 0,
  "pageSize": 50,
  "paged": true,
  "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 3,
  "last": true,
  "size": 50,
  "number": 0,
  "sort": {
  "empty": true,
  "unsorted": true,
  "sorted": false
  },
  "numberOfElements": 3,
  "first": true,
  "empty": false
  },
  "code": "POINT_S_005",
  "status": 200,
  "resultMsg": "포인트 적립/사용 내역 조회가 성공하였습니다."
  }
  ```

- 회원별 적립금 사용 취소
  - Api Uri :  http://localhost:1017/api/v1/point/:memberId/transaction/:pointLedgerId
  - ##### Parameters
    Name | Type                                        | Description | Notes
    ------------- |---------------------------------------------|-------------| -------------
    **memberId** | **Long** | 멤버아이디       | 필수
    **pointLedgerId** | **Long** | 포인트 거래 아이디  | 필수

  - ##### Request/Response
    ```json
    request :
    curl --location --request DELETE 'http://localhost:1017/api/v1/point/1/transaction/7' \
    --header 'Cookie: JSESSIONID=536B6ABB74D28AEF68234547127648F4' \
    --data-raw ''
    
    response :
    {
    "result": null,
    "code": "POINT_S_003",
    "status": 201,
    "resultMsg": "포인트 사용 취소가 성공하였습니다."
    }
    
    ```


