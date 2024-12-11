CREATE TABLE IF NOT EXISTS notifications (
    id                  SERIAL PRIMARY KEY,
    message             TEXT                    NOT NULL,
    message_type        TEXT,
    error               TEXT,
    user_uid            VARCHAR(36),
    notification_status VARCHAR(32),
    trigger_code        VARCHAR(128),
    object_type         VARCHAR(255),
    object_id           VARCHAR(36),
    subject             VARCHAR(128),
    created_by          VARCHAR(255),
    has_confirm_otp     BOOLEAN   DEFAULT FALSE,
    expiration_date     TIMESTAMP               NOT NULL,
    created_at          TIMESTAMP DEFAULT NOW() NOT NULL,
    modified_at         TIMESTAMP,
    CONSTRAINT notification_user_uid_status_type_trigger_code_idx
        UNIQUE (object_id, notification_status, message_type, trigger_code)
);