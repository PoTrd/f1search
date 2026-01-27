CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS web_domains (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    domain_url TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS data (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    domain_id UUID NOT NULL REFERENCES web_domains(id),
    url TEXT NOT NULL,
    html_content TEXT,
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

CREATE TABLE IF NOT EXISTS robots_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    domain_id UUID NOT NULL UNIQUE REFERENCES web_domains(id),
    ua_f1search_allowed BOOLEAN,
    is_sitemap BOOLEAN,
    disallowed_paths TEXT,
    allowed_paths TEXT,
    sitemap_link TEXT
);
