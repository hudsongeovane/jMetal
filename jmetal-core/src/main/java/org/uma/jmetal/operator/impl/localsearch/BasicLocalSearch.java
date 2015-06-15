package org.uma.jmetal.operator.impl.localsearch;

import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.Comparator;

/**
 * This class implements a basic local search operator based in the use of a
 * mutation operator.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class BasicLocalSearch<S extends Solution<?>> implements LocalSearchOperator<S>{
  private Problem<S> problem;
  private int improvementRounds ;
  private Comparator constraintComparator ;
  private Comparator comparator ;

  private MutationOperator mutationOperator;
  private int evaluations ;

  private JMetalRandom randomGenerator ;
  /**
   * Constructor.
   * Creates a new local search object.
   * @param improvementRounds number of iterations
   * @param mutationOperator mutation operator
   * @param comparator comparator to determine which solution is the best
   * @param problem problem to resolve

   */
  public BasicLocalSearch(int improvementRounds, MutationOperator<S> mutationOperator,
      Comparator<S> comparator, Problem<S> problem){
    this.problem=problem;
    this.mutationOperator=mutationOperator;
    this.improvementRounds=improvementRounds;
    this.comparator  = comparator ;
    constraintComparator = new OverallConstraintViolationComparator();

    randomGenerator = JMetalRandom.getInstance() ;
  }

  /**
   * Executes the local search.
   * @param  solution The solution to improve
   * @return An improved solution
   */
  public S execute(S solution) {
    int i = 0;
    int best ;
    evaluations = 0;

    int rounds = improvementRounds;

    while (i < rounds) {
      S mutatedSolution = (S) mutationOperator.execute(solution.copy());
      if (problem.getNumberOfConstraints() > 0) {

        ((ConstrainedProblem) problem).evaluateConstraints(mutatedSolution);
        best = constraintComparator.compare(mutatedSolution, solution);
        if (best == 0) {
          problem.evaluate(mutatedSolution);
          evaluations++;
          best = comparator.compare(mutatedSolution, solution);
        } else if (best == -1) {
          problem.evaluate(mutatedSolution);
          evaluations++;
        }
      } else {
        problem.evaluate(mutatedSolution);
        evaluations++;
        best = comparator.compare(mutatedSolution, solution);
      }
      if (best == -1) {
        solution = mutatedSolution;
      }
      else if (best == 1) {
        ;
      }
      else {
        if (randomGenerator.nextDouble() < 0.5) {
          solution = mutatedSolution ;
        }
      }
      i++ ;
    }
    return (S) solution.copy();
  }

  /**
   * Returns the number of evaluations
   */
  public int getEvaluations() {
    return evaluations;
  }
}