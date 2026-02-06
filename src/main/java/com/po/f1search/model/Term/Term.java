package com.po.f1search.model.Term;

import java.util.UUID;

public record Term(
        UUID id,
        String value,
        Long df
) {
    public Term(
            String value,
            Long df
    ) {
        this(null, value, df);
    }
}
