import yahoofinance.Stock

/*
This class have been created ir order to put everything related with the YahooFinance API
 */
object Finance {
    //It founds the price of a stock with its Ticker
    fun encontrarPrecioApi(ticker: String?): BigDecimal {
        return try {
            val stock: Stock = YahooFinance.get(ticker)
            stock.getQuote().getPrice()
        } catch (e: Exception) {
            BigDecimal(0)
        }
    }
}