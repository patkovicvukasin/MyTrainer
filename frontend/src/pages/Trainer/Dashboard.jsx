import React, { useState, useEffect, useContext, useCallback } from 'react';
import { getJSON, postJSON } from '../../api/api';
import AuthContext from '../../context/AuthContext';
import Button from '../../components/Button';
import CodeConfirmModal from '../../components/CodeConfirmModal';
import { useNavigate } from 'react-router-dom';
import '../../styles/index.css';

const formatISO = dateObj => dateObj.toISOString().slice(0, 10);
const parseISODate = str => new Date(str);
const formatTime = isoStr => new Date(isoStr).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

function SessionItem({ session, onCancel }) {
  const resv = session.reservations.find(r => r.status === 'ACTIVE');
  const time = formatTime(session.startTime);
  return (
    <div key={session.id} className="session-item">
      <div>{time} — {resv.userName}</div>
      <Button className="cancel-btn" onClick={() => onCancel(resv.reservationId)}>Cancel</Button>
    </div>
  );
}

function DateNavigator({ date, onPrev, onNext, disablePrev }) {
  const d = parseISODate(date);
  const weekdayNames = ['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'];
  return (
    <div className="dashboard-navigation">
      <Button onClick={onPrev} disabled={disablePrev}>Previous</Button>
      <span className="current-day">
        {weekdayNames[d.getDay()]}, {d.toLocaleDateString()}
      </span>
      <Button onClick={onNext}>Next</Button>
    </div>
  );
}

export default function Dashboard() {
  const { token, logout } = useContext(AuthContext);
  const [trainerId, setTrainerId]     = useState(null);
  const [trainerName, setTrainerName] = useState('');
  const [view, setView]               = useState('daily');
  const [date, setDate]               = useState(() => formatISO(new Date()));
  const [sessions, setSessions]       = useState([]);
  const [toCancel, setToCancel]       = useState(null);
  const [awaitingCode, setAwaitingCode] = useState(false);
  const nav = useNavigate();

  const fetchTrainer = useCallback(() => {
    getJSON('/api/trainers/me', token)
      .then(data => {
        setTrainerId(data.id);
        setTrainerName(data.name);
      })
      .catch(console.error);
  }, [token]);

  const fetchSessions = useCallback(() => {
    if (!trainerId) return;
    getJSON(`/api/trainers/${trainerId}/sessions?view=${view}&date=${date}`, token)
      .then(data => setSessions(
        data.filter(s => s.reservations.some(r => r.status === 'ACTIVE'))
      ))
      .catch(console.error);
  }, [token, trainerId, view, date]);

  useEffect(fetchTrainer, [fetchTrainer]);
  useEffect(fetchSessions, [fetchSessions]);

  const changeDate = delta => {
    const d = parseISODate(date);
    d.setDate(d.getDate() + delta);
    setDate(formatISO(d));
  };

  const openConfirm  = id => { setToCancel(id); setAwaitingCode(true); };
  const closeConfirm = () => { setToCancel(null); setAwaitingCode(false); };

  const handleCodeConfirm = async code => {
    try {
      const { token: newToken } = await postJSON('/api/auth/login', { code });
      const res = await fetch(
        `/api/trainers/me/reservations/${toCancel}`,
        { method: 'DELETE', headers: { Authorization: `Bearer ${newToken}` } }
      );
      if (res.ok) {
        setSessions(prev => prev.filter(s =>
          !s.reservations.some(r => r.reservationId === toCancel)
        ));
        alert('Reservation canceled.');
      } else {
        const { error } = await res.json();
        alert(error || 'Cancel failed');
      }
    } catch {
      alert('Invalid code or network error');
    } finally {
      closeConfirm();
    }
  };

  if (trainerId == null) {
    return (
      <div className="app-container">
        <div className="app-window dashboard-window">
          <p>Loading your schedule…</p>
        </div>
      </div>
    );
  }

  return (
    <div className="app-container">
      <div className="app-window dashboard-window">
        <div className="dashboard-header">
          <h2 className="trainer-title">Trainer {trainerName}</h2>
          <h3 className="schedule-title">Schedule</h3>
        </div>

        <div className="dashboard-toggle">
          <Button onClick={() => setView('daily')} className={view==='daily' ? 'active' : ''}>Daily</Button>
          <Button onClick={() => setView('weekly')} className={view==='weekly' ? 'active' : ''}>Weekly</Button>
        </div>

        {view === 'daily' && (
          <DateNavigator
            date={date}
            onPrev={() => changeDate(-1)}
            onNext={() => changeDate(1)}
            disablePrev={date === formatISO(new Date())}
          />
        )}

        <div className="dashboard-content">
          {view === 'daily' ? (
            sessions.length > 0 ? sessions.map(s => (
              <SessionItem key={s.id} session={s} onCancel={openConfirm} />
            )) : <p className="no-sessions">No sessions for this day.</p>
          ) : (
            <div className="weekly-grid">
              {Array.from({ length: 7 }).map((_, i) => {
                const d = parseISODate(date);
                d.setDate(d.getDate() + i);
                const dayStr = formatISO(d);
                const daySessions = sessions.filter(s => s.startTime.startsWith(dayStr));
                return (
                  <div key={dayStr} className="weekday-column">
                    <h4 className="weekday-title">
                      {d.toLocaleDateString(undefined, { weekday: 'long' })} — {d.toLocaleDateString()}
                    </h4>
                    {daySessions.length > 0 ? daySessions.map(s => (
                      <SessionItem key={s.id} session={s} onCancel={openConfirm} />
                    )) : <p className="no-sessions-sm">No sessions</p>}
                  </div>
                );
              })}
            </div>
          )}

        </div>

        {awaitingCode && toCancel && (
          <CodeConfirmModal onConfirm={handleCodeConfirm} onCancel={closeConfirm} />
        )}

        <div className="dashboard-footer">
          <Button className="book-btn" onClick={() => nav(`/book/${trainerId}?date=${date}`)}>Book a Session</Button>
          <Button onClick={() => { logout(); nav('/'); }}>Logout</Button>
        </div>
      </div>
    </div>
  );
}
