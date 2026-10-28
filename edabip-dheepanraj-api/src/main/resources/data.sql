-- Seed data (idempotent). password_hash 'CHANGE_ME' is replaced with a BCrypt hash
-- of app.seed.password by DataInitializer on first start.
INSERT IGNORE INTO roles (id, name, description, status) VALUES
 (1, 'Administrator',    'Full access to all modules and settings',            'Active'),
 (2, 'Editor',           'Can create and modify data, reports and dashboards', 'Active'),
 (3, 'Viewer',           'Can view data, reports and dashboards',              'Active'),
 (4, 'Business Analyst', 'Can view and analyze reports',                       'Active'),
 (5, 'Auditor',          'Read-only access for audits',                        'Inactive');

INSERT IGNORE INTO permissions (id, module, action) VALUES
 (1, 'Dashboard', 'View'),  (2, 'Dashboard', 'Edit'),
 (3, 'Reports',   'View'),  (4, 'Reports',   'Edit'),
 (5, 'Billing',   'View'),  (6, 'Billing',   'Edit'),
 (7, 'Users',     'View'),  (8, 'Users',     'Manage'),
 (9, 'Settings',  'View'),  (10, 'Settings', 'Edit');

INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
 (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),
 (2,1),(2,2),(2,3),(2,4),
 (3,1),(3,3),
 (4,1),(4,3),(4,5),
 (5,1),(5,3),(5,7);

INSERT IGNORE INTO users (id, name, email, role_id, status, last_login, created_at, password_hash) VALUES
 (1,  'Alice Johnson',  'alice@example.com',  1, 'Active',   '2025-05-12 10:30:00', '2025-01-10 09:00:00', 'CHANGE_ME'),
 (2,  'Bob Williams',   'bob@example.com',    3, 'Active',   '2025-05-14 11:25:00', '2025-02-11 09:00:00', 'CHANGE_ME'),
 (3,  'Charlie Brown',  'charlie@example.com',2, 'Active',   '2025-05-18 11:20:00', '2025-03-12 09:00:00', 'CHANGE_ME'),
 (4,  'Diana Smith',    'diana@example.com',  4, 'Active',   '2025-06-27 10:30:00', '2025-04-13 09:00:00', 'CHANGE_ME'),
 (5,  'Ethan King',     'ethan@example.com',  3, 'Active',   '2025-06-28 14:14:00', '2025-05-14 09:00:00', 'CHANGE_ME'),
 (6,  'Fiona Clark',    'fiona@example.com',  3, 'Inactive', '2025-05-12 10:30:00', '2025-06-15 09:00:00', 'CHANGE_ME'),
 (7,  'George Miller',  'george@example.com', 2, 'Active',   '2025-06-28 14:14:00', '2026-10-02 09:00:00', 'CHANGE_ME'),
 (8,  'Hannah Davis',   'hannah@example.com', 4, 'Inactive', '2025-05-18 11:20:00', '2026-10-05 09:00:00', 'CHANGE_ME'),
 (9,  'Ian Wilson',     'ian@example.com',    3, 'Active',   '2025-06-27 10:30:00', '2026-10-08 09:00:00', 'CHANGE_ME'),
 (10, 'Julia Moore',    'julia@example.com',  3, 'Active',   '2025-05-25 10:30:00', '2025-07-20 09:00:00', 'CHANGE_ME');
