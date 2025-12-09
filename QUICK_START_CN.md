# Fast Bean Copy - 快速参考指南（中文版）

## 📌 项目概览

**Fast Bean Copy** 是一个高性能、类型安全的 Java Bean 拷贝工具，基于注解处理器实现。

### 核心特性
- ✅ 基于注解的编译时代码生成
- ✅ 零运行时依赖
- ✅ 类型安全
- ✅ 高性能
- ✅ Java 1.8+ 兼容

## 🔧 最近修改

### JDK 版本
- **原版本**: Java 21
- **当前版本**: Java 1.8+
- **向下兼容**: 支持 Java 1.8 及更高版本

### 文档语言
- **原语言**: 英文
- **当前语言**: 中文
- **覆盖范围**: 所有源代码注释、JavaDoc 文档

## 📂 项目结构

```
fast-bean-copy/
├── fast-bean-copy-annotations/        # 注解模块
│   └── src/main/java/com/github/.../annotation/
│       ├── ComponentModel.java          # DI 框架模型
│       ├── NullValueStrategy.java       # 空值处理策略
│       ├── CopyTarget.java              # 核心注解
│       ├── CopyField.java               # 字段映射注解
│       ├── CopyTargetConfig.java        # 包级配置注解
│       └── TypeConverter.java           # 自定义转换器接口
│
├── fast-bean-copy-processor/          # 处理器模块
│   ├── src/main/java/.../processor/
│   │   ├── CopyTargetProcessor.java     # 注解处理器
│   │   └── CopierGenerator.java         # 代码生成器
│   │
│   └── src/test/java/.../
│       ├── CopyTargetProcessorTest.java # 测试用例
│       └── example/
│           ├── User.java                # 示例实体
│           └── UserDto.java             # 示例 DTO
│
├── build/lib/                           # 构建输出
│   ├── fast-bean-copy-annotations-1.3.1.jar
│   └── fast-bean-copy-processor-1.3.1.jar
│
├── pom.xml                              # Maven 配置
├── README.md                            # 项目说明
├── GUIDE.md                             # 使用指南
├── CHANGES_SUMMARY.md                   # 修改摘要
└── COMPLETION_REPORT.md                 # 完成报告
```

## 🎯 核心概念

### 1. @CopyTarget 注解
标记一个类为拷贝目标，注解处理器在编译时生成拷贝器类。

```java
@CopyTarget(
    source = User.class,                // 源类
    ignore = {"password"},              // 忽略的字段
    componentModel = ComponentModel.DEFAULT,
    nullValueStrategy = NullValueStrategy.IGNORE
)
public class UserDto {
    // DTO 字段定义
}
```

### 2. @CopyField 注解
在字段级别指定自定义的映射规则。

```java
@CopyField(
    source = "fullName",               // 源字段名
    converter = StringToUpperConverter.class
)
private String name;
```

### 3. TypeConverter 接口
实现自定义类型转换逻辑。

```java
public class StringToListConverter implements TypeConverter<String, List<String>> {
    @Override
    public List<String> convert(String source, String format) {
        // 自定义转换逻辑
        return Arrays.asList(source.split(format != null ? format : ","));
    }
}
```

## 📖 快速开始

### 1. 添加依赖
在 pom.xml 中添加：
```xml
<dependency>
    <groupId>com.github.jackieonway</groupId>
    <artifactId>fast-bean-copy-annotations</artifactId>
    <version>1.3.1</version>
</dependency>
```

注解处理器会自动注册到编译时处理。

### 2. 标记目标类
```java
@CopyTarget(source = User.class)
public class UserDto {
    private Long id;
    private String name;
    private String email;
    
    // getter/setter...
}
```

### 3. 编译与使用
编译时，处理器会自动生成 `UserDtoCopier` 类：
```java
User user = new User(1L, "张三", "zhangsan@example.com");

// 使用生成的拷贝器
UserDto dto = UserDtoCopier.toUserDto(user);

// 批量拷贝
List<UserDto> dtos = UserDtoCopier.toUserDtoList(users);
```

## 🔌 支持的 DI 框架

通过 `ComponentModel` 枚举配置：

```java
@CopyTarget(
    source = User.class,
    componentModel = ComponentModel.SPRING  // Spring 依赖注入
)
public class UserDto {
    // 自动生成 @Component 注解的拷贝器
}
```

**支持的框架**:
- `DEFAULT`: 无依赖注入（默认）
- `SPRING`: Spring 框架
- `CDI`: CDI 框架
- `JSR330`: JSR-330 标准

## ⚙️ 空值处理

使用 `NullValueStrategy` 配置空值处理方式：

```java
@CopyTarget(
    source = User.class,
    nullValueStrategy = NullValueStrategy.REPLACE  // 替换空值
)
public class UserDto {
    // ...
}
```

**策略选项**:
- `IGNORE`: 忽略空值（默认）
- `REPLACE`: 替换为目标类型的默认值

## 📋 常见用法

### 场景 1: 简单对象拷贝
```java
@CopyTarget(source = User.class)
public class UserDto {
    private Long id;
    private String name;
    private String email;
}

// 使用
UserDto dto = UserDtoCopier.toUserDto(user);
```

### 场景 2: 字段映射
```java
@CopyTarget(source = User.class)
public class UserDto {
    @CopyField(source = "fullName")
    private String name;
}

// 自动将 user.fullName 映射到 dto.name
```

### 场景 3: 自定义转换
```java
@CopyTarget(source = User.class)
public class UserDto {
    @CopyField(converter = DateToStringConverter.class)
    private String createdDate;
}
```

### 场景 4: 批量拷贝
```java
List<User> users = Arrays.asList(...);

// 直接生成的批量方法
List<UserDto> dtos = UserDtoCopier.toUserDtoList(users);
List<UserDto> setDtos = UserDtoCopier.toUserDtoSet(userSet);
Map<String, UserDto> mapDtos = UserDtoCopier.toUserDtoMap(userMap);
```

## 🐛 故障排除

### 问题 1: 注解处理器未执行
**解决方案**: 确保编译器设置中启用了注解处理
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <!-- 确保编码设置 -->
        <encoding>UTF-8</encoding>
    </configuration>
</plugin>
```

### 问题 2: 找不到生成的拷贝器类
**解决方案**: 
1. 确保源文件使用了 @CopyTarget 注解
2. 清理构建目录并重新编译
3. 检查编译日志中的错误信息

## 📊 性能指标

- **编译时**: 代码在编译时生成，零运行时开销
- **执行速度**: 与手写拷贝代码性能相同
- **内存占用**: 无额外内存占用
- **依赖**: 仅需要注解模块，零运行时依赖

## 🔗 相关资源

- **GitHub 仓库**: https://github.com/jackieonway/fast-bean-copy
- **使用指南**: 见 GUIDE.md
- **完整 API**: 见 JavaDoc 注释（中文）
- **修改日志**: 见 CHANGES_SUMMARY.md

## ✅ 验证清单

在使用本项目前，请确认：
- [x] Java 环境版本 1.8 或更高
- [x] Maven 3.x 或更高
- [x] 项目正确配置了注解处理器路径
- [x] 所有源文件都是 UTF-8 编码

## 💡 最佳实践

1. **命名约定**: 
   - DTO 类名推荐以 `Dto` 或 `VO` 结尾
   - 生成的拷贝器类名为 `{TargetClassName}Copier`

2. **字段映射**:
   - 如果源字段名和目标字段名相同，无需 @CopyField
   - 不同的字段名使用 @CopyField 显式指定

3. **性能优化**:
   - 优先使用不可变对象
   - 对于大批量操作使用批量方法（List/Set/Map）

4. **错误处理**:
   - 检查编译日志中的处理器错误信息
   - 对于无法转换的类型，使用自定义 TypeConverter

---
**文档版本**: 1.0 中文版
**最后更新**: 2024-12-09
**项目版本**: 1.3.1

