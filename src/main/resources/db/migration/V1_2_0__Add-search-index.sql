-- 1) Delete the old "score" column from data
ALTER TABLE data
DROP COLUMN IF EXISTS score;

-- 2) Terms table (vocabulary)
CREATE TABLE IF NOT EXISTS terms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    term TEXT NOT NULL UNIQUE
    );

-- 3) Inverted index table (term -> resource)
CREATE TABLE IF NOT EXISTS term_index (
    term_id UUID NOT NULL REFERENCES terms(id) ON DELETE CASCADE,
    resource_id UUID NOT NULL REFERENCES data(id) ON DELETE CASCADE,
    occurrences INT NOT NULL,
    score DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (term_id, resource_id)
    );

-- 4) Indexes for performance
CREATE INDEX IF NOT EXISTS idx_terms_term ON terms(term);
CREATE INDEX IF NOT EXISTS idx_term_index_term ON term_index(term_id);
CREATE INDEX IF NOT EXISTS idx_term_index_resource ON term_index(resource_id);
CREATE INDEX IF NOT EXISTS idx_term_index_term_score ON term_index(term_id, score DESC);

