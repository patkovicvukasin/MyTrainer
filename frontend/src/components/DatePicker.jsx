import React from 'react';
export default function DatePicker({ value, onChange }) {
  return <input type="date" className="p-2 border rounded" value={value} onChange={onChange} />;
}