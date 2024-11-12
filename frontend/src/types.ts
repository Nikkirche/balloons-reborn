export interface InfoHolder {
  token: string | null,
  setToken: (token: string | null) => void,
  info: Info,
  fetchInfo: () => Promise<void>
};

export interface Info {
  contestName: string
  canRegister?: boolean
  login?: string
  canAccess?: boolean
  canManage?: boolean,
  status: string
};

export interface Volunteer {
  id: number;
  login: string;
  canAccess: boolean;
  canManage: boolean;
};

export interface Problem {
  id: string;
  alias: string;
  name: string;
  color: string | null;
}

export interface Team {
  id: string;
  displayName: string;
  fullName: string;
  hall: string | null;
}

export interface Balloon {
  runId: string;
  isFTS: boolean;
  team: Team;
  problemId: string;
  time: number;
  takenBy: string | null;
  delivered: boolean;
}

export interface State {
  problems: Problem[];
  balloons: Balloon[];
}

export type Event =
  | { type: 'problemsUpdated', problems: Problem[] }
  | { type: 'balloonUpdated', balloon: Balloon }
  | { type: 'balloonDeleted', runId: string };

export type WebSocketMessage = Event | State;

export type Command =
  | { type: 'takeBalloon', runId: string }
  | { type: 'dropBalloon', runId: string }
  | { type: 'deliverBalloon', runId: string };