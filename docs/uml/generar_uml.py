#!/usr/bin/env python3
"""Genera el diagrama de clases UML de ArenaGT en SVG.

El diagrama se construye con cajas y relaciones declaradas explícitamente
para no depender de herramientas externas ni de conexión a Internet.
"""

from html import escape

W, H = 3200, 2280
FONT = "Segoe UI, Arial, Helvetica, sans-serif"


class Box:
    def __init__(self, key, title, x, y, attrs, methods, stereotype="", w=400):
        self.key = key
        self.title = title
        self.x = x
        self.y = y
        self.w = w
        self.attrs = attrs
        self.methods = methods
        self.stereotype = stereotype
        self.h = self._height()

    def _height(self):
        h = 12 + 24 + 10
        if self.stereotype:
            h += 18
        if self.attrs:
            h += 6 + len(self.attrs) * 17
        if self.methods:
            h += 6 + len(self.methods) * 17
        return h + 10

    @property
    def cx(self):
        return self.x + self.w / 2

    @property
    def cy(self):
        return self.y + self.h / 2

    def edge(self, tx, ty, pad=2):
        dx = tx - self.cx
        dy = ty - self.cy
        if dx == 0 and dy == 0:
            return self.cx, self.cy
        hw = self.w / 2 + pad
        hh = self.h / 2 + pad
        escala = min(hw / abs(dx) if dx else float("inf"),
                     hh / abs(dy) if dy else float("inf"))
        return self.cx + dx * escala, self.cy + dy * escala


boxes = {}


def add(key, title, x, y, attrs, methods, stereotype="", w=400):
    boxes[key] = Box(key, title, x, y, attrs, methods, stereotype, w)


# ------------------------------------------------------------------
# Modelo
# ------------------------------------------------------------------
add("Empresa", "Empresa", 60, 110,
    ["- nombre : String"],
    ["+ Empresa(nombre : String)", "+ getNombre() : String",
     "+ equals(Object) : boolean", "+ hashCode() : int"])
add("Participante", "Participante", 500, 110,
    ["- codigo : String", "- nombre : String", "- empresa : Empresa"],
    ["+ Participante(codigo, nombre, empresa)",
     "+ getCodigo() : String", "+ getNombre() : String",
     "+ getEmpresa() : Empresa", "+ equals(Object) : boolean"])
add("Resultado", "«interface» Resultado", 980, 110,
    [],
    ["+ participantes() : Collection<Participante>",
     "+ puntos(p : Participante) : int",
     "+ diferencia(p : Participante) : int",
     "+ victoria(p : Participante) : boolean",
     "+ descripcion() : String"])
add("ResultadoEncuentro", "ResultadoEncuentro", 1400, 110,
    ["- local, visitante : Participante",
     "- puntosLocal, puntosVisitante : int",
     "- diferenciaLocal, diferenciaVisitante : int",
     "- victoriaLocal, victoriaVisitante : boolean",
     "- descripcion : String"],
    ["+ ResultadoEncuentro(...)",
     "+ puntos(p : Participante) : int",
     "+ diferencia(p : Participante) : int",
     "+ victoria(p : Participante) : boolean"])
add("ResultadoCarrera", "ResultadoCarrera", 1830, 110,
    ["- estadisticas : Map<Participante, Estadistica>",
     "- descripcion : String"],
    ["+ ResultadoCarrera(estadisticas, descripcion)",
     "+ puntos(p : Participante) : int",
     "+ diferencia(p : Participante) : int",
     "+ victoria(p : Participante) : boolean"])
add("Entrada", "«interface» Entrada", 2260, 110,
    [],
    ["+ leerTexto(mensaje) : String",
     "+ leerEntero(mensaje) : int",
     "+ leerDecimal(mensaje) : double"])
add("FilaTabla", "FilaTabla", 980, 420,
    ["- participante : Participante", "- puntos : int",
     "- diferencia : int", "- victorias : int"],
    ["+ compareTo(otra : FilaTabla) : int"],
    "«Comparable<FilaTabla>»")
add("FilaRanking", "FilaRanking", 1400, 420,
    ["- empresa : Empresa", "- puntos : int"],
    ["+ compareTo(otra : FilaRanking) : int"],
    "«Comparable<FilaRanking>»")
add("EstadoTorneo", "«enum» EstadoTorneo", 1830, 420,
    ["EN_INSCRIPCION", "EN_JUEGO", "FINALIZADO"], [])
add("EstadoEvento", "«enum» EstadoEvento", 2260, 420,
    ["SIN_PROGRAMAR", "PROGRAMADO", "FINALIZADO"], [])
add("ReglaNegocioException", "ReglaNegocioException", 2680, 420,
    [],
    ["+ ReglaNegocioException(mensaje : String)"], w=360)

# ------------------------------------------------------------------
# Disciplinas
# ------------------------------------------------------------------
add("Disciplina", "«abstract» Disciplina", 60, 760,
    ["- nombre : String", "- minParticipantes : int",
     "- maxParticipantes : int", "- cuota : double"],
    ["+ {abstract} generarEventos(torneo) : List<Evento>",
     "+ getNombre() : String", "+ getMinParticipantes() : int",
     "+ getMaxParticipantes() : int", "+ getCuota() : double",
     "+ calcularPremio(inscritos : int) : double"])
add("DisciplinaEncuentro", "«abstract» DisciplinaEncuentro", 60, 1060,
    [],
    ["+ generarEventos(torneo) : List<Evento>",
     "+ {abstract} leerResultado(e, entrada) : Resultado"])
add("DisciplinaCarrera", "«abstract» DisciplinaCarrera", 520, 1060,
    [],
    ["+ generarEventos(torneo) : List<Evento>",
     "+ {abstract} leerResultado(c, entrada) : Resultado"])
add("FutbolSala", "FutbolSala", 60, 1290,
    [],
    ["+ FutbolSala()", "+ leerResultado(e, entrada) : Resultado",
     "# construirResultado(...) : Resultado"])
add("FutbolSalaPenales", "FutbolSalaPenales", 60, 1510,
    [],
    ["+ FutbolSalaPenales()",
     "+ leerResultado(e, entrada) : Resultado"])
add("Baloncesto", "Baloncesto", 520, 1290,
    [],
    ["+ Baloncesto()", "+ leerResultado(e, entrada) : Resultado"])
add("Voleibol", "Voleibol", 960, 1290,
    [],
    ["+ Voleibol()", "+ leerResultado(e, entrada) : Resultado"])
add("Relevos", "Relevos", 520, 1510,
    [],
    ["+ Relevos()", "+ leerResultado(c, entrada) : Resultado"])

# ------------------------------------------------------------------
# Eventos
# ------------------------------------------------------------------
add("Evento", "«abstract» Evento", 1460, 760,
    ["- codigo : String", "- torneo : Torneo",
     "- participantes : List<Participante>",
     "- fecha : LocalDate", "- hora : int", "- sede : String",
     "- estado : EstadoEvento", "- resultado : Resultado"],
    ["+ {abstract} getTipoEvento() : String",
     "+ {abstract} getDescripcion() : String",
     "+ {abstract} leerResultado(entrada) : Resultado",
     "+ aplicarProgramacion(fecha, hora, sede) : void",
     "+ registrarResultado(r : Resultado) : void",
     "+ puntosDe(p) : int", "+ diferenciaDe(p) : int",
     "+ victoriaDe(p) : boolean", "+ compareTo(otro) : int"],
    "«Comparable<Evento>»")
add("Encuentro", "Encuentro", 1460, 1270,
    ["- disciplina : DisciplinaEncuentro", "- local : Participante",
     "- visitante : Participante"],
    ["+ Encuentro(codigo, torneo, disciplina, local, visitante)",
     "+ leerResultado(entrada) : Resultado",
     "+ getLocal() : Participante", "+ getVisitante() : Participante"])
add("Carrera", "Carrera", 1900, 1270,
    ["- disciplina : DisciplinaCarrera"],
    ["+ Carrera(codigo, torneo, disciplina, participantes)",
     "+ leerResultado(entrada) : Resultado"])
add("Calendario", "Calendario", 1900, 1510,
    ["- eventos : List<Evento>"],
    ["+ Calendario(eventos : Collection<Evento>)",
     "+ iterator() : Iterator<Evento>"],
    "«Iterable<Evento>»")

# ------------------------------------------------------------------
# Torneos y contabilidad
# ------------------------------------------------------------------
add("Torneo", "Torneo", 960, 1780,
    ["- codigo : String", "- nombre : String",
     "- disciplina : Disciplina", "- estado : EstadoTorneo",
     "- participantes : List<Participante>",
     "- eventos : List<Evento>"],
    ["+ Torneo(codigo, nombre, disciplina)",
     "+ inscribir(p : Participante) : void",
     "+ cerrarInscripciones() : void",
     "+ verificarFinalizacion() : boolean",
     "+ getTablaPosiciones() : TablaPosiciones",
     "+ getCampeon() : Participante", "+ getPremio() : double"])
add("TablaPosiciones", "TablaPosiciones", 300, 2060,
    ["- filas : List<FilaTabla>"],
    ["+ TablaPosiciones(participantes, eventos)",
     "+ iterator() : Iterator<FilaTabla>"],
    "«Iterable<FilaTabla>»")
add("RankingEmpresas", "RankingEmpresas", 720, 2060,
    ["- filas : List<FilaRanking>"],
    ["+ RankingEmpresas(participantes, torneos)",
     "+ iterator() : Iterator<FilaRanking>"],
    "«Iterable<FilaRanking>»")
add("Finanzas", "Finanzas", 1140, 2060,
    ["- ingresosPorDisciplina : Map<String, Double>",
     "- premios : List<Premio>", "- totalIngresos : double"],
    ["+ registrarIngreso(d, monto) : void",
     "+ registrarPremio(t : Torneo) : boolean",
     "+ getTotalIngresos() : double", "+ getTotalPremios() : double"])

# ------------------------------------------------------------------
# Sistema y consola
# ------------------------------------------------------------------
add("ArenaGT", "ArenaGT", 1830, 1750,
    ["- participantes : Map<String, Participante>",
     "- torneos : Map<String, Torneo>",
     "- empresas : Map<String, Empresa>",
     "- disciplinas : List<Disciplina>", "- finanzas : Finanzas"],
    ["+ {static} cargarDatosIniciales() : ArenaGT",
     "+ registrarParticipante(codigo, nombre, empresa)",
     "+ crearTorneo(codigo, nombre, disciplina) : Torneo",
     "+ cotizar(disciplina, cantidad) : double",
     "+ inscribir(torneo, participante) : double",
     "+ cerrarInscripciones(torneo) : void",
     "+ programarEvento(evento, fecha, hora, sede) : void",
     "+ registrarResultado(evento, entrada) : Resultado",
     "+ calendarioGeneral() : Calendario",
     "+ calendarioTorneo(codigo) : Calendario",
     "+ calendarioParticipante(codigo) : Calendario",
     "+ getRankingEmpresas() : RankingEmpresas"])
add("Menu", "Menu", 2320, 1660,
    ["- sistema : ArenaGT", "- entrada : Entrada"],
    ["+ ejecutar() : void"])
add("Consola", "Consola", 2780, 1450,
    ["- scanner : Scanner"],
    ["+ leerTexto(mensaje) : String", "+ leerEntero(mensaje) : int",
     "+ leerDecimal(mensaje) : double", "+ cerrar() : void"])
add("EntradaGuion", "EntradaGuion", 2780, 1690,
    ["- respuestas : Deque<String>"],
    ["+ leerTexto(mensaje) : String", "+ leerEntero(mensaje) : int",
     "+ leerDecimal(mensaje) : double"])
add("Principal", "Principal", 2320, 1950,
    [], ["+ {static} main(args : String[]) : void"])
add("PruebasArenaGT", "PruebasArenaGT", 2700, 2010,
    [], ["+ {static} main(args : String[]) : void"], w=420)

rels = []


def rel(src, dst, kind, src_mult="", dst_mult="", label=""):
    rels.append((src, dst, kind, src_mult, dst_mult, label))


# Herencia
rel("Disciplina", "DisciplinaEncuentro", "inherit")
rel("Disciplina", "DisciplinaCarrera", "inherit")
rel("DisciplinaEncuentro", "FutbolSala", "inherit")
rel("FutbolSala", "FutbolSalaPenales", "inherit")
rel("DisciplinaEncuentro", "Baloncesto", "inherit")
rel("DisciplinaEncuentro", "Voleibol", "inherit")
rel("DisciplinaCarrera", "Relevos", "inherit")
rel("Evento", "Encuentro", "inherit")
rel("Evento", "Carrera", "inherit")

# Implementaciones
rel("Resultado", "ResultadoEncuentro", "implement")
rel("Resultado", "ResultadoCarrera", "implement")
rel("Entrada", "Consola", "implement")
rel("Entrada", "EntradaGuion", "implement")

# Asociaciones y composiciones
rel("Participante", "Empresa", "assoc", "0..*", "1")
rel("ResultadoEncuentro", "Participante", "assoc", "", "2")
rel("ResultadoCarrera", "Participante", "assoc", "", "1..*")
rel("FilaTabla", "Participante", "assoc", "", "1")
rel("FilaRanking", "Empresa", "assoc", "", "1")
rel("Evento", "Resultado", "assoc")
rel("Evento", "Torneo", "assoc", "0..*", "1")
rel("Evento", "EstadoEvento", "assoc")
rel("Encuentro", "DisciplinaEncuentro", "assoc", "", "1")
rel("Carrera", "DisciplinaCarrera", "assoc", "", "1")
rel("Torneo", "Evento", "composition", "1", "0..*")
rel("Torneo", "Participante", "aggregation", "1", "2..8")
rel("Torneo", "Disciplina", "assoc", "0..*", "1")
rel("Torneo", "EstadoTorneo", "assoc")
rel("TablaPosiciones", "FilaTabla", "composition", "1", "0..*")
rel("RankingEmpresas", "FilaRanking", "composition", "1", "0..*")
rel("Calendario", "Evento", "assoc", "1", "0..*")
rel("ArenaGT", "Torneo", "aggregation", "1", "0..*")
rel("ArenaGT", "Participante", "aggregation", "1", "0..*")
rel("ArenaGT", "Empresa", "aggregation", "1", "0..*")
rel("ArenaGT", "Disciplina", "aggregation", "1", "5")
rel("ArenaGT", "Finanzas", "composition", "1", "1")
rel("Menu", "ArenaGT", "assoc", "1", "1")
rel("Menu", "Entrada", "assoc", "1", "1")
rel("Principal", "ArenaGT", "assoc", "1", "1")
rel("Principal", "Menu", "assoc", "1", "1")

# Dependencias
rel("Disciplina", "Torneo", "dependency")
rel("Disciplina", "Evento", "dependency")
rel("ArenaGT", "TablaPosiciones", "dependency")
rel("ArenaGT", "RankingEmpresas", "dependency")
rel("ArenaGT", "Calendario", "dependency")
rel("PruebasArenaGT", "ArenaGT", "dependency")
rel("PruebasArenaGT", "EntradaGuion", "dependency")


def line_color(kind):
    return "#546e7a" if kind != "inherit" else "#2e4053"


def arrow_triangle(x, y, dx, dy, fill="white"):
    import math
    ang = math.atan2(dy, dx)
    size = 13
    p1 = (x, y)
    p2 = (x - size * math.cos(ang - 0.42), y - size * math.sin(ang - 0.42))
    p3 = (x - size * math.cos(ang + 0.42), y - size * math.sin(ang + 0.42))
    return f'<polygon points="{p1[0]:.1f},{p1[1]:.1f} {p2[0]:.1f},{p2[1]:.1f} {p3[0]:.1f},{p3[1]:.1f}" fill="{fill}" stroke="#2e4053" stroke-width="1.4"/>'


def arrow_open(x, y, dx, dy):
    import math
    ang = math.atan2(dy, dx)
    size = 12
    a = (x - size * math.cos(ang - 0.38), y - size * math.sin(ang - 0.38))
    b = (x - size * math.cos(ang + 0.38), y - size * math.sin(ang + 0.38))
    return (f'<polyline points="{a[0]:.1f},{a[1]:.1f} {x:.1f},{y:.1f} {b[0]:.1f},{b[1]:.1f}" '
            'fill="none" stroke="#546e7a" stroke-width="1.5"/>')


def diamond(x, y, dx, dy, filled):
    import math
    ang = math.atan2(dy, dx)
    size = 8
    p1 = (x, y)
    p2 = (x - size * math.cos(ang) - size * 0.7 * math.sin(ang),
          y - size * math.sin(ang) + size * 0.7 * math.cos(ang))
    p3 = (x - 2 * size * math.cos(ang), y - 2 * size * math.sin(ang))
    p4 = (x - size * math.cos(ang) + size * 0.7 * math.sin(ang),
          y - size * math.sin(ang) - size * 0.7 * math.cos(ang))
    fill = "#2e4053" if filled else "white"
    return (f'<polygon points="{p1[0]:.1f},{p1[1]:.1f} {p2[0]:.1f},{p2[1]:.1f} '
            f'{p3[0]:.1f},{p3[1]:.1f} {p4[0]:.1f},{p4[1]:.1f}" '
            f'fill="{fill}" stroke="#2e4053" stroke-width="1.3"/>')


def draw_relationship(src, dst, kind, src_mult, dst_mult, label):
    import math
    a = boxes[src]
    b = boxes[dst]
    x1, y1 = a.edge(b.cx, b.cy)
    x2, y2 = b.edge(a.cx, a.cy)
    dx = x2 - x1
    dy = y2 - y1
    dash = ' stroke-dasharray="9,7"' if kind in ("implement", "dependency") else ""
    color = line_color(kind)
    parts = [f'<line x1="{x1:.1f}" y1="{y1:.1f}" x2="{x2:.1f}" y2="{y2:.1f}" '
             f'stroke="{color}" stroke-width="1.5"{dash}/>' ]

    if kind == "inherit":
        parts.append(arrow_triangle(x2, y2, dx, dy, "white"))
    elif kind == "implement":
        parts.append(arrow_triangle(x2, y2, dx, dy, "white"))
    elif kind == "assoc":
        parts.append(arrow_open(x2, y2, dx, dy))
    elif kind == "dependency":
        parts.append(arrow_open(x2, y2, dx, dy))
    elif kind == "composition":
        parts.append(diamond(x1, y1, dx, dy, True))
    elif kind == "aggregation":
        parts.append(diamond(x1, y1, dx, dy, False))

    if src_mult:
        mx = x1 + dx * 0.12
        my = y1 + dy * 0.12
        parts.append(f'<text x="{mx:.1f}" y="{my - 6:.1f}" class="mult">{escape(src_mult)}</text>')
    if dst_mult:
        mx = x2 - dx * 0.12
        my = y2 - dy * 0.12
        parts.append(f'<text x="{mx:.1f}" y="{my - 6:.1f}" class="mult">{escape(dst_mult)}</text>')
    if label:
        mx = (x1 + x2) / 2
        my = (y1 + y2) / 2
        parts.append(f'<text x="{mx:.1f}" y="{my - 6:.1f}" class="label">{escape(label)}</text>')
    return "\n".join(parts)


def draw_box(box):
    out = []
    header_h = 24 + (18 if box.stereotype else 0)
    out.append(f'<g class="cls">')
    out.append(f'<rect x="{box.x}" y="{box.y}" width="{box.w}" height="{box.h}" rx="5" '
               'fill="#ffffff" stroke="#2e4053" stroke-width="1.7"/>')
    out.append(f'<path d="M {box.x+5} {box.y} H {box.x+box.w-5} Q {box.x+box.w} {box.y} '
               f'{box.x+box.w} {box.y+5} V {box.y+header_h} H {box.x} V {box.y+5} '
               f'Q {box.x} {box.y} {box.x+5} {box.y} Z" fill="#eaf1f8"/>')
    title_y = box.y + 24
    out.append(f'<text x="{box.cx:.1f}" y="{title_y}" class="title" text-anchor="middle">'
               f'{escape(box.title)}</text>')
    y = title_y + 12
    if box.stereotype:
        y += 15
        out.append(f'<text x="{box.cx:.1f}" y="{y}" class="stereo" text-anchor="middle">'
                   f'{escape(box.stereotype)}</text>')
    y += 8
    if box.attrs:
        out.append(f'<line x1="{box.x}" y1="{y}" x2="{box.x+box.w}" y2="{y}" '
                   'stroke="#b0bec5" stroke-width="1"/>')
        y += 16
        for attr in box.attrs:
            out.append(f'<text x="{box.x+10}" y="{y}" class="member">{escape(attr)}</text>')
            y += 17
    if box.methods:
        out.append(f'<line x1="{box.x}" y1="{y}" x2="{box.x+box.w}" y2="{y}" '
                   'stroke="#b0bec5" stroke-width="1"/>')
        y += 16
        for method in box.methods:
            out.append(f'<text x="{box.x+10}" y="{y}" class="member">{escape(method)}</text>')
            y += 17
    out.append('</g>')
    return "\n".join(out)


svg = []
svg.append(f'<svg xmlns="http://www.w3.org/2000/svg" width="{W}" height="{H}" viewBox="0 0 {W} {H}">')
svg.append("<defs><style>"
           f".title{{font-family:{FONT};font-size:15px;font-weight:700;fill:#17202a}}"
           f".stereo{{font-family:{FONT};font-size:12px;font-style:italic;fill:#455a64}}"
           f".member{{font-family:{FONT};font-size:12.5px;fill:#212f3d}}"
           f".mult{{font-family:{FONT};font-size:11px;fill:#37474f}}"
           f".label{{font-family:{FONT};font-size:11px;fill:#37474f}}"
           f".pkg{{font-family:{FONT};font-size:20px;font-weight:700;fill:#78909c}}"
           f".note{{font-family:{FONT};font-size:13px;fill:#37474f}}"
           "</style></defs>")
svg.append(f'<rect width="{W}" height="{H}" fill="#ffffff"/>')
svg.append('<text x="50" y="45" class="pkg" style="font-size:27px;fill:#17202a">'
           'ArenaGT · Diagrama de clases UML</text>')

# Fondos de paquetes
regions = [
    (30, 75, 3060, 560, "arena.modelo"),
    (30, 690, 1400, 940, "arena.disciplinas"),
    (1430, 690, 820, 940, "arena.eventos"),
    (280, 1740, 1480, 440, "arena.torneo"),
    (1790, 1580, 1380, 650, "arena.sistema / arena.ui"),
]
for x, y, w, h, name in regions:
    svg.append(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="12" '
               'fill="#f7fafc" stroke="#cfd8dc" stroke-width="1.2" stroke-dasharray="6,5"/>')
    svg.append(f'<text x="{x+12}" y="{y+24}" class="pkg">{escape(name)}</text>')

for box in boxes.values():
    svg.append(draw_box(box))

for r in rels:
    svg.append(draw_relationship(*r))

# Notas
notes = [
    (1660, 520,
     ["El orden cronológico (fecha, hora, sede) se resuelve en",
      "Evento.compareTo. Calendario solo recorre eventos ya ordenados."]),
    (2680, 700,
     ["Cada disciplina decide cupos, cuota, validación,",
      "puntos de tabla y diferencia. No hay instanceof."]),
    (1660, 2100,
     ["ArenaGT es la única clase que conoce todos los torneos:",
      "valida conflictos de sede y participante, y registra",
      "ingresos solo por inscripciones efectivas."]),
    (2300, 2150,
     ["TablaPosiciones, RankingEmpresas y Calendario implementan",
      "Iterable. Evento, FilaTabla y FilaRanking implementan",
      "Comparable. Los estereotipos lo indican en cada caja."]),
]
for x, y, lines in notes:
    h = 18 + len(lines) * 17
    svg.append(f'<rect x="{x}" y="{y}" width="580" height="{h}" rx="4" fill="#fffde7" stroke="#f9a825" stroke-width="1.3"/>')
    for i, line in enumerate(lines):
        svg.append(f'<text x="{x+10}" y="{y+22+i*17}" class="note">{escape(line)}</text>')

# Leyenda
svg.append('<g>')
svg.append('<text x="50" y="2210" class="pkg" style="font-size:15px;fill:#37474f">Leyenda:</text>')
svg.append('<line x1="160" y1="2204" x2="230" y2="2204" stroke="#2e4053" stroke-width="1.5"/>')
svg.append('<polygon points="230,2204 217,2198 217,2210" fill="white" stroke="#2e4053"/>')
svg.append('<text x="238" y="2209" class="note">herencia</text>')
svg.append('<line x1="360" y1="2204" x2="430" y2="2204" stroke="#546e7a" stroke-width="1.5" stroke-dasharray="9,7"/>')
svg.append('<polygon points="430,2204 417,2198 417,2210" fill="white" stroke="#2e4053"/>')
svg.append('<text x="438" y="2209" class="note">implementación</text>')
svg.append('<line x1="620" y1="2204" x2="690" y2="2204" stroke="#2e4053" stroke-width="1.5"/>')
svg.append('<polygon points="620,2204 634,2198 648,2204 634,2210" fill="#2e4053" stroke="#2e4053"/>')
svg.append('<text x="696" y="2209" class="note">composición</text>')
svg.append('<line x1="860" y1="2204" x2="930" y2="2204" stroke="#2e4053" stroke-width="1.5"/>')
svg.append('<polygon points="860,2204 874,2198 888,2204 874,2210" fill="white" stroke="#2e4053"/>')
svg.append('<text x="896" y="2209" class="note">agregación</text>')
svg.append('<text x="1060" y="2209" class="note">Los métodos abstractos se marcan con {abstract}; los métodos concretos de una subclase sobrescriben al de la superclase o interfaz.</text>')
svg.append('</g>')

svg.append('</svg>')

with open("docs/uml/diagrama-clases.svg", "w", encoding="utf-8") as f:
    f.write("\n".join(svg))

print("SVG generado: docs/uml/diagrama-clases.svg")
print("Cajas:", len(boxes), "Relaciones:", len(rels))
