CREATE TABLE users
(
    id BIGSERIAL PRIMARY KEY,

    username VARCHAR(50) NOT NULL UNIQUE,

    email VARCHAR(255) NOT NULL UNIQUE,

    password VARCHAR(255) NOT NULL,

    role VARCHAR(20) NOT NULL DEFAULT 'USER',

    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rooms
(
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(100) NOT NULL UNIQUE,

    capacity INTEGER NOT NULL CHECK (capacity > 0),

    description TEXT,

    has_projector BOOLEAN DEFAULT FALSE,

    has_whiteboard BOOLEAN DEFAULT FALSE,

    location VARCHAR(255),

    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE bookings
(
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    room_id BIGINT NOT NULL,

    title VARCHAR(200) NOT NULL,

    start_time TIMESTAMP NOT NULL,

    end_time TIMESTAMP NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_booking_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_booking_room
        FOREIGN KEY (room_id)
            REFERENCES rooms(id)
            ON DELETE CASCADE
);

CREATE TABLE notifications
(
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    booking_id BIGINT,

    type VARCHAR(50) NOT NULL,

    message TEXT NOT NULL,

    is_read BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_notification_booking
        FOREIGN KEY (booking_id)
            REFERENCES bookings(id)
            ON DELETE SET NULL
);