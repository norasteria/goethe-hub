CREATE TABLE users (
  id            UUID,
  created_at    TIMESTAMP WITH TIME ZONE   NOT NULL,
  updated_at    TIMESTAMP WITH TIME ZONE   NOT NULL,
  full_name     TEXT                       NOT NULL,
  email         TEXT                       NOT NULL,
  password_hash TEXT                       NOT NULL,
  is_active     BOOLEAN                    NOT NULL,
  reset_token   TEXT,
  reset_token_expiry TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT unique_email UNIQUE(email)
);