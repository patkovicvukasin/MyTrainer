import React, { useState, useEffect } from 'react';
import { getJSON } from '../../api/api';
import { useNavigate, Link } from 'react-router-dom';
import Button from '../../components/Button';
import '../../styles/index.css';

export default function TrainerList() {
  const [trainers, setTrainers] = useState([]);
  const [date, setDate]         = useState(new Date().toISOString().slice(0,10));
  const nav = useNavigate();

  useEffect(() => {
    getJSON('/api/trainers').then(setTrainers);
  }, []);

  return (
    <div className="app-container">
      <div className="app-window trainer-list-window">
        <h2 className="list-title">Book a Session</h2>
        <input
          type="date"
          value={date}
          onChange={e => setDate(e.target.value)}
          className="trainer-date-picker"
        />

        <div className="trainer-buttons">
          {trainers.map(t => (
            <Button
              key={t.id}
              onClick={() => nav(`/book/${t.id}?date=${date}`)}
            >
              {t.name}
            </Button>
          ))}
        </div>

        <div className="page-footer">
          <Link to="/">
            <Button>Home</Button>
          </Link>
        </div>
      </div>
    </div>
  );
}
