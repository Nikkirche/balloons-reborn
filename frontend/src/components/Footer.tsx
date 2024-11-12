import { useNavigate } from 'react-router-dom';
import { InfoHolder } from '../types';

const Footer = ({ infoHolder }: { infoHolder: InfoHolder }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    infoHolder.setToken(null);
    navigate('/');
  };

  return (
    <footer className="footer mt-auto py-3 bg-light">
      <div className="container d-flex justify-content-between align-items-center">
        <div>
          <a 
            href="https://github.com/nsychev/balloons-reborn" 
            target="_blank" 
            rel="noopener noreferrer"
            className="text-muted text-decoration-none"
          >
            Open source
          </a>
        </div>
        
        {infoHolder.info?.login && (
          <div className="text-muted">
            {infoHolder.info.login}{' '}
            <button 
              onClick={handleLogout}
              className="btn btn-link text-muted p-0 text-decoration-none"
            >
              Выйти
            </button>
          </div>
        )}
      </div>
    </footer>
  );
};

export default Footer; 