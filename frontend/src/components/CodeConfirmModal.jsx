import React, { useState } from 'react';
import Button from './Button';
import Input from './Input';
import '../styles/index.css';

export default function CodeConfirmModal({ onConfirm, onCancel }) {
  const [code, setCode] = useState('');
  const [error, setError] = useState('');

  const handleOk = () => {
    if (!code.trim()) {
      setError('Access code is required');
      return;
    }
    setError('');
    onConfirm(code.trim());
  };

  return (
    <div className="reservation-modal-overlay">
      <div className="reservation-modal-window">
        <p>Enter your access code to confirm cancellation:</p>
        <Input
          type="password"
          value={code}
          onChange={e => setCode(e.target.value)}
          placeholder="Access code"
        />
        {error && <p className="text-red-600 mt-1">{error}</p>}
        <div className="flex justify-end space-x-2 mt-4">
          <Button onClick={onCancel}>No</Button>
          <Button onClick={handleOk}>Yes</Button>
        </div>
      </div>
    </div>
  );
}