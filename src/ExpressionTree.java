import java.util.*;

/**
 * Stores an expression in a tree structure with operators as internal nodes
 * variables and coefficients as leaf nodes
 * 
 * @author Chris Lamb
 *
 */
public class ExpressionTree implements Comparable<ExpressionTree>{
	
	/**
	 * Binary expression tree nodes store node type, value, parent node, left child, and right child 
	 */
	public class ExpressionTreeNode {
		private Type type;
		private Object value;
		private ExpressionTreeNode leftChild;
		private ExpressionTreeNode rightChild;
		private ExpressionTreeNode parent;
		
		/**
		 * Constructs an expression tree node
		 * @param type the type of node operator, variable, or coefficient
		 * @param value the data
		 * @throws IllegalArgumentException if the type of the value does not match the type specified
		 */
		public ExpressionTreeNode(Type type, Object value) throws IllegalArgumentException{
			this.type = type;
			if(type == Type.OPERATOR){
				if(!(value instanceof Operator)){
					throw new IllegalArgumentException("value does not match type");
				}
				this.value = (Operator) value;
			}
			else if(type == Type.VARIABLE){
				if(!(value instanceof String)){
					throw new IllegalArgumentException("value does not match type");
				}
				this.value = (String) value;
			}
			else if(type == Type.COEFFICIENT){
				if(!(value instanceof Double)){
					throw new IllegalArgumentException("value does not match type");
				}
				this.value = (Double) value;
			}
			else{
				throw new IllegalArgumentException("Invalid type");
			}
			this.leftChild = null;
			this.rightChild = null;
			this.parent = null;
		}
		
		/**
		 * Prints the expression represented by the node and its children
		 */
		public void print(){
			if(leftChild != null){
				System.out.print("(");
				leftChild.print();
			}
			System.out.print(value.toString() + "");
			if(rightChild != null){
				rightChild.print();
				System.out.print(")");
			}
		}

		/**
		 * Returns the value of the expression
		 * @param variables a map of variable names to values
		 * @return the value of the expression represented by the node and its children
		 * @throws IllegalArgumentException if a variable is not defined in the map
		 */
		public double evaluate(HashMap<String, Double> variables) throws IllegalArgumentException{
			if(type == Type.COEFFICIENT){
				return (double) value;
			}
			else if(type == Type.VARIABLE){
				if(!variables.containsKey(value)){
					throw new IllegalArgumentException("Undefined variable");
				}
				return variables.get(value);
			}
			else{
				double left = leftChild.evaluate(variables);
				double right = rightChild.evaluate(variables);
				if(value == Operator.ADD){
					return left + right;
				}
				else if(value == Operator.SUBTRACT){
					return left - right;
				}
				else if(value == Operator.MULTIPLY){
					return left * right;
				}
				else if(value == Operator.DIVIDE){
<<<<<<< HEAD
					if(right == 0.0 ){
=======
					if(right == 0.0){
>>>>>>> origin/master
						//Dividing by 0 returns 1000000.0 right now we need to figure out what we want to do with this.
						return 1000000.0;
					}
					return left / right;
				}
				return 0.0;
			}
		}

		/**
		 * @return the number of nodes in the tree rooted at the node
		 */
		public int getSize(){
			int leftSize = 0;
			int rightSize = 0;
			if(leftChild != null){
				leftSize = leftChild.getSize();
			}
			if(rightChild != null){
				rightSize = rightChild.getSize();
			}
			return 1 + leftSize + rightSize;
		}
		
		/**
		 * @return a deep copy of the node and its children
		 */
		public ExpressionTreeNode copy(){
			//Recursively creates deep copy of node
			ExpressionTreeNode copy = new ExpressionTreeNode(type, value);
			if(leftChild != null){
				copy.leftChild = leftChild.copy();
				copy.leftChild.parent = copy;
			}

			if(rightChild != null){
				copy.rightChild = rightChild.copy();
				copy.rightChild.parent = copy;
			}
			return copy;
		}
		
		/**
		 * @return a random node in the tree rooted at the node
		 */
		public ExpressionTreeNode getRandomNode(){
			//Get the size of the tree generate a random position less than size return node
			//in that position of in order traversal
			int size = this.getSize();
			Random random = new Random();
			int position = random.nextInt(size-1);
			return getKthNode(position);
		}

		/**
		 * @param k the position of the node to be returned
		 * @return the kth node in an in-order traversal of the tree rooted at the node
		 */
		private ExpressionTreeNode getKthNode(int k){
			int leftSize = 0;
			if(leftChild != null){
				leftSize = leftChild.getSize();
			}
			//If size of left subtree equals k node is kth element 
			if(leftSize == k){
				return this;
			}
			//If left subtree is greater than k kth element is in left subtree
			else if(leftSize > k){
				return leftChild.getKthNode(k);
			}
			//If left subtree is less than k kth element is k - leftSize - 1st element in right subtree
			else{
				return rightChild.getKthNode(k - leftSize - 1);
			}
		}

		/**
		 * If the nodes type is coefficient changes the value of the node by an integer between
		 * minMutation and maxMutation with the probability of mutationRate
		 * @param mutationRate the probablility to mutate the node
		 * @param minMutation the smallest amount to mutate the node by
		 * @param maxMutation the greatest amout to mutate the node by
		 * @param random a random number generator
		 */
		public void mutate(double mutationRate, int minMutation, int maxMutation, Random random){
			//Only mutate coefficients
			if(type == Type.COEFFICIENT){
				if(random.nextDouble() < mutationRate){
					value = (double) value + (double) (random.nextInt(maxMutation - minMutation + 1) + minMutation);
				}
			}
			if(leftChild != null){
				leftChild.mutate(mutationRate, minMutation, maxMutation, random);
			}
			if(rightChild != null){
				rightChild.mutate(mutationRate, minMutation, maxMutation, random);
			}
		}
	}

	private ExpressionTreeNode root;
	private double fitness;
	
	/**
	 * Construcs an expression tree
	 * @param operators a list of operators to add to the tree
	 * @param variables a list of variables to add to the tree
	 * @param coefficients a list of coefficients to add to the tree
	 * @param data a data set to calculate the fitness of the tree
	 */
	public ExpressionTree(List<Operator> operators, List<String> variables, List<Double> coefficients, DataSet data){
		//Queue to store operator nodes
		Queue<ExpressionTreeNode> q1 = new LinkedList<ExpressionTreeNode>();
		//Queue to store terminal nodes
		Queue<ExpressionTreeNode> q2 = new LinkedList<ExpressionTreeNode>();
		for(Operator op : operators){
			q1.add(new ExpressionTreeNode(Type.OPERATOR, op));
		}
		for(String var : variables){
			q2.add(new ExpressionTreeNode(Type.VARIABLE, var));
		}
		for(Double coef: coefficients){
			q2.add(new ExpressionTreeNode(Type.COEFFICIENT, coef));
		}
		//Shuffle q2 so that the variables are not always to the left of the first generation of trees
		Collections.shuffle((List<?>) q2);
		ExpressionTreeNode current = null;
		//While there are still operators remaining
		while(!q1.isEmpty()){
			//Poll an operator from q1
			current = q1.poll();
			//Set its left and right children terminal nodes from q2
			current.leftChild = q2.poll();
			current.leftChild.parent = current;
			current.rightChild = q2.poll();
			current.rightChild.parent = current;
			//Add the newly created terminal node back to q2
			q2.add(current);
		}
		this.root = current;
		this.fitness = data.fitness(this);
	}
	
	/**
	 * Constructs an expression tree
	 * @param root the expression tree node that roots the tree
	 * @param fitness the fitness of the tree
	 */
	public ExpressionTree(ExpressionTreeNode root, double fitness){
		this.root = root;
		this.fitness = fitness;
	}

	/**
	 * @return the root of the tree
	 */
	public ExpressionTreeNode getRoot(){
		return this.root;
	}

	/**
	 * @return the fitness of the tree
	 */
	public double getFitness(){
		return this.fitness;
	}

	/**
	 * 
	 * @param variables a map of variables to values
	 * @return the value of the expression
	 */
	public double evaluate(HashMap<String, Double> variables){
		return root.evaluate(variables);
	}
	
	/**
	 * Prints the expression represented by the tree
	 */
	public void print(){
		root.print();
		System.out.println();
	}
	
	/**
	 * Returns a deep copy of the tree
	 */
	public ExpressionTree clone(){
		if(root == null){
			return null;
		}
		//Deep copies root node and creates new tree rooted at copy
		return new ExpressionTree(root.copy(), fitness);
	}

	/**
	 * Combines two trees into two new trees and returns a list containing the new trees
	 * @param other the tree to be combined with the current tree
	 * @return a list containing the two new trees
	 */
	public List<ExpressionTree> crossover(ExpressionTree other, DataSet data){
		List<ExpressionTree> output = new ArrayList<ExpressionTree>();
		//Clone the two expression trees to be crossed over
		ExpressionTree offspringOne = this.clone();
		ExpressionTree offspringTwo = other.clone();
		//Select random nodes to be crossover points. Nodes cannot be roots of the trees
		ExpressionTreeNode crossoverPointOne = offspringOne.root.getRandomNode();
		while(crossoverPointOne.parent == null){
			crossoverPointOne = offspringOne.root.getRandomNode();
		}
		ExpressionTreeNode crossoverPointTwo = offspringTwo.root.getRandomNode();
		while(crossoverPointTwo.parent == null){
			crossoverPointTwo = offspringTwo.root.getRandomNode();
		}
		//Swap crossover point one and crossover point two
		if(crossoverPointOne.parent.leftChild == crossoverPointOne){
			crossoverPointOne.parent.leftChild = crossoverPointTwo;
		}
		else{
			crossoverPointOne.parent.rightChild = crossoverPointTwo;
		}

		if(crossoverPointTwo.parent.leftChild == crossoverPointTwo){
			crossoverPointTwo.parent.leftChild = crossoverPointOne;
		}
		else{
			crossoverPointTwo.parent.rightChild = crossoverPointOne;
		}
		//Swap parent pointers for crossover point one and crossover point two
		ExpressionTreeNode temp = crossoverPointOne.parent;
		crossoverPointOne.parent = crossoverPointTwo.parent;
		crossoverPointTwo.parent = temp;
		
<<<<<<< HEAD
		//Simplify offspring
		//offspringOne.simplify();
		//offspringTwo.simplify();
		
=======
>>>>>>> origin/master
		//Update fitness of offspring
		offspringOne.fitness = data.fitness(offspringOne);
		offspringTwo.fitness = data.fitness(offspringTwo);

		output.add(offspringOne);
		output.add(offspringTwo);
		return output;
	}

	/**
	 * Changes each of the coefficients of the tree by a value between minMutation and
	 * maxMutation with a probability of mutationRate
	 * @param mutationRate the probability to mutate each coefficient
	 * @param minMutation the smallest mutation amount
	 * @param maxMutation the largest mutation amount
	 * @param random a random number generator
	 */
	public void mutate(double mutationRate, int minMutation, int maxMutation, Random random){
		root.mutate(mutationRate, minMutation, maxMutation, random);
	}

	/**
	 * Compares two trees 
	 * @param the expression tree to compare to the current tree
	 * @return 1 if the fitness of the current tree is greater than other and -1 if the fitness of other tree is
	 * greater than or equal to the fitness of the current tree
	 */
	@Override
	public int compareTo(ExpressionTree other) {
<<<<<<< HEAD
=======
		//return (this.fitness - other.fitness) > 0.0 ? 1 : -1;
>>>>>>> origin/master
		return Double.compare(this.fitness, other.fitness);
	}
}
