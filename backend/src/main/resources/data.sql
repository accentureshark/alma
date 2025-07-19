-- Insert test users (H2 doesn't support schema in the same way)
INSERT INTO "user" (id, email, password, administrador) VALUES 
('11111111-1111-1111-1111-111111111111', 'admin@alma.com', 'admin123', true);

INSERT INTO "user" (id, email, password, administrador) VALUES 
('22222222-2222-2222-2222-222222222222', 'user@alma.com', 'user123', false);

INSERT INTO "user" (id, email, password, administrador) VALUES 
('33333333-3333-3333-3333-333333333333', 'test@alma.com', 'test123', false);