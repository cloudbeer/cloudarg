docker run --rm -d \
    -p 8020:6379 \
    --name redis \
    redis:alpine \
    redis redis-server --requirepass "zhwell" 
