import http from 'k6/http'
import {check,sleep} from 'k6'

export let options = {
    stages: [
        { duration: '1s', target: 10},
        { duration: '3s', target: 50},
        { duration: '3s', target: 100},
        { duration: '3s', target: 250},
        { duration: '3s', target: 500},
        { duration: '3s', target: 1000},
        { duration: '6s', target: 2000},
    ],
    thresholds: {
        http_req_failed: ['rate<0.01'], // http errors should be less than 1%
        http_req_duration: ['p(100)<500'], // 100% of requests should be below 500ms
    },
}
export default function (){
    let response = http.get('http://localhost:8080/stream/song');

    check(response,{
        'is status 200': (r) => r.status === 200,
        'has correct content type': (r) => r.headers['Content-Type'] === 'audio/mpeg',
        'response has data': (r) => r.body.length > 0,
    });

    sleep(1);
}