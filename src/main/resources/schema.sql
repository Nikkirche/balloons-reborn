create table if not exists volunteer (
    id bigserial not null primary key,
    login varchar(255) not null unique,
    password_hash varchar(255) not null,
    can_access boolean not null default false,
    can_manage boolean not null default false
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
insert (id, login, password_hash, can_access, can_manage)
values (1, 'test', '$2a$12$C0W8h4eyi/JoocJ7ZUiwzuwJMtzbtmvUXv9U.1Uj88Yp5xe5Y6zVm', true, true);
-- password is (who could predict?) also "test"