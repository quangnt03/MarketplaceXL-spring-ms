ALTER SESSION SET CONTAINER = FREEPDB1;

DECLARE
  user_count INTEGER;
BEGIN
  SELECT COUNT(*) INTO user_count FROM all_users WHERE username = UPPER('marketplace_app');
  IF user_count = 0 THEN
    EXECUTE IMMEDIATE 'CREATE USER marketplace_app IDENTIFIED BY marketplace_app_password';
    EXECUTE IMMEDIATE 'GRANT CONNECT, RESOURCE TO marketplace_app';
    EXECUTE IMMEDIATE 'ALTER USER marketplace_app QUOTA UNLIMITED ON USERS';
  END IF;
END;
/
