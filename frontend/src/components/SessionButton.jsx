import React from 'react';
export default function SessionButton({ time, taken, onClick }) {
  const base = 'text-white py-2 rounded';
  const color = taken ? 'bg-red-500' : 'bg-green-500 hover:bg-green-600';
  return (
    <button onClick={onClick} disabled={taken} className={`${base} ${color}`}>
      {time}
    </button>
  );
}