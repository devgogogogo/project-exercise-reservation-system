# 🏋️‍♀️ 운동 예약 및 커뮤니티 플랫폼
---
## 📌 프로젝트 소개
> 운동 시설 예약과 회원 간 소통을 통합한 **운동 커뮤니티 웹 애플리케이션**입니다.  
> 사용자는 원하는 수업을 간편하게 예약하고, 커뮤니티 게시판에서 운동 정보를 공유할 수 있습니다.  
> 관리자는 예약 현황을 직관적으로 확인하고 효율적인 시설 운영이 가능합니다.


## 📌 목차

### 프로젝트 개요
- [① 개발 기간](#-개발-기간)
- [② 기술 스택](#-기술-스택)

### UI & 기능 소개
- [③ 와이어프레임](#-와이어프레임)
- [④ 주요 기능](#-주요-기능)

### 기타
- [⑤ 트러블슈팅](#-트러블슈팅)
- [⑥ CI/CD](#-cicd)
- [⑦ 시스템 아키텍처](#-시스템-아키텍처)

## 📅 개발 기간
2025.09 ~ 2026.11


## 🛠 기술 스택

| 구분 | 기술 |
|:---|:---|
| **Frontend** | Thyemleaf,Tailwind CSS   |
| **Backend** | Java 21, Spring Boot, Spring Data JPA ,Spring Security, JWT, OAuth2 Client,Docker, Docker Compose |
| **DB** | MySQL, Redis |
| **CI/CD** | GitHub Actions, GHCR, AWS EC2 |
| **부하 테스트** | k6 |

## 📐 와이어프레임
<img width="1499" height="1150" alt="와이어 프레임" src="https://github.com/user-attachments/assets/9e1cf8f7-4a0b-4019-9c8a-ea953db5fef8" />

## 🗂 ERD

<img width="1941" height="690" alt="erd" src="https://github.com/user-attachments/assets/edb657b7-3e2c-4e78-8b20-ebb620661e80" />

## 🚀 CI/CD 파이프라인
> 자동 빌드 및 배포 환경 구성

<img width="635" height="312" alt="CI/CD" src="https://github.com/user-attachments/assets/83b0cdc1-6a47-4296-aef7-55c8cb45a1a7" />

- GitHub Actions로 자동 빌드 ,배포
- Nginx Reverse Proxy로 무중단 서비스 지원

## 🏗 시스템 아키텍처

<img width="2504" height="1527" alt="시스템 아키텍처" src="https://github.com/user-attachments/assets/ddc9dc66-a1cb-466b-a89f-3dfb49a87672" />

## 🩻 주요 기능
- Spring Security + JWT 기반 인증 및 OAuth2 소셜 로그인 구현
- Redis를 활용한 토큰 관리
- Domain별 계층형 패키지 구조 (Controller - Service - Repository)
- Testcontainers를 이용한 통합 테스트 환경 구성

### 1. 회원 관리
- 회원가입 / 로그인 / 프로필 수정 기능 구현
- Security를 사용해 사용자 권한(회원 / 관리자) 분리
- JWT AccessToken 발급 및 Redis 기반 토큰 관리
- OAuth2 소셜 로그인 지원

### 2. 운동 프로그램 관리
- 날짜별 운동 프로그램 CRUD (생성 / 수정 / 삭제 / 날짜별 조회)
- 관리자 권한 기반 프로그램 등록 및 수정

### 3. 수업 일정 및 예약
- 관리자가 수업을 생성 후 실시간 수업 예약 및 정원 확인
- **비관적 쓰기 락(Pessimistic Lock)** 적용으로 중복 예약 방지
- 예약 목록 조회할 수 있고 인덱스를 사용해 조회 성능 개선 

### 🧍‍♀️ 마이페이지
- 개인정보 조회,수정 관리

### 💬공지사항 게시판 및 댓글
- 공지사항 CRUD 구현
- 관리자 권한으로 공지 작성 및 관리
- 게시글 댓글 작성 / 수정 / 삭제 기능 구현

## 💡 트러블슈팅

> (내용 추가 예정)




