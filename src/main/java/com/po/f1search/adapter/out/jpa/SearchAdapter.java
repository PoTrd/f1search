package com.po.f1search.adapter.out.jpa;

import com.po.f1search.application.ports.out.SearchRepository;
import com.po.f1search.model.SearchResult.SearchResult;
import com.po.f1search.model.utils.Url;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class SearchAdapter implements SearchRepository {

    private final JdbcTemplate jdbcTemplate;

    public SearchAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<SearchResult> findResourceByTerms(List<String> lstTermsValues, Long offset, Long limit) {
        if (lstTermsValues == null || lstTermsValues.isEmpty()) {
            return Collections.emptyList();
        }

        String inSql = String.join(",", Collections.nCopies(lstTermsValues.size(), "?"));

        String sql = String.format("""
            SELECT
                d.url,
                d.title,
                d.description,
                SUM(ti.score) as total_score
            FROM data d
            JOIN term_index ti ON d.id = ti.resource_id
            JOIN terms t ON ti.term_id = t.id
            WHERE t.term IN (%s)
            GROUP BY d.id
            ORDER BY total_score DESC
            LIMIT ? OFFSET ?
        """, inSql);

        List<Object> argsSql = new ArrayList<>(lstTermsValues);
        argsSql.add(limit);
        argsSql.add(offset);

        return jdbcTemplate.query(sql, (rs, rowNum) -> new SearchResult(
                new Url(rs.getString("url")),
                rs.getString("title"),
                rs.getString("description"),
                rs.getDouble("total_score")
        ), argsSql.toArray());
    }
}
