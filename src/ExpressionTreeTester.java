import java.util.*;

public class ExpressionTreeTester {
	public static final String DATASET1 = "dataset1.csv";
	public static final String DATASET2 = "dataset2.csv";
	public static final String DATASET3 = "dataset3.csv";
	public static final Operator[] OPERATORS = {Operator.ADD, Operator.SUBTRACT, Operator.MULTIPLY, Operator.DIVIDE};
	public static final int GENERATIONS = 10;
	public static final int POPULATION_SIZE = 20000;
	public static final double SURVIVAL_RATE = 0.001;
	public static final int INITIAL_DEPTH = 3;
	public static final int NUMBER_OF_VARIABLES = 1;
	public static final int MIN_COEFFICIENT = -5;
	public static final int MAX_COEFFICIENT = 5;
	public static final int MIN_MUTATION = -1;
	public static final int MAX_MUTATION = 1;
	public static final double MUTATION_RATE = .1;


	/**
	 * Generate a random expression tree
	 * @param depth the depth of the tree
	 * @param numberOfVariables the number of variables in the expression
	 * @param random a random number generator
	 * @param data the data set used to generate the tree's fitness
	 * @return a random expression tree
	 */
	public static ExpressionTree generateRandomTree(int depth, int numberOfVariables, Random random, DataSet data){
		int numberOfOperators = 1;
		for(int i = 0; i < depth; i++){
			numberOfOperators *= 2;
		}
		numberOfOperators -= 1;

		List<Operator> ops = new ArrayList<Operator>();
		for(int i = 0; i < numberOfOperators; i++){
			ops.add(OPERATORS[random.nextInt(OPERATORS.length)]);
		}
		List<String> vars = new ArrayList<String>();
		for(int i = 1; i <= numberOfVariables; i++){
			vars.add("x" + i);
		}
		List<Double> coefficients = new ArrayList<Double>();
		for(int i = 0; i < numberOfOperators - numberOfVariables + 1; i++){
			coefficients.add((double)(random.nextInt(MAX_COEFFICIENT - MIN_COEFFICIENT + 1) + MIN_COEFFICIENT));
		}
		return new ExpressionTree(ops, vars, coefficients, data);
	}

	/**
	 * Selects two expression trees from a list of trees based on their fitness as a proportion of
	 * the total fitness of the list of trees
	 * @param trees the list of trees
	 * @return a list of two expression trees
	 */
	public static ExpressionTree[] selectForCrossover(List<ExpressionTree> trees){
		ExpressionTree[] output = new ExpressionTree[2];
		Random random = new Random(System.nanoTime());
		double tree1 = random.nextDouble();
		double tree2 = random.nextDouble();
		boolean tree1Selected = false;
		boolean tree2Selected = false;
		int treeCount = trees.size();
		double totalFitness = 0.0;
		for(int i = 0; i < treeCount; i++){
			totalFitness += trees.get(i).getFitness();
		}
		double probability = 0.0;
		for(int i = 0; i < treeCount; i++){
			probability += (totalFitness - trees.get(i).getFitness()) / totalFitness;
			if(!tree1Selected && probability > tree1){
				output[0]=trees.get(i);
				tree1Selected = true;	
			}
			if(!tree2Selected && probability > tree2){
				output[1]=trees.get(i);
				tree2Selected = true;
			}
			if(tree1Selected && tree2Selected){
				break;
			}
		}
		if(output[0]==output[1]){
			output[1] = trees.get(random.nextInt(trees.size())); //Force genetic diversity
		}
		return output;
	}

	
	
	public static void main(String[] args) {
		double start = System.nanoTime(); //Run time testing
		DataSet data = new DataSet(DATASET1);

		Random random = new Random(System.nanoTime());

		System.out.println("Now Generating initial population...");
		List<ExpressionTree> trees = new ArrayList<ExpressionTree>();
		for(int i = 0; i < POPULATION_SIZE; i++){
			trees.add(generateRandomTree(INITIAL_DEPTH, NUMBER_OF_VARIABLES, random, data));
		}
		Collections.sort(trees);
		System.out.println("Best initial tree:");
		trees.get(0).print();
		System.out.println(trees.get(0).getFitness());
		System.out.println();

		ExpressionTree bestTree = trees.get(0);
		int i=1;
		//		for(int i = 1; i <= GENERATIONS; i++){
		while(bestTree.getFitness()>20.0){
			System.out.println("Generating generation " + i + " ...");
			List<ExpressionTree> nextGen = new ArrayList<ExpressionTree>();
			int survivors = (int) Math.ceil(POPULATION_SIZE * SURVIVAL_RATE);
			for(int j = 0; j < survivors; j++){
				nextGen.add(trees.get(j));
			}
			while(nextGen.size() < POPULATION_SIZE){
				ExpressionTree[] crossover = selectForCrossover(trees);
				ExpressionTree[] offspring = crossover[0].crossover(crossover[1], data);
				offspring[0].mutate(MUTATION_RATE, MIN_MUTATION, MAX_MUTATION, random);
				offspring[1].mutate(MUTATION_RATE, MIN_MUTATION, MAX_MUTATION, random);
				if(offspring[0].getFitness()!=crossover[0].getFitness() && offspring[0].getFitness()!=crossover[1].getFitness())
				nextGen.add(offspring[0]);
				if(offspring[1].getFitness()!=crossover[0].getFitness() && offspring[1].getFitness()!=crossover[1].getFitness())
				nextGen.add(offspring[1]);
			}
			trees = nextGen;
			Collections.sort(trees);
			if(trees.get(0).getFitness() < bestTree.getFitness()){
				bestTree = trees.get(0);
			}
			System.out.println("Generation " + i + " best tree:");
			trees.get(0).print();
			System.out.println(trees.get(0).getFitness());
			System.out.println();
			i++;
		}
		System.out.println("Best fit:");
		bestTree.print();
		System.out.println(bestTree.getFitness());
		System.out.println("Execution Time (s): " + (System.nanoTime()-start)/1000000000);
	}

}
