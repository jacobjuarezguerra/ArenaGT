# ArenaGT — Gestión de copas deportivas

Aplicación de consola en Java desarrollada para el **Lab 4 - Polimorfismo** del curso
**CC2008 - Programación Orientada a Objetos** (Universidad del Valle de Guatemala,
Ciclo 2, 2026).

## Contenido de la entrega

- `src/arena/…` — código fuente Java (34 archivos `.java`)
- `docs/uml/diagrama-clases.png` — diagrama de clases UML (renderizado)
- `ArenaGT_Entrega.pdf` — análisis, diagrama UML y evidencia de las pruebas
- `README.md` — este archivo

La carpeta `out/` no forma parte de la entrega: la crea la compilación con las
clases compiladas (36 archivos `.class`: 34 clases y 2 clases anidadas).

## Requisitos

- Java JDK 17 o superior (probado con JDK 21).
- No se requieren librerías externas, bases de datos ni archivos de datos. Todo
  se conserva en memoria.

## Compilación

Desde la raíz del proyecto (la carpeta que contiene `src/`):

```powershell
javac -encoding UTF-8 -d out src/arena/*.java src/arena/*/*.java
```

En Linux o macOS funciona el mismo comando desde la terminal.

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

Resultado esperado: `Pruebas ejecutadas: 14, fallidas: 0`.

## Datos iniciales

El programa inicia con seis participantes de tres empresas (Alfa, Beta, Gamma) y
un torneo de cada disciplina, cargados directamente (sin inscripción, por lo
que sin ingresos ni premios registrados).

Los cinco torneos inician en juego, con sus eventos programados y al menos un
resultado ya registrado, salvo el torneo de relevos, cuya carrera única aún no
tiene resultado: registrarlo finalizaría el torneo y entregaría su premio, y
los datos iniciales no deben registrar premios; los puntos de relevos se
demuestran en la suite de pruebas. Los datos iniciales muestran el empate de
fútbol sala (Jaguares 1-1 Serpientes, 1 punto cada uno), la victoria en tiempo
reglamentario de fútbol sala con penales (Halcones 2-1 Serpientes, 3 puntos
como en fútbol sala), el desempate por diferencia (fútbol sala: Los Tigres +3
frente a Halcones +2, ambos con 3 puntos), el desempate por victorias (fútbol
sala con penales: Los Tigres y Jaguares con 3 puntos y 1 victoria frente a
Cóndores con 3 puntos y 0 victorias) y un encuentro de fútbol sala con penales
definido por penales. El ranking inicial es Alfa 20, Beta 12 y Gamma 3.

## Repositorio público

https://github.com/jacobjuarezguerra/ArenaGT
