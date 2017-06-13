import java.util.*

/**
 * Created by alanfortlink on 6/11/17.
 */

fun nextInt(): Int{
    return (Math.random() * 100000000000).toInt();
}

class TS(val instance: Instance, val timeLimit: Int, val numIteration: Int, val frequencyOfIntensification: Int, val tenureSize: Int, val target: Int){

    var evaluator = Evaluator();
    var tabuList = mutableListOf<Customer>()

    fun solve() : Solution{
        evaluator.calcDistances(instance.customers)

        var solution = getBaseSolution()

        var timeStart = System.currentTimeMillis()
        var timeSpent = System.currentTimeMillis() - timeStart

        var k = 0

        for(i in 1..numIteration){
            var bestNeighbor = getBestNeighbor(solution)

            k += 1
            if(k == frequencyOfIntensification){

                k = 0
                bestNeighbor = getIntensifiedSolution(bestNeighbor)

            }

            if(evaluator.getSolutionCost(bestNeighbor) < evaluator.getSolutionCost(solution)) {
                solution = bestNeighbor
            }

            timeSpent = System.currentTimeMillis() - timeStart

            if(evaluator.getSolutionCost(solution) < target){

                break

            }

            if(timeSpent > timeLimit){
                break
            }
        }

        println("${evaluator.getSolutionCost(solution)} ${timeSpent.toDouble()/1000.0}")

        return solution
    }

    fun getBestNeighbor(source: Solution) : Solution{
        var i = 0
        while(i < source.vehicles.size){

            if(source.vehicles[i].customers.size <= 1){
                source.vehicles.removeAt(i--)
            }

            i++

        }

        val randomVOut = source.vehicles[maxOf(nextInt() % source.vehicles.size, 1)]
        val randomVIn = source.vehicles[maxOf(1, nextInt() % source.vehicles.size)]

        val randomCOut = randomVOut.customers[maxOf(1, nextInt() % randomVOut.customers.size)]
        val randomCIn = randomVIn.customers[maxOf(1, nextInt() % randomVIn.customers.size)]

        var bestMovementExchange = TabuItem(randomVOut, randomCOut, randomVIn, randomCIn)
        var bestMovementAdding = TabuItem(randomVOut, randomCOut, randomVIn, randomCIn)

        var bestCost = evaluator.getExchangeCost(randomVOut, randomCOut, randomVIn, randomCIn)
        var bestIsExchange = bestCost > evaluator.getAdditionCost(randomVOut, randomVIn, randomCOut)

        Collections.shuffle(source.vehicles);

        for(vehicleOut in source.vehicles){
            for(customerOut in vehicleOut.customers.subList(1, vehicleOut.customers.size)){
                for(vehicleIn in source.vehicles){
                    for(customerIn in vehicleIn.customers.subList(1, vehicleIn.customers.size)){
                        var exchangeCost = evaluator.getExchangeCost(vehicleOut, customerOut, vehicleIn, customerIn)

                        if(exchangeCost > bestCost && (!tabuList.contains(customerIn) && !tabuList.contains(customerOut))){
                            bestCost = exchangeCost
                            bestMovementExchange = TabuItem(vehicleOut, customerOut, vehicleIn, customerIn)
                            bestIsExchange = true
                        }

                    }
                }
            }
        }

        for(vehicleIn in source.vehicles){
            for(vehicleOut in source.vehicles){
                if(vehicleOut != vehicleIn)
                    for(customerOut in vehicleOut.customers.subList(1, vehicleOut.customers.size)){

                        var addingCost = evaluator.getAdditionCost(vehicleOut, vehicleIn, customerOut)

                        if(addingCost > bestCost && !tabuList.contains(customerOut)){

                            bestMovementAdding = TabuItem(vehicleOut, customerOut, vehicleIn, customerOut)
                            bestIsExchange = false

                        }

                    }
            }
        }

        if(bestIsExchange){
            tabuList.add(bestMovementExchange.customerIn)
        }else{
            tabuList.add(bestMovementAdding.customerOut)
        }

        if(tabuList.size > tenureSize){
            tabuList.removeAt(0)
        }

        var newSolution = Solution()
        newSolution.vehicles = source.vehicles.toMutableList()

        if(bestIsExchange){
            val indexOfVehicleOut = newSolution.vehicles.indexOf(bestMovementExchange.vehicleOut)
            val indexOfVehicleIn = newSolution.vehicles.indexOf(bestMovementExchange.vehicleIn)

            val indexOfCustomerOut = newSolution.vehicles[indexOfVehicleOut].customers.indexOf(bestMovementExchange.customerOut)
            val indexOfCustomerIn = newSolution.vehicles[indexOfVehicleIn].customers.indexOf(bestMovementExchange.customerIn)

            newSolution.vehicles[indexOfVehicleOut].customers[indexOfCustomerOut] = bestMovementExchange.customerIn
            newSolution.vehicles[indexOfVehicleIn].customers[indexOfCustomerIn] = bestMovementExchange.customerOut
        }else{
            newSolution.vehicles[newSolution.vehicles.indexOf(bestMovementAdding.vehicleIn)].customers.add(bestMovementAdding.customerIn)
            newSolution.vehicles[newSolution.vehicles.indexOf(bestMovementAdding.vehicleOut)].customers.remove(bestMovementAdding.customerOut)
        }

        var newVehicles = mutableListOf<Vehicle>()
        for(vehicle in newSolution.vehicles){
            if(vehicle.customers.size > 1){
                newVehicles.add(vehicle)
            }
        }

        newSolution.vehicles = newVehicles

        return newSolution

    }

    fun getBaseSolution() : Solution{
        var solution = Solution()

        for(customer in instance.customers.subList(1, instance.customers.size)){

            var vehicle = Vehicle()
            vehicle.capacity = instance.vehicleCapacity
            vehicle.customers.add(instance.customers[0])
            vehicle.customers.add(customer)
            solution.vehicles.add(vehicle)

        }

        return solution
    }

    fun getIntensifiedSolution(solution: Solution) : Solution{
        var newNeighbor = getBestNeighbor(solution)

        while(true){
            var searching = getBestNeighbor(newNeighbor)
            if(evaluator.getSolutionCost(searching) > evaluator.getSolutionCost(newNeighbor)){
                newNeighbor = searching
            }else{
                break
            }
        }

        return newNeighbor
    }

}

fun main(args: Array<String>) {
    var filename = args[0]

    var instance = Instance()
    instance.readFromFile(filename)

    var ts = TS(instance, 5 * 60 * 1000, 100000, instance.size/args[1].toInt(), instance.size/args[2].toInt(), args[3].toInt())

    var solution = ts.solve()
}