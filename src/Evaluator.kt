import org.jetbrains.annotations.Mutable

/**
 * Created by alanfortlink on 6/11/17.
 */

class Evaluator{

    var distances = mutableListOf<MutableList<Int>>()

    fun calcDistances(customers: MutableList<Customer>){
        for(customerA in customers){

            var distancesA = mutableListOf<Int>()

            for(customerB in customers){
                var distance = Math.floor( Math.sqrt( Math.pow((customerA.x - customerB.x).toDouble(), 2.0) + Math.pow((customerA.y - customerB.y).toDouble(), 2.0) ) + 0.5 ).toInt()
                distancesA.add(distance)
            }

            distances.add(distancesA)

        }
    }

    fun getVehicleUsedCapacity(vehicle: Vehicle) : Int{
        var usedCapacity = 0

        for(customer in vehicle.customers){
            usedCapacity += customer.demand
        }

        return usedCapacity
    }

    fun getAdditionCost(vehicleOut: Vehicle, vehicleIn: Vehicle, cOut: Customer) : Int{

        var prevIndexOut = (vehicleOut.customers.indexOf(cOut)-1) % vehicleOut.customers.size
        var nextIndexOut = (vehicleOut.customers.indexOf(cOut)+1) % vehicleOut.customers.size

        var costOfRemoving = distances[vehicleOut.customers[prevIndexOut].index][cOut.index] + distances[cOut.index][vehicleOut.customers[nextIndexOut].index]
                            - distances[vehicleOut.customers[prevIndexOut].index][vehicleOut.customers[nextIndexOut].index]

        var costOfAdding = distances[vehicleIn.customers[vehicleIn.customers.size - 1].index][0] -
                            distances[vehicleIn.customers[vehicleIn.customers.size - 1].index][cOut.index] + distances[cOut.index][0]


        if(getVehicleUsedCapacity(vehicleIn) + cOut.demand > vehicleIn.capacity){
            return Int.MIN_VALUE
        }


        return costOfAdding + costOfRemoving

    }

    fun getExchangeCost(vOut: Vehicle, cOut: Customer, vIn: Vehicle, cIn: Customer) : Int{

        var exchangeCost = 0

        var prevIndexOut = (vOut.customers.indexOf(cOut)-1) % vOut.customers.size
        var nextIndexOut = (vOut.customers.indexOf(cOut)+1) % vOut.customers.size

        var prevIndexIn = (vIn.customers.indexOf(cIn)-1) % vIn.customers.size
        var nextIndexIn = (vIn.customers.indexOf(cIn)+1) % vIn.customers.size

        var prevOut = vOut.customers[prevIndexOut]
        var nextOut = vOut.customers[nextIndexOut]

        var prevIn = vIn.customers[prevIndexIn]
        var nextIn = vIn.customers[nextIndexIn]

        var outCost =  (distances[prevOut.index][cOut.index] + distances[cOut.index][nextOut.index]) - (distances[prevOut.index][cIn.index] + distances[cIn.index][nextOut.index])
        var inCost =  (distances[prevIn.index][cIn.index] + distances[cIn.index][nextIn.index]) - (distances[prevIn.index][cOut.index] + distances[cOut.index][nextIn.index])

        exchangeCost = outCost + inCost


        if(getVehicleUsedCapacity(vOut) - cOut.demand + cIn.demand > vOut.capacity || getVehicleUsedCapacity(vIn) - cIn.demand + cOut.demand > vIn.capacity){
            return Int.MIN_VALUE
        }

        return exchangeCost

    }

    fun getVehicleCost(vehicle: Vehicle) : Int{

        var vehicleCost = 0

        for(i in 0..vehicle.customers.size){
            var customerDistance = distances[vehicle.customers[i % vehicle.customers.size].index][vehicle.customers[(i+1) % vehicle.customers.size].index]

            vehicleCost += customerDistance
        }

        return vehicleCost

    }

    fun getSolutionCost(solution: Solution) : Int{


        var solutionCost = 0

        for(vehicle in solution.vehicles){

            solutionCost += getVehicleCost(vehicle)

        }

        return solutionCost

    }

}
