#!/bin/sh

# Start MinIO in background and capture its PID
minio server /data --console-address ":9001" &
MINIO_PID=$!

# Wait for MinIO to start
sleep 5

# Set alias and create access key
mc alias set local http://127.0.0.1:9000 $MINIO_ROOT_USER $MINIO_ROOT_PASSWORD
mc admin user svcacct add local $MINIO_ROOT_USER --access-key "$MINIO_ACCESS_KEY" --secret-key "$MINIO_SECRET_KEY" 2>/dev/null || true

# Wait for the MinIO process to finish
wait $MINIO_PID
