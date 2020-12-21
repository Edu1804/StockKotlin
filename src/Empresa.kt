import java.sql.DriverManager
import java.lang.Exception
import java.sql.ResultSet
import kotlin.jvm.JvmStatic
import java.io.FileReader
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class Empresa {
    //Main attributes that are not expected to change during the execution of the code
    var nombre: String
        private set
    var sector: String
        private set
    var subsector: String
        private set
    var ticker: String
        private set

    //Attributes for the basic analysis
    var payout_fcf = 0f
        private set
    var ev_fcf = 0f
        private set
    var ev_ebitda = 0f
        private set

    //Atrributes for the advanced analysis
    var p_b = 0f
        private set
    var p_s = 0f
        private set
    var per = 0f
        private set

    //Atrribute for the calification based in others (atrributes)
    var calificacion = 0f
        private set

    //Atrribute necessary to storage the price (it changes during the code execution many times)
    private var cotizacion: BigDecimal? = null

    //Other atrributes that have been created in order to support the others
    var acciones = 0f
        private set
    var cash = 0f
        private set
    var deuda = 0f
        private set
    var fcf = 0f
        private set
    var ebitda = 0f
        private set

    //Other atrributes for the advanced analysis that have been created in order to support the others
    private var ben_neto = 0f
    private var equity = 0f
    private var net_revenues = 0f

    //Empty basic constructor
    constructor() {
        nombre = ""
        sector = ""
        subsector = ""
        ticker = ""
    }

    //constructor for the Ticker
    constructor(ticker: String) : this() {
        this.ticker = ticker
    }

    //Full constructor
    constructor(
        ticker: String,
        nombre: String,
        sector: String,
        subsector: String,
        payout_fcf: Float,
        ev_fcf: Float,
        ev_ebitda: Float,
        p_b: Float,
        p_s: Float,
        per: Float,
        puntuacion: Float,
        acciones: Float,
        cash: Float,
        deuda: Float,
        fcf: Float,
        ebitda: Float,
        ben_neto: Float,
        equity: Float,
        net_revenues: Float
    ) {
        this.nombre = nombre
        this.sector = sector
        this.subsector = subsector
        this.ticker = ticker
        this.payout_fcf = payout_fcf
        this.ev_fcf = ev_fcf
        this.ev_ebitda = ev_ebitda
        this.p_b = p_b
        this.p_s = p_s
        this.per = per
        calificacion = calificacion
        this.cash = cash
        this.deuda = deuda
        this.acciones = acciones
        this.fcf = fcf
        this.ebitda = ebitda
        this.ben_neto = ben_neto
        this.equity = equity
        this.net_revenues = net_revenues
    }

    //Equals method
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val empresa = o as Empresa
        return ticker == empresa.ticker
    }

    //String toString method
    override fun toString(): String {
        return """Empresa: $nombre
     Sector: $sector
     Subsector: $subsector
     Ticker: $ticker
     PayOut/FCF: $payout_fcf%
     EV/FCF: $ev_fcf
     EV/EBITDA: $ev_ebitda
     PER: $per
     P/B: $p_b
     P/S: $p_s
     Puntuación: $calificacion"""
    }

    //Basic method of hashCode
    override fun hashCode(): Int {
        return Objects.hash(ticker)
    }

    //Main function that introduce all the parameters of a stock
    fun introducirParametros() {
        val entrada = Scanner(System.`in`)
        println("Digite las caracteristicas de la empresa:")
        println("Introduzca el nombre de la empresa: ")
        nombre = entrada.nextLine()
        println("Elige la opción del sector al que pertenece: ")
        val indice = elegir(sectores)
        sector = sectores[indice]
        println("Elige la opción del subsector al que pertenece: ")
        subsector = elegirSubSector(indice)
        println("Introduzca el ticker de la empresa: ")
        ticker = entrada.nextLine()
        cotizacion = Finance.encontrarPrecioApi(ticker)
        println("Introduzca el numero de acciones diluidas de la empresa: ")
        acciones = entrada.nextFloat()
        entrada.nextLine()
        println("Indique de cuanto es el dividendo cada vez que te pagan: ")
        var dividendo = entrada.nextFloat()
        entrada.nextLine()
        dividendo = calculo_dividendo(tipoDividendo(), dividendo)
        val payout_total = calculo_payout(acciones, dividendo)
        println("Introduzca el FCF de la empresa en el año fiscal actual: ")
        fcf = entrada.nextFloat()
        payout_fcf = Payout_FCF(payout_total, fcf)
        entrada.nextLine()
        println("Introduzca la deuda a largo plazo: ")
        val deuda_largo = entrada.nextFloat()
        entrada.nextLine()
        println("Introduzca la deuda a corto plazo: ")
        val deuda_corto = entrada.nextFloat()
        entrada.nextLine()
        println("Introduzca el dinero en efectivo (Cash & cash equivalents): ")
        cash = entrada.nextFloat()
        entrada.nextLine()
        val capital = calculo_capitalizacion(cotizacion, acciones)
        deuda = deuda_neta(deuda_largo, deuda_corto)
        val enterprise = enterprise_value(capital, cash, deuda)
        ev_fcf = EV_FCF(enterprise, fcf)
        println("Introduzca el beneficio neto: ")
        val ebit = entrada.nextFloat()
        entrada.nextLine()
        println("Introduzca la depreciación: ")
        val depreciacion = entrada.nextFloat()
        entrada.nextLine()
        println("Introduzca la amortización: ")
        val amortizacion = entrada.nextFloat()
        entrada.nextLine()
        ebitda = EBITDA(ebit, depreciacion, amortizacion)
        ev_ebitda = EV_EBITDA(enterprise, ebitda)
    }

    //Function necessary if you are doing an advanced analysis
    fun introducirParametrosAvanzados() {
        val entrada = Scanner(System.`in`)
        println("Introduzca el beneficio neto(net income): ")
        ben_neto = entrada.nextFloat()
        entrada.nextLine()
        per = PER(cotizacion, acciones, ben_neto)
        println("Introduzca el patrimono(equity): ")
        equity = entrada.nextFloat()
        entrada.nextLine()
        p_b = P_B(cotizacion, acciones, equity)
        println("Introduzca el total de ventas(net revenue): ")
        net_revenues = entrada.nextFloat()
        entrada.nextLine()
        p_s = P_S(cotizacion, acciones, net_revenues)
    }

    //This functions are necessaries for the 'Sector' && 'Subsector'
    private fun elegir(elementos: Array<String>): Int {
        var comprobar = false
        val entrada = Scanner(System.`in`)
        var num = 0
        while (!comprobar) {
            for (i in elementos.indices) {
                println("${i+1}. " + elementos[i])
            }
            num = entrada.nextInt()
            entrada.nextLine()
            comprobar = num <= elementos.size && num > 0
            if (!comprobar) {
                println("Tienes que introducir un índice válido: ")
            }
        }
        return num - 1
    }

    private fun elegirSubSector(indice: Int): String {
        if (indice == 0) {
            return subsectores1[elegir(subsectores1)]
        } else if (indice == 1) {
            return subsectores2[elegir(subsectores2)]
        } else if (indice == 2) {
            return subsectores3[elegir(subsectores3)]
        }
        return ""
    }

    //It calculates the punctuation of a basic analysis
    fun aniadirPuntuacion() {
        calificacion = (sumaEV_FCF(ev_fcf) * 1.66 + sumaEV_EBITDA(ev_ebitda) * 1.66).toFloat()
    }

    //It calculates the punctuation of an advanced analysis
    fun aniadirPuntuacionAvanzada() {
        calificacion = (sumaEV_FCF(ev_fcf) + sumaEV_EBITDA(ev_ebitda) + sumaPER(
            per
        ) + sumaPB(p_b) + sumaPS(p_s))
    }

    //It updates all the data based in the Stock Price
    fun actualizarDatos() {
        cotizacion = Finance.encontrarPrecioApi(ticker)
        val capital = calculo_capitalizacion(cotizacion, acciones)
        val enterprise = enterprise_value(capital, cash, deuda)
        ev_fcf = EV_FCF(enterprise, fcf)
        ev_ebitda = EV_EBITDA(enterprise, ebitda)
        if (per == 0f) {
            aniadirPuntuacion()
        } else {
            aniadirPuntuacionAvanzada()
            per = PER(cotizacion, acciones, ben_neto)
            p_b = P_B(cotizacion, acciones, equity)
            p_s = P_S(cotizacion, acciones, net_revenues)
        }
    }

    companion object {
        //Arrays for the atrributes 'Sector' & 'Subsector'
        var sectores = arrayOf("Defensivo", "Sensitivo", "Ciclico")
        var subsectores1 = arrayOf("Consumo Defensivo", "Salud", "Utilities")
        var subsectores2 = arrayOf("Industrial", "Servicios de Telecomunicacion", "Tecnologia", "Energia")
        var subsectores3 = arrayOf("Consumo Ciclico", "Servicios Financieros", "Real Estate", "Materiales Basicos")

        //It calculates the amount of dividend per year based on the dividend period
        private fun calculo_dividendo(opcion: Int, dividendo: Float): Float {
            var dividendo = dividendo
            if (opcion == 1) {
                dividendo *= 4f
            } else if (opcion == 2) {
                dividendo *= 2f
            }
            return dividendo
        }

        //Auxiliary menu for the dividend
        private fun mensajeDividendo() {
            println("¿Es es dividendo trimestral, semestral o anual?")
            println("Indique el tipo de dividendo que es:")
            println("1.Dividendo trimestral")
            println("2.Dividendo semestral")
            println("3.Dividendo anual")
        }

        //Auxiliary function for 'calculo_dividendo'
        private fun tipoDividendo(): Int {
            mensajeDividendo()
            val entrada = Scanner(System.`in`)
            val opcion = entrada.nextInt()
            entrada.nextLine()
            return opcion
        }

        //It calculates the PayOut of a stock
        private fun calculo_payout(num_acciones: Float, dividendo: Float): Float {
            return num_acciones * dividendo
        }

        //It calculates the PayOut/FCF of a stock
        private fun Payout_FCF(payout: Float, fcf: Float): Float {
            return payout / fcf * 100
        }

        //It calculates the capitalization of a stock
        private fun calculo_capitalizacion(cotizacion: BigDecimal?, acciones: Float): Float {
            //BigDecimal cotizacion = new BigDecimal(2.36359);
            val rounded = cotizacion!!.setScale(2, RoundingMode.DOWN).toFloat()
            return rounded * acciones
        }

        //It calculates the net debt of a stock
        private fun deuda_neta(deudalargo: Float, deudacorto: Float): Float {
            return deudacorto + deudalargo
        }

        //It calculates the Enterprise Value of a stock
        private fun enterprise_value(calculo_capitalizacion: Float, cash: Float, deuda_neta: Float): Float {
            return calculo_capitalizacion + deuda_neta - cash
        }

        //It calculates the EV/FCF of a stock
        private fun EV_FCF(enterprise_value: Float, fcf: Float): Float {
            return enterprise_value / fcf
        }

        //It calculates the EBITDA of a stock
        private fun EBITDA(income: Float, deprecia: Float, amort: Float): Float {
            return income + deprecia + amort
        }

        //It calculates the EV/EBITDA of a stock
        private fun EV_EBITDA(ev: Float, ebitda: Float): Float {
            return ev / ebitda
        }

        //It calculates the PER of a stock
        private fun PER(cotizacion: BigDecimal?, acciones: Float, ben_neto: Float): Float {
            val rounded = cotizacion!!.setScale(2, RoundingMode.DOWN).toFloat()
            return rounded / (ben_neto / acciones)
        }

        //It calculates the P/B ratio of a stock
        private fun P_B(cotizacion: BigDecimal?, acciones: Float, equity: Float): Float {
            val rounded = cotizacion!!.setScale(2, RoundingMode.DOWN).toFloat()
            return rounded / (equity / acciones)
        }

        //It calculates the P/S ratio of a stock
        private fun P_S(cotizacion: BigDecimal?, acciones: Float, net_revenues: Float): Float {
            val rounded = cotizacion!!.setScale(2, RoundingMode.DOWN).toFloat()
            return rounded / (net_revenues / acciones)
        }

        //Auxiliary method for the punctuation
        fun sumaEV_FCF(ev_fcf: Float): Float {
            var nota = 0f
            var i = 5
            var barometro = 24
            do {
                nota += 0.5f
                barometro = barometro - 2
                i--
            } while (i > 0 && ev_fcf < barometro)
            return nota
        }

        //Auxiliary method for the punctuation
        fun sumaEV_EBITDA(ev_ebitda: Float): Float {
            var nota = 0f
            var i = 5
            var barometro = 18
            do {
                nota += 0.5f
                barometro -= 2
                i--
            } while (i > 0 && ev_ebitda < barometro)
            return nota
        }

        //Auxiliary method for the punctuation
        fun sumaPER(per: Float): Float {
            var nota = 0f
            var i = 3
            var barometro = 20
            do {
                nota += 0.25f
                barometro -= 3
                i--
            } while (i > 0 && per < barometro)
            return nota
        }

        //Auxiliary method for the punctuation
        fun sumaPB(p_b: Float): Float {
            var nota = 0f
            var i = 5
            var barometro = 5.9.toFloat()
            do {
                nota += 0.25f
                barometro -= 0.5f
                i--
            } while (i > 0 && p_b < barometro)
            return nota
        }

        //Auxiliary method for the punctuation
        fun sumaPS(p_s: Float): Float {
            var nota = 0f
            var i = 3
            var barometro = 3.7.toFloat()
            do {
                nota += 0.25f
                barometro -= 0.3f
                i--
            } while (i > 0 && p_s < barometro)
            return (nota * 1.5).toFloat()
        }
    }
}