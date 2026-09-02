# Redis Clone (Java)

A simplified in-memory key-value store implemented in Java, covering the core mechanics of Redis: a TCP command protocol, concurrent client handling, LRU eviction, and key expiration. Benchmarked against production Redis to measure how a from-scratch implementation compares to optimized, production-grade software.

## Features

- TCP server handling multiple concurrent clients, one thread per connection
- Commands: `SET`, `GET`, `DELETE`, `EXPIRE`
- LRU eviction via a fixed-capacity `LinkedHashMap` in access-order mode
- Lazy expiration: expired keys are checked and removed on access rather than through a background sweep

## Benchmark: vs. Production Redis

100,000 sequential requests per command, averaged across five runs, compared against `redis-benchmark` on the same machine.

| | This project | Redis (`redis-benchmark`) | % of Redis |
|---|---|---|---|
| SET | ~50,877 ops/sec | 217,391 ops/sec | ~23.4% |
| GET | ~54,570 ops/sec | 224,719 ops/sec | ~24.3% |

The first run in each session consistently measured lower (~46K ops/sec on SET), consistent with JVM warmup: the JIT compiler has not yet optimized the hot loop on a cold start. That run was excluded from the steady-state averages above.

Redis is written in C with a highly optimized event loop and years of low-level tuning. Reaching roughly a quarter of its throughput from an unoptimized Java implementation is a reasonable result for a from-scratch build, not a shortfall to obscure.

## Limitations and next steps

- LRU eviction currently relies on Java's built-in `LinkedHashMap`. A hand-rolled hash table and doubly linked list implementation would demonstrate the underlying mechanism directly rather than delegating to a standard library class.
- No persistence; state is lost on restart.
- Does not implement the RESP wire protocol, so standard Redis client libraries cannot connect to it.

## Usage

Start the server:
```
java EchoServer
```

Connect with netcat:
```
nc localhost 6379
SET name rohan
GET name
```

Run `Benchmark.java` against a running instance to reproduce the throughput results above.