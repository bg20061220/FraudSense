# 🚨 FraudSense

A real-time fraud detection pipeline that processes Stripe transactions through an event-driven architecture, detects anomalies, and surfaces suspicious activity on an interactive dashboard.

## Overview

FraudSense demonstrates how to build a production-grade, real-time fraud detection system using:
- **Event-driven architecture** with Apache Kafka for decoupled, scalable processing
- **Anomaly detection** through weighted scoring algorithms
- **Real-time caching** with Redis for sub-millisecond pattern detection
- **Cloud-native design** with AWS (EC2, DynamoDB, Confluent Cloud)
- **Interactive dashboard** with React for live fraud alerts

### Architecture

```
Stripe Webhooks
      ↓
Spring Boot API (Port 8080)
      ↓
Kafka (Message Queue)
      ↓
Fraud Scorer
(Kafka Consumer)
      ↓
Redis (Pattern Cache)     DynamoDB (Transaction Log)
      ↓                            ↓
React Dashboard (Port 3000)
```

**Data Flow:**
1. Stripe sends payment webhooks to the Spring Boot API
2. Transactions are published to Kafka topics
3. Consumer services process messages and calculate risk scores using pattern detection
4. Redis caches user velocity patterns and anomalies for fast lookup
5. DynamoDB stores the transaction log and scores
6. React dashboard subscribes to fraud alerts and displays them in real-time

---

## Tech Stack

### Backend
- **Java 17** with Spring Boot 3.x
- **Apache Kafka** (Confluent Cloud for production)
- **Redis** 7.x for in-memory caching
- **AWS DynamoDB** for persistent storage
- **Maven** for dependency management

### Frontend
- **React 19.x** with React Router
- **CSS3** for responsive design
- **Axios** for HTTP requests (optional, for live webhook integration)

### Infrastructure
- **Docker** & **Docker Compose** for local development
- **AWS EC2** for application hosting
- **AWS DynamoDB** for production database

---

## Quick Start

### Local Development with Docker

#### Prerequisites
- Docker & Docker Compose installed
- Java 17+
- Maven 3.8+
- Node.js 18+ (for React frontend)

#### Step 1: Start Infrastructure

```bash
docker-compose up -d
```

This starts:
- **Zookeeper** (Port 2181) — Kafka coordination
- **Kafka** (Port 9092) — Message broker
- **Redis** (Port 6379) — Cache layer
- **DynamoDB Local** (Port 8000) — Local database

Verify they're running:
```bash
docker-compose ps
```

#### Step 2: Build & Run Backend

```bash
mvn clean package
mvn spring-boot:run
```

The Spring Boot API will start on `http://localhost:8080`

**Key Endpoints:**
- `POST /api/transactions` — Receive Stripe webhooks
- `GET /api/transactions` — Fetch transaction history
- `GET /api/health` — Health check

#### Step 3: Start Frontend

```bash
cd client
npm install
npm start
```

The React dashboard opens on `http://localhost:3000`

---

## Configuration

### Backend (application.properties)

```properties
# Kafka Settings
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

# Redis Settings
spring.redis.host=localhost
spring.redis.port=6379

# DynamoDB Settings
aws.dynamodb.endpoint=http://localhost:8000
aws.dynamodb.region=us-east-1
aws.dynamodb.table.transactions=transactions

# Stripe (for webhook verification)
stripe.api.key=${STRIPE_API_KEY}
stripe.webhook.secret=${STRIPE_WEBHOOK_SECRET}
```

### Frontend (environment variables)

```bash
REACT_APP_API_URL=http://localhost:8080
REACT_APP_WS_URL=ws://localhost:8080/ws
```

---

## Fraud Detection Logic

### Scoring Algorithm

Each transaction receives a **risk score (0-100)** based on:

1. **Velocity Check** — How many transactions by this user in the last 5 minutes?
   - Score increases with frequency (e.g., 5+ in 5 min = high risk)

2. **Geolocation Anomaly** — Is the transaction location consistent with user history?
   - Rapid location changes (e.g., US to EU in <1 hour) = suspicious

3. **Amount Spike** — Is the amount unusual for this user?
   - Amount > 3x median of recent transactions = flagged

4. **Merchant Risk** — Is this a high-risk merchant category?
   - Gambling, crypto, liquor merchants flagged

**Thresholds:**
- **≥ 70:** FRAUDULENT (block or require verification)
- **40–69:** SUSPICIOUS (review later)
- **< 40:** CLEAN (accept)

### Example Transaction

```json
{
  "id": "ch_1001",
  "userId": "user_8234",
  "amount": 4500.00,
  "currency": "USD",
  "riskScore": 85,
  "status": "FRAUDULENT",
  "flags": ["Velocity", "Geo Anomaly"],
  "timestamp": "2026-05-14T10:30:45Z"
}
```

---

## Dashboard Features

- **Live Transaction Feed** — Real-time updates of flagged transactions
- **Summary Stats** — Count of fraudulent, suspicious, and clean transactions
- **Risk Score Visualization** — Color-coded risk levels
- **Flag Details** — Why a transaction was flagged (Velocity, Geo Anomaly, etc.)
- **Timestamp & User Info** — Full context for investigation

---

## AWS Deployment

### Prerequisites
- AWS account with free tier eligibility
- EC2 t3.micro instance (free tier)
- Confluent Cloud account (free tier sufficient)
- AWS DynamoDB (managed)

### Deployment Steps

See [aws.md](./aws.md) for detailed instructions.

**Quick Summary:**
1. Launch EC2 instance (t3.micro, Amazon Linux 2)
2. SSH into instance and install Java 17, Maven
3. Clone this repo: `git clone <repo-url>`
4. Update `application.properties` with:
   - Confluent Cloud Kafka broker endpoints
   - AWS DynamoDB region and credentials
   - Stripe webhook secret
5. Run: `mvn clean package && java -jar target/fraudsense-app.jar`
6. Update Stripe webhook endpoint to EC2 public IP

### Cost Estimate (First 12 Months)
- **EC2 t3.micro:** Free (750 hours/month free tier)
- **DynamoDB:** Free (25 GB storage, 25 read/write units)
- **Confluent Cloud Kafka:** Free tier (up to 30 partitions, 5 GB storage)
- **Total:** ~$0 (within free tier limits)

---

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Manual Testing with Kafka

**Publish a test transaction:**
```bash
kafka-console-producer --broker-list localhost:9092 --topic transactions
{"id": "ch_test_001", "userId": "user_123", "amount": 5000, ...}
```

**Consume fraud alerts:**
```bash
kafka-console-consumer --bootstrap-server localhost:9092 --topic fraud-alerts --from-beginning
```

---

## Project Structure

```
FraudSense/
├── src/                          # Java Spring Boot backend
│   ├── main/
│   │   ├── java/
│   │   │   └── com/fraudsense/
│   │   │       ├── api/          # REST controllers
│   │   │       ├── service/      # Business logic (fraud scoring)
│   │   │       ├── event/        # Kafka producers/consumers
│   │   │       ├── model/        # Domain models (Transaction, etc.)
│   │   │       └── config/       # Spring config
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/                 # Unit and integration tests
├── client/                        # React frontend
│   ├── src/
│   │   ├── Dashboard.js          # Main fraud dashboard
│   │   ├── Dashboard.css         # Styling
│   │   └── index.js              # Entry point
│   ├── public/
│   └── package.json
├── docker-compose.yml            # Local dev infrastructure
├── pom.xml                        # Maven dependencies
├── aws.md                         # AWS deployment guide
└── README.md                      # This file
```

---

## Key Learnings

This project was built to bridge specific gaps and demonstrate:

1. **Event-Driven Architecture** — Why Kafka decouples producers from consumers, enabling independent scaling
2. **Real-Time Processing** — Sub-millisecond anomaly detection using Redis caching
3. **Fraud Detection** — Implementing weighted anomaly scoring with multiple signals
4. **Cloud Deployment** — Integrating AWS managed services (EC2, DynamoDB) with self-hosted components
5. **Full-Stack Integration** — Building a complete pipeline from API to dashboard

---

## Monitoring & Observability

### Kafka Topics
- `transactions` — Incoming transaction events
- `fraud-scores` — Calculated risk scores
- `fraud-alerts` — High-risk transactions (≥70 score)
- `dlq` — Dead letter queue for failed processing

### Metrics to Track
- **Throughput:** Transactions processed per second
- **Latency:** Time from transaction receipt to fraud score (target: <100ms)
- **False Positive Rate:** % of legitimate transactions flagged as fraudulent
- **Detection Rate:** % of actual fraud caught

### Logs
```bash
# Spring Boot logs
tail -f logs/fraudsense.log

# Kafka broker logs
docker logs <kafka-container-id>

# Redis commands
redis-cli
> KEYS *
> GET user:8234:velocity
```

---

## Troubleshooting

### Kafka Connection Issues
```bash
# Test Kafka connectivity
kafka-broker-api-versions --bootstrap-server localhost:9092
```

### DynamoDB Not Found
```bash
# Ensure DynamoDB Local is running
docker-compose logs dynamodb-local
```

### Dashboard Not Updating
- Check browser console for WebSocket errors
- Verify Kafka consumer group lag: `kafka-consumer-groups --bootstrap-server localhost:9092 --group fraudsense-consumer --describe`

---

## Contributing

Pull requests welcome! Areas for improvement:
- [ ] Implement machine learning model for fraud scoring (replace rules)
- [ ] Add alerting (Slack, email) for high-risk transactions
- [ ] Build admin dashboard for manual review queue
- [ ] Add transaction history export (CSV/JSON)
- [ ] Implement transaction webhook callback to Stripe

---

## License

MIT License — See LICENSE file for details.

---

## Contact

Built by [Your Name] as a learning project demonstrating real-time fraud detection and event-driven systems.

Questions? Check [questions.md](./questions.md) for common Q&A and conceptual deep-dives.
