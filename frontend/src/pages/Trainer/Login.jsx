import React, { useState, useContext } from 'react';
import { postJSON } from '../../api/api';
import AuthContext from '../../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import Input from '../../components/Input';
import Button from '../../components/Button';
import '../../styles/index.css';

export default function Login() {
  const [codeVal, setCodeVal] = useState('');
  const { setToken }          = useContext(AuthContext);
  const nav                   = useNavigate();

  const handle = async e => {
    e.preventDefault();
    const { token } = await postJSON('/api/auth/login', { code: codeVal });
    setToken(token);
    nav('/trainer/dashboard');
  };

  return (
    <div className="app-container">
      <div className="app-window auth-window">
        <h2 className="auth-title">Trainer Login</h2>

        <form onSubmit={handle} className="auth-form">
          <div className="form-group">
            <Input
              placeholder="Access Code"
              value={codeVal}
              onChange={e => setCodeVal(e.target.value)}
              required
            />
          </div>
          <div className="form-actions">
            <Button type="submit">Login</Button>
          </div>
        </form>

        <div className="page-footer">
          <Link to="/">
            <Button>Home</Button>
          </Link>
        </div>
      </div>
    </div>
  );
}
