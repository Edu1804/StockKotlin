import java.sql.DriverManager
import java.lang.Exception
import java.sql.ResultSet
import kotlin.jvm.JvmStatic
import java.io.FileReader
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

object Main {
    //HashSt necessary to keep the data from the BDD
    var lista = HashSet<Empresa>()

    //Main function that has got the menus
    @JvmStatic
    fun main(args: Array<String>) {
        var resul = 0
        BDD.inicializarConexion()
        BDD.actualizarDatosSet(lista)
        do {
            resul = mostrarMenuPrincipal()
            menuPrincipal(resul)
        } while (resul != -1)
    }

    //Main menu display that shows all the possible options
    fun mostrarMenuPrincipal(): Int {
        println("Seleccione una opción del menú: ")
        println("1.Añadir empresa")
        println("2.Eliminar empresa")
        println("3.Número total de empresas guardadas")
        println("4.Ver empresas guardadas")
        println("5.Actualizar Datos")
        println("6.Salir")
        val entrada = Scanner(System.`in`)
        var opcion = entrada.nextInt()
        entrada.nextLine()
        if (opcion == 6) {
            opcion = -1
        }
        return opcion
    }

    //Main menu to choose the main options
    fun menuPrincipal(opcion: Int) {
        println("Seleccione una opción: ")
        when (opcion) {
            1 -> {
                //Add a stock to the BDD
                val resul = mostrarMenuAvanzado()
                menu_avanzado(resul)
            }
            2 -> {
                //Delete a stock from the BDD looking for the Ticker
                val ticker = pedirTickerEmpresa()
                borrarEmpresa(ticker)
                BDD.eliminarEmpresa(ticker)
            }
            3 -> {
                //Number of stocks that we've got in the BDD
                //System.out.println(lista.size());
                println("Número de empresas guardadas: ")
                println(BDD.listaEmpresas())
            }
            4 -> {
                //Watch stock in the HashSet and the BDD
                for (e in lista) {
                    println(e.toString())
                }
                BDD.mostrarBDD()
            }
            5 ->                 //Updates the BDD based in the actual price
                actualizarDatosBDD()
            6 -> {
            }
            else -> println("Tiene que introducir uno de los números (1 al 6)")
        }
    }

    //Advanced menu display that shows the options to add a stock
    fun mostrarMenuAvanzado(): Int {
        println("Seleccione una de las 2 opciones: ")
        println("Opción 1. Si lo que desea es obtener un análisis basado en EV/FCF, EV/EBITDA y PayOut/FCF")
        println("Opción 2. Si además quieres tener un análisis más completo añadiendo P/B, P/S y PER")
        val entrada = Scanner(System.`in`)
        val opcion = entrada.nextInt()
        entrada.nextLine()
        return opcion
    }

    //Advanced menu to choose what type of analysis will be done to add a stock
    fun menu_avanzado(opcion: Int) {
        when (opcion) {
            1 -> {
                //Basic analysis
                val e = Empresa()
                e.introducirParametros()
                e.aniadirPuntuacion()
                lista.add(e)
                BDD.aniadirNuevaEmpresa(e)
            }
            2 -> {
                //Advanced analysis
                val b = Empresa()
                b.introducirParametros()
                b.introducirParametrosAvanzados()
                b.aniadirPuntuacionAvanzada()
                lista.add(b)
                BDD.aniadirNuevaEmpresa(b)
            }
            else -> println("Tienes que introducir uno de los números(1 o 2)")
        }
    }

    //It return the Ticker of a stock
    fun pedirTickerEmpresa(): String {
        println("Dime el ticker de la empresa: ")
        val entrada = Scanner(System.`in`)
        val ticker = entrada.next()
        entrada.nextLine()
        return ticker
    }

    //It deletes a stock from the HashSet
    fun borrarEmpresa(ticker: String) {
        val e = Empresa(ticker)
        lista.remove(e)
    }

    //It updates the data from the BDD to the HashSet
    fun actualizarDatosBDD() {
        try {
            for (e in lista) {
                e.actualizarDatos()
                BDD.actualizarEmpresa(e)
            }
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }
}