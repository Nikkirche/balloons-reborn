import { useState, FormEvent } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { InfoHolder } from '../types';
import backendUrls from '../util/backendUrls';

const Login = ({ infoHolder }: { infoHolder: InfoHolder }) => {
  const navigate = useNavigate();
  const [login, setLogin] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  // Redirect if already logged in
  if (infoHolder.info?.login) {
    return <Navigate to="/" />;
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);

    try {
      const response = await fetch(backendUrls.login(), {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ login, password }),
      });

      if (response.status === 403) {
        setError('Неверный логин или пароль');
        return;
      }

      if (response.ok) {
        const data = await response.json() as { token: string };
        infoHolder.setToken(data.token);
        navigate('/');
      }
    } catch (err) {
      console.error(err);
      setError('Произошла ошибка при входе');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6 col-lg-4">
          <h2 className="mb-4">Вход</h2>
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
              {isLoading ? 'Загрузка...' : 'Войти'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login; 