# 🍗 ChickenGet (치킨 가챠 시스템)

## 📝 프로젝트 개요
`ChickenGet`은 실시간 선착순 치킨 쿠폰 이벤트를 시뮬레이션하는 웹 애플리케이션입니다.

수천 명의 사용자가 동시에 접속하여 제한된 수량의 쿠폰을 뽑는 상황에서 **데이터의 정합성을 보장**하고 **동시성 제어**를 수행하는 데 중점을 두었습니다. `Redis` 기반의 분산 락(`Redisson`)을 활용하여 초과 당첨(Race Condition)을 방지하고 정확한 재고 차감 로직을 구현하였습니다.

## 🛠 구현 내용

### Backend (Spring Boot)
- **가챠 API**: `POST /api/v1/gacha/draw?userId={id}` 엔드포인트 구현
- **동시성 제어**: `Redisson`을 이용한 분산 락(Distributed Lock) 적용으로 중복 당첨 및 재고 오류 방지
- **데이터베이스**: MySQL(영속성) 및 Redis(분산 락 관리) 연동

### Frontend (Next.js)
- **가챠 페이지**: `/gacha` 경로에서 치킨 쿠폰 뽑기 UI 및 상태 관리(IDLE, LOADING, SUCCESS, ERROR) 구현
- **스타일링**: Tailwind CSS를 활용한 반응형 디자인
- **API 연동**: `fetch` API를 사용한 백엔드 서버 통신

---

## 🚀 실행 방법

### 1. 인프라 실행 (Docker)
루트 디렉토리에서 MySQL과 Redis를 실행합니다.
```bash
docker-compose up -d
```

### 2. 백엔드 서버 실행
`backend` 디렉토리에서 실행합니다.
```bash
cd backend
./gradlew bootRun
```
- API 주소: `http://localhost:8080`

### 3. 프론트엔드 서버 실행
`frontend` 디렉토리에서 실행합니다.
```bash
cd frontend
npm install
npm run dev
```
- 접속 주소: `http://localhost:3000` (자동으로 `/gacha`로 이동)
