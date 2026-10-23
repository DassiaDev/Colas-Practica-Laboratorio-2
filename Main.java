import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static ListaEstudiantes lista = new ListaEstudiantes();

    // Clase que representa a un estudiante
    static class Estudiante {
        String matricula;
        String nombre;
        double promedio;

        Estudiante(String matricula, String nombre, double promedio) {
            this.matricula = matricula;
            this.nombre = nombre;
            this.promedio = promedio;
        }

        void mostrar() {
            System.out.printf("%-12s %-22s %8.2f%n",
                    matricula, nombre, promedio);
        }
    }

    // Nodo de la lista enlazada
    static class Nodo {
        Estudiante dato;
        Nodo siguiente;

        Nodo(Estudiante dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    // Estructura de datos: lista enlazada simple
    static class ListaEstudiantes {
        Nodo cabeza;
        int cantidad = 0;

        // Inserción al final: O(n)
        void agregar(Estudiante estudiante) {
            Nodo nuevo = new Nodo(estudiante);

            if (cabeza == null) {
                cabeza = nuevo;
            } else {
                Nodo actual = cabeza;

                while (actual.siguiente != null) {
                    actual = actual.siguiente;
                }

                actual.siguiente = nuevo;
            }

            cantidad++;
        }

        // Búsqueda lineal: O(n)
        Estudiante buscar(String matricula) {
            Nodo actual = cabeza;

            while (actual != null) {
                if (actual.dato.matricula.equalsIgnoreCase(matricula)) {
                    return actual.dato;
                }
                actual = actual.siguiente;
            }

            return null;
        }

        // Eliminación por matrícula: O(n)
        boolean eliminar(String matricula) {
            if (cabeza == null) {
                return false;
            }

            if (cabeza.dato.matricula.equalsIgnoreCase(matricula)) {
                cabeza = cabeza.siguiente;
                cantidad--;
                return true;
            }

            Nodo actual = cabeza;

            while (actual.siguiente != null) {
                if (actual.siguiente.dato.matricula
                        .equalsIgnoreCase(matricula)) {
                    actual.siguiente = actual.siguiente.siguiente;
                    cantidad--;
                    return true;
                }

                actual = actual.siguiente;
            }

            return false;
        }

        // Copia los nodos a un arreglo dinámico
        ArrayList<Estudiante> aLista() {
            ArrayList<Estudiante> datos = new ArrayList<>();
            Nodo actual = cabeza;

            while (actual != null) {
                datos.add(actual.dato);
                actual = actual.siguiente;
            }

            return datos;
        }

        // Reconstruye la lista enlazada con el orden obtenido
        void actualizar(ArrayList<Estudiante> datos) {
            cabeza = null;
            cantidad = 0;

            for (Estudiante e : datos) {
                agregar(e);
            }
        }

        void mostrarTodos() {
            if (cabeza == null) {
                System.out.println("No hay estudiantes registrados.");
                return;
            }

            System.out.printf("%-12s %-22s %8s%n",
                    "MATRICULA", "NOMBRE", "PROMEDIO");
            System.out.println("-----------------------------------------------");

            Nodo actual = cabeza;

            while (actual != null) {
                actual.dato.mostrar();
                actual = actual.siguiente;
            }
        }
    }

    // Ordenamiento Burbuja: O(n^2)
    static void burbuja(ArrayList<Estudiante> datos) {
        int n = datos.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (datos.get(j).promedio >
                        datos.get(j + 1).promedio) {

                    Estudiante temporal = datos.get(j);
                    datos.set(j, datos.get(j + 1));
                    datos.set(j + 1, temporal);
                }
            }
        }
    }

    // Ordenamiento por Inserción: O(n^2) promedio
    static void insercion(ArrayList<Estudiante> datos) {
        for (int i = 1; i < datos.size(); i++) {
            Estudiante clave = datos.get(i);
            int j = i - 1;

            while (j >= 0 &&
                    datos.get(j).promedio > clave.promedio) {
                datos.set(j + 1, datos.get(j));
                j--;
            }

            datos.set(j + 1, clave);
        }
    }

    // Ordenamiento por Selección: O(n^2)
    static void seleccion(ArrayList<Estudiante> datos) {
        int n = datos.size();

        for (int i = 0; i < n - 1; i++) {
            int menor = i;

            for (int j = i + 1; j < n; j++) {
                if (datos.get(j).promedio <
                        datos.get(menor).promedio) {
                    menor = j;
                }
            }

            Estudiante temporal = datos.get(i);
            datos.set(i, datos.get(menor));
            datos.set(menor, temporal);
        }
    }

    // Búsqueda binaria por matrícula: O(log n)
    // La lista de trabajo se ordena primero por matrícula.
    static Estudiante busquedaBinaria(String matricula) {
        ArrayList<Estudiante> datos = lista.aLista();

        datos.sort((a, b) ->
                a.matricula.compareToIgnoreCase(b.matricula));

        int inicio = 0;
        int fin = datos.size() - 1;

        while (inicio <= fin) {
            int medio = inicio + (fin - inicio) / 2;
            int comparacion = datos.get(medio).matricula
                    .compareToIgnoreCase(matricula);

            if (comparacion == 0) {
                return datos.get(medio);
            } else if (comparacion < 0) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }

        return null;
    }

    static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    static void registrar() {
        String matricula = leerTexto("Matrícula: ");

        if (matricula.isEmpty()) {
            System.out.println("La matrícula no puede estar vacía.");
            return;
        }

        if (lista.buscar(matricula) != null) {
            System.out.println("Esa matrícula ya está registrada.");
            return;
        }

        String nombre = leerTexto("Nombre: ");

        if (nombre.isEmpty()) {
            System.out.println("El nombre no puede estar vacío.");
            return;
        }

        try {
            System.out.print("Promedio (0 a 10): ");
            double promedio = Double.parseDouble(sc.nextLine());

            if (!Double.isFinite(promedio)
                    || promedio < 0 || promedio > 10) {
                System.out.println("El promedio debe estar entre 0 y 10.");
                return;
            }

            lista.agregar(new Estudiante(matricula, nombre, promedio));
            System.out.println("Estudiante registrado correctamente.");

        } catch (NumberFormatException e) {
            System.out.println("Ingresa un promedio numérico válido.");
        }
    }

    static void buscarLineal() {
        String matricula = leerTexto("Matrícula a buscar: ");
        Estudiante e = lista.buscar(matricula);

        if (e == null) {
            System.out.println("Estudiante no encontrado.");
        } else {
            System.out.println("\nEstudiante encontrado:");
            System.out.printf("%-12s %-22s %8s%n",
                    "MATRICULA", "NOMBRE", "PROMEDIO");
            e.mostrar();
        }
    }

    static void buscarBinaria() {
        String matricula = leerTexto("Matrícula a buscar: ");
        Estudiante e = busquedaBinaria(matricula);

        if (e == null) {
            System.out.println("Estudiante no encontrado.");
        } else {
            System.out.println("\nEstudiante encontrado:");
            System.out.printf("%-12s %-22s %8s%n",
                    "MATRICULA", "NOMBRE", "PROMEDIO");
            e.mostrar();
        }
    }

    static void ordenar() {
        if (lista.cantidad < 2) {
            System.out.println("Registra al menos dos estudiantes.");
            return;
        }

        System.out.println("\nORDENAR POR PROMEDIO ASCENDENTE");
        System.out.println("1. Burbuja");
        System.out.println("2. Inserción");
        System.out.println("3. Selección");
        System.out.print("Selecciona un algoritmo: ");

        String opcion = sc.nextLine();
        ArrayList<Estudiante> datos = lista.aLista();

        switch (opcion) {
            case "1":
                burbuja(datos);
                break;
            case "2":
                insercion(datos);
                break;
            case "3":
                seleccion(datos);
                break;
            default:
                System.out.println("Opción no válida.");
                return;
        }

        lista.actualizar(datos);
        System.out.println("\nEstudiantes ordenados correctamente:");
        lista.mostrarTodos();
    }

    static void eliminar() {
        String matricula = leerTexto("Matrícula a eliminar: ");

        if (lista.eliminar(matricula)) {
            System.out.println("Estudiante eliminado correctamente.");
        } else {
            System.out.println("No se encontró esa matrícula.");
        }
    }

    public static void main(String[] args) {
        String opcion;

        do {
            System.out.println("\n==================================");
            System.out.println(" SISTEMA DE GESTIÓN DE ESTUDIANTES");
            System.out.println("==================================");
            System.out.println("1. Registrar estudiante");
            System.out.println("2. Mostrar estudiantes");
            System.out.println("3. Buscar (lineal)");
            System.out.println("4. Buscar (binaria)");
            System.out.println("5. Ordenar por promedio");
            System.out.println("6. Eliminar estudiante");
            System.out.println("0. Salir");
            System.out.print("Selecciona una opción: ");

            opcion = sc.nextLine();

            switch (opcion) {
                case "1" -> registrar();
                case "2" -> lista.mostrarTodos();
                case "3" -> buscarLineal();
                case "4" -> buscarBinaria();
                case "5" -> ordenar();
                case "6" -> eliminar();
                case "0" -> System.out.println("Programa finalizado.");
                default -> System.out.println("Opción no válida.");
            }

        } while (!opcion.equals("0"));

        sc.close();
    }
}
