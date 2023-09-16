package com.paprika.plugins

import com.paprika.database.models.dish.DishModel
import com.paprika.services.MPSolverService
import io.ktor.server.application.*

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