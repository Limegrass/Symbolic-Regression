import java.util.*;

public class ExpressionTreeTester {
	public static final Operator[] OPERATORS = {Operator.ADD, Operator.SUBTRACT, Operator.MULTIPLY, Operator.DIVIDE};
	public static final int DEPTH = 4;
	public static final int NUMBER_OF_VARIABLES = 1;
	public static final int MIN_COEFFICIENT = -5;
	public static final int MAX_COEFFICIENT = 5;
	public static final int MIN_MUTATION = -3;
	public static final int MAX_MUTATION = 3;
	public static final double MUTATION_RATE = 0.1;


	public static ExpressionTree generateRandomTree(int depth, int numberOfVariables, Random r, DataSet data){
		int numberOfOperators = 1;
		for(int i = 0; i < depth; i++){
			numberOfOperators *= 2;
		}
		numberOfOperators -= 1;

		List<Operator> ops = new ArrayList<Operator>();
		for(int i = 0; i < numberOfOperators; i++){
			ops.add(OPERATORS[r.nextInt(OPERATORS.length)]);
		}
		List<String> vars = new ArrayList<String>();
		for(int i = 1; i <= numberOfVariables; i++){
			vars.add("x" + i);
		}
		List<Double> coefficients = new ArrayList<Double>();
		for(int i = 0; i < numberOfOperators - numberOfVariables + 1; i++){
			coefficients.add((double)(r.nextInt(MAX_COEFFICIENT - MIN_COEFFICIENT + 1) + MIN_COEFFICIENT));
		}
		return new ExpressionTree(ops, vars, coefficients, data);
	}

	public static void main(String[] args) {
		String fileName = "dataset1.csv";
		DataSet data = new DataSet(fileName);
		
		Random r = new Random();
		HashMap<String, Double> variables = new HashMap<String, Double>();
		for(int i = 1; i <= NUMBER_OF_VARIABLES; i++){
			String variableName = "x" + Integer.toString(i);
			variables.put(variableName, r.nextDouble()*10);
		}
		for(String s : variables.keySet()){
			System.out.println(s + " = " + variables.get(s));
		}

		ExpressionTree tree1 = generateRandomTree(DEPTH, NUMBER_OF_VARIABLES, r, data);
		ExpressionTree tree2 = generateRandomTree(DEPTH, NUMBER_OF_VARIABLES, r, data);

		System.out.print("Printing tree1: ");
		tree1.print();
		System.out.print('\n');
		System.out.print("Evaluating tree1: ");
		System.out.println(tree1.evaluate(variables));
		
		/*
		for(HashMap<String,Double> map : data.xValues()){
			System.out.println();
			System.out.println(tree1.evaluate(map));
			System.out.println(data.fx(map));
		}
		*/
		System.out.println(tree1.getFitness());
	}

}
