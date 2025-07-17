# Credit Card Detector

## Overview

A Spring Boot application that detects credit card numbers in messages.  
Messages are received via HTTP API, scanned using regex, and detection events are stored in MongoDB.

## Endpoints

- **POST /message** – Receives a message and scans for credit card numbers.
- **GET /detections** – Returns aggregated detection counts per sender in a time range.

## Scaling Approach

- **RabbitMQ** is used to decouple HTTP requests from processing.
- Consumers handle message processing asynchronously for higher throughput.
- The system can scale horizontally by adding more consumers.

## Trade-offs

| Choice | Trade-off |
|---------|----------|
| Async processing | Immediate HTTP 200 response, even before DB persistence |
| Regex detection | Simple and fast, but may produce false positives |
| MongoDB (NoSQL) | Flexible schema, but no strict transaction guarantees |

## Run with Docker

```bash
docker-compose up --build
