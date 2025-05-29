// src/pages/Booking/ReservationForm.jsx
import React, { useState } from 'react';
import { postJSON } from '../../api/api';
import { useParams, useNavigate } from 'react-router-dom';
import Input from '../../components/Input';
import Button from '../../components/Button';
import '../../styles/index.css';

export default function ReservationForm() {
  const { sessionId } = useParams();
  const [name, setName]         = useState('');
  const [phone, setPhone]       = useState('');
  const [email, setEmail]       = useState('');
  const [duration, setDuration] = useState(30);
  const navigate = useNavigate();

  const handleSubmit = async e => {
    e.preventDefault();
    try {
      await postJSON('/api/reservations', {
        sessionId: +sessionId,
        name,
        phone,
        email,
        duration            // ← važno: prosleđujemo izabrano trajanje
      });
      alert('Reserved!');
      navigate('/');
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <div className="app-container">
      <div className="app-window reservation-window">
        <h2 className="reservation-title">Book a Session</h2>
        <form onSubmit={handleSubmit} className="reservation-form">
          <Input
            placeholder="Name"
            value={name}
            onChange={e => setName(e.target.value)}
            required
          />
          <Input
            placeholder="Phone"
            value={phone}
            onChange={e => setPhone(e.target.value)}
            required
          />
          <Input
            type="email"
            placeholder="Email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
          />
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
              />
              <span>60 min</span>
            </label>
          </div>
          <Button type="submit">Confirm</Button>
        </form>
      </div>
    </div>
  );
}
