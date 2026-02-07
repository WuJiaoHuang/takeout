Spring Catche是一个框架，实现了基于注解的缓存功能，只需要简单加一个注解，就能实现缓存功能

Spring Cache提供了一层抽象，第层可以切换不同的缓存实现
例如EHCache
Caffeine
Redis


@EnableCatching 开启注解功能，通常加在启动类上
@Cacheable 在方法执行前先查询缓存中是否有数据，如果有数据，则直接返回缓存数据，如果没有缓存数据，调用方法并将方法返回值放放到缓存中
@CachePut 将方法的返回值放到缓存中
@CacheEvict 将一条或多条数据从缓存中删除


