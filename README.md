# f1search
F1-focused crawler and search API built with Spring Boot.

``` shell
    make db-shell
```

``` sql
    ALTER TABLE crawl_queue
    ALTER COLUMN id
    SET DEFAULT gen_random_uuid();
    
    INSERT INTO crawl_queue (state, url)
    VALUES ('PENDING', 'https://www.espn.com/f1/')
    RETURNING id;
```
