CREATE KEYSPACE IF NOT EXISTS spotify_clone
    WITH replication = {
        'class': 'SimpleStrategy',
        'replication_factor': 1
        };

USE spotify_clone;

-- Create a flag file to indicate initialization is complete
CREATE TABLE IF NOT EXISTS system_initialization (
                                                     id text PRIMARY KEY,
                                                     initialized boolean
);

INSERT INTO system_initialization (id, initialized) VALUES ('cassandra_init', true);