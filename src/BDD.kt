import java.sql.DriverManager
import java.lang.Exception
import java.sql.ResultSet
import java.util.HashSet
import kotlin.jvm.JvmStatic
import java.io.FileReader
import java.math.BigDecimal
import java.util.Objects
import java.math.RoundingMode
import java.sql.Connection

object BDD {
    private var myConn: Connection? = null

    //Function that provides the connection with our Database in MySQL
    //Sometimes, there is an error related with the 'Time Zone'
    //Just execute this query in mysql: SET GLOBAL time_zone = '+1:00'; (for Spain)

    fun inicializarConexion() {
        val dbUrl = "jdbc:mysql://localhost:3306/stock?useSSL=false"
        val user = "connectionDBA"
        val pass = "Prueba456Prueba123"
        try {
            //Comunicación con la base de datos
            myConn = DriverManager.getConnection(dbUrl, user, pass)
            println("Database connection successful!\n")
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

    /*
    Queries for MySQL in order to create the table
use stock;
drop table if exists empresas;
CREATE TABLE empresas (
    Ticker varchar(15) NOT NULL,
    Nombre varchar(64) DEFAULT NULL,
    Sector varchar(64) DEFAULT NULL,
    Subsector varchar(64) DEFAULT NULL,
    `Payout/FCF(%)`float(4,2) DEFAULT NULL,
    `EV/FCF` float(4,2) DEFAULT NULL,
    `EV/EBITDA` float(4,2) DEFAULT NULL,
    PER float(4,2) DEFAULT NULL,
    `P/B` float(6,3) DEFAULT NULL,
    `P/S` float(6,3) DEFAULT NULL,
    Puntuacion float(6, 3) DEFAULT NULL,
    Numero_Acciones float(9,2) DEFAULT NULL,
    Dinero_Efectivo float(9,2) DEFAULT NULL,
    Deuda float(9,2) DEFAULT NULL,
    Flujo_Caja_Libre float(9,2) DEFAULT NULL,
    EBITDA float(9,2) DEFAULT NULL,
    Beneficio_Neto float(9,2) DEFAULT NULL,
    Equity float(9,2) DEFAULT NULL,
    Net_Revenues float(9,2) DEFAULT NULL,
    PRIMARY KEY (Ticker)
    )


    SELECT Ticker, Nombre, Sector, Subsector, `Payout/FCF(%)`, `EV/FCF`, `EV/EBITDA`, Puntuacion FROM stock.empresas;
SELECT Ticker, Nombre, Sector, Subsector, `Payout/FCF(%)`, `EV/FCF`, `EV/EBITDA`, PER, `P/B`, `P/S` Puntuacion FROM stock.empresas;
     */
    //This function add a stock into the BDD
    fun aniadirNuevaEmpresa(e: Empresa) {
        try {
            val myStmt = myConn!!.createStatement()
            if (comprobarEmpresa(e)) {
                println("Esta empresa ya se ha añadido a la base de datos\n")
            } else {
                val rowsAffected = myStmt.executeUpdate(
                    "insert into empresas " +
                            "(Ticker, Nombre, Sector, Subsector, `Payout/FCF(%)`, `EV/FCF`, `EV/EBITDA`, `PER`, `P/B`, `P/S`, `Puntuacion`, `Numero_Acciones`, `Dinero_Efectivo`, `Deuda`, `Flujo_Caja_Libre`, `EBITDA`)" +
                            " values " + "('" + e.ticker + "'," +
                            "" + " '" + e.nombre + "'," +
                            "" + " '" + e.sector + "'," +
                            "" + " '" + e.subsector + "'," +
                            "" + " '" + e.payout_fcf + "'," +
                            "" + "" + " '" + e.ev_fcf + "'," +
                            "" + " '" + e.ev_ebitda + "'," +
                            "" + " '" + e.per + "'," +
                            "" + " '" + e.p_b + "'," +
                            "" + "'" + e.p_s + "'," +
                            "" + " '" + e.calificacion + "'," +
                            "" + " '" + e.acciones + "'," +
                            "" + " '" + e.cash + "'," +
                            "" + " '" + e.deuda + "'," +
                            "" + " '" + e.fcf + "'," +
                            "" + " '" + e.ebitda + "')"
                )
            }
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

    //Auxiliary function for the function 'aniadirNuevaEmpresa' that checks if a stock already exists in the BDD
    fun comprobarEmpresa(e: Empresa): Boolean {
        var encontrado = false
        try {
            //PreparedStatement myStmt = myConn.prepareStatement("SELECT * FROM empresas WHERE `Ticker`=?");
            //myStmt.setString(1, e.getTicker());
            val myStmt = myConn!!.createStatement()
            var myRs: ResultSet? = null
            myRs = myStmt.executeQuery("SELECT * FROM empresas WHERE `Ticker`='" + e.ticker + "'")
            encontrado = if (!myRs.next()) { //The stock doesn't exist in the BDD
                false
            } else {
                true
            }
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
        return encontrado
    }

    //A function that deletes a stock from the BDD
    //You need to give it the ticker in order to find it in the BDD
    fun eliminarEmpresa(ticker: String) {
        try {
            val myStmt = myConn!!.createStatement()
            val rowsAffected = myStmt.executeUpdate("DELETE FROM empresas WHERE Ticker='$ticker'")
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

    //It shows the BDD in the console of IntelliJ
    fun mostrarBDD() {
        try {
            val myStmt = myConn!!.createStatement()
            val rowsAffected = myStmt.executeQuery("SELECT * FROM empresas")
            while (rowsAffected.next()) {
                //System.out.println("Ticker Nombre Sector Subsector PayOut/FCF(%)  EV/FCF  EV/EBITDA  PER  P/B  P/S\n");
                println(
                    rowsAffected.getString("Ticker") +
                            " " + rowsAffected.getString("Nombre") +
                            "  " + rowsAffected.getString("Sector") +
                            "  " + rowsAffected.getString("Subsector") +
                            "  " + rowsAffected.getFloat("PayOut/FCF(%)") +
                            "  " + rowsAffected.getFloat("EV/FCF") +
                            "  " + rowsAffected.getFloat("EV/EBITDA") +
                            "  " + rowsAffected.getFloat("PER") +
                            "  " + rowsAffected.getFloat("P/B") +
                            "  " + rowsAffected.getFloat("P/S") +
                            "  " + rowsAffected.getFloat("Puntuacion")
                )
                println("\n")
            }
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

    //It returns the number of stocks that we've got in our BDD
    fun listaEmpresas(): Int {
        var num = 0
        try {
            val myStmt = myConn!!.createStatement()
            val rowsAffected = myStmt.executeQuery("SELECT * FROM empresas")
            while (rowsAffected.next()) {
                num++
            }
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
        return num
    }

    //It updates the data of our 'Set' in Java
    //The update is made with the BDD data
    fun actualizarDatosSet(lista: HashSet<Empresa>) {
        try {
            val myStmt = myConn!!.createStatement()
            lista.clear()
            val rowsAffected = myStmt.executeQuery("SELECT * FROM empresas")
            if (rowsAffected.next()) {
                val ticker = rowsAffected.getString("Ticker")
                val nombre = rowsAffected.getString("Nombre")
                val sector = rowsAffected.getString("Sector")
                val subsector = rowsAffected.getString("Subsector")
                val payout_fcf = rowsAffected.getFloat("PayOut/FCF(%)")
                val ev_fcf = rowsAffected.getFloat("EV/FCF")
                val ev_ebitda = rowsAffected.getFloat("EV/EBITDA")
                val p_b = rowsAffected.getFloat("PER")
                val p_s = rowsAffected.getFloat("P/B")
                val per = rowsAffected.getFloat("P/S")
                val puntuacion = rowsAffected.getFloat("Puntuacion")
                val acciones = rowsAffected.getFloat("Numero_Acciones")
                val cash = rowsAffected.getFloat("Dinero_Efectivo")
                val deuda = rowsAffected.getFloat("Deuda")
                val fcf = rowsAffected.getFloat("Flujo_Caja_Libre")
                val ebitda = rowsAffected.getFloat("EBITDA")
                val ben_neto = rowsAffected.getFloat("Beneficio_Neto")
                val equity = rowsAffected.getFloat("Equity")
                val net_revenues = rowsAffected.getFloat("Net_Revenues")
                val e = Empresa(
                    ticker,
                    nombre,
                    sector,
                    subsector,
                    payout_fcf,
                    ev_fcf,
                    ev_ebitda,
                    p_b,
                    p_s,
                    per,
                    puntuacion,
                    acciones,
                    cash,
                    deuda,
                    fcf,
                    ebitda,
                    ben_neto,
                    equity,
                    net_revenues
                )
                lista.add(e)
            }
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

    //It updates a stock based in the price(that is always changing)
    fun actualizarEmpresa(e: Empresa) {
        try {
            val myStmt = myConn!!.createStatement()
            val sql = "UPDATE empresas SET `EV/FCF` = '" + e.ev_fcf + "' WHERE `Ticker`='" + e.ticker + "'"
            val sql2 = "UPDATE empresas SET `EV/EBITDA` = '" + e.ev_ebitda + "' WHERE `Ticker`='" + e.ticker + "'"
            val sql3 = "UPDATE empresas SET `Puntuacion` = '" + e.calificacion + "' WHERE `Ticker`='" + e.ticker + "'"
            val rowsAffected = myStmt.executeUpdate(sql)
            val rowsAffected2 = myStmt.executeUpdate(sql2)
            val rowsAffected3 = myStmt.executeUpdate(sql3)
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }
}