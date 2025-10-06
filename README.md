# 🏋️‍♀️ 운동 예약 및 커뮤니티 플랫폼

---

## 📑 목차
- [개요](#-개요)
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [와이어프레임](#-와이어프레임)
- [ERD](#-erd)
- [API 명세서](#-api-명세서)
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
- Kakao, Google, Naver 등 **OAuth2 기반 소셜 로그인**
- JWT 기반 **Access / Refresh Token 인증 구조**
- 사용자 권한(회원 / 관리자) 분리

### 🗓️ 예약 시스템
- 실시간 수업 예약 및 정원 확인
- **비관적 락(Pessimistic Lock)** 적용으로 중복 예약 방지
- 예약 취소 및 대기자 관리 기능

### 🧍‍♀️ 마이페이지
- 내 예약 현황, 작성한 게시글 및 댓글 관리
- 알림 설정 및 개인정보 수정 가능

### 💬 커뮤니티 게시판
- 자유 게시판, 공지사항, 운동 팁 등 카테고리별 게시판
- 좋아요, 댓글, 대댓글 기능
- 관리자 공지 작성 및 관리

### ⚡ 실시간 알림
- 예약 확정, 취소, 댓글 등 주요 이벤트에 대해 **WebSocket 기반 실시간 알림** 제공

### 🧾 결제 기능 (확장 예정)
- 클래스 결제 및 포인트 시스템
- 결제 내역 조회 및 환불 프로세스

---

## 🛠 기술 스택

| 구분 | 기술 |
|:--|:--|
| **Frontend** | React, TypeScript, Tailwind CSS, Vite |
| **Backend** | Spring Boot, Spring Security, JPA (Hibernate) |
| **Database** | MySQL, Redis (세션 및 캐싱) |
| **CI/CD** | GitHub Actions, AWS EC2, Docker, Nginx |
| **Authentication** | OAuth2, JWT |
| **Communication** | REST API, WebSocket |
| **ETC** | Gradle, Lombok, Postman, Swagger |

---

## 📐 와이어프레임
> 전체 서비스 플로우 및 화면 구성 예시

<img width="1118" height="846" alt="와이어프레임" src="https://github.com/user-attachments/assets/e57eed50-a48b-4c7b-a2ce-01a15a7235c0" />

---

## 🧩 ERD
> 주요 테이블 간 관계도

<img width="900" height="550" alt="ERD" src="https://github.com/user-attachments/assets/1abcde00-0000-aaaa-1111-222222222222" />

---

## 📑 API 명세서
👉 [포스트맨 문서 바로가기](https://documenter.getpostman.com/view/00000000/2sB34cpNFk)

---

## 🚀 CI/CD 파이프라인
> 자동 빌드 및 배포 환경 구성

<img width="635" height="312" alt="CI/CD" src="https://github.com/user-attachments/assets/83b0cdc1-6a47-4296-aef7-55c8cb45a1a7" />

- GitHub Actions로 자동 빌드
- Docker 이미지 생성 후 EC2 배포
- Nginx Reverse Proxy로 무중단 서비스 지원

---

## 🏗 시스템 아키텍처
> 클라우드 기반 모듈 구조

<img width="829" height="479" alt="시스템 아키텍처" src="https://github.com/user-attachments/assets/4a6bf3c7-b806-4d4d-ad5a-6318695b7848" />

---

## 💬 향후 개선 방향
- AI 기반 운동 추천 알고리즘 추가
- 그룹 클래스 및 코치 평가 시스템 도입
- 실시간 화상 트레이닝 기능 (WebRTC)
