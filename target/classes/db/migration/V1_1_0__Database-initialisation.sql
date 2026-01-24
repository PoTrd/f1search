CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS data (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    url TEXT NOT NULL,
    html_content TEXT,
    title TEXT,
    metadata TEXT,
    link_list TEXT,
    score DOUBLE PRECISION,
    state TEXT
);

CREATE TABLE IF NOT EXISTS queue (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    url TEXT NOT NULL,
    state TEXT
);
