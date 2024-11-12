import { useEffect, useState, useCallback } from 'react'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { Info, InfoHolder } from './types'
import backendUrls from './util/backendUrls'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import Login from './pages/Login'
import Register from './pages/Register'
import Main from './pages/Main'
import Volunteers from './pages/Volunteers'
import Balloons from './pages/Balloons'
import { GlobalError } from './components/GlobalError'

function App() {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'));
  const [info, setInfo] = useState<Info>({ contestName: 'Unknown', status: 'loading' });

  const setTokenWithStorage = useCallback((newToken: string | null) => {
    if (newToken) {
      localStorage.setItem('token', newToken);
    } else {
      localStorage.removeItem('token');
    }
    setToken(newToken);
  }, []);

  const fetchInfo = useCallback(async () => {
    try {
      const response = await fetch(backendUrls.getInfo(), {
        headers: token ? {
          Authorization: `Bearer ${token}`
        } : undefined
      });
      const data = await response.json() as Info;
      setInfo({ ...data, status: 'success' })
    } catch (exc) {
      console.error('Error fetching info:', exc)
      setInfo(prevInfo => ({ ...prevInfo, status: 'error' }))
    }
  }, [token]);

  useEffect(() => {
    void fetchInfo();
  }, [fetchInfo]);

  const infoHolder: InfoHolder = { token, setToken: setTokenWithStorage, info, fetchInfo };

  if (info.status === 'loading') {
    return (
      <div className="global-error">
        <h1>Loading...</h1>
        <p>Please wait while we load the application.</p>
      </div>
    );
  }

  return (
    <BrowserRouter>
      <Navbar infoHolder={infoHolder} />
      <Routes>
        <Route path="/" element={<Main infoHolder={infoHolder} />} />
        <Route path="/balloons" element={<Balloons infoHolder={infoHolder} />} />
        <Route path="/login" element={<Login infoHolder={infoHolder} />} />
        <Route path="/register" element={<Register infoHolder={infoHolder} />} />
        <Route path="/volunteers" element={<Volunteers infoHolder={infoHolder} />} />
        <Route path="*" element={
          <GlobalError title="Not Found" message="The page you&apos;re looking for does not exist." />
        } />
      </Routes>
      <Footer infoHolder={infoHolder} />
    </BrowserRouter>
  )
}

export default App
