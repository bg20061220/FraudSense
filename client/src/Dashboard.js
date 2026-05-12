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

  const fraudulentCount = transactions.filter(t => t.status === 'FRAUDULENT').length;
  const suspiciousCount = transactions.filter(t => t.status === 'SUSPICIOUS').length;
  const totalAmount = transactions.reduce((sum, t) => sum + t.amount, 0);

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1>🚨 FraudSense Dashboard</h1>
        <div className="pitch">
          <p className="pitch-intro">
            Hey there if you're here, you're probably deciding whether to hire me. Here's my case:
          </p>
          <p className="pitch-body">
            I built a real-time fraud detection pipeline from the ground up: <strong>Stripe webhooks → Kafka streaming → custom scoring algorithms → Redis caching + DynamoDB persistence → live dashboard.</strong> Every transaction is scored in real time using weighted anomaly detection (velocity checks, geo-anomalies, amount spikes). The whole system is deployed on AWS (EC2, DynamoDB, CloudKarafka), not just local.
          </p>
          <p className="pitch-tech">
            <strong>Built with:</strong> Java, Spring Boot, Kafka, Redis, DynamoDB, AWS, React.
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
        <p>Last updated: {new Date().toLocaleTimeString()}</p>
        <p>Auto-refreshes every 5 seconds</p>
      </footer>
    </div>
  );
};

export default Dashboard;
