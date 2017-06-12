import java.io.File

/**
 * Created by alanfortlink on 6/11/17.
 */
class Instance{
    var vehicleCapacity = 0;
    var size = 0;
    var customers = mutableListOf<Customer>()

    fun readFromFile(filename: String){

        var file = File(filename)
        var lines = file.readLines()

        var dimension = lines[3].split(":")[1].trim().toInt()
        var capacity = lines[5].split(":")[1].trim().toInt()

        var customers = mutableListOf<Customer>()

        for(it in lines.subList(7, 7 + dimension)){

            var customer = Customer(it.split("	")[1].trim().toInt(), it.split("	")[2].trim().toInt())
            customer.index = it.split("	")[0].trim().toInt()-1

            customers.add(customer)

        }

        for(it in lines.subList(dimension + 8, dimension + 8 + dimension)){

            var index = it.split("	")[0].trim().toInt() - 1
            var demand = it.split("	")[1].trim().toInt()

            for(customer in customers){
                if(customer.index == index){
                    customer.demand = demand
                }
            }

        }

        this.customers = customers
        this.size = dimension
        this.vehicleCapacity = capacity
    }
}