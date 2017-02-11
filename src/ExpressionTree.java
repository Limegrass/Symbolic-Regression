import java.util.*;


public class ExpressionTree implements Comparable<ExpressionTree>{
	private ExpressionTreeNode root;
	private double fitness;


	public class ExpressionTreeNode {
		private Type type;
		private Object value;
		private ExpressionTreeNode leftChild;
		private ExpressionTreeNode rightChild;
		private ExpressionTreeNode parent;

		private ExpressionTreeNode(Type type, Object value) throws IllegalArgumentException{
			this.type = type;
			if(type == Type.OPERATOR){
				if(!(value instanceof Operator)){
					throw new IllegalArgumentException("Value does not match type");
				}
				this.value = (Operator) value;
			}
			else if(type == Type.VARIABLE){
				if(!(value instanceof String)){
					throw new IllegalArgumentException("Value does not match type");
				}
				this.value = (String) value;
			}
			else if(type == Type.COEFFICIENT){
				if(!(value instanceof Double)){
					throw new IllegalArgumentException("Value does not match type");
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

		public double evaluate(HashMap<String, Double> variables) throws IllegalArgumentException{
			if(type == Type.COEFFICIENT){
				return (double) value;
			}
			else if(type == Type.VARIABLE){
				if(!variables.containsKey(value)){
					throw new IllegalArgumentException("Variable not defined");
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
					if(right == 0.0){
						//Dividing by 0 returns 1 right now we need to figure out what we want to do with this.
						return 1.0;
					}
					return left / right;
				}
				return 0.0;
			}
		}

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

		public ExpressionTreeNode getRandomNode(){
			//Get the size of the tree generate a random position less than size return node
			//in that position of in order traversal
			int size = this.getSize();
			Random r = new Random();
			int position = r.nextInt(size-1);
			return getKthNode(position);
		}

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

		public void mutate(double mutationRate, int minMutation, int maxMutation, Random r){
			//Only mutate coefficients
			if(type == Type.COEFFICIENT){
				if(r.nextDouble() < mutationRate){
					value = (double) value + (double) (r.nextInt(maxMutation - minMutation + 1) + minMutation);
				}
			}
			if(leftChild != null){
				leftChild.mutate(mutationRate, minMutation, maxMutation, r);
			}
			if(rightChild != null){
				rightChild.mutate(mutationRate, minMutation, maxMutation, r);
			}
		}
	}


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

	public ExpressionTree(ExpressionTreeNode root, double fitness){
		this.root = root;
		this.fitness = fitness;
	}

	public ExpressionTreeNode getRoot(){
		return this.root;
	}

	public double getFitness(){
		return this.fitness;
	}

	public double evaluate(HashMap<String, Double> variables){
		return root.evaluate(variables);
	}

	public void print(){
		root.print();
	}

	public ExpressionTree clone(){
		if(root == null){
			return null;
		}
		//Deep copies root node and creates new tree rooted at copy
		return new ExpressionTree(root.copy(), fitness);
	}

	public List<ExpressionTree> crossover(ExpressionTree other){
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

		output.add(offspringOne);
		output.add(offspringTwo);
		return output;
	}

	public void mutate(double mutationRate, int minMutation, int maxMutation, Random r){
		root.mutate(mutationRate, minMutation, maxMutation, r);
	}

	@Override
	public int compareTo(ExpressionTree other) {
		return (this.fitness - other.fitness) > 0.0 ? -1 : 1;
	}
}
