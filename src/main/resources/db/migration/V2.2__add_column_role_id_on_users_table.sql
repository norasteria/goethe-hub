ALTER TABLE users
  ADD COLUMN role_id UUID,
  ADD FOREIGN KEY (role_id) REFERENCES roles(id);