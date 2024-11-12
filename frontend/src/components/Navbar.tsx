import { Link, useLocation } from 'react-router-dom';
import { InfoHolder } from '../types';
import { HTMLProps } from 'react';

const Navbar = ({ infoHolder }: { infoHolder: InfoHolder }) => {
  const location = useLocation();

  function getLinkClass(path: string): HTMLProps<HTMLAnchorElement> {
    return location.pathname === path ? {className: 'active', 'aria-current': 'page'} : {};
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light">
      {!infoHolder.info.login ? (
        <>
          <Link {...getLinkClass('/login')} to="/login">
            Вход
          </Link>
          <Link {...getLinkClass('/register')} to="/register">
            Регистрация
          </Link>
        </>
      ) : (
        <>
          {infoHolder.info.canAccess && (
            <Link {...getLinkClass('/balloons')} to="/balloons">
              Очередь
            </Link>
          )}
          {infoHolder.info.canManage && (
            <Link {...getLinkClass('/volunteers')} to="/volunteers">
              Волонтеры
            </Link>
          )}
        </>
      )}
    </nav>
  );
};

export default Navbar;
