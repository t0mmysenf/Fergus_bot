ALTER TABLE SONG ADD COLUMN RADIONAME VARCHAR(100) AFTER URL;

UPDATE SONG SET RADIONAME = 'teatime';
COMMIT;