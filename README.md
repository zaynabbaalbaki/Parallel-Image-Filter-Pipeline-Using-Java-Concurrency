# Parallel-Image-Filter-Pipeline-Using-Java-Concurrency
This project aims to demonstrate a parallelized image filtering pipeline in Java using the Fork/Join framework. By applying convolution-based filters in parallel, we achieve significant speed-up while maintaining correctness.
A Java Swing application for applying image filters using both **sequential** and **parallel (multithreaded)** processing. Includes a GUI interface for loading images and selecting filters, as well as **benchmarking tools** that generate a CSV of performance metrics (speedup, efficiency) by thread count.

---

## 🎯 Features

- ✅ Load and view any `.jpg`, `.jpeg`, or `.png` image
- 🎛️ Apply various filters:
  - Blur (Box filter)
  - Gaussian Blur
  - Sharpen
  - Edge Detection (Sobel)
  - Emboss
- ⚙️ Parallel filtering using `ForkJoinPool`
- ⏱ Measures time for sequential and parallel filtering
- 📊 Exports performance data to `benchmark.csv`
- 📈 Displays calculated **Speed-up** and **Efficiency**
- 🖥 GUI built with Java Swing

---

## 🖼️ Example

<img src="C:\Users\User\Downloads\Screenshot 2025-06-09 153831.png" width="1000"/>

---

## 📂 Project Structure
ImageFilterProject/
├── src/
│ └── main/
│ └── java/
│ ├── ImageFilterApp.java
│ ├── SequentialFilter.java
│ ├── ParallelFilter.java
│ ├── ImageUtils.java
│ └── ImageViewer.java
├── input.jpg
├── benchmark.csv # Generated after filter run
├── build.gradle
├── settings.gradle
└── README.md

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java 17+
- Gradle 8.5+  
  _OR use the Gradle wrapper included:_

```bash
./gradlew build
./gradlew run
