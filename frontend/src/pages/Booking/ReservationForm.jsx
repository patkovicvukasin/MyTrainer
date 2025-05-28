import React, { useState } from 'react';
import { postJSON } from '../../api/api';
import { useParams, useNavigate } from 'react-router-dom';
import Input from '../../components/Input';
import Button from '../../components/Button';
export default function ReservationForm() {
  const { sessionId } = useParams();
  const [name,setName] = useState('');
  const [phone,setPhone] = useState('');
  const [email,setEmail] = useState('');
  const [duration,setDuration] = useState(30);
  const nav = useNavigate();
  const handle = async e => {
    e.preventDefault();
    await postJSON('/api/reservations', { sessionId: +sessionId, name, phone, email, duration });
    alert('Reserved!'); nav('/');
  };
  return (
    <form onSubmit={handle} className="max-w-md mx-auto mt-10 space-y-4">...
    </form>
  );
}