CREATE TABLE trainer (
  id          SERIAL PRIMARY KEY,
  access_code VARCHAR(50) NOT NULL UNIQUE,
  name        VARCHAR(100) NOT NULL,
  email       VARCHAR(100)
);

CREATE TABLE app_user (
  id    SERIAL PRIMARY KEY,
  phone VARCHAR(20) NOT NULL UNIQUE,
  name  VARCHAR(100) NOT NULL
);

CREATE TABLE session (
  id         SERIAL PRIMARY KEY,
  trainer_id INTEGER NOT NULL REFERENCES trainer(id) ON DELETE CASCADE,
  start_time TIMESTAMP WITH TIME ZONE NOT NULL,
  duration   INTEGER NOT NULL 
);

CREATE TYPE reservation_status AS ENUM ('ACTIVE', 'CANCELED');

CREATE TABLE reservation (
  id          SERIAL PRIMARY KEY,
  session_id  INTEGER NOT NULL REFERENCES session(id) ON DELETE CASCADE,
  user_id     INTEGER NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
  status      reservation_status NOT NULL DEFAULT 'ACTIVE',
  created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
