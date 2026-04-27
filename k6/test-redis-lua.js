import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        contacts: {
            executor: 'per-vu-iterations',
            vus: 3000,
            iterations: 1,
            maxDuration: '120s',
        },
    },
};

export default function () {
    const userId = __VU; 
    const url = `http://localhost:8080/api/v1/gacha/draw-lua?userId=${userId}`;
    const res = http.post(url);

    const body = JSON.parse(res.body);
    
    check(res, {
        'is status 200': (r) => r.status === 200,
        'is success true': (r) => body.success === true,
    });
}
