package com.paprika.services

import com.google.ortools.Loader
import com.google.ortools.linearsolver.MPConstraint
import com.google.ortools.linearsolver.MPObjective
import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import com.paprika.database.dao.dish.DishDao
import com.paprika.dto.mpsolver.ConstraintDto
import org.jetbrains.exposed.sql.Expression

class MPSolverService internal constructor() {
    enum class SolveDirection {
        MINIMIZE, MAXIMIZE
    }

    companion object {
        fun initSolver(init: MPSolverService.() -> Unit): MPSolverService {
            val solver = MPSolverService()
            solver.apply(init)
            return solver
        }
    }

    private var constraints: MutableList<ConstraintDto> = mutableListOf()
    private var solveDirection: SolveDirection = SolveDirection.MINIMIZE
    private var itemsInAnswer: Int = 2
    private var data: List<DishDao> = listOf()
    private lateinit var solver: MPSolver
    private lateinit var objectiveKey: Expression<*>

    infix fun onDirection(solveDirection: SolveDirection) {
        this.solveDirection = solveDirection
    }
    infix fun answersCount(answersCount: Int) {
        this.itemsInAnswer = answersCount
    }
    infix fun setConstraint(inject: ConstraintDto.() -> Unit) {
        this.constraints.add(ConstraintDto().apply(inject))
    }
    infix fun constraints(constraints: List<ConstraintDto>) {
        this.constraints.addAll(constraints)
    }
    infix fun onData(data: List<DishDao>) {
        this.data = data
    }
    infix fun withObjective(objectiveKey: Expression<*>) {
        this.objectiveKey = objectiveKey
    }

    private val mpVariables
        get() = data.map {
            solver.makeBoolVar(it.name)
        }

    private val mpConstraints
        get() = constraints.map {
            solver.makeConstraint(it.bottom, it.top, it.name)
        }

    private val mpObjective: MPObjective
        get() = solver.objective().apply {
            if (solveDirection == SolveDirection.MAXIMIZE)
                setMaximization()
            else
                setMinimization()
        }

    private fun setCoefficients(constraints: List<MPConstraint>, variables: List<MPVariable>, objective: MPObjective) {
        variables.mapIndexed { varIndex, variable ->
            val dish = data[varIndex].readValues
            constraints.mapIndexed { constIndex, mpConstraint -> run {
                    val constraint = this.constraints[constIndex]
                    if (constraint.bool)
                        mpConstraint.setCoefficient(variable, 1.0)
                    else
                        mpConstraint.setCoefficient(variable, dish[constraint.modelKey] as Double)
                }
            }
            objective.setCoefficient(variable, (dish[objectiveKey] as Int).toDouble())
        }
    }

    private fun initialize(): List<MPVariable> {
        setConstraint {
            name = "count"
            bool = true
            top = itemsInAnswer + 0.1
            bottom = itemsInAnswer - 0.1
        }

        Loader.loadNativeLibraries()
        this.solver = MPSolver.createSolver("SCIP")
        val variables = mpVariables

        setCoefficients(mpConstraints, variables, mpObjective)

        println(solver.constraints())

        return variables
    }

    fun solve(): List<DishDao> {
        println(constraints)
        println(itemsInAnswer)
        println(data.map {
            it.name
        })
        val varsOnSolve = initialize()
        val result = solver.solve()

        // Check that the problem has an optimal solution.
        if (result != MPSolver.ResultStatus.OPTIMAL) {
            println("The problem does not have an optimal solution!")
            if (result == MPSolver.ResultStatus.FEASIBLE) {
                println("A potentially suboptimal solution was found.")
            } else {
                println("The solver could not solve the problem.")
                return listOf()
            }
        }

        val answers = varsOnSolve.filter {
            it.solutionValue() > 0.0
        }

        return answers.map {
            data[varsOnSolve.indexOf(it)]
        }
    }

}