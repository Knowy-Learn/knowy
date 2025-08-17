#!/usr/bin/env bash

for sql_file in sql/*; do
	psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f "$sql_file"
done
