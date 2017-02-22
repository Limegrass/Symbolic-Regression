import java.util.*;

/**
 * Stores an expression in a tree structure with operators as internal nodes
 * variables and coefficients as leaf nodes
 * 
 * @author Chris Lamb
 * @author James Ni
 */
public class ExpressionTree implements Comparable<ExpressionTree>{
	private ExpressionTreeNode root;
	private double fitness;
	public static final double EPSILON = 0E-5;
	/**
	 * Binary expression tree nodes store node type, value, parent node, left child, and right child 
	 */
	public class ExpressionTreeNode {
		private Type type;
		private Object value;
		private ExpressionTreeNode leftChild;
		private ExpressionTreeNode rightChild;
		private ExpressionTreeNode parent;
		private int size;

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
			this.size = 1;
		}

		/**
		 * Prints the expression represented by the node and its children
		 */
		public void print(){
			if(leftChild != null){
				if(parent!=null)
					System.out.print("(");
				leftChild.print();
			}
			System.out.print(value.toString() + "");
			if(rightChild != null){
				rightChild.print();
				if(parent!=null)
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
					if(right == 0.0 ){
						//Dividing by 0 returns 1000000.0 right now we need to figure out what we want to do with this.
						return 1000000000 ;
					}
					return left / right;
				}
				return 0.0;
			}
		}
		
		private boolean evaluateOperator(ExpressionTreeNode operatorNode, 
				ExpressionTreeNode child1, ExpressionTreeNode child2){
			if(parent.value == Operator.ADD){
				this.value = (double)child1.value + (double)child2.value;
			}
			else if(parent.value == Operator.SUBTRACT){
				this.value = (double)child1.value - (double)child2.value;
			}
			else if(parent.value == Operator.MULTIPLY){

				this.value = (double)child1.value * (double)child2.value;
			}
			//Operation is division
			else{
				if(child1 != null && child2 != null){
					this.size = 1 + leftChild.size + rightChild.size;
				}
				if((double)child2.value == 0.0){
					return true;
				}
				this.value = (double)child1.value / (double)child2.value;
			}
			return false;
		}
		
		/**
		 * Simplifies the tree rooted at node
		 * @return true if tree contains an illegal operation false otherwise
		 */
		public boolean simplify(){
			boolean zeroDivision = false;
			if(leftChild != null)
				zeroDivision = zeroDivision || leftChild.simplify();
			if(rightChild != null)
				zeroDivision = zeroDivision || rightChild.simplify();
			if(this.parent!=null && type==Type.OPERATOR){
				//If both children are coefficients replace node with coefficient result of operation
				if(leftChild.type==Type.COEFFICIENT && rightChild.type==Type.COEFFICIENT){
					zeroDivision = zeroDivision || this.evaluateOperator(this, this.leftChild, this.rightChild);
					if(zeroDivision){
						return zeroDivision;
					}
					this.leftChild = null;
					this.rightChild = null;
					this.type = Type.COEFFICIENT;
					this.size = 1;

				}
				//Replaces variable divided by itself with 1
				else if(leftChild.type==Type.VARIABLE && rightChild.type==Type.VARIABLE && this.value==Operator.DIVIDE
						&& leftChild.value.equals(rightChild.value)){	
					this.value = 1.0;
					this.leftChild = null;
					this.rightChild = null;
					this.type = Type.COEFFICIENT;
					this.size = 1;
				}
				//Replaces variable subtracted from itself with 0
				else if((leftChild.type==Type.VARIABLE && rightChild.type==Type.VARIABLE && this.value==Operator.SUBTRACT
						&& leftChild.value.equals(rightChild.value)) 
						|| (leftChild.type==Type.COEFFICIENT && (double)leftChild.value ==  0.0 
						&& (this.value == Operator.MULTIPLY || this.value == Operator.DIVIDE)) 
						|| (rightChild.type==Type.COEFFICIENT 
						&& (double)rightChild.value == 0.0 && this.value == Operator.MULTIPLY )){	
					this.value = 0.0;
					this.leftChild = null;
					this.rightChild = null;
					this.type = Type.COEFFICIENT;
					this.size = 1;
				}
				//Simplifies nodes that add or subtract 0
				else if((this.value == Operator.SUBTRACT || this.value == Operator.ADD) && 
						((leftChild.type==Type.COEFFICIENT && (double)leftChild.value == 0.0) 
								|| (rightChild.type==Type.COEFFICIENT && (double)rightChild.value == 0.0))){

					if(leftChild.type == Type.COEFFICIENT && (double)leftChild.value == 0.0 && rightChild!=null){
						if(this.parent.leftChild == this){
							this.parent.size = this.parent.size + this.rightChild.size - this.size;
							this.parent.leftChild = this.rightChild;
						}
						else{
							this.parent.size = this.parent.size + this.rightChild.size - this.size;
							this.parent.rightChild = this.rightChild;
						}
					}
					else if(rightChild.type == Type.COEFFICIENT && (double)rightChild.value == 0.0 && leftChild !=null){
						if(this.parent.leftChild == this){
							this.parent.size = this.parent.size + this.leftChild.size - this.size;
							this.parent.leftChild = this.leftChild;
						}
						else{
							this.parent.size = this.parent.size + this.leftChild.size - this.size;
							this.parent.rightChild = this.leftChild;
						}
					} 
				}

			}
			//Rounds coefficients to 5 decimal places
			if(this.type == Type.COEFFICIENT){
				if((double)this.value-EPSILON < Math.floor((double)this.value)){
					this.value = Math.floor((double)this.value);
				}
				else if((double)this.value+EPSILON > Math.ceil((double)this.value)){
					this.value = Math.ceil((double)this.value);
				}
			}
			if(leftChild != null && rightChild != null){
				this.size = 1 + leftChild.size + rightChild.size;
			}
			return zeroDivision;
		}

		/**
		 * @return the number of nodes in the tree rooted at the node
		 */
		public int getSize(){
			return this.size;
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
			copy.size = this.size;
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
			int position = random.nextInt(size-1);	//Can't pick root
			return getNthNode(position);
		}

		/**
		 * @param n the position of the node to be returned
		 * @return the nth node in an in-order traversal of the tree rooted at the node
		 */
		private ExpressionTreeNode getNthNode(int n){
			int leftSize = 0;
			if(leftChild != null){
				leftSize = leftChild.getSize();
			}
			//If size of left subtree equals n node is nth element 
			if(leftSize == n){
				return this;
			}
			//If left subtree is greater than n nth element is in left subtree
			else if(leftSize > n){
				return leftChild.getNthNode(n);
			}
			//If left subtree is less than n nth element is n - leftSize - 1st element in right subtree
			else{
				if(rightChild==null){
					System.out.println("yo");
				}
				return rightChild.getNthNode(n - leftSize - 1);
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
					//For real coefficients
					value = (double) value + (double) (random.nextDouble() * (maxMutation - minMutation + 1) + minMutation);
					//For integer coefficients
					//value = (double) value + (double) (random.nextInt(maxMutation - minMutation + 1) + minMutation);
				}
			}
			if(leftChild != null){
				leftChild.mutate(mutationRate, minMutation, maxMutation, random);
			}
			if(rightChild != null){
				rightChild.mutate(mutationRate, minMutation, maxMutation, random);
			}
		}


		/**
		 * Subtree mutation done by generating a random new tree. and attaching it in the place of the current node
		 */
		public void mutate(int depth, int numberOfVariables){

			//Generate a random new tree to replace the current node
			Random random = new Random();
			ExpressionTree mutation = ExpressionTreeTester.generateRandomTree(depth, numberOfVariables, random);
			mutation.simplify();
			ExpressionTreeNode fixSizes = this.parent;
			//Find the current node and then replace it 
			if( this.parent.leftChild!=null && this.parent.leftChild == this){


				this.parent.leftChild = mutation.root;


				mutation.root.parent = this.parent;

				while(fixSizes.parent!=null){
					fixSizes.size = 1 + fixSizes.leftChild.size + fixSizes.rightChild.size;
					fixSizes = fixSizes.parent;
				}
				fixSizes.size = 1 + fixSizes.leftChild.size + fixSizes.rightChild.size;

			}
			else{
				this.parent.rightChild = mutation.root;
				mutation.root.parent = this.parent;
				while(fixSizes.parent!=null){
					fixSizes.size = 1 + fixSizes.leftChild.size + fixSizes.rightChild.size;
					fixSizes = fixSizes.parent;
				}
				fixSizes.size = 1 + fixSizes.leftChild.size + fixSizes.rightChild.size;
			}
		}
	}


	/**
	 * Construcs an expression tree
	 * @param operators a list of operators to add to the tree
	 * @param variables a list of variables to add to the tree
	 * @param coefficients a list of coefficients to add to the tree
	 * @param training a data set to calculate the fitness of the tree
	 */
	public ExpressionTree(List<Operator> operators, List<String> variables, List<Double> coefficients){
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
			//Update size of current
			current.size = 1 + current.rightChild.size + current.leftChild.size;
			//Add the newly created terminal node back to q2
			q2.add(current);
		}
		this.root = current;

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
	 * @return the fitness of the tree
	 */
	public void setFitness(double fitness){
		this.fitness = fitness;
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
	 * Simplifies the trees
	 */
	public void simplify(){
		if(this.root.simplify()){
			this.fitness = -1;
		}
	}

	/**
	 * Prints the expression represented by the tree
	 */
	public void print(){
		root.print();
		System.out.println();
	}

	/**
	 * 
	 * @return the number of nodes in the tree
	 */
	public int getSize(){
		return this.root.getSize();
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
	public ExpressionTree[] crossover(ExpressionTree other, DataSet data){
		ExpressionTree[] output = new ExpressionTree[2];
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

		//Update size
		while(crossoverPointOne.parent != null){
			crossoverPointOne = crossoverPointOne.parent;
			crossoverPointOne.size = 1 + crossoverPointOne.leftChild.size + crossoverPointOne.rightChild.size;
		}
		while(crossoverPointTwo.parent != null){
			crossoverPointTwo = crossoverPointTwo.parent;
			crossoverPointTwo.size = 1 + crossoverPointTwo.leftChild.size + crossoverPointTwo.rightChild.size;
		}

		//Update fitness of offspring
		offspringOne.fitness = data.fitness(offspringOne, false);
		offspringTwo.fitness = data.fitness(offspringTwo, false);

		//Simplify offspring
		offspringOne.simplify();
		offspringTwo.simplify();

		output[0] = offspringOne;
		output[1] = offspringTwo;
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
	 * Replaces a random subtree with a new randomly generated tree
	 */
	public void mutate(int depth, int numberOfVariables){
		ExpressionTreeNode mutationPoint = root.getRandomNode();
		while(mutationPoint.parent == null){
			mutationPoint = root.getRandomNode();
		}
		mutationPoint.mutate(depth, numberOfVariables);
	}

	/**
	 * Compares two trees 
	 * @param the expression tree to compare to the current tree
	 * @return Positive value if the fitness of the current tree is greater than other and negative value
	 * if the fitness of other tree is greater than or equal to the fitness of the current tree
	 */
	@Override
	public int compareTo(ExpressionTree other) {
		return Double.compare(this.fitness, other.fitness);
	}
}
