# ArenaGT — Gestión de copas deportivas

Aplicación de consola en Java que organiza copas deportivas entre empresas, desarrollada como el **Lab 4 - Polimorfismo** del curso CC2008 - Programación Orientada a Objetos (Universidad del Valle de Guatemala, Ciclo 2, 2026).

## Estructura del proyecto

```
lab/
├── src/arena/                 # Código fuente Java
│   ├── Principal.java         # Clase con el método main
│   ├── disciplina/            # Reglas de cada disciplina (Disciplina, Fútbol sala, Baloncesto, Voleibol, Relevos, ...)
│   ├── eventos/               # Eventos del torneo (Evento, Encuentro, Carrera, Calendario)
│   ├── modelo/                # Modelo de dominio (Participante, Empresa, Resultado, FilaTabla, ...)
│   ├── sistema/               # Fachada del sistema y entrada (ArenaGT, EntradaGuion)
│   ├── torneo/                # Torneo, tabla de posiciones, finanzas, ranking de empresas
│   ├── ui/                    # Interfaz de consola (Menú, Consola)
│   └── pruebas/               # Suite de pruebas automatizadas sin JUnit
├── out/                       # Clases compiladas
├── docs/
│   ├── evidencia-pruebas.txt  # Evidencia de las 12 pruebas ejecutadas (0 fallos)
│   └── uml/                   # Diagrama de clases UML (PlantUML + SVG)
└── README.md                  # Este archivo
```

## Requisitos

- Java JDK 26 (o superior)
- No se requieren librerías externas, bases de datos ni archivos de datos. Todo se conserva en memoria.

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

### Suite de pruebas

```powershell
java -cp out arena.pruebas.PruebasArenaGT
```

Resultado esperado: `Pruebas ejecutadas: 12, fallidas: 0`.

## Datos iniciales

El programa inicia con datos cargados directamente (sin pasar por inscripción, por lo que no hay ingresos ni premios registrados):

- Seis participantes de tres empresas (Alfa, Beta, Gamma).
- Un torneo de cada disciplina; al menos dos en juego con eventos programados y al menos un resultado registrado.

## Entregables del laboratorio

- Análisis, diagrama UML y evidencia de pruebas: `docs/`
- Código fuente: `src/arena/**/*.java`
- URL del repositorio público en GitHub con todos los archivos `.java` y este `README.md`.

## Notas de implementación

- Las decisiones que dependen de la disciplina se resuelven por polimorfismo sobre clases abstractas e interfaces (no se verifica el tipo del objeto).
- Las cotizaciones no generan ingresos ni inscriben a nadie.
- Las operaciones rechazadas no modifican el estado previo ni los ingresos.
- Los montos se muestran con dos decimales.
