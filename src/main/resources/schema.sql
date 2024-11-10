create table if not exists balloon (
    id bigserial not null primary key,
    problem_id varchar(255) not null,
    team_id varchar(255) not null,
    volunteer_id bigint references volunteer (id),
    delivered boolean not null default false
);