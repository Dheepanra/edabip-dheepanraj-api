-- EDABIP | Users module | seed data (15 users, mix of roles/status; 4 created in Oct 2026)
INSERT INTO users (user_id, name, email, role, status, last_login, created_at) VALUES
('USR001', 'Alice Johnson',  'alice@example.com',   'Administrator',    'Active',   '2025-05-12 10:30:00', '2025-01-10 09:00:00'),
('USR002', 'Bob Williams',   'bob@example.com',     'User',             'Active',   '2025-05-14 11:25:00', '2025-01-15 09:30:00'),
('USR003', 'Charlie Brown',  'charlie@example.com', 'Editor',           'Active',   '2025-05-18 11:20:00', '2025-02-02 10:00:00'),
('USR004', 'Diana Smith',    'diana@example.com',   'Viewer',           'Active',   '2025-06-27 10:30:00', '2025-02-20 14:15:00'),
('USR005', 'Ethan King',     'ethan@example.com',   'Business Analyst', 'Inactive', '2025-06-28 14:14:00', '2025-03-05 08:45:00'),
('USR006', 'Fiona Clark',    'fiona@example.com',   'User',             'Active',   '2025-05-12 10:30:00', '2025-03-18 11:00:00'),
('USR007', 'George Miller',  'george@example.com',  'Editor',           'Inactive', '2025-06-28 14:14:00', '2025-04-01 16:20:00'),
('USR008', 'Hannah Davis',   'hannah@example.com',  'Viewer',           'Active',   '2025-05-18 11:20:00', '2025-04-22 12:10:00'),
('USR009', 'Ian Wilson',     'ian@example.com',     'User',             'Active',   '2025-06-27 10:30:00', '2025-06-11 09:05:00'),
('USR010', 'Julia Moore',    'julia@example.com',   'Business Analyst', 'Active',   '2025-05-25 10:30:00', '2025-07-07 13:40:00'),
('USR011', 'Kevin Taylor',   'kevin@example.com',   'User',             'Inactive', NULL,                  '2026-08-14 10:00:00'),
('USR012', 'Laura Anderson', 'laura@example.com',   'Administrator',    'Active',   '2026-10-06 09:12:00', '2026-10-01 09:00:00'),
('USR013', 'Mohan Raj',      'mohan@example.com',   'Editor',           'Active',   '2026-10-07 15:45:00', '2026-10-02 10:30:00'),
('USR014', 'Nisha Patel',    'nisha@example.com',   'Viewer',           'Active',   NULL,                  '2026-10-05 11:15:00'),
('USR015', 'Omar Sheikh',    'omar@example.com',    'User',             'Inactive', NULL,                  '2026-10-07 08:20:00');
