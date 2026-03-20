#!/usr/bin/env python3
import json
import time
import urllib.request

BASE = "http://127.0.0.1:8080"

def post(path, payload):
    data = json.dumps(payload).encode("utf-8")
    req = urllib.request.Request(BASE + path, data=data, headers={"Content-Type": "application/json"}, method="POST")
    t0 = time.perf_counter()
    with urllib.request.urlopen(req, timeout=10) as r:
        _ = r.read()
    return (time.perf_counter() - t0) * 1000


def main():
    samples = []
    for _ in range(30):
        samples.append(post("/api/public/ask", {"question": "社保怎么转到本地", "topN": 3}))
    samples.sort()
    p50 = samples[len(samples)//2]
    p95 = samples[int(len(samples)*0.95)-1]
    print(f"ask_count={len(samples)} p50_ms={p50:.2f} p95_ms={p95:.2f}")


if __name__ == "__main__":
    main()
