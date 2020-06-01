package boot.imvc.servlet.cache;

/**
 * Support for pluggable caches.
 * @author liudong
 */
public interface ICacheProvider {

	/**
	 * Configure the cache
	 *
	 * @param regionName the name of the cache region
	 * @param autoCreate autoCreate settings
	 * @throws CacheException
	 */
	public ICache buildCache(String regionName, boolean autoCreate) throws CacheException;

	/**
	 * Callback to perform any necessary initialization of the underlying cache implementation
	 * during SessionFactory construction.
	 *
	 * @param properties current configuration settings.
	 */
	public void start() throws CacheException;

	/**
	 * Callback to perform any necessary cleanup of the underlying cache implementation
	 * during SessionFactory.close().
	 */
	public void stop();
	
	/**
	 * 清除所有缓存
	 */
	public void cleanAll();
}
