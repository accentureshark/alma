# Makefile

.PHONY: local up build down

local:
	docker compose up alma-db ollama

up:
	docker compose up

build:
	docker compose build

down:
	docker compose down