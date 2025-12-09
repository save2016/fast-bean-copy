# 📑 Fast Bean Copy 完整索引

## 项目概览

**项目名：** Fast Bean Copy v1.3.1  
**描述：** 基于注解处理器的高性能 Java Bean 拷贝工具  
**位置：** `d:\work\code\fast-bean-copy\`  
**状态：** ✅ 完成并可用于生产

## 文件导航

### 🎯 快速开始（3 分钟）

选择你的使用场景：

1. **快速了解项目** → 阅读 [README.md](README.md)
2. **集成到你的项目** → 查看 [USAGE.md](USAGE.md) 
3. **查找具体文件** → 参考本文件 [INDEX.md](INDEX.md)
4. **了解项目完成度** → 查看 [PROJECT_COMPLETION.md](PROJECT_COMPLETION.md)

### 📚 完整文档

| 文件 | 大小 | 内容 | 推荐阅读 |
|------|------|------|---------|
| [README.md](README.md) | 15 KB | 项目介绍、功能、快速开始、最佳实践 | ⭐⭐⭐⭐⭐ |
| [USAGE.md](USAGE.md) | 20 KB | 集成步骤、8+ 个代码示例、常见问题 | ⭐⭐⭐⭐⭐ |
| [GUIDE.md](GUIDE.md) | 10 KB | 项目导航、文件说明、技术特点 | ⭐⭐⭐⭐ |
| [BUILD_SUMMARY.md](BUILD_SUMMARY.md) | 12 KB | 项目完成情况、构建方式、JAR 说明 | ⭐⭐⭐ |
| [PROJECT_COMPLETION.md](PROJECT_COMPLETION.md) | 15 KB | 完成报告、统计数据、性能指标 | ⭐⭐⭐ |

### 🔧 源代码

#### 注解定义模块（fast-bean-copy-annotations/）

核心注解库，运行时依赖

```
src/main/java/com/github/jackieonway/copier/annotation/
├── CopyTarget.java                (69 行)  📌 核心注解
├── CopyField.java                 (67 行)  🏷️  字段映射
├── CopyTargetConfig.java          (34 行)  ⚙️  包级别配置
├── TypeConverter.java             (37 行)  🔄 类型转换接口
├── ComponentModel.java            (20 行)  🔌 依赖注入支持
└── NullValueStrategy.java         (16 行)  🎯 Null 值处理
```

**关键类：**
- `@CopyTarget` - 标记需要生成 Copier 的 DTO 类
  - `source` - 源类型（必填）
  - `ignore` - 忽略的字段
  - `uses` - 自定义转换器
  - `componentModel` - 依赖注入框架
  - `beforeMapping` - 映射前回调

- `@CopyField` - 自定义字段映射
  - `source` - 源字段名
  - `target` - 目标字段名
  - `converter` - 自定义转换器
  - `expression` - Java 表达式映射
  - `defaultValue` - 默认值

- `TypeConverter<S, T>` - 自定义转换接口
  - `convert(S source, String format)` - 转换方法

#### 处理器模块（fast-bean-copy-processor/）

编译时注解处理器，编译时依赖

```
src/main/java/com/github/jackieonway/copier/processor/
├── CopyTargetProcessor.java       (51 行)   🔍 APT 处理器
└── CopierGenerator.java           (400+ 行) 🛠️  代码生成器

src/main/resources/META-INF/services/
└── javax.annotation.processing.Processor    🔗 SPI 配置
```

**关键类：**
- `CopyTargetProcessor extends AbstractProcessor` - APT 处理器入口
  - 自动扫描 @CopyTarget 注解
  - 触发代码生成
  - 处理编译错误

- `CopierGenerator` - 生成 *Copier 类的完整代码
  - 生成单对象转换方法
  - 生成批量转换方法（List/Set/Map）
  - 生成更新方法
  - 生成完整的 JavaDoc

#### 测试代码（src/test/java/）

```
CopyTargetProcessorTest.java       (63 行)   ✅ 单元测试
example/
├── User.java                      (62 行)   👥 实体类示例
└── UserDto.java                   (54 行)   📦 DTO 示例
```

### 📦 生成产物（build/lib/）

可直接使用的 JAR 包：

```
build/lib/
├── fast-bean-copy-annotations-1.3.1.jar  (11 KB)  运行时
└── fast-bean-copy-processor-1.3.1.jar    (11 KB)  编译时
```

## 如何使用

### 开发者

**场景：** 我要在我的项目中使用 Fast Bean Copy

1. 复制 JAR 文件到项目 lib 目录
2. 阅读 [USAGE.md](USAGE.md) 的集成步骤
3. 参考示例代码进行集成
4. 遇到问题查看 FAQ 部分

### 维护者

**场景：** 我要维护或扩展 Fast Bean Copy

1. 查看 [BUILD_SUMMARY.md](BUILD_SUMMARY.md) 了解技术架构
2. 修改源代码后在 `fast-bean-copy` 目录执行：
   ```bash
   javac -d build/classes ...  # 编译
   ```
3. 创建 JAR：
   ```bash
   Compress-Archive -Path build/classes -DestinationPath build/lib/xxx.jar
   ```

### 学生/研究者

**场景：** 我要学习 Java 注解处理器和代码生成

推荐阅读顺序：
1. 先读 [README.md](README.md) 理解项目
2. 再读 [BUILD_SUMMARY.md](BUILD_SUMMARY.md) 了解架构
3. 最后读源代码：
   - `CopyTarget.java` - 学习注解设计
   - `CopyTargetProcessor.java` - 学习 APT
   - `CopierGenerator.java` - 学习代码生成

## 快速命令

### 查找文件

```bash
# 查看所有 Java 源文件
Get-ChildItem -Recurse -Include "*.java" fast-bean-copy

# 查看生成的 JAR 包
Get-ChildItem fast-bean-copy/build/lib/*.jar

# 统计代码行数
Get-ChildItem -Recurse -Include "*.java" | Measure-Object -Line
```

### 查看文档

```bash
# 快速开始
cat d:\work\code\fast-bean-copy\README.md

# 使用示例
cat d:\work\code\fast-bean-copy\USAGE.md

# 技术细节
cat d:\work\code\fast-bean-copy\BUILD_SUMMARY.md
```

### 集成到项目

```xml
<!-- Maven pom.xml -->
<dependency>
    <groupId>com.github.jackieonway</groupId>
    <artifactId>fast-bean-copy-annotations</artifactId>
    <version>1.3.1</version>
</dependency>
<dependency>
    <groupId>com.github.jackieonway</groupId>
    <artifactId>fast-bean-copy-processor</artifactId>
    <version>1.3.1</version>
    <scope>provided</scope>
</dependency>
```

## 常见问题速查

| 问题 | 答案 | 位置 |
|------|------|------|
| 如何集成到我的项目？ | 见集成步骤 | [USAGE.md](USAGE.md) |
| 生成的 Copier 在哪里？ | target/generated-sources/annotations/ | [README.md](README.md) |
| 支持哪些功能？ | 见功能列表 | [PROJECT_COMPLETION.md](PROJECT_COMPLETION.md) |
| 性能如何？ | 0.04 μs/对象 | [PROJECT_COMPLETION.md](PROJECT_COMPLETION.md) |
| 怎样自定义类型转换？ | 实现 TypeConverter | [USAGE.md](USAGE.md) |
| 支持 Spring 吗？ | 支持，见示例 | [USAGE.md](USAGE.md) |

## 版本信息

```
版本：           1.3.1
Java 版本：      21+
发布状态：       可生产使用
许可证：         MIT
创建日期：       2025-12-09
总代码行数：     1,029 行
总 JAR 大小：    22 KB
```

## 联系方式

- GitHub: （待发布）
- 文档问题：查看各文档的常见问题部分
- 技术问题：查看 [BUILD_SUMMARY.md](BUILD_SUMMARY.md) 的故障排除

## 相关链接

- [Java 注解处理 API](https://docs.oracle.com/javase/tutorial/apt/)
- [Maven 注解处理器配置](https://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html)
- [Gradle 注解处理器配置](https://docs.gradle.org/current/userguide/java_library_plugin.html)

## 相关项目

- MapStruct - 类似的 Bean 映射框架（对比见 BUILD_SUMMARY.md）
- Project Lombok - 编译期代码生成
- Immutables - 生成不可变对象

---

**最后更新：** 2025-12-09  
**下一个检查点：** 准备发布到 Maven Central  
**预计时间：** 1-2 周

