import { useState, FormEvent } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { InfoHolder } from '../types';
import backendUrls from '../util/backendUrls';

const Register = ({ infoHolder }: { infoHolder: InfoHolder }) => {
  const navigate = useNavigate();
  const [login, setLogin] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  if (infoHolder.info?.login) {
    return <Navigate to="/" />;
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);

    // Validate passwords
    if (password.length < 6) {
      setError('Пароль должен содержать минимум 6 символов');
      return;
    }

    if (password !== confirmPassword) {
      setError('Пароли не совпадают');
      return;
    }

    setIsLoading(true);

    try {
      const response = await fetch(backendUrls.register(), {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ login, password }),
      });

      if (response.status === 409) {
        setError('Пользователь с таким логином уже существует');
        return;
      }

      if (response.ok) {
        const data = await response.json() as { token: string };
        infoHolder.setToken(data.token);
        navigate('/');
      }
    } catch (err) {
      console.error(err);
      setError('Произошла ошибка при регистрации');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6 col-lg-4">
          <h2 className="mb-4">Регистрация</h2>
          <form onSubmit={(e) => { void handleSubmit(e) }}>
            <div className="mb-3">
              <label htmlFor="login" className="form-label">Логин</label>
              <input
                type="text"
                className="form-control"
                id="login"
                value={login}
                onChange={(e) => setLogin(e.target.value)}
                disabled={isLoading}
                required
              />
            </div>
            <div className="mb-3">
              <label htmlFor="password" className="form-label">Пароль</label>
              <input
                type="password"
                className="form-control"
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={isLoading}
                required
                minLength={6}
              />
            </div>
            <div className="mb-3">
              <label htmlFor="confirmPassword" className="form-label">Повторите пароль</label>
              <input
                type="password"
                className="form-control"
                id="confirmPassword"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                disabled={isLoading}
                required
              />
            </div>
            {error && (
              <div className="alert alert-danger" role="alert">
                {error}
              </div>
            )}
            <button 
              type="submit" 
              className="btn btn-primary w-100"
              disabled={isLoading}
            >
              {isLoading ? 'Загрузка...' : 'Зарегистрироваться'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Register; 