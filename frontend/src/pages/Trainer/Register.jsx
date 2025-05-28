import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import Input from '../../components/Input';
import Button from '../../components/Button';
import '../../styles/index.css';

export default function Register() {
  const [name, setName]       = useState('');
  const [email, setEmail]     = useState('');
  const [code, setCode]       = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError]     = useState('');  

  const handle = async e => {
    e.preventDefault();
    if (isSubmitting) return;
    setError('');  
    setIsSubmitting(true);

    try {
      const res = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email })
      });

      if (res.status === 409) {
        setError('This email already has an account.');
        return;
      }
      if (!res.ok) {
        setError('Error during registration.');
        return;
      }

      const data = await res.json();
      setCode(data.accessCode);

    } catch {
      setError('Network error.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="app-container">
      <div className="app-window auth-window">
        <h2 className="auth-title">Trainer Register</h2>

        <form onSubmit={handle} className="auth-form">
          <div className="form-group">
            <Input
              placeholder="Name"
              value={name}
              onChange={e => setName(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <Input
              placeholder="Email"
              type="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="form-actions">
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Registering…' : 'Register'}
            </Button>
          </div>
        </form>

        {error && (
          <p className="error-message">{error}</p>
        )}

        {code && (
          <p className="code-output">
            Your access code: <strong>{code}</strong>
          </p>
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
