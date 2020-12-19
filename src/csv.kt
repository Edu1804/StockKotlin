import com.opencsv.*

object csv {
    private const val CSV_FILE_PATH = "C:\\Users\\Eduardo\\Documents\\CSV\\pruebacsv.csv"
    private const val CSV_FILE_CUSTOM_SEPERATOR = "C:\\Users\\Eduardo\\Documents\\CSV\\pruebacsv.csv"
    private const val csvFile = "C:\\Users\\Eduardo\\Documents\\CSV\\pruebacsv.csv"
    @JvmStatic
    fun main(args: Array<String>) {
        println("Read Data Line by Line With Header \n")
        readDataLineByLine(CSV_FILE_PATH)
        println("_______________________________________________")
        println("Read All Data at Once and Hide the Header also \n")
        readAllDataAtOnce(CSV_FILE_PATH)
        println("_______________________________________________")
        println("Custom Seperator here semi-colon\n")
        readDataFromCustomSeperator(CSV_FILE_CUSTOM_SEPERATOR)
        println("_______________________________________________")
    }

    fun readDataLineByLine(file: String?) {
        try {

            // Create an object of filereader class
            // with CSV file as a parameter.
            val filereader = FileReader(file)

            // create csvReader object passing
            // filereader as parameter
            val csvReader: com.opencsv.CSVReader = CSVReader(filereader)
            var nextRecord: Array<String>

            // we are going to read data line by line
            while (csvReader.readNext().also { nextRecord = it } != null) {
                for (cell in nextRecord) {
                    print(cell + "\t")
                }
                println("\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun readAllDataAtOnce(file: String?) {
        try {

            // Create an object of filereader class
            // with CSV file as a parameter.
            val filereader = FileReader(file)

            // create csvReader object
            // and skip first Line
            val csvReader: com.opencsv.CSVReader = CSVReaderBuilder(filereader).withSkipLines(1).build()
            val allData: List<Array<String>> = csvReader.readAll()

            // print Data
            for (row in allData) {
                for (cell in row) {
                    print(cell + "\t")
                }
                println()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun readDataFromCustomSeperator(file: String?) {
        try {
            // Create object of filereader
            // class with csv file as parameter.
            val filereader = FileReader(file)

            // create csvParser object with
            // custom seperator semi-colon
            val parser: CSVParser = CSVParserBuilder().withSeparator(';').build()

            // create csvReader object with
            // parameter filereader and parser
            val csvReader: com.opencsv.CSVReader = CSVReaderBuilder(filereader).withCSVParser(parser).build()

            // Read all data at once
            val allData: List<Array<String>> = csvReader.readAll()

            // print Data
            for (row in allData) {
                for (cell in row) {
                    print(cell + "\t")
                }
            }
            println()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}