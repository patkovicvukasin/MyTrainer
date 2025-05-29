import React, { useState } from 'react';
import Button from './Button';
import Input from './Input';
import '../styles/index.css';

export default function ReservationModal({
  session,
  nextSession,
  onClose,
  onConfirm
}) {
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [duration, setDuration] = useState(30);

  const handleSubmit = e => {
    e.preventDefault();
    onConfirm({ session, nextSession, name, phone, email, duration });
  };

  const timeStr = new Date(session.startTime)
    .toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

  return (
    <div className="reservation-modal-overlay">
      <div className="reservation-modal-window">
        <h2 className="reservation-header">Reserve {timeStr}</h2>
        <form onSubmit={handleSubmit} className="reservation-form">

          <div className="form-group">
            <label className="form-label"></label>
            <Input
              placeholder="Name"
              value={name}
              onChange={e => setName(e.target.value)}
              required
              className="reservation-input"
            />
          </div>

          <div className="form-group">
            <label className="form-label"></label>
            <Input
              placeholder="Phone"
              value={phone}
              onChange={e => setPhone(e.target.value)}
              required
              className="reservation-input"
            />
          </div>

          <div className="form-group">
            <label className="form-label"></label>
            <Input
              placeholder="Email"
              type="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
              className="reservation-input"
            />
          </div>

          <div className="radio-group">
            <label className="radio-label">
              <input
                type="radio"
                name="duration"
                value={30}
                checked={duration === 30}
                onChange={() => setDuration(30)}
              />
              <span>30 min</span>
            </label>
            <label className="radio-label">
              <input
                type="radio"
                name="duration"
                value={60}
                checked={duration === 60}
                onChange={() => setDuration(60)}
                disabled={!nextSession} // onemogućeno ako nema sledeće sesije
              />
              <span>60 min</span>
            </label>
          </div>

          <div className="modal-actions">
            <Button type="submit" className="confirm-btn">Confirm</Button>
            <Button type="button" className="cancel-btn" onClick={onClose}>Cancel</Button>
          </div>
        </form>
      </div>
    </div>
  );
}