package arche.cloud.netty.ratelimit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class BucketFactory {
  private static Cache<String, Bucket> bucketCache;

  static {
    bucketCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();
  }


  private static Bucket newBucket(int rateLimit) {
    long overdraft = (long) (rateLimit * 1.5);
    Refill refill = Refill.greedy(rateLimit, Duration.ofSeconds(1));
    Bandwidth limit = Bandwidth.classic(overdraft, refill);
    return Bucket4j.builder().addLimit(limit).build();
  }

  public static Bucket getBucket(String path, int rateLimit) {

    Bucket bucket = bucketCache.getIfPresent(path);
    if (bucket != null){
      return bucket;
    }
    bucket = newBucket(rateLimit);
    bucketCache.put(path, bucket);
    return bucket;
  }
}
