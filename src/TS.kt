/**
 * Created by alanfortlink on 6/11/17.
 */

fun nextInt(): Int{
    return (Math.random() * 100000000000).toInt();
}

class TS(val instance: Instance, val timeLimit: Int, val numIteration: Int){

    var evaluator = Evaluator();
    var tabuList = mutableListOf<Customer>()
    var tenureSize = 0

    fun solve() : Solution{
        evaluator.calcDistances(instance.customers)

        var solution = getBaseSolution()

        for(i in 1..numIteration){
            println("Iteration ${i}")

            var i = 0
            while(i < solution.vehicles.size){

                if(solution.vehicles[i].customers.size <= 1){
                    solution.vehicles.removeAt(i--)
                }

                i++

            }

            var bestNeighbor = getBestNeighbor(solution)

            if(evaluator.getSolutionCost(bestNeighbor) < evaluator.getSolutionCost(solution)) {
                solution = bestNeighbor
            }
        }

        return solution
    }

    fun getBestNeighbor(source: Solution) : Solution{

        val randomVOut = source.vehicles[maxOf(nextInt() % source.vehicles.size, 1)]
        val randomVIn = source.vehicles[maxOf(1, nextInt() % source.vehicles.size)]

        val randomCOut = randomVOut.customers[maxOf(1, nextInt() % randomVOut.customers.size)]
        val randomCIn = randomVIn.customers[maxOf(1, nextInt() % randomVIn.customers.size)]

        var bestMovementExchange = TabuItem(randomVOut, randomCOut, randomVIn, randomCIn)
        var bestMovementAdding = TabuItem(randomVOut, randomCOut, randomVIn, randomCIn)

        var bestCost = evaluator.getExchangeCost(randomVOut, randomCOut, randomVIn, randomCIn)
        var bestIsExchange = bestCost > evaluator.getAdditionCost(randomVOut, randomVIn, randomCOut)

        for(vehicleOut in source.vehicles){
            for(customerOut in vehicleOut.customers.subList(1, vehicleOut.customers.size)){
                for(vehicleIn in source.vehicles){
                    for(customerIn in vehicleIn.customers.subList(1, vehicleIn.customers.size)){
                        var exchangeCost = evaluator.getExchangeCost(vehicleOut, customerOut, vehicleIn, customerIn)

                        if(exchangeCost > bestCost){
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

                    if(addingCost > bestCost){

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

            newSolution.vehicles[indexOfCustomerOut].customers[indexOfCustomerOut] = bestMovementExchange.customerIn
            newSolution.vehicles[indexOfCustomerIn].customers[indexOfCustomerIn] = bestMovementExchange.customerOut
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

    fun getIntensifiedSolution() : Solution{
        var solution = Solution()


        return solution
    }

}

fun main(args: Array<String>) {
    var filename = "/Users/alanfortlink/Desktop/atividade9/vrp_instances/instance2.vrp"

    var instance = Instance()
    instance.readFromFile(filename)

    var ts = TS(instance, 10000, 1000)

    var solution = ts.solve()

    var evaluator = Evaluator()
    evaluator.calcDistances(instance.customers)


    println(evaluator.getSolutionCost(solution))
}