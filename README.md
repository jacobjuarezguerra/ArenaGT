# 🏆 ArenaGT — Gestión de Copas Deportivas

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Paradigma](https://img.shields.io/badge/Paradigma-POO%20%7C%20Polimorfismo-007396?style=flat)
![Universidad](https://img.shields.io/badge/UVG-CC2008%20(Ciclo%202%2C%202026)-1E6B38?style=flat)

Aplicación de consola desarrollada en Java para la gestión integral de torneos deportivos empresariales, cálculo polimórfico de puntuaciones, criterios de desempate y rankings por empresa.

Este proyecto corresponde al **Lab 4 — Polimorfismo** del curso **CC2008 - Programación Orientada a Objetos** de la **Universidad del Valle de Guatemala** (Ciclo 2, 2026).

---

## 📁 Contenido de la entrega

```text
ArenaGT/
├── src/
│   └── arena/                  # Código fuente Java (34 archivos .java)
├── docs/
│   └── uml/
│       └── diagrama-clases.png # Diagrama de clases UML renderizado
├── ArenaGT_Entrega.pdf         # Documento con análisis, diagrama UML y evidencia de pruebas
└── README.md                   # Documentación del proyecto
```

> **Nota sobre compilación:** La carpeta `out/` no forma parte de la entrega en el repositorio; se genera automáticamente al compilar y contiene los 36 archivos `.class` resultantes (34 clases principales y 2 clases anidadas).

---

## ⚙️ Requisitos

* **Java Development Kit (JDK):** Versión **17 o superior** (probado y verificado con **JDK 21**).
* **Dependencias:** Ninguna. No requiere librerías externas (como JUnit), bases de datos ni archivos externos; toda la información se gestiona en memoria durante la ejecución.

---

## 🚀 Compilación y Ejecución

Abre una terminal en la **raíz del proyecto** (el directorio que contiene la carpeta `src/`). Los siguientes comandos funcionan de manera idéntica en **Windows (PowerShell/CMD)**, **Linux** y **macOS**.

### 1. Compilar el proyecto

```bash
javac -encoding UTF-8 -d out src/arena/*.java src/arena/*/*.java
```

### 2. Ejecutar el programa principal

```bash
java -cp out arena.Principal
```

El menú interactivo permanecerá activo en consola hasta que selecciones la opción `0) Salir`.

### 3. Ejecutar la suite de pruebas

El proyecto incluye una suite de pruebas automatizadas propia (sin dependencias de JUnit):

```bash
java -cp out arena.pruebas.PruebasArenaGT
```

**Salida esperada:**

```text
Pruebas ejecutadas: 14, fallidas: 0
```

---

## 📊 Datos iniciales de prueba

Al iniciar, el sistema carga en memoria un escenario preconfigurado con **6 participantes** pertenecientes a **3 empresas** (`Alfa`, `Beta` y `Gamma`) y **5 torneos** (uno por cada disciplina).

* **Estado financiero inicial:** Los participantes y torneos se cargan directamente sin proceso de inscripción, por lo que el sistema inicia sin ingresos ni premios registrados.
* **Estado de los torneos:** Todos los torneos inician en curso con sus eventos programados y al menos un resultado registrado, **a excepción del torneo de relevos**, cuya carrera única permanece pendiente:
  * *Motivo:* Registrar ese resultado finalizaría automáticamente el torneo y entregaría el premio correspondiente, alterando la condición inicial sin premios. El cálculo de puntos para relevos se valida directamente en la suite de pruebas.

### Escenarios y desempates demostrados al iniciar

* **Empate en fútbol sala:** `Jaguares 1 - 1 Serpientes` (1 punto para cada equipo).
* **Victoria en tiempo reglamentario (fútbol sala con penales):** `Halcones 2 - 1 Serpientes` (3 puntos al ganador, equivalente al fútbol sala tradicional).
* **Desempate por diferencia de goles (fútbol sala):** `Los Tigres` (`+3`) se posiciona por encima de `Halcones` (`+2`), ambos empatados con 3 puntos.
* **Desempate por victorias (fútbol sala con penales):** `Los Tigres` y `Jaguares` (3 puntos y 1 victoria) superan a `Cóndores` (3 puntos y 0 victorias).
* **Definición por penales:** Incluye un encuentro de fútbol sala con penales resuelto mediante tanda de penales.

### Ranking inicial por empresa

| Posición | Empresa | Puntaje Inicial |
| :---: | :--- | :---: |
| 1 | **Alfa** | `20` |
| 2 | **Beta** | `12` |
| 3 | **Gamma** | `3` |

---

## 🔗 Repositorio

Código fuente disponible en GitHub: [github.com/jacobjuarezguerra/ArenaGT](https://github.com/jacobjuarezguerra/ArenaGT)
