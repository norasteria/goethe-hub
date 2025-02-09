CREATE TABLE login_activities (
  id          UUID                       NOT NULL,
  user_id     UUID,
  created_at  TIMESTAMP WITH TIME ZONE   NOT NULL,
  updated_at  TIMESTAMP WITH TIME ZONE   NOT NULL,
  status      TEXT                       NOT NULL,
  device_type TEXT                      NOT NULL,
  ip_address  TEXT                      NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);