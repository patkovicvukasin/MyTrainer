import React, { useState } from 'react';
import Button from './Button';
import Input from './Input';

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
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
      <div className="bg-white p-6 rounded shadow-lg max-w-sm w-full">
        <p className="mb-2">Enter your access code to confirm cancellation:</p>
        <Input
          type="password"
          value={code}
          onChange={e => setCode(e.target.value)}
          placeholder="Access code"
        />
        {error && <p className="text-red-600 mt-1">{error}</p>}
        <div className="flex justify-end space-x-2 mt-4">
          <Button className="bg-gray-500" onClick={onCancel}>No</Button>
          <Button onClick={handleOk}>Yes</Button>
        </div>
      </div>
    </div>
  );
}
