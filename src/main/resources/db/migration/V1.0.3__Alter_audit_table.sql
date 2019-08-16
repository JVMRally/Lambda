CREATE SEQUENCE audit_serial AS integer START 1 OWNED BY audit.id;

ALTER TABLE audit ALTER COLUMN id SET DEFAULT nextval('audit_serial');