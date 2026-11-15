# ArenaGT — Gestión de copas deportivas

Aplicación de consola en Java desarrollada para el **Lab 4 - Polimorfismo** del curso
**CC2008 - Programación Orientada a Objetos** (Universidad del Valle de Guatemala,
Ciclo 2, 2026).

## Contenido de la entrega

- `src/arena/…` — código fuente Java (34 archivos `.java`)
- `out/` — clases compiladas (36 clases `.class`)
- `docs/uml/diagrama-clases.puml` — diagrama de clases UML (fuente PlantUML)
- `docs/uml/diagrama-clases.svg` — diagrama de clases UML (renderizado)
- `README.md` — este archivo

## Requisitos

- Java JDK 26 o superior.
- No se requieren librerías externas, bases de datos ni archivos de datos. Todo
  se conserva en memoria.

## Compilación

Desde la raíz del proyecto (`lab`):

```powershell
javac -encoding UTF-8 -d out src/arena/**/*.java
```

## Ejecución

### Programa principal

```powershell
java -cp out arena.Principal
```

El menú permanece disponible hasta que el usuario elige la opción `0) Salir`.

### Suite de pruebas (sin JUnit)

```powershell
java -cp out arena.pruebas.PruebasArenaGT
```

Resultado esperado: `Pruebas ejecutadas: 12, fallidas: 0`.

## Datos iniciales

El programa inicia con seis participantes de tres empresas (Alfa, Beta, Gamma) y
un torneo de cada disciplina, cargados directamente (sin inscripción, por lo
que sin ingresos ni premios registrados).

## Repositorio público

https://github.com/jacobjuarezguerra/ArenaGT
