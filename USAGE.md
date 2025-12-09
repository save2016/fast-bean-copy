# Fast Bean Copy 使用指南

## 集成到你的项目

### 步骤 1：复制 JAR 文件

将生成的 JAR 文件放到你的项目中：
- `fast-bean-copy-annotations-1.3.1.jar` → 项目 lib 目录或本地 Maven 仓库
- `fast-bean-copy-processor-1.3.1.jar` → 项目 lib 目录或本地 Maven 仓库

### 步骤 2：配置项目依赖

**使用 Maven：**

```xml
<dependencies>
    <!-- 运行时依赖 -->
    <dependency>
        <groupId>com.github.jackieonway</groupId>
        <artifactId>fast-bean-copy-annotations</artifactId>
        <version>1.3.1</version>
    </dependency>
    
    <!-- 编译时依赖 -->
    <dependency>
        <groupId>com.github.jackieonway</groupId>
        <artifactId>fast-bean-copy-processor</artifactId>
        <version>1.3.1</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

**使用 Gradle：**

```gradle
dependencies {
    implementation 'com.github.jackieonway:fast-bean-copy-annotations:1.3.1'
    annotationProcessor 'com.github.jackieonway:fast-bean-copy-processor:1.3.1'
}
```

### 步骤 3：启用 IDE 注解处理（可选）

**IntelliJ IDEA:**
- File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
- 勾选 "Enable annotation processing"

**Eclipse:**
- 右键项目 → Properties → Java Compiler → Annotation Processing
- 勾选 "Enable annotation processing"

## 使用示例

### 基础用法

```java
import com.github.jackieonway.copier.annotation.CopyTarget;

// 定义实体类
public class User {
    private Long id;
    private String name;
    private String email;
    
    // getter/setter...
}

// 定义 DTO 类，使用 @CopyTarget 注解
@CopyTarget(source = User.class)
public class UserDto {
    private Long id;
    private String name;
    private String email;
    
    // getter/setter...
}

// 编译后使用（生成的 UserDtoCopier 类）
public class Main {
    public static void main(String[] args) {
        // 创建源对象
        User user = new User(1L, "John Doe", "john@example.com");
        
        // 转换为 DTO
        UserDto dto = UserDtoCopier.toDto(user);
        
        // 转换回实体
        User restored = UserDtoCopier.fromDto(dto);
        
        // 批量转换
        List<User> users = Arrays.asList(
            new User(1L, "User 1", "user1@example.com"),
            new User(2L, "User 2", "user2@example.com")
        );
        List<UserDto> dtos = UserDtoCopier.toDtoList(users);
    }
}
```

### 忽略字段

某些敏感字段（如密码）不需要复制：

```java
@CopyTarget(source = User.class, ignore = {"password", "salt"})
public class UserDto {
    private Long id;
    private String name;
    private String email;
    // password 和 salt 不会被映射
}
```

### 字段重命名

当源和目标字段名称不同时：

```java
import com.github.jackieonway.copier.annotation.CopyField;

@CopyTarget(source = Order.class)
public class OrderDto {
    @CopyField(source = "id", target = "orderId")
    private Long orderId;
    
    @CopyField(source = "customerName", target = "buyerName")
    private String buyerName;
}
```

### 嵌套对象映射

自动处理嵌套对象的映射：

```java
@CopyTarget(source = Address.class)
public class AddressDto {
    private String street;
    private String city;
}

@CopyTarget(source = User.class)
public class UserDto {
    private Long id;
    private String name;
    private AddressDto address;  // 自动调用 AddressDtoCopier
}
```

### 集合映射

自动生成 List、Set、Map 的批量转换方法：

```java
@CopyTarget(source = User.class)
public class UserDto {
    private List<String> tags;
    private List<AddressDto> addresses;
    private Set<Integer> scores;
    private Map<String, String> metadata;
}

// 使用
List<UserDto> dtos = UserDtoCopier.toDtoList(users);
Set<UserDto> dtoSet = UserDtoCopier.toDtoSet(userSet);
Map<Long, UserDto> dtoMap = UserDtoCopier.toDtoMap(userMap);
```

### 自定义类型转换

实现 TypeConverter 接口进行自定义转换：

```java
import com.github.jackieonway.copier.annotation.TypeConverter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeToStringConverter 
        implements TypeConverter<LocalDateTime, String> {
    
    @Override
    public String convert(LocalDateTime source, String format) {
        if (source == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            format != null ? format : "yyyy-MM-dd HH:mm:ss"
        );
        return source.format(formatter);
    }
}

@CopyTarget(source = Event.class, uses = {LocalDateTimeToStringConverter.class})
public class EventDto {
    private Long id;
    private String name;
    
    @CopyField(source = "createdAt", 
               converter = LocalDateTimeToStringConverter.class,
               format = "yyyy-MM-dd")
    private String createdDate;
}
```

### 使用 Spring 依赖注入

将生成的 Copier 作为 Spring Bean：

```java
@CopyTarget(source = User.class, 
            componentModel = ComponentModel.SPRING)
public class UserDto {
    private Long id;
    private String name;
    private String email;
}

@Service
public class UserService {
    @Autowired
    private UserDtoCopier userDtoCopier;
    
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id);
        return userDtoCopier.toDto(user);
    }
}
```

### 包级别配置

在 `package-info.java` 中进行包级别配置：

```java
// src/main/java/com/example/dto/package-info.java
@CopyTargetConfig(
    componentModel = ComponentModel.SPRING,
    nullValueStrategy = NullValueStrategy.IGNORE
)
package com.example.dto;

import com.github.jackieonway.copier.annotation.*;
```

所有该包中的 `@CopyTarget` 类都会继承这些配置。

### 更新现有对象

而不是创建新对象，可以更新现有对象：

```java
// 创建或获取目标对象
UserDto dto = new UserDto();
dto.setId(99L);

// 从源对象更新
User user = new User(1L, "John", "john@example.com");
UserDtoCopier.updateDto(dto, user);
// 现在 dto.id = 1L, dto.name = "John", dto.email = "john@example.com"
```

### 默认值和常量

设置默认值和常量：

```java
@CopyTarget(source = User.class)
public class UserDto {
    @CopyField(source = "status", defaultValue = "ACTIVE")
    private String status;
    
    @CopyField(constant = "SYSTEM")
    private String source;
}
```

### 表达式映射

使用 Java 表达式进行复杂映射：

```java
@CopyTarget(source = Order.class)
public class OrderDto {
    @CopyField(expression = "java(source.getItems().stream()" +
                           ".mapToDouble(Item::getPrice)" +
                           ".sum())")
    private double totalPrice;
    
    @CopyField(expression = "java(source.getId() + \"-\" + " +
                           "source.getOrderNo())")
    private String orderKey;
}
```

## 常见问题

### Q: 生成的 Copier 类在哪里？

A: 通常在 `target/generated-sources/annotations/` 目录（Maven）或 `build/generated/` 目录（Gradle）。可以在 IDE 中导航到生成的文件。

### Q: 如何处理复杂的字段映射？

A: 有多种方式：
1. 使用 `@CopyField(source = "fieldName")`
2. 使用自定义 TypeConverter
3. 使用表达式映射
4. 编译后手动处理

### Q: Copier 类支持链式调用吗？

A: 不直接支持，但可以结合 UnaryOperator：
```java
UserDto dto = UserDtoCopier.toDto(user, result -> {
    result.setAdditionalField("value");
    return result;
});
```

### Q: 性能如何？

A: 生成的代码性能与手写代码相当，比运行时反射框架快 100+ 倍。

### Q: 可以用于 Spring Boot 吗？

A: 完全支持！只需在 `@CopyTarget` 中指定 `componentModel = ComponentModel.SPRING`。

## 项目发布

将你的项目依赖这两个 JAR：

1. **快照开发版本：** 直接使用本地编译的 JAR
2. **稳定发布版本：** 上传到 Maven Central 或私有 Maven 仓库
3. **Gradle 项目：** 配置 `annotationProcessor` 依赖

## 进一步了解

- 查看 `/docs/参考文档.md` 了解详细的功能说明
- 查看 `/fast-bean-copy-processor/src/test/java/` 中的测试用例
- 在 IDE 中浏览生成的 `*Copier` 类源代码

---

**Happy Copying! 🚀**

