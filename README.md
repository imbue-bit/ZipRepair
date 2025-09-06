# ZipRepair Pro for macOS

在 macOS 上运行的 ZipRepair 修复工具。使用 Java Swing 编写。

![demo](https://github.com/imbue-bit/ZipRepair/blob/main/demo.png?raw=true)

## ✨ 功能特性

-   图形化操作界面：无需记忆复杂的命令行参数，所有操作通过点击按钮即可完成。
-   单个文件修复：快速选择一个损坏的 ZIP 压缩包进行修复。
-   批量文件修复：选择一个包含多个 ZIP 文件的文件夹，程序会自动批量处理所有文件。
-   后台处理：修复过程在后台线程中执行，不会导致界面冻结。
-   实时进度与日志：在批量处理模式下，提供进度条和详细的日志输出，让您随时了解修复状态。
-   原生 macOS 观感：采用系统默认的 Look and Feel，与 macOS 环境融为一体。
-   开源免费：完全免费使用，并欢迎开发者贡献代码。

## 🖥️ 系统要求

-   **操作系统**：macOS (因为程序依赖于 macOS 内置的 `zip` 命令行工具)。
-   **运行环境**：Java Development Kit (JDK) 或 Java Runtime Environment (JRE) 8 或更高版本。

## 🚀 快速开始

### 1. 下载

您可以从项目的 [Releases 页面](https://github.com/imbue-bit/ziprepair/releases)下载最新的可执行 `.jar` 文件。
### 2. 运行程序

下载 `ZipRepairPro.jar` 文件后，您可以通过以下两种方式运行它：

-   **双击运行**：
  直接在 Finder 中双击 `ZipRepairPro.jar` 文件。如果系统安全设置阻止了该操作，请右键点击文件，选择“打开”，然后确认即可。

-   **通过终端运行**：
  打开“终端” (Terminal) 应用，导航到文件所在的目录，然后执行以下命令：
  ```bash
  java -jar ZipRepairPro.jar
  ```

## 📖 使用指南

### 单个文件修复

1.  在左侧导航栏选择 **"Repair Zip File"**。
2.  点击第一个 **"Browse"** 按钮，选择您需要修复的损坏的 ZIP 文件。
3.  程序会自动为您生成一个修复后的文件名（通常是 `原文件名_fixed.zip`）。您也可以点击第二个 **"Browse"** 按钮自定义保存路径和文件名。
4.  点击右下角的 **"Repair"** 按钮开始修复。
5.  修复完成后会弹出提示框。

### 批量文件修复

1.  在左侧导航栏选择 **"Batch Zip Repair"**。
2.  点击 **"Source folder"** 旁边的 **"Browse"** 按钮，选择包含多个 ZIP 文件的文件夹。
3.  程序会自动将该文件夹下的所有 `.zip` 文件加载到左侧的 "Files to Process" 列表中。
4.  程序会自动建议一个用于存放修复文件的目标文件夹（通常是 `源文件夹/repaired_files`）。您也可以点击 **"Target folder"** 旁边的 **"Browse"** 按钮来自定义。
5.  点击右下角的 **"Start Batch Repair"** 按钮开始批量处理。
6.  下方的进度条会显示总体进度，右侧的 "Log" 区域会输出每个文件的详细处理日志。
7.  全部处理完成后会弹出结果摘要。

## 🛠️ 从源码构建

如果您想从源代码自行构建，请按照以下步骤操作：

1.  **克隆仓库**:
    ```bash
    git clone https://github.com/imbue-bit/ziprepair.git
    cd ziprepair
    ```

2.  **编译**:
    使用您喜欢的 Java IDE (如 IntelliJ IDEA, Eclipse) 打开项目，或者使用命令行直接编译。
    ```bash
    javac src/ZipRepairProApp.java
    ```

3.  **打包成可执行 JAR**:
    ```bash
    cd src
    jar cfe ../ZipRepairPro.jar ZipRepairProApp *.class
    ```
    这条命令会创建一个名为 `ZipRepairPro.jar` 的可执行文件。

## 🤝 贡献

欢迎任何形式的贡献！如果您有好的建议或发现了 Bug，请随时提交 [Issue](https://github.com/imbue-bit/ziprepair/issues)。如果您想贡献代码，请 Fork 本仓库并提交 Pull Request。

这不是 Cotix AI 官方支持的产品。
