// src/pages/Booking/CancelReservation.jsx
import React, { useState } from 'react';
import { getJSON } from '../../api/api';
import { Link } from 'react-router-dom';
import Button from '../../components/Button';
import Input from '../../components/Input';
import ConfirmModal from '../../components/ConfirmModal';  // koristimo običan ConfirmModal
import '../../styles/index.css';

export default function CancelReservation() {
  const [phone, setPhone] = useState('');
  const [reservations, setReservations] = useState([]);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [toCancel, setToCancel] = useState(null);

  const fetchReservations = async e => {
    e.preventDefault();
    setMessage('');
    setError('');
    try {
      const data = await getJSON(`/api/reservations?phone=${encodeURIComponent(phone)}`);
      const active = data.filter(r => r.status === 'ACTIVE');
      setReservations(active);
      if (active.length === 0) setMessage('No active reservations found for this phone.');
    } catch (err) {
      console.error(err);
      setError('Error fetching reservations.');
    }
  };

  const requestCancel = id => {
    setToCancel(id);
    setError('');
    setMessage('');
  };
  const cancelModal = () => setToCancel(null);

  const confirmCancel = async () => {
    try {
      const res = await fetch(`/api/reservations/${toCancel}`, {
        method: 'DELETE'
      });
      if (res.status === 204) {
        setReservations(prev => prev.filter(r => r.id !== toCancel));
        setMessage('Canceled successfully.');
      } else {
        const body = await res.json();
        setError(body.error || 'Cancellation failed.');
      }
    } catch {
      setError('Network error.');
    } finally {
      setToCancel(null);
    }
  };

  return (
    <div className="app-container">
      <div className="app-window cancel-window">
        <h2 className="cancel-title">Cancel Reservation</h2>

        <form onSubmit={fetchReservations} className="cancel-form">
          <Input
            type="text"
            placeholder="Enter your phone"
            value={phone}
            onChange={e => setPhone(e.target.value)}
            required
            className="cancel-input"
          />
          <div className="cancel-actions">
            <Button type="submit">Fetch Reservations</Button>
          </div>
        </form>

        {message && <p className="cancel-message">{message}</p>}
        {error   && <p className="cancel-error">{error}</p>}

        <ul className="cancel-list">
          {reservations.map(r => {
            const start = new Date(r.startTime);
            const end = new Date(start.getTime() + r.duration * 60000);
            const fmt = { hour: '2-digit', minute: '2-digit' };
            const timeRange = `${start.toLocaleTimeString([], fmt)} – ${end.toLocaleTimeString([], fmt)}`;

            return (
              <li key={r.id} className="cancel-item">
                <div className="cancel-item-info">
                  <span className="cancel-time">{timeRange}</span> — <span className="cancel-name">{r.userName}</span>
                </div>
                <Button className="cancel-btn" onClick={() => requestCancel(r.id)}>Cancel</Button>
              </li>
            );
          })}
        </ul>

        <div className="page-footer">
          <Link to="/">
            <Button>Home</Button>
          </Link>
        </div>

        {toCancel !== null && (
          <ConfirmModal
            message="Are you sure you want to cancel this reservation?"
            onConfirm={confirmCancel}
            onCancel={cancelModal}
          />
        )}
      </div>
    </div>
  );
}
