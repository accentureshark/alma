import '../styles/login-form.css';
import { useState, useRef } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Toast } from 'primereact/toast';
import { CustomCard } from '../components/ui/CustomCard';
import { CustomButton } from '../components/ui/CustomButton';
import { InputField } from '../components/ui/InputField';

const Register = () => {
  const [form, setForm] = useState({ email: '', password: '' });
  const toast = useRef(null);
  const navigate = useNavigate();

  const handleChange = (field, value) => {
    setForm(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const baseUrl = import.meta.env.VITE_API_URL || '/api';
    const res = await fetch(`${baseUrl}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form)
    });
    const data = await res.json();
    if (data.success) {
      toast.current.show({ severity: 'success', summary: 'Registro', detail: data.message });
      setTimeout(() => navigate('/login'), 1500);
    } else {
      toast.current.show({ severity: 'error', summary: 'Error', detail: data.message });
    }
  };

  return (
    <div className="login-container">
      <Toast ref={toast} />
      <CustomCard className="login-card">
        <div className="login-header">
          <h2>Crear Cuenta</h2>
          <p>Solo emails @accenture.com</p>
        </div>
        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <InputField id="email" type="email" value={form.email} onChange={e => handleChange('email', e.target.value)} className="login-input" />
          </div>
          <div className="form-group">
            <label htmlFor="password">Contraseña</label>
            <InputField id="password" type="password" value={form.password} onChange={e => handleChange('password', e.target.value)} className="login-input" />
          </div>
          <CustomButton type="submit" label="Registrarme" className="login-button" />
        </form>
        <div className="login-footer">
          <Link to="/login">Volver a iniciar sesión</Link>
        </div>
      </CustomCard>
    </div>
  );
};

export default Register;
