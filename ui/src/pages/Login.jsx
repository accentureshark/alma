import '../styles/login-form.css'
import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Toast } from 'primereact/toast';
import { CustomCard } from '../components/ui/CustomCard';
import { CustomButton } from '../components/ui/CustomButton';
import { CustomCheckbox } from '../components/ui/CustomCheckbox';
import { InputField } from '../components/ui/InputField';

const Login = () => {
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const [rememberMe, setRememberMe] = useState(false);
  const [showRoleButtons, setShowRoleButtons] = useState(false);
  const [validatedUser, setValidatedUser] = useState(null);
  const { login, isLoading } = useAuth();
  const navigate = useNavigate();
  const toast = useRef(null);

  const handleInputChange = (field, value) => {
    setCredentials(prev => ({ ...prev, [field]: value }));
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    if (!credentials.email || !credentials.password) {
      toast.current.show({
        severity: 'warn',
        summary: 'Campos requeridos',
        detail: 'Por favor ingresa email y contraseña'
      });
      return;
    }

    const { success, user, error } = await login(credentials.email, credentials.password);
    
    if (success) {
      setValidatedUser(user);
      setShowRoleButtons(true);
      toast.current.show({
        severity: 'success',
        summary: 'Usuario validado',
        detail: `Usuario encontrado: ${user.email}${user.isAdmin ? ' (Administrador)' : ''}`
      });
    } else {
      toast.current.show({
        severity: 'error',
        summary: 'Error de autenticación',
        detail: error || 'Credenciales inválidas'
      });
    }
  };

  const handleRoleNavigation = (roleType) => {
    if (!validatedUser) return;
    
    if (roleType === 'admin') {
      if (validatedUser.isAdmin) {
        navigate('/home');
      } else {
        toast.current.show({
          severity: 'warn',
          summary: 'Acceso denegado',
          detail: 'No tienes permisos de administrador'
        });
      }
    } else {
      navigate('/user-dashboard');
    }
  };

  return (
    <div className="login-container">
      <Toast ref={toast} />
      <CustomCard className="login-card">
        <div className="login-header">
          <h2>Iniciar Sesión</h2>
          <p>Ingresa tus credenciales para acceder</p>
        </div>
        
        <form onSubmit={handleLogin} className="login-form">
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <InputField
              id="email"
              type="email"
              placeholder="Ingresa tu email"
              value={credentials.email}
              onChange={(e) => handleInputChange('email', e.target.value)}
              className="login-input"
              disabled={showRoleButtons}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Contraseña</label>
            <InputField
              id="password"
              type="password"
              placeholder="Ingresa tu contraseña"
              value={credentials.password}
              onChange={(e) => handleInputChange('password', e.target.value)}
              className="login-input"
              disabled={showRoleButtons}
            />
          </div>

          <div className="form-options">
            <div className="p-field-checkbox">
                <CustomCheckbox 
                  label="Recordame" 
                  inputId="rememberMe" 
                  onChange={e => setRememberMe(e.checked)} 
                  checked={rememberMe}
                  disabled={showRoleButtons}
                />
            </div>
            <a href="#" className="forgot-password">
              ¿Olvidaste tu contraseña?
            </a>
            <a href="/register" className="forgot-password">
              Crear cuenta
            </a>
          </div>

          {!showRoleButtons && (
            <CustomButton
              type="submit"
              label={isLoading ? "Validando..." : "Validar Usuario"}
              disabled={isLoading}
              className="login-button"
              icon={isLoading ? "pi pi-spin pi-spinner" : "pi pi-check"}
            />
          )}
        </form>

        {showRoleButtons && validatedUser && (
          <div className="login-footer">
            <p>Usuario validado correctamente. Selecciona tu área de trabajo:</p>
            <div className="role-buttons">
              {validatedUser.isAdmin && (
                <CustomButton 
                  label="Administrador" 
                  onClick={() => handleRoleNavigation('admin')} 
                  disabled={isLoading} 
                  severity="secondary"
                  icon="pi pi-cog"
                />
              )}
              <CustomButton 
                label="Usuario" 
                onClick={() => handleRoleNavigation('user')} 
                disabled={isLoading} 
                severity="secondary"
                icon="pi pi-user"
              />
            </div>
            <CustomButton 
              label="Cambiar Usuario" 
              onClick={() => {
                setShowRoleButtons(false);
                setValidatedUser(null);
                setCredentials({ email: '', password: '' });
              }}
              className="secondary-button"
              severity="help"
              outlined
            />
          </div>
        )}
      </CustomCard>
    </div>
  );
};

export default Login;
