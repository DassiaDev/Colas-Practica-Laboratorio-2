"""
Pila dinámica (lista enlazada) y evaluación de expresiones.

Contenido:
  - Nodo y PilaDinamica: estructura de datos básica (LIFO) con nodos enlazados.
  - validar_parentesis: verifica el balanceo de ( ) usando la pila.
  - tokenizar: separa una expresión infija en números, operadores y paréntesis.
  - infija_a_postfija: algoritmo Shunting-Yard usando la pila.
  - evaluar_postfija: evalúa una expresión postfija usando la pila.
  - insercion / busqueda_secuencial / busqueda_binaria: para ordenar y buscar
    entre los resultados de los 10 problemas.
  - main(): resuelve los 10 problemas e imprime una tabla de resultados.
"""


# ---------------------------------------------------------------------------
# 1. Estructura de datos: pila dinámica
# ---------------------------------------------------------------------------
class Nodo:
    """Nodo de la lista enlazada: guarda un dato y apunta al nodo de abajo."""

    def __init__(self, dato, siguiente=None):
        self.dato = dato
        self.siguiente = siguiente


class PilaDinamica:
    """Pila LIFO implementada con nodos enlazados (memoria dinámica)."""

    def __init__(self):
        self._tope = None
        self._tamano = 0

    def esta_vacia(self):
        return self._tope is None

    def tamano(self):
        return self._tamano

    def push(self, dato):
        """Apila un dato. O(1)."""
        self._tope = Nodo(dato, self._tope)
        self._tamano += 1

    def pop(self):
        """Desapila y devuelve el dato del tope. O(1)."""
        if self.esta_vacia():
            raise IndexError("La pila está vacía")
        nodo = self._tope
        self._tope = nodo.siguiente
        self._tamano -= 1
        return nodo.dato

    def peek(self):
        """Consulta el tope sin quitarlo. O(1)."""
        if self.esta_vacia():
            raise IndexError("La pila está vacía")
        return self._tope.dato


# ---------------------------------------------------------------------------
# 2. Evaluación de expresiones
# ---------------------------------------------------------------------------
PRECEDENCIA = {"+": 1, "-": 1, "*": 2, "/": 2, "neg": 3, "^": 4}
ASOCIATIVIDAD_DERECHA = {"^", "neg"}
BINARIOS = {"+", "-", "*", "/", "^"}


def validar_parentesis(expresion):
    """True si los paréntesis están balanceados (usa la pila)."""
    pila = PilaDinamica()
    for c in expresion:
        if c == "(":
            pila.push(c)
        elif c == ")":
            if pila.esta_vacia():
                return False
            pila.pop()
    return pila.esta_vacia()


def tokenizar(expresion):
    """Convierte una expresión infija en una lista de tokens.
    El '-' unario (al inicio, tras '(' o tras un operador) se marca como 'neg'."""
    tokens = []
    i = 0
    while i < len(expresion):
        c = expresion[i]
        if c.isspace():
            i += 1
        elif c.isdigit() or c == ".":
            j = i
            while j < len(expresion) and (expresion[j].isdigit() or expresion[j] == "."):
                j += 1
            tokens.append(float(expresion[i:j]))
            i = j
        elif c in "+-*/^()":
            if c == "-" and (not tokens or tokens[-1] in BINARIOS or tokens[-1] in ("(", "neg")):
                tokens.append("neg")
            else:
                tokens.append(c)
            i += 1
        else:
            raise ValueError(f"Carácter no válido: '{c}'")
    return tokens


def infija_a_postfija(expresion):
    """Algoritmo Shunting-Yard. Devuelve la lista de tokens en notación postfija."""
    if not validar_parentesis(expresion):
        raise ValueError("Paréntesis desbalanceados")
    salida = []
    pila = PilaDinamica()
    for t in tokenizar(expresion):
        if isinstance(t, float):
            salida.append(t)
        elif t == "neg":
            pila.push(t)  # operador prefijo: se apila directamente
        elif t == "(":
            pila.push(t)
        elif t == ")":
            while pila.peek() != "(":
                salida.append(pila.pop())
            pila.pop()  # descarta '('
        else:  # operador binario
            while (not pila.esta_vacia() and pila.peek() != "("
                   and (PRECEDENCIA[pila.peek()] > PRECEDENCIA[t]
                        or (PRECEDENCIA[pila.peek()] == PRECEDENCIA[t]
                            and t not in ASOCIATIVIDAD_DERECHA))):
                salida.append(pila.pop())
            pila.push(t)
    while not pila.esta_vacia():
        salida.append(pila.pop())
    return salida


def evaluar_postfija(tokens):
    """Evalúa una lista de tokens postfija usando la pila."""
    pila = PilaDinamica()
    for t in tokens:
        if isinstance(t, float):
            pila.push(t)
        elif t == "neg":
            pila.push(-pila.pop())
        else:
            b = pila.pop()
            a = pila.pop()
            if t == "+":
                pila.push(a + b)
            elif t == "-":
                pila.push(a - b)
            elif t == "*":
                pila.push(a * b)
            elif t == "/":
                if b == 0:
                    raise ZeroDivisionError("División entre cero")
                pila.push(a / b)
            elif t == "^":
                pila.push(a ** b)
    resultado = pila.pop()
    if not pila.esta_vacia():
        raise ValueError("Expresión mal formada")
    return resultado


def postfija_desde_texto(texto):
    """Convierte '5 1 2 + 4 * + 3 -' en tokens listos para evaluar."""
    tokens = []
    for p in texto.split():
        tokens.append(p if p in BINARIOS else float(p))
    return tokens


def formatear(tokens):
    return " ".join("neg" if t == "neg" else (f"{t:g}" if isinstance(t, float) else t)
                    for t in tokens)


# ---------------------------------------------------------------------------
# 3. Ordenamiento y búsqueda sobre los resultados
# ---------------------------------------------------------------------------
def insercion(lista):
    """Ordenamiento por inserción. Devuelve (lista_ordenada, comparaciones)."""
    a = list(lista)
    comparaciones = 0
    for i in range(1, len(a)):
        clave = a[i]
        j = i - 1
        while j >= 0:
            comparaciones += 1
            if a[j] > clave:
                a[j + 1] = a[j]
                j -= 1
            else:
                break
        a[j + 1] = clave
    return a, comparaciones


def busqueda_secuencial(lista, objetivo):
    """Devuelve (indice o -1, comparaciones)."""
    for i, x in enumerate(lista):
        if x == objetivo:
            return i, i + 1
    return -1, len(lista)


def busqueda_binaria(lista_ordenada, objetivo):
    """Devuelve (indice o -1, comparaciones). Requiere lista ordenada."""
    bajo, alto, comparaciones = 0, len(lista_ordenada) - 1, 0
    while bajo <= alto:
        medio = (bajo + alto) // 2
        comparaciones += 1
        if lista_ordenada[medio] == objetivo:
            return medio, comparaciones
        if lista_ordenada[medio] < objetivo:
            bajo = medio + 1
        else:
            alto = medio - 1
    return -1, comparaciones


# ---------------------------------------------------------------------------
# 4. Los 10 problemas
# ---------------------------------------------------------------------------
PROBLEMAS = [
    ("infija", "3 + 4 * 2"),
    ("infija", "(3 + 4) * 2"),
    ("infija", "10 / 2 + 3 * 4 - 1"),
    ("infija", "2 ^ 3 ^ 2"),
    ("infija", "(8 - 2) * (5 + 3) / 4"),
    ("infija", "-3 ^ 2 + 30"),
    ("infija", "100 / (5 * (2 + 3))"),
    ("infija", "((15 - 5) * 2 + 8) / 4 ^ 2"),
    ("postfija", "5 1 2 + 4 * + 3 -"),
    ("postfija", "2 3 4 * + 5 6 + *"),
]

PRUEBAS_ERROR = [
    ("infija", "(3 + 4 * 2"),
    ("infija", "8 / (4 - 4)"),
    ("postfija", "3 +"),
]


def resolver(tipo, texto):
    """Devuelve (postfija_como_texto, resultado)."""
    if tipo == "infija":
        tokens = infija_a_postfija(texto)
    else:
        tokens = postfija_desde_texto(texto)
    return formatear(tokens), evaluar_postfija(tokens)


def main():
    print("=" * 78)
    print("EVALUACIÓN DE EXPRESIONES CON PILA DINÁMICA")
    print("=" * 78)
    resultados = []
    for n, (tipo, texto) in enumerate(PROBLEMAS, 1):
        post, res = resolver(tipo, texto)
        resultados.append(res)
        print(f"{n:>2}. [{tipo:<8}] {texto:<28} postfija: {post:<22} = {res:g}")

    print("\nPruebas de error (deben fallar de forma controlada):")
    for tipo, texto in PRUEBAS_ERROR:
        try:
            resolver(tipo, texto)
            print(f"  {texto!r}: NO falló (inesperado)")
        except (ValueError, ZeroDivisionError, IndexError) as e:
            print(f"  {texto!r}: {type(e).__name__}: {e}")

    ordenados, comp = insercion(resultados)
    print("\nResultados originales:", [f"{r:g}" for r in resultados])
    print("Ordenados (inserción):", [f"{r:g}" for r in ordenados], f"-> {comp} comparaciones")
    for objetivo in (21.0, 14.0, 99.0):
        i1, c1 = busqueda_secuencial(ordenados, objetivo)
        i2, c2 = busqueda_binaria(ordenados, objetivo)
        print(f"Buscar {objetivo:g}: secuencial -> idx {i1} ({c1} comp.), "
              f"binaria -> idx {i2} ({c2} comp.)")


if __name__ == "__main__":
    main()