import http from 'k6/http';
import { check, sleep, group } from 'k6';

// =======================
// 환경 설정
// =======================
const BASE_URL = 'http://app:8080'; // ✅ localhost → app (docker compose 내부 통신)
const SCHEDULE_ID = 1; // Test Class A (고정)

// ⚠️ 테스트용 USER JWT 토큰 (환경변수로 주입)
const TOKEN = __ENV.JWT_TOKEN;
if (!TOKEN) {
    throw new Error('JWT_TOKEN env is missing'); // ✅ 토큰 없으면 즉시 실패
}

// =======================
// k6 옵션
// =======================
export const options = {
    vus: 100,
    duration: '30s',
    thresholds: {
        http_req_duration: ['p(95)<1000'],
        http_req_failed: ['rate<0.1'],
    },
};

// =======================
// 메인 테스트
// =======================
export default function () {
    group('Class Schedule Reservation', () => {
        const url = `${BASE_URL}/api/class-schedules/${SCHEDULE_ID}/reservation`;

        const params = {
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${TOKEN}`,
            },
        };

        const payload = JSON.stringify({}); // ✅ null 대신 빈 JSON (415/400 방지용)
        const res = http.post(url, payload, params);

        check(res, {
            'status is 200 or 409': (r) => r.status === 200 || r.status === 409,
        });

        sleep(1);
    });
}

