import java.util.*;

public class ExpressionTreeTester {

	public static final Operator[] OPERATORS = {Operator.ADD, Operator.SUBTRACT, Operator.MULTIPLY, Operator.DIVIDE};
	public static final double SURVIVAL_RATE = 0.00;
	public static final int INITIAL_DEPTH = 4;
	public static final double MUTATION_RATE = .02;
	public static final int NUM_BEST_KEPT = 20;

	public static final double CUT_OFF = 100.0;
	public static final int POPULATION_SIZE = 500;

	public static final String FILE_Name = "dataset2noZero.csv";
	public static final int NUMBER_OF_VARIABLES = 3;
	public static final int MIN_COEFFICIENT = -100;
	public static final int MAX_COEFFICIENT = 100;
	public static final int MIN_MUTATION = -2;
	public static final int MAX_MUTATION = 2;

	/**
	 * Generate a random expression tree
	 * @param depth the depth of the tree
	 * @param numberOfVariables the number of variables in the expression
	 * @param random a random number generator
	 * @param data the data set used to generate the tree's fitness
	 * @return a random expression tree
	 */
	public static ExpressionTree generateRandomTree(int depth, int numberOfVariables, Random random){
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
			//For real coefficients
			coefficients.add((double)(random.nextDouble() * (MAX_COEFFICIENT - MIN_COEFFICIENT + 1) + MIN_COEFFICIENT));
			//For integer coefficient
			//			coefficients.add((double)(random.nextInt(MAX_COEFFICIENT - MIN_COEFFICIENT + 1) + MIN_COEFFICIENT));
		}
		return new ExpressionTree(ops, vars, coefficients);
	}

	/**
	 * Selects two expression trees from a list of trees based on their fitness as a proportion of
	 * the total fitness of the list of trees
	 * @param trees the list of trees
	 * @return a list of two expression trees
	 */
	public static ExpressionTree[] selectForCrossover(List<ExpressionTree> trees){
		ExpressionTree[] output = new ExpressionTree[2];
		Random random = new Random();
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
				output[0] = trees.get(i);
				tree1Selected = true;
			}
			if(!tree2Selected && probability > tree2){
				output[1] = trees.get(i);
				tree2Selected = true;
			}
			if(tree1Selected && tree2Selected){
				break;
			}
		}
		while(output[0] == output[1]){
			output[1] = trees.get(random.nextInt(treeCount));
		}
		return output;

	}

	public static void main(String[] args) {
		DataSet data = new DataSet(FILE_Name);

		Random random = new Random();

		System.out.println("Generating initial population...");
		List<ExpressionTree> trees = new ArrayList<ExpressionTree>();
		List<ExpressionTree> bestSet = new ArrayList<ExpressionTree>();
		for(int i = 0; i < POPULATION_SIZE; i++){
			ExpressionTree initTree = generateRandomTree(INITIAL_DEPTH, NUMBER_OF_VARIABLES, random);
			initTree.setFitness(data.fitness(initTree, false)); 
			trees.add(initTree);

		}
		Collections.sort(trees);
		System.out.println("Best initial tree:");
		trees.get(0).print();
		System.out.println(trees.get(0).getFitness());
		System.out.println();

		ExpressionTree bestTree = trees.get(0);


		//for(int i = 1; i <= GENERATIONS; i++){
		int gen = 1;
		while(bestSet.size() < NUM_BEST_KEPT){
			System.out.println("Generating generation " + gen + " ...");
			List<ExpressionTree> nextGen = new ArrayList<ExpressionTree>();
			int survivors = (int) Math.ceil(POPULATION_SIZE * SURVIVAL_RATE);
			for(int j = 0; j < survivors; j++){
				nextGen.add(trees.get(j));
			}
			while(nextGen.size() < POPULATION_SIZE){
				ExpressionTree[] crossover = selectForCrossover(trees);
				ExpressionTree[] offspring = crossover[0].crossover(crossover[1], data);
				if(random.nextDouble() < MUTATION_RATE){
					offspring[0].mutate();
					offspring[0].setFitness(data.fitness(offspring[0], false));
				}
				if(random.nextDouble() < MUTATION_RATE){
					offspring[1].mutate();
					offspring[1].setFitness(data.fitness(offspring[1], false));
				}
				offspring[0].mutate(MUTATION_RATE, MIN_MUTATION, MAX_MUTATION, random);
				offspring[1].mutate(MUTATION_RATE, MIN_MUTATION, MAX_MUTATION, random);
				if((offspring[0].getFitness() != crossover[0].getFitness() || offspring[0].getSize()<crossover[0].getSize()) 
						&& (offspring[0].getFitness() != crossover[1].getFitness()) || offspring[0].getSize()<crossover[1].getSize()){
					nextGen.add(offspring[0]);
					if(offspring[1].getFitness() < CUT_OFF){
						bestSet.add(offspring[0]);
					}

				}
				if((offspring[1].getFitness() != crossover[0].getFitness() || offspring[1].getSize()<crossover[0].getSize()) 
						&& (offspring[1].getFitness() != crossover[1].getFitness()) || offspring[1].getSize()<crossover[1].getSize()){
					nextGen.add(offspring[1]);
					if(offspring[1].getFitness() < CUT_OFF){
						bestSet.add(offspring[1]);
					}
				}
			}
			trees = nextGen;
			Collections.sort(trees);
			if(trees.get(0).getFitness() < bestTree.getFitness()){
				bestTree = trees.get(0);
			}
			System.out.println("Generation " + gen + " best tree:");
			trees.get(0).print();
			System.out.println(trees.get(0).getFitness());
			System.out.println();
			gen++;
		}
		System.out.println("Best fit:");
		bestTree.print();
		System.out.println(bestTree.getFitness());

		for(int j=0; j<bestSet.size(); j++){
			bestSet.get(j).setFitness(data.fitness(bestSet.get(j), true));
		}
		Collections.sort(bestSet);
		if(trees.get(0).getFitness() < bestTree.getFitness()){
			bestTree = bestSet.get(0);
		}
		System.out.println("Best fit:");
		bestTree.print();
		System.out.println(bestTree.getFitness());

	}

}
