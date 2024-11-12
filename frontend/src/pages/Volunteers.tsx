import { useState, useEffect, useCallback } from 'react';
import { Navigate } from 'react-router-dom';
import { InfoHolder } from '../types';
import backendUrls from '../util/backendUrls';

interface Volunteer {
  id: number;
  login: string;
  canAccess: boolean;
  canManage: boolean;
}

const VolunteersView = ({ infoHolder }: { infoHolder: InfoHolder }) => {
  const [volunteers, setVolunteers] = useState<Volunteer[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isUpdating, setIsUpdating] = useState<string | null>(null);

  const fetchVolunteers = useCallback(async () => {
    try {
      const response = await fetch(backendUrls.getVolunteers(), {
        headers: {
          Authorization: `Bearer ${infoHolder.token}`
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch volunteers');
      }

      const data = await response.json() as Volunteer[];
      setVolunteers(data);
      setError(null);
    } catch (err) {
      console.error(err);
      setError('Не удалось загрузить список волонтеров');
    } finally {
      setIsLoading(false);
    }
  }, [infoHolder.token]);

  useEffect(() => {
    void fetchVolunteers();
  }, [fetchVolunteers]);

  const handleRoleChange = async (id: number, role: 'canAccess' | 'canManage', newValue: boolean) => {
    if (isUpdating) return;
    
    setIsUpdating(String(id));
    try {
      const response = await fetch(backendUrls.patchVolunteer(id), {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${infoHolder.token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ [role]: newValue }),
      });

      if (!response.ok) {
        throw new Error('Failed to update volunteer');
      }

      setVolunteers(volunteers.map(v => 
        v.id === id ? { ...v, [role]: newValue } : v
      ));
    } catch (err) {
      console.error(err);
      setError('Не удалось обновить права доступа');
    } finally {
      setIsUpdating(null);
    }
  };

  if (isLoading) {
    return (
      <div className="container mt-5">
        <div className="text-center">
          <div className="spinner-border" role="status">
            <span className="visually-hidden">Загрузка...</span>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-5">
      <h2 className="mb-4">Волонтеры</h2>
      {error && (
        <div className="alert alert-danger alert-dismissible fade show" role="alert">
          {error}
          <button type="button" className="btn-close" onClick={() => setError(null)}></button>
        </div>
      )}
      <div className="table-responsive">
        <table className="table table-striped">
          <thead>
            <tr>
              <th>Логин</th>
              <th>Доступ к очереди</th>
              <th>Администратор</th>
            </tr>
          </thead>
          <tbody>
            {volunteers.length === 0 ? (
              <tr>
                <td colSpan={3} className="text-center">
                  {error ? 'Не удалось загрузить данные' : 'Нет волонтеров'}
                </td>
              </tr>
            ) : (
              volunteers.map((volunteer) => {
                const isSelf = volunteer.login === infoHolder.info?.login;
                const isUpdatingThis = isUpdating === String(volunteer.id);
                
                return (
                  <tr key={volunteer.id}>
                    <td>{volunteer.login}</td>
                    <td>
                      <div className="d-flex align-items-center gap-2">
                        <span className={`badge ${volunteer.canAccess ? 'bg-success' : 'bg-secondary'}`}>
                          {volunteer.canAccess ? 'Да' : 'Нет'}
                        </span>
                        {!isSelf && (!volunteer.canAccess || !volunteer.canManage) && (
                          <button
                            className={`btn btn-sm btn-${volunteer.canAccess ? 'danger' : 'success'}`}
                            onClick={() => void handleRoleChange(volunteer.id, 'canAccess', !volunteer.canAccess)}
                            disabled={isUpdatingThis}
                          >
                            {isUpdatingThis ? '...' : (volunteer.canAccess ? 'Отозвать' : 'Выдать')}
                          </button>
                        )}
                      </div>
                    </td>
                    <td>
                      <div className="d-flex align-items-center gap-2">
                        <span className={`badge ${volunteer.canManage ? 'bg-success' : 'bg-secondary'}`}>
                          {volunteer.canManage ? 'Да' : 'Нет'}
                        </span>
                        {!isSelf && (
                          <button
                            className={`btn btn-sm btn-${volunteer.canManage ? 'danger' : 'success'}`}
                            onClick={() => void handleRoleChange(volunteer.id, 'canManage', !volunteer.canManage)}
                            disabled={isUpdatingThis}
                          >
                            {isUpdatingThis ? '...' : (volunteer.canManage ? 'Отозвать' : 'Выдать')}
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

const Volunteers = ({ infoHolder }: { infoHolder: InfoHolder }) => {
  if (!infoHolder.info?.canManage) {
    return <Navigate to="/" />;
  }

  return <VolunteersView infoHolder={infoHolder} />;
};

export default Volunteers; 