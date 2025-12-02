# MathPlot – Documentation

### Advanced Software Engineering Exam  
**Student:** Nicole Tiokhin 
**Course:** Advanced Software Engineering  
**Deadline:** December 5th, 2025  

---

## 1 Introduction

MathPlot is a function plotter written in Java.  
The user enters a mathematical function and the software:
3
- Parses the input (AOS or RPN)
- Evaluates the function at any x
- Computes the first-order derivative
- Prints function & derivative in AOS and RPN format
- Plots both function and derivative
  - Cartesian coordinates (required)
  - Polar coordinates (optional – implemented ✔)
- Computes area under the curve
- Simplifies the expression & its derivative

Everything was implemented **only in MathPlot.java**, as required.

---

## 2 Requirements Compliance

| Requirement | Status |
|------------|:-----:|
| AOS input format | ✔ |
| RPN input format | ✔ |
| Operators `+ - * / ^` | ✔ |
| Standard functions (`sin`, `cos`, `exp`, `ln`) | ✔ |
| First-order derivative computation | ✔ |
| Print function & derivative in AOS or RPN | ✔ Both formats |
| Cartesian plot with axes + grid | ✔ |
| Polar plot | ✔ (bonus feature) |
| Area (Rectangular + Trapezoidal) | ✔ |
| Simplification | ✔ |
| Code changes only in MathPlot.java | ✔ Verified |
| Unit testing coverage ≥ 80% | ✔ Achieved |

➡ **All minimum + all optional requirements completed** 

---

## 3 System Design Overview

MathPlot uses an **Expression Tree (AST)** internally.

### System Design Flow (Mermaid Diagram)

