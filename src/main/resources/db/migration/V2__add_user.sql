INSERT INTO users (username, email, password, role, enabled)
VALUES ('admin', 'admin@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', TRUE)
    ON CONFLICT (email) DO NOTHING;