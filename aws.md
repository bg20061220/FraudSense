# Phase 8: AWS Deployment — FraudSense to the Cloud

## Why We're Doing This

Your app currently runs on `localhost:8080` with local Kafka (Docker) and local DynamoDB. That only works on *your machine*. Phase 8 moves it to **AWS** so it runs **24/7 online** and is reachable from the internet (Stripe can send webhooks to it).

**Cost:** $0 for 12 months (AWS free tier).

---

## Architecture: Local → Cloud

### Before (Local)
```
Your Laptop:
Stripe webhooks → Spring Boot (localhost:8080)
                → Kafka (Docker, :9092)
                → DynamoDB (local)
                → React dashboard (localhost:3000)
```

### After (AWS)
```
Internet:
Stripe webhooks → EC2 instance (public IP:8080)
                → Kafka (running on EC2)
                → DynamoDB (AWS managed)
                → React dashboard (accessed via EC2 IP)
```

**Key difference:** Everything runs on AWS. Stripe can reach you. You don't need to run anything on your laptop.

---

## Step 1: Launch EC2 Instance

### What is EC2?
EC2 = "Elastic Compute Cloud" = a virtual machine on AWS. Think of it as:
- A computer in AWS's data center
- You SSH into it and run commands (Java, Kafka, Maven, etc.)
- It has a public IP so the internet can reach it
- You pay per hour (free tier = 750 hrs/month = 24/7 for 12 months)

### Why t3.micro?
- **Micro** = smallest instance size (1 vCPU, 1 GB RAM)
- **t3** = burstable (can handle spikes, sleeps when idle, cheap)
- **Free tier eligible** = no cost for 750 hours/month
- Plenty for a Spring Boot app + Kafka

### Instance Details We Chose
```
Name: fraudsense-app
Instance Type: t3.micro (free tier)
AMI: Amazon Linux 2 (Linux OS, AWS-optimized, free tier)
Public IP: Auto-assign enabled (so internet can reach it)
Storage: 8 GiB (within free tier's 30 GiB/month)
Key pair: fraudsense-key.pem (SSH authentication)
```

### Security Group: What Traffic Is Allowed?

A **security group** is a firewall for your instance. It controls inbound/outbound traffic.

We created `fraudsense-sg` with:

| Direction | Protocol | Port | Source | Why |
|-----------|----------|------|--------|-----|
| Inbound | SSH | 22 | 0.0.0.0/0 | SSH into the instance from your laptop |
| Inbound | TCP | 8080 | 0.0.0.0/0 | Stripe webhooks reach your app |
| Outbound | All | All | All | Instance can reach Kafka, DynamoDB, internet |

**`0.0.0.0/0`** = "anyone on the internet" (simpler for a learning project; in production you'd restrict this).

---

## Why Confluent Cloud?

**Confluent Cloud** is the recommended option:
- **Managed Kafka** (Confluent handles ops, no infrastructure overhead)
- **Free tier available** (sufficient for learning and testing)
- **Easy integration** with Spring Boot via SASL_SSL configuration
- **No self-hosting burden** (unlike running Kafka on EC2)

This means your app on EC2 will simply connect to Confluent Cloud's hosted Kafka cluster via SASL_SSL credentials.

---

## What's Next?

Once the instance is running, we'll:
1. **SSH into it** (using the .pem file you downloaded)
2. **Install Java 17** (needed to run Spring Boot)
3. **Install Maven** (needed to build the app)
4. **Install Kafka** (needed to process transactions)
5. **Upload your code** (git clone or SCP)
6. **Update application.properties** (point Kafka to localhost:9092 on the instance)
7. **Run `mvn spring-boot:run`** (app starts listening on :8080)
8. **Update Stripe webhook endpoint** (point it to EC2's public IP instead of localhost)
9. **Test:** Trigger a payment, watch the pipeline work

---

## Key Principles

1. **Free tier eligibility:** Always check AWS free tier docs. t3.micro, 30 GiB EBS, DynamoDB read/write limits all included.

2. **Security groups = stateful firewall:** Inbound rules control who can reach you. Outbound rules control where you can reach. Default outbound is "allow all" (we kept it).

3. **Public IP vs. private IP:** 
   - **Public IP** = reachable from internet (needed for Stripe webhooks)
   - **Private IP** = only reachable within AWS VPC (internal only)
   - We auto-assigned a public IP.

4. **Key pairs for SSH:** No password login. You authenticate with a .pem file (like an SSH key). Safer than passwords.

5. **Leverage managed services where possible:** DynamoDB is "managed" (AWS runs it). Kafka we're self-hosting (ops burden). In production, you'd use AWS MSK (managed Kafka) but it costs money.

---

## Files We'll Need

- `fraudsense-key.pem` — SSH private key (keep safe, don't commit to git)
- `application.properties` — Will update this with Kafka broker + AWS DynamoDB region
- Code from your repo — Will SCP or git clone this to EC2

---

## Progress Checklist

- [x] EC2 instance launched (t3.micro, running)
- [ ] Public IPv4 address obtained
- [ ] SSH into instance
- [ ] Install Java, Maven, Kafka
- [ ] Upload code
- [ ] Update application.properties
- [ ] Run Spring Boot app
- [ ] Update Stripe webhook endpoint
- [ ] Test end-to-end
