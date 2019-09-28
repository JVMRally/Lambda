CREATE TABLE IF NOT EXISTS jeps (
    id integer PRIMARY KEY,
    title varchar(100) UNIQUE,
    jep_type varchar(20) NOT NULL,
    jep_status varchar(30) NOT NULL,
    release varchar(10) NOT NULL,
    component varchar(50) NOT NULL,
    jep_url varchar(50) NOT NULL
);