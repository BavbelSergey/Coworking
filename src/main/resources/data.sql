-- Insert test users
INSERT INTO users (name, email, phone, password, role) VALUES
('Admin User', 'admin@example.com', '+79161234567', '$2a$10$S1keqHz0E8cX2fV5DV5/pOaMrEkJuoFlkqUwq9NQxqfLvJWnbY7/K', 'ADMIN'),
('Test User', 'user@example.com', '+79169876543', '$2a$10$S1keqHz0E8cX2fV5DV5/pOaMrEkJuoFlkqUwq9NQxqfLvJWnbY7/K', 'USER')
ON CONFLICT DO NOTHING;

-- Insert workspaces
INSERT INTO workspaces (name, phone_number, capacity, price_per_hour) VALUES
('Meeting Room A', '+79161234567', 4, 500.00),
('Conference Room B', '+79161234567', 10, 1000.00),
('Open Space C', '+79161234567', 20, 1500.00),
('Private Office D', '+79161234567', 2, 300.00),
('Event Space E', '+79161234567', 50, 2500.00)
ON CONFLICT DO NOTHING;

-- Insert amenities
INSERT INTO amenities (name, description) VALUES
('Wi-Fi', 'High-speed internet connection'),
('Coffee Machine', 'Free coffee service'),
('Parking', 'Free parking available'),
('Projector', 'Conference projector'),
('Whiteboard', 'Interactive whiteboard'),
('Air Conditioning', 'Climate control'),
('Reception Desk', 'Professional reception'),
('Kitchen', 'Fully equipped kitchen')
ON CONFLICT DO NOTHING;

-- Link amenities to workspaces
INSERT INTO workspace_amenities (workspace_id, amenity_id) VALUES
(1, 1), (1, 2), (1, 4), (1, 5),
(2, 1), (2, 2), (2, 4), (2, 5), (2, 6),
(3, 1), (3, 2), (3, 6), (3, 7),
(4, 1), (4, 4),
(5, 1), (5, 2), (5, 3), (5, 4), (5, 6), (5, 8)
ON CONFLICT DO NOTHING;

