import { Link } from 'react-router-dom';
import { InfoHolder } from '../types';

const Navbar = ({ infoHolder }: { infoHolder: InfoHolder }) => {
  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light">
      <div className="container">
        <Link className="navbar-brand" to="/">
          Шарики
        </Link>
        
        <div className="navbar-nav ms-auto">
          {!infoHolder.info.login ? (
            <>
              <Link className="nav-link" to="/login">
                Вход
              </Link>
              <Link className="nav-link" to="/register">
                Регистрация
              </Link>
            </>
          ) : (
            <>
              {infoHolder.info.canAccess && (
                <Link className="nav-link" to="/queue">
                  Очередь
                </Link>
              )}
              {infoHolder.info.canManage && (
                <Link className="nav-link" to="/volunteers">
                  Волонтеры
                </Link>
              )}
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
