import { useState, useEffect } from 'react';
import axios from 'axios';
import './Dashboard.css';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

function Dashboard({ token, setToken }) {
  const [account, setAccount] = useState(null);
  const [allAccounts, setAllAccounts] = useState([]);
  const [accountsError, setAccountsError] = useState('');
  const [transactions, setTransactions] = useState([]);
  const [amount, setAmount] = useState('');
  const [toAccount, setToAccount] = useState('');
  const [activeTab, setActiveTab] = useState('deposit');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [copyMessage, setCopyMessage] = useState('');

  const api = axios.create({
    baseURL: API_BASE_URL,
    headers: { Authorization: `Bearer ${token}` }
  });

  useEffect(() => {
    fetchAccount();
    fetchAllAccounts();
    fetchTransactions();
  }, []);

  const fetchAccount = async () => {
    try {
      const response = await api.get('/api/account');
      setAccount(response.data);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchTransactions = async () => {
    try {
      const response = await api.get('/api/account/transactions');
      setTransactions(response.data);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchAllAccounts = async () => {
    try {
      const response = await api.get('/api/account/all');
      setAllAccounts(response.data);
      setAccountsError('');
    } catch (err) {
      console.error(err);
      setAccountsError(err.response?.data?.message || 'Could not load account list');
    }
  };

  const handleDeposit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      await api.post('/api/account/deposit', { amount: parseFloat(amount) });
      setSuccess('Deposit successful!');
      setAmount('');
      fetchAccount();
      fetchAllAccounts();
      fetchTransactions();
    } catch (err) {
      setError(err.response?.data?.message || 'Deposit failed');
    }
  };

  const handleTransfer = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      await api.post('/api/account/transfer', { toAccountNumber: toAccount, amount: parseFloat(amount) });
      setSuccess('Transfer successful!');
      setAmount('');
      setToAccount('');
      fetchAccount();
      fetchAllAccounts();
      fetchTransactions();
    } catch (err) {
      setError(err.response?.data?.message || 'Transfer failed');
    }
  };

  const handleLogout = () => {
    setToken(null);
  };

  const handleCopyAccountNumber = async () => {
    if (!account?.accountNumber) {
      return;
    }

    try {
      await navigator.clipboard.writeText(account.accountNumber);
      setCopyMessage('Account number copied');
    } catch (err) {
      setCopyMessage('Copy failed. Please copy it manually.');
    }

    window.setTimeout(() => setCopyMessage(''), 2000);
  };

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <div className="logo">🏦 BankApp</div>
        <div className="user-info">
          <span>Welcome, {account?.fullName}</span>
          <button onClick={handleLogout} className="logout-btn">Logout</button>
        </div>
      </header>

      <div className="dashboard-content">
        <div className="balance-card">
          <div className="balance-label">Available Balance</div>
          <div className="balance-amount">${account?.balance?.toFixed(2) || '0.00'}</div>
          <div className="account-tools">
            <div className="account-number">Account: {account?.accountNumber}</div>
            <button type="button" className="copy-account-btn" onClick={handleCopyAccountNumber}>
              Copy Account Number
            </button>
          </div>
          <div className="account-helper">Share your account number to receive money faster.</div>
          {copyMessage && <div className="copy-message">{copyMessage}</div>}
        </div>

        <div className="action-panel">
          <div className="tabs">
            <button className={`tab ${activeTab === 'deposit' ? 'active' : ''}`} onClick={() => setActiveTab('deposit')}>Deposit</button>
            <button className={`tab ${activeTab === 'transfer' ? 'active' : ''}`} onClick={() => setActiveTab('transfer')}>Transfer</button>
          </div>

          <div className="tab-content">
            {activeTab === 'deposit' && (
              <form onSubmit={handleDeposit}>
                <div className="form-group">
                  <label>Amount</label>
                  <input type="number" value={amount} onChange={(e) => setAmount(e.target.value)} min="1" step="0.01" required placeholder="Enter amount" />
                </div>
                <button type="submit" className="action-btn">Deposit Money</button>
              </form>
            )}
            {activeTab === 'transfer' && (
              <form onSubmit={handleTransfer}>
                <div className="form-group">
                  <label>Recipient Account Number</label>
                  <input type="text" value={toAccount} onChange={(e) => setToAccount(e.target.value)} required placeholder="Enter account number" />
                </div>
                <div className="form-group">
                  <label>Amount</label>
                  <input type="number" value={amount} onChange={(e) => setAmount(e.target.value)} min="1" step="0.01" required placeholder="Enter amount" />
                </div>
                <button type="submit" className="action-btn transfer">Transfer Money</button>
              </form>
            )}
          </div>
          {error && <div className="error-message">{error}</div>}
          {success && <div className="success-message">{success}</div>}
        </div>

        <div className="transactions-panel">
          <h3>Transaction History</h3>
          <div className="transactions-list">
            {transactions.length === 0 ? <p className="no-transactions">No transactions yet</p> : transactions.map((tx) => (
              <div key={tx.id} className="transaction-item">
                <div className="tx-info">
                  <span className="tx-type">{tx.type}</span>
                  <span className="tx-date">{new Date(tx.createdAt).toLocaleDateString()}</span>
                </div>
                <div className={`tx-amount ${tx.incoming ? 'positive' : 'negative'}`}>
                  {tx.incoming ? '+' : '-'}${tx.amount.toFixed(2)}
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="accounts-panel">
          <div className="panel-heading">
            <h3>All Accounts</h3>
            <span>{allAccounts.length} total</span>
          </div>
          <div className="accounts-list">
            {accountsError ? (
              <p className="no-transactions">{accountsError}</p>
            ) : allAccounts.length === 0 ? (
              <p className="no-transactions">No accounts found</p>
            ) : (
              allAccounts.map((item) => (
                <div
                  key={item.id}
                  className={`account-row ${item.accountNumber === account?.accountNumber ? 'current-account' : ''}`}
                >
                  <div className="account-row-top">
                    <div>
                      <div className="account-owner">{item.fullName}</div>
                      <div className="account-meta">Username: @{item.username}</div>
                    </div>
                    <div className="account-balance">${item.balance.toFixed(2)}</div>
                  </div>
                  <div className="account-grid">
                    <div>
                      <span className="detail-label">Account Number</span>
                      <span className="detail-value">{item.accountNumber}</span>
                    </div>
                    <div>
                      <span className="detail-label">Email</span>
                      <span className="detail-value">{item.email}</span>
                    </div>
                    <div>
                      <span className="detail-label">Created</span>
                      <span className="detail-value">{new Date(item.createdAt).toLocaleDateString()}</span>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
