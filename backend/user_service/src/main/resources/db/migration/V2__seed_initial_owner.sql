INSERT INTO users (id, username, password, role, is_active, created_at, updated_at)
VALUES (
           'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'::uuid,
           'owner',
           '$2a$10$uYCWtjrIoipRwZvk/7h4K.NtFJ1ct07wa16VqWq4F8z019NwJV6de', -- "ownerselaludihati"
           'OWNER',
           true,
           NOW(),
           NOW()
       ) ON CONFLICT (username) DO NOTHING;

INSERT INTO user_profiles (id, full_name, phone_number, user_id)
VALUES (
           gen_random_uuid(),
           'Owner Utama',
           '081234567890',
           'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'::uuid
       ) ON CONFLICT DO NOTHING;