import React, { useState } from 'react';
import { getJSON } from '../../api/api';
import { Link } from 'react-router-dom';
import Button from '../../components/Button';
import Input from '../../components/Input';
import '../../styles/index.css';

export default function CancelReservation() {
  const [phone, setPhone] = useState('');
  const [reservations, setReservations] = useState([]);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const fetchReservations = async e => {
    e.preventDefault();
    setMessage(''); setError('');
    try {
      const data = await getJSON(`/api/reservations?phone=${encodeURIComponent(phone)}`);
      const now = new Date();
      const active = data.filter(r =>
        r.status === 'ACTIVE' &&
        new Date(r.startTime) >= now
      );
      setReservations(active);
      if (active.length === 0) {
        setMessage('No active reservations found for this phone.');
      }
    } catch {
      setError('Error fetching reservations.');
    }
  };

  const handleCancel = async id => {
    setMessage(''); setError('');
    try {
      const res = await fetch(`/api/reservations/${id}`, { method: 'DELETE' });
      if (res.status === 204) {
        setReservations(r => r.filter(x => x.id !== id));
        setMessage('Canceled successfully.');
      } else {
        const { error } = await res.json();
        setError(error);
      }
    } catch {
      setError('Network error.');
    }
  };

  return (
    <div className="app-container">
      <div className="app-window cancel-window">
        <h2 className="cancel-title">Cancel Reservation</h2>

        <form onSubmit={fetchReservations} className="cancel-form">
          <label className="cancel-label">
            <Input
              type="text"
              placeholder="Enter your phone"
              value={phone}
              onChange={e => setPhone(e.target.value)}
              required
              className="cancel-input"
            />
          </label>
          <div className="cancel-actions">
            <Button type="submit">Fetch Reservations</Button>
          </div>
        </form>

        {message && <p className="cancel-message">{message}</p>}
        {error   && <p className="cancel-error">{error}</p>}

        <ul className="cancel-list">
          {reservations.map(r => (
            <li key={r.id} className="cancel-item">
              <div>
                <div>
                  {new Date(r.startTime).toLocaleDateString()}{' '}
                  {new Date(r.startTime).toLocaleTimeString([], {
                    hour: '2-digit',
                    minute: '2-digit'
                  })}
                </div>
                <div>
                  Session ID: {r.sessionId} — Status: {r.status}
                </div>
              </div>
              <Button onClick={() => handleCancel(r.id)}>Cancel</Button>
            </li>
          ))}
        </ul>

        <div className="page-footer">
          <Link to="/">
            <Button>Home</Button>
          </Link>
        </div>
      </div>
    </div>
  );
}
