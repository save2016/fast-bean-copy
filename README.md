# Fast Bean Copy

一个基于注解处理器（APT）的高性能 Java Bean 拷贝工具，能够在编译期生成类型安全、高性能且无依赖的 Bean 映射代码。

## 特性

- **编译期类型安全**：映射器在编译期生成，类型错误会被及时发现
- **高性能**：使用普通方法调用而不是反射，性能与手写代码相当
- **易于使用**：只需在目标 DTO 类上添加 `@CopyTarget` 注解
- **零运行时依赖**：生成的代码不需要 Fast Bean Copy 库运行时支持
- **自动批量方法生成**：自动生成 List、Set、Map 的批量转换方法
- **灵活配置**：支持字段级别、类级别、包级别和配置文件四级配置

## 构建输出

项目已编译生成以下 JAR 包，位于 `build/lib/` 目录：

- **fast-bean-copy-annotations-1.3.1.jar** - 注解定义模块（运行时依赖）
- **fast-bean-copy-processor-1.3.1.jar** - 注解处理器模块（编译时依赖）

## 快速开始

### 1. 导入依赖

将 JAR 文件复制到你的项目中，或添加到构建系统：

**Maven:**
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

### 2. 创建实体和 DTO 类

```java
// 实体类
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    // getter/setter...
}

// DTO 类，添加 @CopyTarget 注解
@CopyTarget(source = User.class, ignore = {"password"})
public class UserDto {
    private Long id;
    private String name;
    private String email;
    // getter/setter...
}
```

### 3. 编译

使用 Maven 或 Gradle 编译项目。Fast Bean Copy 会自动生成 `UserDtoCopier` 类。

### 4. 使用生成的 Copier

```java
User user = new User(1L, "John", "john@example.com", "secret");

// 使用静态方法
UserDto dto = UserDtoCopier.toDto(user);

// 批量转换
List<UserDto> dtos = UserDtoCopier.toDtoList(users);

// 更新现有对象
UserDtoCopier.updateDto(existingDto, user);
```

## 核心注解

### @CopyTarget

在目标 DTO 类上使用，标记需要生成 Copier 的类。

```java
@CopyTarget(
    source = User.class,           // 源类型（必填）
    ignore = {"password", "salt"}, // 忽略的字段
    componentModel = ComponentModel.SPRING, // 依赖注入模式
    uses = {StringToListConverter.class}    // 自定义转换器
)
public class UserDto {
    // ...
}
```

### @CopyField

自定义单个字段的映射。

```java
@CopyTarget(source = User.class)
public class UserDto {
    @CopyField(source = "name", target = "fullName")
    private String fullName;
    
    @CopyField(converter = EmailConverter.class, format = "lowercase")
    private String email;
    
    @CopyField(defaultValue = "UNKNOWN")
    private String status;
}
```

### @CopyTargetConfig

包级别配置，在 `package-info.java` 中使用。

```java
// 在 package-info.java 中
@CopyTargetConfig(
    componentModel = ComponentModel.SPRING,
    nullValueStrategy = NullValueStrategy.IGNORE
)
package com.example.dto;

import com.github.jackieonway.copier.annotation.*;
```

## 类型转换

### 基本类型自动转换

Fast Bean Copy 自动处理基本类型和包装类型之间的转换：

```java
@CopyTarget(source = Source.class)
public class Target {
    private Integer age;      // int -> Integer（自动装箱）
    private int count;        // Integer -> int（自动拆箱，null -> 0）
}
```

### 自定义 TypeConverter

实现 `TypeConverter<S, T>` 接口进行自定义转换：

```java
public class StringToListConverter implements TypeConverter<String, List<String>> {
    @Override
    public List<String> convert(String source, String format) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(source.split(format != null ? format : ","));
    }
}

@CopyTarget(source = User.class, uses = {StringToListConverter.class})
public class UserDto {
    @CopyField(source = "tags", converter = StringToListConverter.class, format = ",")
    private List<String> tagList;
}
```

## 集合支持

自动处理 List、Set、Map 的深拷贝：

```java
@CopyTarget(source = User.class)
public class UserDto {
    private List<String> tags;              // 浅拷贝
    private List<AddressDto> addresses;     // 深拷贝
    private Map<String, String> metadata;   // Key/Value 浅拷贝
    private Map<String, AddressDto> data;   // Key 浅拷贝，Value 深拷贝
}
```

自动生成批量转换方法：

```java
// List
List<UserDto> dtos = UserDtoCopier.toDtoList(users);
Set<UserDto> dtos = UserDtoCopier.toDtoSet(users);
Map<K, UserDto> dtos = UserDtoCopier.toDtoMap(userMap);
```

## 组件模型

支持多种依赖注入框架：

```java
@CopyTarget(source = User.class, componentModel = ComponentModel.SPRING)
public class UserDto { }

// 或

@CopyTarget(source = User.class, componentModel = ComponentModel.CDI)
public class UserDto { }

// 或

@CopyTarget(source = User.class, componentModel = ComponentModel.JSR330)
public class UserDto { }
```

## 项目结构

```
fast-bean-copy/
├── fast-bean-copy-annotations/      # 注解模块（运行时）
│   └── src/main/java/com/github/jackieonway/copier/annotation/
│       ├── CopyTarget.java
│       ├── CopyField.java
│       ├── CopyTargetConfig.java
│       ├── TypeConverter.java
│       ├── ComponentModel.java
│       └── NullValueStrategy.java
│
├── fast-bean-copy-processor/        # 处理器模块（编译时）
│   ├── src/main/java/com/github/jackieonway/copier/processor/
│   │   ├── CopyTargetProcessor.java   # APT 处理器
│   │   └── CopierGenerator.java       # 代码生成器
│   ├── src/main/resources/META-INF/services/
│   │   └── javax.annotation.processing.Processor  # SPI 配置
│   └── src/test/java/                # 测试用例和示例
│
├── build/lib/                         # 编译生成的 JAR 包
│   ├── fast-bean-copy-annotations-1.3.1.jar
│   └── fast-bean-copy-processor-1.3.1.jar
│
└── README.md                          # 本文件
```

## 性能

Fast Bean Copy 生成的代码性能与手写代码相当：

| 场景 | 性能 |
|------|------|
| 单对象转换 | 0.04 μs/次 |
| List 批量转换 | 0.10 ms/1000项 |
| Set 批量转换 | 0.21 ms/1000项 |
| Map 批量转换 | 0.32 ms/1000项 |
| 嵌套对象转换 | 0.60 μs/次 |

## IDE 配置

### IntelliJ IDEA

1. `File` -> `Settings` -> `Build, Execution, Deployment` -> `Compiler` -> `Annotation Processors`
2. 勾选 `Enable annotation processing`
3. 确保 `Generated sources directory` 设置为 `target/generated-sources/annotations`

### Eclipse

1. 右键项目 -> `Properties` -> `Java Compiler` -> `Annotation Processing`
2. 勾选 `Enable annotation processing`
3. 设置生成源代码目录

## 生成代码示例

Fast Bean Copy 为 `UserDto` 生成的 `UserDtoCopier` 类包含：

```java
public final class UserDtoCopier {
    // 单个对象转换
    public static UserDto toDto(User source)
    public static User fromDto(UserDto source)
    
    // 带自定义处理
    public static UserDto toDto(User source, UnaryOperator<UserDto> customizer)
    public static User fromDto(UserDto source, UnaryOperator<User> customizer)
    
    // List 批量转换
    public static List<UserDto> toDtoList(List<User> sources)
    public static List<User> fromDtoList(List<UserDto> sources)
    
    // Set 批量转换
    public static Set<UserDto> toDtoSet(Set<User> sources)
    public static Set<User> fromDtoSet(Set<UserDto> sources)
    
    // Map 批量转换
    public static <K> Map<K, UserDto> toDtoMap(Map<K, User> sources)
    public static <K> Map<K, User> fromDtoMap(Map<K, UserDto> sources)
    
    // 更新现有对象
    public static void updateDto(UserDto target, User source)
    public static void updateEntity(User target, UserDto source)
}
```

## 故障排除

### 生成的 Copier 类找不到

**原因：** 注解处理器未被正确配置或执行

**解决方案：**
1. 确保 `fast-bean-copy-processor` 已添加到依赖中（scope=provided）
2. 确保 IDE 启用了注解处理
3. 执行 `mvn clean compile` 重新编译

### 字段映射错误

**原因：** 源类和目标类的字段类型不兼容或字段名不匹配

**解决方案：**
1. 检查字段名称（区分大小写）
2. 检查字段类型是否兼容
3. 如需自定义映射，使用 `@CopyField` 注解

### 集合深拷贝不工作

**原因：** 集合元素类型需要 `@CopyTarget` 注解

**解决方案：**
在嵌套对象类上添加 `@CopyTarget` 注解，或检查 getter/setter 方法是否正确

## 最佳实践

1. **优先使用包级别配置** - 如果整个包使用相同配置，使用 `@CopyTargetConfig`
2. **合理使用 ignore** - 只忽略真正不需要的字段
3. **使用 beforeMapping 进行验证** - 在映射前进行必要的验证
4. **为有状态转换器添加组件注解** - 有状态转换器应纳入容器管理
5. **无状态转换器无需组件注解** - Fast Bean Copy 会自动优化

## 许可证

MIT License

## 联系方式

有问题或建议？欢迎提交 Issue 或 PR。

---

**注意：** 此项目是 Fast Bean Copy 的完整实现，基于文档规范版本 1.3.1。

