import React from 'react';
import { Link } from 'react-router-dom';
import Button from '../components/Button';
import '../styles/index.css';

export default function Home() {
  return (
    <div className="app-container">
      <div className="app-window">
        <h1>MyTrainer</h1>
        <div className="button-group">
          <Link to="/book"><Button>Book a Session</Button></Link>
          <Link to="/cancel"><Button>Cancel Reservation</Button></Link>
          <Link to="/trainer/login"><Button>Trainer Login</Button></Link>
          <Link to="/trainer/register"><Button>Trainer Register</Button></Link>
        </div>
      </div>
    </div>
  );
}
