create TABLE IF NOT EXISTS users
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    email
    VARCHAR
(
    512
) NOT NULL,
    name VARCHAR
(
    512
) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY
(
    id
),
    CONSTRAINT UQ_USER_EMAIL UNIQUE
(
    email
)

    );

create TABLE IF NOT EXISTS items
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    description
    VARCHAR
(
    1024
) NOT NULL,
    name VARCHAR NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    owner_id BIGINT,
    CONSTRAINT pk_item PRIMARY KEY
(
    id
),
    CONSTRAINT FK_ITEM_OWNER FOREIGN KEY
(
    owner_id
) REFERENCES users
(
    id
));

create TABLE IF NOT EXISTS bookings
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    booking_start
    TIMESTAMP
    NOT
    NULL,
    booking_end
    TIMESTAMP
    NOT
    NULL,
    booking_status
    VARCHAR
    NOT
    NULL,
    owner_id
    BIGINT,
    item_id
    BIGINT,
    CONSTRAINT
    pk_booking
    PRIMARY
    KEY
(
    id
),
    CONSTRAINT FK_BOOKING_OWNER FOREIGN KEY
(
    owner_id
) REFERENCES users
(
    id
),
    CONSTRAINT FK_BOOKING_ITEM FOREIGN KEY
(
    item_id
) REFERENCES items
(
    id
));