import { useMemo } from "react";
import { Problem, State } from "../types";
import ProblemBox from "./ProblemBox";

const ProblemBlock = ({ problem, solves }: { problem: Problem, solves: number }) => {
  return useMemo(() => <div title={problem.name}>
    <ProblemBox problem={problem} />
    <div className="problem-solves">
      {solves}
    </div>
  </div>, [problem, solves]);
};

const ProblemList = ({ problems, balloons }: State) => {
  return useMemo(() => (
      <div className="problem-list">
        {problems.map(problem => (
          <ProblemBlock key={problem.id} problem={problem} solves={balloons.filter(b => b.problemId === problem.id).length} />
        ))}
      </div>
  ), [problems, balloons]);
};

export default ProblemList;