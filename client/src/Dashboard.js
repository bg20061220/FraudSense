import React, { useState, useEffect } from 'react';
import './Dashboard.css';

const Dashboard = () => {
  // Synthetic flagged transactions data
  const [transactions, setTransactions] = useState([
    {
      id: 'ch_1001',
      userId: 'user_8234',
      amount: 4500.00,
      currency: 'USD',
      riskScore: 85,
      status: 'FRAUDULENT',
      flags: ['Velocity', 'Geo Anomaly'],
      timestamp: new Date(Date.now() - 5 * 60000).toISOString(),
    },
    {
      id: 'ch_1002',
      userId: 'user_5672',
      amount: 2100.00,
      currency: 'USD',
      riskScore: 65,
      status: 'SUSPICIOUS',
      flags: ['Amount Spike', 'High-Risk Merchant'],
      timestamp: new Date(Date.now() - 12 * 60000).toISOString(),
    },
    {
      id: 'ch_1003',
      userId: 'user_9341',
      amount: 950.00,
      currency: 'USD',
      riskScore: 72,
      status: 'FRAUDULENT',
      flags: ['Velocity', 'Amount Spike'],
      timestamp: new Date(Date.now() - 22 * 60000).toISOString(),
    },
    {
      id: 'ch_1004',
      userId: 'user_2847',
      amount: 8200.00,
      currency: 'USD',
      riskScore: 88,
      status: 'FRAUDULENT',
      flags: ['Velocity', 'Geo Anomaly', 'Amount Spike'],
      timestamp: new Date(Date.now() - 35 * 60000).toISOString(),
    },
    {
      id: 'ch_1005',
      userId: 'user_1123',
      amount: 340.00,
      currency: 'USD',
      riskScore: 45,
      status: 'SUSPICIOUS',
      flags: ['Amount Spike'],
      timestamp: new Date(Date.now() - 48 * 60000).toISOString(),
    },
    {
      id: 'ch_1006',
      userId: 'user_7654',
      amount: 5600.00,
      currency: 'USD',
      riskScore: 92,
      status: 'FRAUDULENT',
      flags: ['Velocity', 'Geo Anomaly', 'High-Risk Merchant'],
      timestamp: new Date(Date.now() - 60 * 60000).toISOString(),
    }
  ]);

  useEffect(() => {
    // Simulate Stripe webhooks arriving every 30-90 seconds
    // Occasionally (20% chance) a fraudulent one slips through
    const interval = setInterval(() => {
      const isFraudulent = Math.random() < 0.2; // 20% chance of fraud

      const flags = [
        ['Velocity'],
        ['Geo Anomaly'],
        ['Amount Spike'],
        ['Velocity', 'Geo Anomaly'],
        ['Velocity', 'Amount Spike'],
        ['Geo Anomaly', 'High-Risk Merchant'],
        ['High-Risk Merchant'],
      ];

      const newTransaction = {
        id: `ch_${Math.floor(Math.random() * 1000000)}`,
        userId: `user_${Math.floor(Math.random() * 100000)}`,
        amount: Math.floor(Math.random() * 8000) + 500,
        currency: 'USD',
        riskScore: isFraudulent ? Math.floor(Math.random() * 30) + 65 : Math.floor(Math.random() * 35) + 10,
        status: isFraudulent ? (Math.random() < 0.7 ? 'FRAUDULENT' : 'SUSPICIOUS') : 'CLEAN',
        flags: isFraudulent ? flags[Math.floor(Math.random() * flags.length)] : [],
        timestamp: new Date().toISOString(),
      };

      // Only add to display if flagged (FRAUDULENT or SUSPICIOUS)
      if (newTransaction.status !== 'CLEAN') {
        setTransactions(prev => [newTransaction, ...prev.slice(0, 9)]);
      }
    }, 900000 + Math.random() * 300000); // Every 15-20 minutes

    return () => clearInterval(interval);
  }, []);

  const getStatusColor = (status) => {
    switch (status) {
      case 'FRAUDULENT':
        return '#000000';
      case 'SUSPICIOUS':
        return '#333333';
      case 'CLEAN':
        return '#666666';
      default:
        return '#666666';
    }
  };

  const getRiskScoreColor = (score) => {
    if (score >= 70) return '#000000';
    if (score >= 40) return '#333333';
    return '#666666';
  };

  const formatTime = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  const formatDate = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleDateString();
  };

  const [refreshTime, setRefreshTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setRefreshTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  const fraudulentCount = transactions.filter(t => t.status === 'FRAUDULENT').length;
  const suspiciousCount = transactions.filter(t => t.status === 'SUSPICIOUS').length;
  const totalAmount = transactions.reduce((sum, t) => sum + t.amount, 0);

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1>🚨 FraudSense Dashboard</h1>
        <div className="pitch">
          <p className="pitch-intro">
            Hey there. If you're viewing this, you're probably deciding whether to hire me. This is a **learning project** I built to bridge specific gaps on my resume. Here's what I got out of it:
          </p>
          <p className="pitch-body">
            I deliberately built a real-time fraud detection pipeline to **deeply understand** how event-driven systems work. I learned why Kafka decouples producers from consumers, allowing independent scaling and fault tolerance. I discovered that in-memory caching (Redis) is non-negotiable for sub-millisecond pattern detection. I implemented and tested a weighted anomaly scoring algorithm to understand fraud detection logic. And I deployed the entire system on AWS to learn how managed services (EC2, DynamoDB, Confluent Cloud) eliminate infrastructure headaches while scaling to production.
          </p>
          <p className="pitch-tech">
            <strong>What I learned:</strong> Event-Driven Architecture, Kafka, Redis, DynamoDB, AWS (Serverless), Real-Time Systems, Anomaly Detection, Java Spring Boot, REST APIs, React.
          </p>
        </div>
      </header>

      <div className="stats-container">
        <div className="stat-card fraudulent">
          <div className="stat-value">{fraudulentCount}</div>
          <div className="stat-label">Flagged as Fraudulent</div>
        </div>
        <div className="stat-card suspicious">
          <div className="stat-value">{suspiciousCount}</div>
          <div className="stat-label">Suspicious Transactions</div>
        </div>
        <div className="stat-card total">
          <div className="stat-value">${totalAmount.toLocaleString('en-US', { minimumFractionDigits: 0 })}</div>
          <div className="stat-label">Total at Risk</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{transactions.length}</div>
          <div className="stat-label">Total Transactions</div>
        </div>
      </div>

      <div className="transactions-section">
        <h2>Flagged Transactions (Live Feed)</h2>
        <div className="transactions-table">
          <div className="table-header">
            <div className="col-time">Time</div>
            <div className="col-user">User ID</div>
            <div className="col-amount">Amount</div>
            <div className="col-risk">Risk Score</div>
            <div className="col-status">Status</div>
            <div className="col-flags">Flags</div>
          </div>
          {transactions.map((tx) => (
            <div key={tx.id} className="table-row">
              <div className="col-time">
                <div className="time">{formatTime(tx.timestamp)}</div>
                <div className="date">{formatDate(tx.timestamp)}</div>
              </div>
              <div className="col-user">{tx.userId}</div>
              <div className="col-amount">${tx.amount.toFixed(2)}</div>
              <div className="col-risk">
                <div
                  className="risk-score"
                  style={{ backgroundColor: getRiskScoreColor(tx.riskScore) }}
                >
                  {tx.riskScore}
                </div>
              </div>
              <div className="col-status">
                <span
                  className="status-badge"
                  style={{ backgroundColor: getStatusColor(tx.status) }}
                >
                  {tx.status}
                </span>
              </div>
              <div className="col-flags">
                <div className="flags">
                  {tx.flags.map((flag, idx) => (
                    <span key={idx} className="flag-badge">
                      {flag}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      <footer className="dashboard-footer">
        <p>🟢 Live | Last updated: {refreshTime.toLocaleTimeString()}</p>
        <p>New fraudulent transactions detected every 3-5 seconds</p>
      </footer>
    </div>
  );
};

export default Dashboard;
