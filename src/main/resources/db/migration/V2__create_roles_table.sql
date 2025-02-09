CREATE TABLE roles (
  id          UUID NOT NULL,
  codename    TEXT NOT NULL,
  created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT unique_codename UNIQUE(codename)
);