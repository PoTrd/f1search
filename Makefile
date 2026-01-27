DB_NAME ?= f1search_db
DB_USER ?= root
DB_PASSWORD ?= root
DB_CONTAINER ?= db

PSQL_DOCKER := docker exec -e PGPASSWORD=$(DB_PASSWORD) $(DB_CONTAINER) psql -U $(DB_USER) -d $(DB_NAME)

.PHONY: db-shell db-query

db-shell:
	@docker exec -it -e PGPASSWORD=$(DB_PASSWORD) $(DB_CONTAINER) psql -U $(DB_USER) -d $(DB_NAME)
