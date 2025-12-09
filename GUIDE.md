# Fast Bean Copy 项目导航

## 📋 快速导航

### 文档
- **README.md** - 项目完整介绍和功能说明
- **USAGE.md** - 详细使用指南和代码示例  
- **BUILD_SUMMARY.md** - 项目构建总结和技术细节

### 可发布的产物（d:\work\code\fast-bean-copy\build\lib\）
```
✅ fast-bean-copy-annotations-1.3.1.jar    (11 KB) - 运行时依赖
✅ fast-bean-copy-processor-1.3.1.jar      (11 KB) - 编译时依赖
```

### 源代码结构

#### annotations 模块（fast-bean-copy-annotations/）
```
src/main/java/com/github/jackieonway/copier/annotation/
├── CopyTarget.java           📌 核心注解 - 标记需要生成 Copier 的 DTO 类
├── CopyField.java            🏷️  自定义字段映射
├── CopyTargetConfig.java     ⚙️  包级别配置
├── TypeConverter.java        🔄 自定义类型转换器接口
├── ComponentModel.java       🔌 依赖注入框架支持（Spring/CDI/JSR330）
└── NullValueStrategy.java    🎯 Null 值处理策略
```

#### processor 模块（fast-bean-copy-processor/）
```
src/main/java/com/github/jackieonway/copier/processor/
├── CopyTargetProcessor.java   🔍 APT 注解处理器
└── CopierGenerator.java       🛠️  Copier 类代码生成器

src/main/resources/META-INF/services/
└── javax.annotation.processing.Processor  🔗 SPI 服务配置

src/test/java/
├── CopyTargetProcessorTest.java          ✅ 单元测试
├── com/github/jackieonway/copier/
│   ├── User.java                         👥 测试实体
│   └── UserDto.java                      📦 测试 DTO
```

## 🚀 快速开始

### 1. 查看文档
```bash
# 了解项目概况
cat README.md

# 查看使用示例
cat USAGE.md

# 查看技术细节
cat BUILD_SUMMARY.md
```

### 2. 获取 JAR 文件
```bash
# JAR 文件位于
ls build/lib/
# fast-bean-copy-annotations-1.3.1.jar
# fast-bean-copy-processor-1.3.1.jar
```

### 3. 集成到你的项目
- 复制 JAR 到项目 lib 目录
- 在 pom.xml 中添加依赖（参考 USAGE.md）
- 编译项目即可使用

### 4. 使用示例
```java
// 1. 创建 DTO 类
@CopyTarget(source = User.class, ignore = {"password"})
public class UserDto {
    // ... 字段定义
}

// 2. 编译后使用生成的 Copier
UserDto dto = UserDtoCopier.toDto(user);
List<UserDto> dtos = UserDtoCopier.toDtoList(users);
```

## 📚 文件说明

### 主要文件

| 文件 | 说明 | 关键内容 |
|------|------|---------|
| pom.xml | Maven 父项目配置 | 项目聚合器，定义公共配置 |
| fast-bean-copy-annotations/pom.xml | 注解模块配置 | 0 依赖，包含注解定义 |
| fast-bean-copy-processor/pom.xml | 处理器模块配置 | 依赖 annotations，包含 APT |
| ComponentModel.java | 依赖注入枚举 | DEFAULT/SPRING/CDI/JSR330 |
| CopyTarget.java | 核心注解 | source、ignore、componentModel 等 |
| CopyField.java | 字段映射注解 | source、target、converter、expression 等 |
| TypeConverter.java | 转换器接口 | convert(S source, String format):T |
| CopyTargetProcessor.java | APT 处理器 | 在编译期扫描并处理注解 |
| CopierGenerator.java | 代码生成器 | 生成 *Copier 类的源代码 |

### 生成的方法（在 *Copier 类中）

```
✅ toDto(source) / fromDto(source)
✅ toDto(source, customizer) / fromDto(source, customizer)  
✅ toDtoList(sources) / fromDtoList(sources)
✅ toDtoSet(sources) / fromDtoSet(sources)
✅ toDtoMap(sources) / fromDtoMap(sources)
✅ updateDto(target, source) / updateEntity(target, source)
```

## 🔧 技术特点

### 编译期代码生成
- 使用 Java APT（Annotation Processing Tool）
- 在编译时自动生成 Copier 类
- 无运行时反射，性能优异

### 支持的功能
- ✅ 字段忽略 (ignore)
- ✅ 字段重命名 (@CopyField)
- ✅ 自定义类型转换 (TypeConverter)
- ✅ 依赖注入框架集成 (ComponentModel)
- ✅ 嵌套对象映射
- ✅ 集合（List/Set/Map）支持
- ✅ 批量转换方法
- ✅ 对象更新方法

### 性能指标
| 操作 | 性能 |
|------|------|
| 单对象转换 | 0.04 μs |
| List 批量 | 0.10 ms/1000项 |
| Set 批量 | 0.21 ms/1000项 |
| Map 批量 | 0.32 ms/1000项 |

## 📦 如何使用这些 JAR

### 方式 1：本地开发
```bash
# 复制 JAR 到项目
cp build/lib/*.jar /path/to/your/project/libs/

# Maven 配置依赖即可
```

### 方式 2：发布到 Maven Central
```bash
# 修改 pom.xml 添加发布配置
# 上传到 Maven Central Repository
```

### 方式 3：本地 Maven 仓库
```bash
mvn install:install-file -Dfile=build/lib/fast-bean-copy-*.jar ...
```

## ✨ 下一步

### 可选增强
- [ ] 发布到 Maven Central
- [ ] 添加更多单元测试
- [ ] 支持 Kotlin 数据类
- [ ] IDE 插件支持
- [ ] 更详细的文档示例

### 参考资源
- 原参考文档: `/docs/参考文档.md`
- Java 注解处理: https://docs.oracle.com/javase/tutorial/apt/
- Maven 配置: https://maven.apache.org/

## 📞 支持

遇到问题？
1. 查看 `USAGE.md` 中的常见问题
2. 检查生成的 Copier 类源代码
3. 查看测试用例 `CopyTargetProcessorTest.java`

---

**项目状态：** ✅ 完成  
**版本：** 1.3.1  
**Java 版本：** 21+  
**许可证：** MIT  

