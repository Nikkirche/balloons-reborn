import { useMemo } from 'react';
import { Balloon, Problem } from '../types';
import ProblemBox from './ProblemBox';

const BalloonRow = ({ balloon, problem, actions }: {
  balloon: Balloon;
  problem: Problem;
  actions: (balloon: Balloon) => React.ReactNode;
}) => {
  const actionContent = actions(balloon);
  
  const content = useMemo(() => (
      <div className="balloon-row">
        <ProblemBox problem={problem}/>
        {balloon.isFTS ? <span className="fts">★</span> : <span></span>}
        <span className="team-hall">{balloon.team.hall == null ? "??" : balloon.team.hall}</span>
        <span className="team-place">{balloon.team.place == null ? "??" : balloon.team.place}</span>
        <div className="actions">{actionContent}</div>
        <span className="team-name">{balloon.team.fullName}</span>
      </div>
  ), [balloon, problem, actionContent]);

  return content;
};

const BalloonList = ({ title, balloons, problems, actions }: {
  title: string;
  balloons: Balloon[];
  problems: Problem[];
  actions: (balloon: Balloon) => React.ReactNode;
}) => {
  if (balloons.length === 0) {
    return null;
  }

  const problemsMap = useMemo(() => {
    return problems.reduce((acc, problem) => {
      acc[problem.id] = problem;
      return acc;
    }, {} as Record<string, Problem>);
  }, [problems]);

  return <>
    <h2>{title} ({balloons.length})</h2>
    <div className="balloon-list">
      {balloons.map(balloon => {
        return (
          <BalloonRow
            key={balloon.runId}
            balloon={balloon}
            problem={problemsMap[balloon.problemId]}
            actions={actions}
          />
        );
      })}
    </div>
  </>;
};

export default BalloonList;
