import { Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import TrainerRegister from './pages/Trainer/Register';
import TrainerLogin from './pages/Trainer/Login';
import TrainerDashboard from './pages/Trainer/Dashboard';
import TrainerList from './pages/Booking/TrainerList';
import SessionGrid from './pages/Booking/SessionGrid';
import ReservationForm from './pages/Booking/ReservationForm';
import CancelReservation from './pages/Booking/CancelReservation';


export default function App() {
  return (
    <div className="container mx-auto p-4">
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/trainer/register" element={<TrainerRegister />} />
        <Route path="/trainer/login" element={<TrainerLogin />} />
        <Route path="/trainer/dashboard" element={<TrainerDashboard />} />
        <Route path="/book" element={<TrainerList />} />
        <Route path="/book/:trainerId" element={<SessionGrid />} />
        <Route path="/reserve/:sessionId" element={<ReservationForm />} />
        <Route path="/cancel" element={<CancelReservation />} />
      </Routes>
    </div>
  );
}
