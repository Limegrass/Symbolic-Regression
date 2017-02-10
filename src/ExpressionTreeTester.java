import java.util.*;

public class ExpressionTreeTester {
	public static Operator[] operators = {Operator.ADD, Operator.SUBTRACT, Operator.MULTIPLY, Operator.DIVIDE};
	public static int depth = 3;
	public static int numberOfVariables = 1;
	public static int minCoefficient = -5;
	public static int maxCoefficient = 5;
	public static int minMutation = -3;
	public static int maxMutation = 3;
	public static double mutationRate = 0.1;


	public static void main(String[] args) {
		Random r = new Random();

		HashMap<String, Double> variables = new HashMap<String, Double>();
		variables.put("x1", 10.0);


		int numberOfOperators = 1;
		for(int i = 0; i < depth; i++){
			numberOfOperators *= 2;
		}
		numberOfOperators -= 1;


		List<Operator> ops = new ArrayList<Operator>();
		for(int i = 0; i < numberOfOperators; i++){
			ops.add(operators[r.nextInt(operators.length)]);
		}
		List<String> vars = new ArrayList<String>();
		for(int i = 1; i <= numberOfVariables; i++){
			vars.add("x" + i);
		}
		List<Double> coefficients = new ArrayList<Double>();
		for(int i = 0; i < numberOfOperators - numberOfVariables + 1; i++){
			coefficients.add((double)(r.nextInt(maxCoefficient - minCoefficient + 1) + minCoefficient));
		}


		ExpressionTree tree1 = new ExpressionTree(ops, vars, coefficients);
		System.out.print("Printing tree1: ");
		ExpressionTree.print(tree1.root);
		System.out.println();
		System.out.println(ExpressionTree.evaluate(tree1.root, variables));

		ExpressionTree tree2 = new ExpressionTree(ops, vars, coefficients);
		System.out.print("Printing tree2: ");
		ExpressionTree.print(tree2.root);
		System.out.println();
	}

}
