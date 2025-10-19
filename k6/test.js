// k6는 부하 테스트용 도구야. http 모듈로 API를 호출하고, check/sleep으로 결과 검증과 대기 처리를 해.
import http from 'k6/http';
import { check, sleep } from 'k6';

// ===================== 🌐 환경 설정 =====================
// BASE_URL : 테스트할 서버 주소 (기본값은 host.docker.internal:8080 → Docker 안에서 내 PC로 접근 가능하게 함)
// NOTICE_ID : 테스트할 게시글 ID (예: /api/test/redis/1 → 1번 게시글 조회)
const BASE_URL  = __ENV.BASE_URL  || 'http://host.docker.internal:8080';
const NOTICE_ID = __ENV.NOTICE_ID || '1';

// ===================== ⚙️ 부하 테스트 옵션 설정 =====================
// stages : 가상 사용자(VU, Virtual User)를 점점 늘렸다가 줄이는 시나리오
// thresholds : 응답 속도와 실패율 기준(성능 통과 조건)
export const options = {
    stages: [
        { duration: '10s', target: 10 },   // 10초 동안 사용자 10명으로 증가 (웜업)
        { duration: '30s', target: 100 },  // 30초 동안 100명까지 부하 상승
        { duration: '30s', target: 100 },  // 30초 동안 100명 유지 (본격 부하)
        { duration: '10s', target: 0 },    // 10초 동안 부하 감소
    ],
    thresholds: {
        // 실패율이 1% 미만이어야 함
        'http_req_failed': ['rate<0.01'],

        // 각 조회 방식별 p95(95% 요청이 이 시간 이내로 끝나야 함)
        'http_req_duration{type:general}':     ['p(95)<80'],  // 일반 조회
        'http_req_duration{type:entitygraph}': ['p(95)<70'],  // EntityGraph 조회
        'http_req_duration{type:query}':       ['p(95)<70'],  // JPQL fetch join 조회
        'http_req_duration{type:redis}':       ['p(95)<15'],  // Redis 캐시 조회
    },
};

// ===================== 🔥 캐시 워밍 (Redis 미리 채워두기) =====================
// 테스트 전에 Redis 캐시를 미리 만들어두는 과정 (첫 호출은 DB에서 불러오니까 느림)
// setup()은 테스트 시작 전에 한 번만 실행됨
export function setup() {
    console.log('🔥 캐시 워밍 중...');
    const warm = http.get(`${BASE_URL}/api/test/redis/${NOTICE_ID}`, {
        tags: { type: 'warm' }, // 태그 붙여서 결과 구분 가능
    });
    check(warm, { 'warm 200': (r) => r.status === 200 }); // HTTP 200인지 확인
    sleep(1);
    console.log('✅ 캐시 워밍 완료');
}

// ===================== 🚀 본격적인 부하 테스트 =====================
// default() 함수 안이 실제 테스트 시 반복적으로 실행됨 (각 가상 사용자마다)
export default function () {
    // 1️⃣ 일반 JPA 조회 (N+1 문제 있음)
    const r1 = http.get(`${BASE_URL}/api/test/General/${NOTICE_ID}`, {
        tags: { type: 'general' }, // 이 요청은 일반 조회로 표시됨
    });
    check(r1, { 'general 200': (r) => r.status === 200 }); // 정상 응답인지 체크

    // 2️⃣ @EntityGraph 조회 (N+1 문제 해결된 버전)
    const r2 = http.get(`${BASE_URL}/api/test/EntityGraph/${NOTICE_ID}`, {
        tags: { type: 'entitygraph' },
    });
    check(r2, { 'entitygraph 200': (r) => r.status === 200 });

    // 3️⃣ @Query(fetch join) 조회 (다른 방식으로 N+1 해결)
    const r3 = http.get(`${BASE_URL}/api/test/Query/${NOTICE_ID}`, {
        tags: { type: 'query' },
    });
    check(r3, { 'query 200': (r) => r.status === 200 });

    // 4️⃣ Redis 캐시 조회 (가장 빠른 방식)
    const r4 = http.get(`${BASE_URL}/api/test/redis/${NOTICE_ID}`, {
        tags: { type: 'redis' },
    });
    check(r4, { 'redis 200': (r) => r.status === 200 });

    // 요청 간 짧은 대기 (너무 동시에 때리면 서버가 죽으니까)
    sleep(0.05);
}
