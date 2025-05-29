import React from 'react';
import Button from './Button';
import '../styles/index.css';

export default function ConfirmModal({ message, onConfirm, onCancel }) {
  return (
    <div className="reservation-modal-overlay">
      <div className="reservation-modal-window">
        <p>{message}</p>
        <div className="flex">
          <Button onClick={onCancel}>No</Button>
          <Button onClick={onConfirm}>Yes</Button>
        </div>
      </div>
    </div>
  );
}
