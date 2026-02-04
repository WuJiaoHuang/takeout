Redis存储的时key-value结构的数据，其中key是字符串类型，value有5种常见的数据类型
字符串 string

SET key value 设置指定key的值
GET key 获取指定key的值
SETEX key seconds value 设置指定key的值，并将key的过期时间设为seconds秒
SETNX key value 只有在key不存在时设置key的值



哈希 hash


HSET key field value 将哈希表key中的字段field的值设为value
HSET key field 获取存储在哈希表中指定字段的值
HDEL key field 删除存储在哈希表中的指定字段
HKEYS key 获取哈希表中所有字段
HVALS key 获取哈希表中所有值




列表 list 按照插入顺序怕排序，可以有重复元素，类似于java的LinkedList

LPUSH key value1 [value2] 将1个或多个值插入到列表头部
LRANGE key start stop 获取列表指定范围内的元素
RPOP 移除并获取指定列表最后一个元素
LLEN key 获取列表长度





集合 set 无序集合，没有重复元素，类似于java的HashSet


SADD key member1 [member2] 向集合添加一个或多个成员
SMEMBERS keys 返回集合中的所有成员
SCARD key 获取集合的成员数
SINTER key1 [key2] 返回给定所有集合的交集
SUNION key1 [key2] 返回所有给定集合的并集
SREM key member1 [member2] 删除集合中一个或多个成员







有序集合 sorted set/zset 集合中每个元素关联一个分数score,根据分数升序怕IXU，没有重复元素


ZADD key score1 member1 [score2 member2] 向有序集合添加一个或多个成员
ZRANGE key start stop [WITHSCORES] 通过索引区间返回有序集合中指定区间内的成员
ZINCRBY key increment member 有序集合中对指定成员的分数加上增量increment
ZREM key member [member...] 移除有序集合中的一个或多个成员




通用命令 不分数据类型的，都可以使用的命令
KEYS pattern 查找所有符合给定模式(pattern)的key
EXISTS  key 检查给定key是否存在
TYPE key 返回key所存储的值的类型
DEL key 当key存在时删除key




Spring Data Redis使用方式
1. 导入Spring Data Redis的maven
2. 配置Reis数据源
3. 编写配置类，创建RedisTemplate对象
4. 通过RedisTemplate对象操作Redis


