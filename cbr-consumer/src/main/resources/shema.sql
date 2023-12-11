CREATE TABLE IF NOT EXISTS currencies
(
    num_code BIGINT UNIQUE NOT NULL,
    charCode VARCHAR(255)  NOT NULL,
    name     VARCHAR(255)  NOT NULL,

    CONSTRAINT pk_currencies PRIMARY KEY (num_code)
);

CREATE TABLE IF NOT EXISTS rates
(
    num_code  BIGINT  NOT NULL,
    date      DATE    NOT NULL,
    nominal   BIGINT  NOT NULL,
    rate      DECIMAL NOT NULL,
    vunitRate DECIMAL NOT NULL,

    CONSTRAINT pk_rates PRIMARY KEY (num_code, date),

    CONSTRAINT fk_currencies_rates FOREIGN KEY (num_code) REFERENCES currencies (num_code) ON DELETE CASCADE
);