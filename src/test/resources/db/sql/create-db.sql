drop table account if exists;
create table account (
  user_id bigint,
  amount int,
  currency VARCHAR(10),
  primary key(user_id, currency)
);