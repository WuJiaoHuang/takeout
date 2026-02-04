# Lombok @Builder 注解详解

## 一、基本概念
**Builder模式**：一种创建对象的设计模式，通过链式调用来设置属性。

## 二、使用方法

### 1. 添加注解
```java
@Data
@Builder  // 在类上添加此注解
public class User {
    private Long id;
    private String name;
    private Integer age;
}
```

### 2. 创建对象（链式调用）
```java
// 链式设置属性，最后.build()创建对象
User user = User.builder()
    .id(1L)
    .name("张三")
    .age(25)
    .build();
```

## 三、对比传统方式

| 方式 | 代码示例 | 特点 |
|------|----------|------|
| **Builder模式** | `User.builder().id(1L).name("张三").build()` | 1. 代码简洁<br>2. 可读性强<br>3. 顺序灵活 |
| **Setter方式** | `user.setId(1L); user.setName("张三");` | 1. 代码冗长<br>2. 对象可变 |
| **构造方法** | `new User(1L, "张三", 25)` | 1. 参数顺序固定<br>2. 参数多时难维护 |

## 四、核心特点

1. **链式调用**：每个设置方法都返回Builder对象
2. **.build()方法**：最终创建对象
3. **部分设置**：不需要设置所有属性
4. **顺序自由**：可以任意顺序设置属性

## 五、常用场景

1. **DTO/VO对象创建**：需要设置多个字段时
2. **配置对象**：参数较多的配置类
3. **测试数据**：快速创建测试对象

## 六、记忆口诀
> **"@Builder注解加类上，链式设置.build()放最后"**

**一句话总结**：用`.builder().属性().build()`三步曲替代繁琐的setter调用。