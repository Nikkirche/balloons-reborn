create table if not exists volunteer (
    id bigserial not null primary key,
    login varchar(255) not null unique,
    password_hash varchar(255) not null,
    admin boolean not null default false
);

create table if not exists balloon (
    id bigserial not null primary key,
    problem_id varchar(255) not null,
    team_id varchar(255) not null,
    volunteer_id bigint references volunteer (id),
    delivered boolean not null default false,
    unique (problem_id, team_id)
);

merge into volunteer
using (select 1 as id) as data
on volunteer.id = data.id
when not matched then
insert (id, login, password_hash, admin)
values (1, 'test', 'test', false);