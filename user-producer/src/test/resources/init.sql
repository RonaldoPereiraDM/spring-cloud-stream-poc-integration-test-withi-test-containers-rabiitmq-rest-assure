CREATE SCHEMA IF NOT EXISTS "consumer-db";
--sem essa configuração abaixo o flyway nao conseguira executar no squema correto.
ALTER DATABASE "consumer-db" SET search_path TO "consumer-db";