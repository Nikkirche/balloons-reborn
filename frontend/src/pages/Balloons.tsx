import { Navigate } from 'react-router-dom';
import { InfoHolder } from '../types';
import { GlobalError } from '../components/GlobalError';
import { useEffect, useMemo, useState } from 'react';
import { State, WebSocketMessage, Problem, Balloon } from '../types';
import backendUrls from '../util/backendUrls';
import ProblemList from '../components/ProblemList';
import BalloonList from '../components/BalloonList';

const BalloonsView = ({ infoHolder }: { infoHolder: InfoHolder }) => {
  const [problems, setProblems] = useState<Problem[]>([]);
  const [balloons, setBalloons] = useState<Balloon[]>([]);
  const [ws, setWs] = useState<WebSocket | null>(null);

  useEffect(() => {
    const websocket = new WebSocket(
      `${window.location.protocol === 'https:' ? 'wss:' : 'ws:'}//${window.location.host}${backendUrls.eventStream()}`
    );
    setWs(websocket);

    websocket.onopen = () => {
      websocket.send(`${infoHolder.token}`);
    };

    websocket.onmessage = (event) => {
      const message: WebSocketMessage = JSON.parse(event.data);
      
      if ('type' in message) {
        switch (message.type) {
          case 'problemsUpdated':
            setProblems(message.problems);
            break;
          case 'balloonUpdated':
            setBalloons(prev => {
              const index = prev.findIndex(b => b.runId === message.balloon.runId);
              if (index >= 0) {
                return [...prev.slice(0, index), message.balloon, ...prev.slice(index + 1)];
              }
              return [...prev, message.balloon];
            });
            break;
          case 'balloonDeleted':
            setBalloons(prev => prev.filter(b => b.runId !== message.runId));
            break;
        }
      } else {
        // Handle State
        const state = message as State;
        setProblems(state.problems);
        setBalloons(state.balloons);
      }
    };

    return () => {
      websocket.close();
    };
  }, []);

  const myBalloons = useMemo(() => {
    return balloons.filter(balloon => balloon.takenBy === infoHolder.info.login && !balloon.delivered);
  }, [balloons, infoHolder.info.login]);
  const queuedBalloons = useMemo(() => {
    return balloons.filter(balloon => balloon.takenBy === null && !balloon.delivered);
  }, [balloons]);
  const takenBalloons = useMemo(() => {
    return balloons.filter(balloon => balloon.takenBy !== null && !balloon.delivered);
  }, [balloons]);
  const deliveredBalloons = useMemo(() => {
    return balloons.filter(balloon => balloon.delivered);
  }, [balloons]);

  return (
    <main className="balloons-main">
      <h2 className="sr-only">Шарики</h2>
      <div className="contest-name"><strong>{infoHolder.info.contestName}</strong></div>
      <ProblemList problems={problems} balloons={balloons} />
      <BalloonList title="Вы несёте" balloons={myBalloons} problems={problems}
        actions={(balloon) => <>
          <a onClick={() => ws?.send(JSON.stringify({ type: "deliverBalloon", runId: balloon.runId }))}>Готово</a>
          <a onClick={() => ws?.send(JSON.stringify({ type: "dropBalloon", runId: balloon.runId }))}>Отказаться</a>
        </>} />
      <BalloonList title="Можно нести" balloons={queuedBalloons} problems={problems}
        actions={(balloon) => <a onClick={() => ws?.send(JSON.stringify({ type: "takeBalloon", runId: balloon.runId }))}>Взять</a>} />
      <BalloonList title="В пути" balloons={takenBalloons} problems={problems}
        actions={(balloon) => <span>Несёт {balloon.takenBy}</span>} />
      <BalloonList title="Доставлены" balloons={deliveredBalloons} problems={problems}
        actions={(balloon) => balloon.takenBy !== null ? <span>Доставлен {balloon.takenBy}</span> : <></>} />
    </main>
  );
};

const Balloons = ({ infoHolder }: { infoHolder: InfoHolder }) => {
  if (!infoHolder.info.login) {
    return <Navigate to="/login" />;
  }

  if (!infoHolder.info.canAccess) {
    return <GlobalError title="Forbidden" message="Ask organizer to give you access." />;
  }

  return <BalloonsView infoHolder={infoHolder} />
};

export default Balloons;
