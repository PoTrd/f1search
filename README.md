# f1search
F1-focused crawler and search API built with Spring Boot.

``` shell
    make db-shell
```

Example of inserting URLs into the `crawl_queue` table to start the crawling process:
``` sql
    ALTER TABLE crawl_queue
    ALTER COLUMN id
    SET DEFAULT gen_random_uuid();
    
    INSERT INTO crawl_queue (state, url)
    VALUES ('PENDING', 'https://www.skysports.com/f1/news')
    RETURNING id;
    
    INSERT INTO crawl_queue (state, url)
    VALUES ('PENDING', 'https://www.the-race.com/category/formula-1/')
    RETURNING id;
    
    INSERT INTO crawl_queue (state, url)
    VALUES ('PENDING', 'https://motorsport.com/f1/news/')
    RETURNING id;
```
