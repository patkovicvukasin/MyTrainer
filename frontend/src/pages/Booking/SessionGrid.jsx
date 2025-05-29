import React, { useState, useEffect } from 'react';
import { getJSON, postJSON } from '../../api/api';
import { useParams, useLocation, Link } from 'react-router-dom';
import SessionButton from '../../components/SessionButton';
import ReservationModal from '../../components/ReservationModal';
import Button from '../../components/Button';
import '../../styles/index.css';

export default function SessionGrid() {
  const { trainerId } = useParams();
  const date = new URLSearchParams(useLocation().search).get('date');
  const [trainerName, setTrainerName] = useState('');
  const [sessions, setSessions] = useState([]);
  const [modalSess, setModalSess] = useState(null);
  const [modalNext, setModalNext] = useState(null);
  const [message, setMessage] = useState('');

  useEffect(() => {
    getJSON('/api/trainers')
      .then(data => {
        const t = data.find(tr => String(tr.id) === trainerId);
        if (t) setTrainerName(t.name);
      })
      .catch(console.error);
  }, [trainerId]);

  useEffect(() => {
    getJSON(`/api/trainers/${trainerId}/sessions?view=daily&date=${date}`)
      .then(data => {
        const free = data.filter(s =>
          !s.reservations.some(r => r.status === 'ACTIVE')
        );
        setSessions(free);
      })
      .catch(console.error);
  }, [trainerId, date]);

  const openModal = s => {
    const idx = sessions.findIndex(x => x.id === s.id);
    const next = sessions[idx + 1];
    setModalSess(s);
    setModalNext(
      next && (new Date(next.startTime) - new Date(s.startTime) === 30 * 60000)
        ? next
        : null
    );
  };

  const handleConfirm = async ({ session, nextSession, name, phone, email, duration }) => {
    try {
      await postJSON('/api/reservations', {
        sessionId: session.id,
        name,
        phone,
        duration
      });

      setSessions(prev =>
        prev.filter(s =>
          duration === 60
            ? s.id !== session.id && s.id !== nextSession?.id
            : s.id !== session.id
        )
      );

      setMessage('Successfully reserved!');
      setModalSess(null);
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <div className="app-container">
      <div className="app-window session-grid-window">
        <h2 className="grid-title">
          Available Sessions for {trainerName || trainerId} on{' '}
          {new Date(date).toLocaleDateString()}
        </h2>
        {message && <p className="success-msg">{message}</p>}

        <div className="session-buttons-grid">
          {sessions.map(s => {
            const time = new Date(s.startTime).toLocaleTimeString([], {
              hour: '2-digit',
              minute: '2-digit'
            });
            return (
              <SessionButton
                key={s.id}
                time={time}
                taken={false}
                onClick={() => openModal(s)}
              />
            );
          })}
        </div>

        {modalSess && (
          <ReservationModal
            session={modalSess}
            nextSession={modalNext}
            onClose={() => setModalSess(null)}
            onConfirm={handleConfirm}
          />
        )}

        <div className="page-footer">
          <Link to="/">
            <Button>Home</Button>
          </Link>
        </div>
      </div>
    </div>
  );
}
