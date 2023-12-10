package com.paprika.plugins

import com.paprika.database.models.dish.DishModel
import com.paprika.services.MPSolverService
import io.ktor.server.application.*

/*

    This plugin was created to init solver at the same time that project starts
    It is used for optimization request processing (first start of solver is too long, so we do it when project starts and then it works faster)

 */
fun Application.mpSolverInitializer() {
    val solver = MPSolverService.initSolver {
        answersCount(2)
        onDirection(MPSolverService.SolveDirection.MAXIMIZE)

        setConstraint {
            name = "Calories"
            bottom = 1.0
            top = 2.0
            modelKey = DishModel.calories
        }

        onData(listOf())
        withObjective(DishModel.calories)
    }

    solver.solve()
}