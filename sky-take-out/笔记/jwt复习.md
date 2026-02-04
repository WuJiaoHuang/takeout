### 生成token
```java
/*
 * 需要输入:
 * 1.密钥 secretKey
 * 2 有效时间 ttlMillls
 * 3 自定义数据 claims
 * 
 * */
String secretKey = "itcast";
long ttiMills = 7200000L;//2小时
Map<String,Object> c = new HashMap<>();


c.put("userId",123);

c.put("username","张三");

String token = JETUtil.createJWT(secretKey,ttlMills,claims);



```

### 解析token
```java
//输入: secretKey和token
String secretKey = "itcast";
String token = "cida(此处省略若干字)"

//调用函数
Claims claims = JwtUtil.parseJWT(secretKey,token);
//输出结果(可以获取所有当初存入的数据)
Integer userId = (Integer)claims.get("userId");




```

