# 🏋️‍♀️ 운동 예약 및 커뮤니티 플랫폼

---

## 📑 목차
- [개요](#-개요)
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [와이어프레임](#-와이어프레임)
- [CI/CD](#-cicd)
- [시스템 아키텍처](#-시스템-아키텍처)

---

## 🧠 개요
> 운동 시설 예약과 회원 간 소통을 통합한 **운동 커뮤니티 웹 애플리케이션**입니다.  
> 사용자는 원하는 수업을 간편하게 예약하고, 커뮤니티 게시판에서 운동 정보를 공유할 수 있습니다.  
> 관리자는 예약 현황을 직관적으로 확인하고 효율적인 시설 운영이 가능합니다.

---

## 🩻 주요 기능

### 🔐 사용자 인증
- JWT 기반 **Access / Refresh Token 인증 구조**
- Security를 사용해 사용자 권한(회원 / 관리자) 분리

### 프로그램
- 날짜별 운동 프로그램에 생성 및 관리

### 🗓️ 예약 시스템
- 관리자가 수업을 생성 후 실시간 수업 예약 및 정원 확인
- **비관적 쓰기 락(Pessimistic Lock)** 적용으로 중복 예약 방지
- 인덱스를 사용해 조회 성능 개선 

### 🧍‍♀️ 마이페이지
- 개인정보 조회,수정 관리

### 💬공지사항 게시판 및 댓글
- 공지사항 및 공지사항에 대한 댓글
- 관리자 공지 작성 및 관리

### 🧾 결제 기능 (추가할 예정)
- 어플리케이션을 구독하기 위해 결제 기능 추가
- 결제 내역 조회 및 환불 프로세스

---

## 🛠 기술 스택

| 구분           | 기술                                                                          |
|:-------------|:----------------------------------------------------------------------------|
| **Frontend** | Thyemleaf,Tailwind CSS                                                      |
| **Backend**  | JAVA21, Spring Boot, Spring Security, JPA ,JWT,GitHub Actions,Docker, Nginx |
| **DB**       | MySQL, Redis                                                                |


---

## 📐 와이어프레임
> 전체 서비스 플로우 및 화면 구성 예시
<img width="1499" height="1150" alt="와이어 프레임" src="https://github.com/user-attachments/assets/9e1cf8f7-4a0b-4019-9c8a-ea953db5fef8" />

---

## 🚀 CI/CD 파이프라인
> 자동 빌드 및 배포 환경 구성

<img width="635" height="312" alt="CI/CD" src="https://github.com/user-attachments/assets/83b0cdc1-6a47-4296-aef7-55c8cb45a1a7" />

- GitHub Actions로 자동 빌드 ,배포
- Nginx Reverse Proxy로 무중단 서비스 지원

---

## 🏗 시스템 아키텍처

<img width="2504" height="1527" alt="시스템 아키텍처" src="https://github.com/user-attachments/assets/ddc9dc66-a1cb-466b-a89f-3dfb49a87672" />


---

## 💬 향후 개선 방향
- 
- 
-
