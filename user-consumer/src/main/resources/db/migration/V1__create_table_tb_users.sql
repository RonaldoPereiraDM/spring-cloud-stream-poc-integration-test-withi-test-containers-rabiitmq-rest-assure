CREATE TABLE tb_users(
    user_id UUID NOT NULL PRIMARY KEY,
    email CHARACTER VARYING(50),
    full_name CHARACTER VARYING(150),
    image_url CHARACTER VARYING(255),
    phone_number CHARACTER VARYING(255),
    user_name CHARACTER VARYING(50),
    user_status VARCHAR(10) NOT NULL CHECK (user_status IN ('ACTIVE', 'BLOCKED')),
    user_type VARCHAR(10) NOT NULL CHECK (user_type IN ('ADMIN', 'USER', 'STUDENT', 'INSTRUCTOR'))
);