#!/bin/bash
cassandra -f &

#TODO: fix infinite waiting here
echo "Waiting for Cassandra to start..."
until cqlsh -u cassandra -p cassandra -e "SELECT release_version FROM system.local" > /dev/null 2>&1; do
  echo "Cassandra is unavailable - sleeping"
  sleep 2
done
echo "Cassandra is available. Executing init.cql..."

# Script doesnt work for now, just enter this manually until i figure out why
cqlsh -f /init_files/init.cql

echo "Initialization complete."

wait