docker run --rm -d \
    -e REDIS_PASSWORD=zhwell \
    -p 8020:6379 \
    --name redis \
    redis:alpine \
    sh -c 'exec redis-server --requirepass "$REDIS_PASSWORD"'
