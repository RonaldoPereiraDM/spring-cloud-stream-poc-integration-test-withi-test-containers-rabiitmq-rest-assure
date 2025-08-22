CREATE TABLE tb_users(
    user_id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    creation_date TIMESTAMP,
    email CHARACTER VARYING(50),
    full_name CHARACTER VARYING(150),
    image_url CHARACTER VARYING(255),
    last_update_date TIMESTAMP,
    password CHARACTER VARYING(255),
    phone_number CHARACTER VARYING(255),
    user_name CHARACTER VARYING(50),
    user_status VARCHAR(10) NOT NULL CHECK (user_status IN ('ACTIVE', 'BLOCKED')),
    user_type VARCHAR(10) NOT NULL CHECK (user_type IN ('ADMIN', 'USER', 'STUDENT', 'INSTRUCTOR'))
);