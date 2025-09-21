import http from 'k6/http';
import { check, sleep } from 'k6';

const minioUrl = 'http://localhost:9001/api/v1/download-shared-object/aHR0cDovLzEyNy4wLjAuMTo5MDAwL211c2ljL3NvbmcubXAzP1gtQW16LUFsZ29yaXRobT1BV1M0LUhNQUMtU0hBMjU2JlgtQW16LUNyZWRlbnRpYWw9TTdXSlBHRVowUDlaMTczUjFJMTYlMkYyMDI1MDkxOSUyRnVzLWVhc3QtMSUyRnMzJTJGYXdzNF9yZXF1ZXN0JlgtQW16LURhdGU9MjAyNTA5MTlUMTAxMDU5WiZYLUFtei1FeHBpcmVzPTQzMjAwJlgtQW16LVNlY3VyaXR5LVRva2VuPWV5SmhiR2NpT2lKSVV6VXhNaUlzSW5SNWNDSTZJa3BYVkNKOS5leUpoWTJObGMzTkxaWGtpT2lKTk4xZEtVRWRGV2pCUU9Wb3hOek5TTVVreE5pSXNJbVY0Y0NJNk1UYzFPRE14T1RjMU1Dd2ljR0Z5Wlc1MElqb2ljM0J2ZEdsbWVTMWhaRzFwYmlKOS5SaFVyOWNyaXBKWnJDcEMzRWFJYW1SY2JGNkVwZHcwTU9GdC1sUVdfckVMaG0xRXFIOG5HRzJDYTZ5ZVQxVDdpMF9pMVRiYnMxUkxKaGxuWG0yWEw4dyZYLUFtei1TaWduZWRIZWFkZXJzPWhvc3QmdmVyc2lvbklkPW51bGwmWC1BbXotU2lnbmF0dXJlPWNiYzY5Y2VhZTUxYjViZTU5NThiYWFlYmM5NjQ5MjA0NWI3ZjQ2ZGQ3NmY4NmRlYjgzMTZhNmM3MDU1YjUzZjU';

export const options = {
    discardResponseBodies: {
        threshold: '0.1s',
        body: true,
    },
    scenarios: {
        find_my_bottleneck: {
            executor: 'ramping-arrival-rate',
            preAllocatedVUs: 200,
            maxVUs: 20000,

            timeUnit: '1s',
            startRate: 1,

            stages: [
                { target: 10, duration: '10s' },
                { target: 50, duration: '10s' },
                { target: 100, duration: '10s' },
                { target: 300, duration: '20s' },
                { target: 500, duration: '20s' },
                { target: 750, duration: '20s' },
                { target: 1000, duration: '30s' },
            ],
        },
    },
    thresholds: {
        http_req_failed: [{ threshold: 'rate<0.01', abortOnFail: false }],
        http_req_duration: [{threshold: 'p(100)<500', abortOnFail: false}],
    },
};

export default function () {
    // A simple GET request to download the object
    let res = http.get(minioUrl);

    // Track whether this request was successful and fast enough
    check(res, {
        'is status 200 or 206': (r) => r.status === 200 || r.status === 206,
    });

    // Optional: Add a very short sleep to be slightly more realistic.
    // sleep(0.1);
}
