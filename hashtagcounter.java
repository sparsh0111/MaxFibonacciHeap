import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * read from the file specified, put the output in output_file.txt
 */
public class hashtagcounter {
	public static final char HASHTAG = '#';
	public static final String BLANK = " ";
	public static final ArrayList<String> STOPPERS = new ArrayList<>();

	public static void main(String[] args) {
		try {
			STOPPERS.add("STOP");
			STOPPERS.add("S");
			STOPPERS.add("stop");
			// Read the file contents

			String outputPath = "";
			if(args.length == 2){
				outputPath = args[1];
			}

			FileReader doc = new FileReader(args[0]);
			BufferedReader bReader = new BufferedReader(doc);
			
			// Clear the output file each time this main function is executed
			if(outputPath != ""){
				BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, false));
			writer.close();
		}
			FibonacciHeap heap = new FibonacciHeap();
			String text = bReader.readLine();
			String[] divideEachHastagSet = text.split(BLANK);

			// Index of HASTAG as 0 indicates that there is a hashtag entry. If not then max
			// hastags of the set has to be displaced based on number entered.
			int hashtag = text.charAt(0) == HASHTAG ? 1 : 0;

			// checking with null != object will help to avoid any Null Pointer Exception
			// This is done as part of maintaining code quality
			while (null != text && !STOPPERS.contains(text)) {

				switch (hashtag) {
				case 0:
					// remove remainingNumber of max elements from fibonacci heap
					heap.removeElements(Integer.parseInt(divideEachHastagSet[0]),outputPath);
					break;
				case 1:
					int number = Integer.parseInt(divideEachHastagSet[1]);
					divideEachHastagSet[0] = divideEachHastagSet[0].substring(1);

					// if key is not found, insert new node
					if (!heap.checkKey(divideEachHastagSet[0]))					
						heap.initialInsertNode(heap, divideEachHastagSet[0], number);
					// if node is already present, increment the key
					else
						heap.incKey(number, heap.getright1(divideEachHastagSet[0]));
					break;
				}

				text = bReader.readLine();
				divideEachHastagSet = text.split(BLANK);
				hashtag = text.charAt(0) == HASHTAG ? 1 : 0;

			}
			bReader.close();
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}

class FibonacciHeap {
	// maintain heap size to avoid re computation
	private int size = 0;
	private Hashtable<String, FibonacciNode> hashTable = new Hashtable<>();

	// Checks whether the hashMap contains the String being added by the user or not
	public boolean checkKey(String checkKey1) {
		return hashTable.containsKey(checkKey1);
	}

	// Adds the value to the set
	public void addValue(String checkKey1, FibonacciNode node) {
		hashTable.put(checkKey1, node);
	}

	public FibonacciNode getright1(String checkKey1) {
		return hashTable.get(checkKey1).getright();
	}

	private FibonacciNode heapMax = null; // for maximum element in heap
	private Queue<FibonacciNode> q = new LinkedList<FibonacciNode>();
	
	/*
	 * -> This function is for removing the first N max elements from the Fibonacci
	 * Heap -> It has one parameter : num -> num tells the value of N i.e how many
	 * max elements need to be removed
	 */
	public void removeElements(int num, String outputPath) {
		try {
			int k = 0;

			if(outputPath != "") {
				BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, true));
            PrintWriter printWriter = new PrintWriter(writer);
			// Call removeMaximum to remove num maxes (num times)
			while (k < num) {
				FibonacciNode currentMax;
				currentMax = removeMaximum();
				
				if (null != currentMax) {				
					printTags(currentMax, printWriter, outputPath, k, num);
					this.q.add(currentMax);
				}
				k++;
			}			
			printWriter.println();
			printWriter.flush();
			printWriter.close();
		
				// output gets written to the output file
			}
			else {
				while (k < num) {
				FibonacciNode currentMax;
				currentMax = removeMaximum();
				
				if (null != currentMax) {				
					if (!(k == num - 1)) {
				System.out.print(currentMax.getKey()+",");
			} else if(k == num - 1)
				System.out.println(currentMax.getKey());
					this.q.add(currentMax);
				}
				k++;
			}	
			}
			
			
			
			insertionAgaintoFiboHeap(this.q);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	// This method will print the required number of top hashtags into output file
	public void printTags(FibonacciNode node, PrintWriter printWriter, String outputPath, int numPrinted, int tobePrinted) {
		try {

			if (!(numPrinted == tobePrinted - 1)) {
				printWriter.write(node.getKey()+",");
			} else if(numPrinted == tobePrinted - 1)
				printWriter.write(node.getKey());

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	// Hastags need to re inserted again into Fibonacci Heap after being removed
	private void insertionAgaintoFiboHeap(Queue<FibonacciNode> que) {
		int len = que.size();
		for(int i=0;i<len;i++) {
			FibonacciNode NodeA = (FibonacciNode) que.remove();
			FibonacciNode fibonacciNode = new FibonacciNode(NodeA.getKey(), NodeA.getData());
			FibonacciNode NodeB = insertNode(fibonacciNode);
			hashTable.get(NodeA.getKey()).setright(NodeB);
		}
	}

	/*
	 * -> This function is for cutting the node from its parent and insert into root
	 * list as its values becomes more than its parent -> It has one parameter :
	 * fibNode -> fibNode tells the node which has to be cut from its parent and
	 * added into the root list
	 */
	private void cutChildNode(FibonacciNode fibNode) {

		// To remove this node from the group of its siblings
		if (!fibNode.getright().equals(fibNode)) {
			fibNode.getleft().setright(fibNode.getright());
			fibNode.getright().setleft(fibNode.getleft());
		}

		// Checking child pointer of node being cut so that it can be changed if needed
		fibNode.getParent().setChild((fibNode.getParent().getChild() == fibNode) ? ((fibNode.getright() != fibNode) ? fibNode.getright() : null): null);
		fibNode.getParent().setDegree(fibNode.getParent().getDegree() - 1);

		// To add the node cut into the main(root) list
		fibNode.setleft(fibNode);
		fibNode.setright(fibNode.getleft());

		// combine list call to move the child of OLD heapMax to the main root list
		if (null == this.heapMax) {
			// heapMax is null, result is fibNode
			this.heapMax = fibNode;
		} else if (null != fibNode) {
			// combine the lists if both heapMax and fibNode is not null
			FibonacciNode maxright;
			maxright = this.heapMax.getright();
			this.heapMax.setright(fibNode.getright());
			this.heapMax.getright().setleft(this.heapMax);
			fibNode.setright(maxright);
			fibNode.getright().setleft(fibNode);

			// pointer to larger node is assigned to heapMax
			int findGreater = Math.max(this.heapMax.getData(), fibNode.getData());
			if (findGreater != this.heapMax.getData())
				this.heapMax = fibNode;
		}

		// Change the childCutVal of parent to true if not marked otherwise cut those whose value is already true
		if (!fibNode.getParent().isChildCutVal())
			fibNode.getParent().setChildCutVal(true);

		else 
			parentNode(fibNode);

		fibNode.setParent(null);
	}
	
	/*
	 * -> This function is for cutting the parent and insert into root
	 * list as its values becomes more than its parent -> It has one parameter :
	 * node -> node tells the node which has to be cut and
	 * added into the root list
	 */
	private void parentNode(FibonacciNode node) {
		
		node.getParent().setChildCutVal(false);
		if(null != node.getParent().getParent())
		cutChildNode(node.getParent());
	}

	/*
	 * -> This function is for increasing the key of the node which is already there
	 * in Fibonacci Heap -> It has two parameters : fibNode and incVal -> fibNode
	 * tells the node whose key is to be increased -> incVal tells by what amount
	 * the key of the fibNode has to be increased
	 */
	public void incKey(int incVal, FibonacciNode fibNode) {

		// increase node value by incVal
		if(null != fibNode) {
			
			fibNode.setData(fibNode.getData() + incVal);

			// if new value of node > heapMax node value, heapMax node is pointed to current
			// node with increased value
			if (fibNode.getData() >= this.heapMax.getData()) {
				this.heapMax = fibNode;
			}
			// if new value of node >= it's parent value, cut the node and insert into root
			// list
			else if (null != fibNode.getParent() && fibNode.getParent().getData() <= fibNode.getData()) {
				fibNode.setChildCutVal(false);
				if(null != fibNode.getParent())
				cutChildNode(fibNode);
			}				
		} else {
			System.out.println("This Node doesn't exists.");
			return;
		}
		
	}

	/*
	 * ->Insert a new node into the Fibonacci Heap ->insertNode function has two
	 * parameters >value tells the value of the hashtag >key tells the name of the
	 * hashtag
	 */
	public FibonacciNode insertNode(FibonacciNode fibonacciNode) {
		
		if (null == this.heapMax) {
			// heapMax is null, result is fibonacciNode
			this.heapMax = fibonacciNode;
		} else if (null != fibonacciNode) {
			// combine the lists if both heapMax and fibonacciNode is not null
			FibonacciNode maxright;
			maxright = this.heapMax.getright();
			this.heapMax.setright(fibonacciNode.getright());
			this.heapMax.getright().setleft(this.heapMax);
			fibonacciNode.setright(maxright);
			fibonacciNode.getright().setleft(fibonacciNode);

			// pointer to larger node is assigned to heapMax
			int findGreater = Math.max(this.heapMax.getData(), fibonacciNode.getData());
			if (findGreater != this.heapMax.getData())
				this.heapMax = fibonacciNode;
		}

		this.size = this.size + 1;
		return fibonacciNode;
	}
	
	//Initial insert when nodes are getting appended to the circular doubly linked list
	public void initialInsertNode(FibonacciHeap heap, String hashtag, int count) {
		
		FibonacciNode pNode = new FibonacciNode(null, -1);
		FibonacciNode fibonacciNode = new FibonacciNode(hashtag, count);
		FibonacciNode index = heap.insertNode(fibonacciNode);
		pNode.setright(index);
		heap.addValue(hashtag, pNode);
		
	}

	/*
	 * ->It throws NoSuchElementException if empty heap ->It returns the largest
	 * element of Fibonacci Heap
	 */
	public FibonacciNode removeMaximum() {

		// Checking if the heap is empty or not
		if (null == this.heapMax)
			return null;
		// decrement the size of heap as the maximum value will be removed
		this.size = this.size - 1;

		// list of nodes to be traversed
		List<FibonacciNode> listNodes = new ArrayList<>();

		// Tree table to track degree of the subtrees while combining pairwise
		List<FibonacciNode> treeTable = new ArrayList<>();

		int numberOfNodes = 0;

		// maximum element to be removed
		FibonacciNode maxNode = this.heapMax;

		// Remove the heapMax element from the root list
		// if heapMax elem is the only element in the root list, make heapMax as null
		// otherwise reassign heapMax to the node right to heapMax node
		if (this.heapMax.getleft() == this.heapMax)
			this.heapMax = null;
		else {
			this.heapMax.getleft().setright(this.heapMax.getright());
			this.heapMax.getright().setleft(this.heapMax.getleft());
			this.heapMax = this.heapMax.getright();
		}

		// Reassign parent of removed heapMax's child to NULL
		if (null != maxNode.getChild()) {
			// check name of node
			FibonacciNode heapMaxChild = maxNode.getChild();

			// This is done if their is only one child of heapMax
			heapMaxChild.setParent(null);
			heapMaxChild = heapMaxChild.getright();

			// Setting null to parent field of each child of heapMax, if there any except
			// one
			while (heapMaxChild != maxNode.getChild()) {
				heapMaxChild.setParent(null);
				heapMaxChild = heapMaxChild.getright();
			}
		}

		// If list gets empty, return heapMax and end
		if (null == this.heapMax && null == maxNode.getChild())
			return maxNode;
		else {
			// combine list call to move the child of OLD heapMax to the main root list
			if (null == this.heapMax) {
				// heapMax is null, result is maxNode.child
				this.heapMax = maxNode.getChild();
			} else if (null != maxNode.getChild()) {
				// combine the lists if both heapMax and maxNode.child is not null
				FibonacciNode maxright;
				maxright = this.heapMax.getright();
				this.heapMax.setright(maxNode.getChild().getright());
				this.heapMax.getright().setleft(this.heapMax);
				maxNode.getChild().setright(maxright);
				maxNode.getChild().getright().setleft(maxNode.getChild());

				// pointer to larger node is assigned to heapMax
				int findGreater = Math.max(this.heapMax.getData(), maxNode.getChild().getData());
				if (findGreater != this.heapMax.getData())
					heapMax = maxNode.getChild();
			}
		}

		FibonacciNode current = this.heapMax;

		if (listNodes.isEmpty()) {
			listNodes.add(current);
			numberOfNodes++;
			current = current.getright();
			if (null != current) {
				while (listNodes.get(0) != current) {
					listNodes.add(current);
					numberOfNodes++;
					current = current.getright();
				}
			}
		}
		
		for (int i = 0; i < numberOfNodes; i++) {
			FibonacciNode currNode = listNodes.get(i);
			for (;;) {

				for (int k = currNode.getDegree(); k >= 0; k--) {
					if (currNode.getDegree() >= treeTable.size())
						treeTable.add(null);
					else
						break;
				}		

				// Check if index(degree of current node) of treeTable is null or not
				// If null that means prior to this no list of this degree present in this
				// Fibonacci Heap
				if (null == treeTable.get(currNode.getDegree())) {
					treeTable.set(currNode.getDegree(), currNode);
					break;
				}

				FibonacciNode other;
				FibonacciNode min;
				FibonacciNode heapMax;

				// Two lists in Fibonacci Heap have got same degree.
				// So this index will cleared as two lists with same degree will be pairwise
				// merged
				other = treeTable.get(currNode.getDegree());
				treeTable.set(currNode.getDegree(), null);

				int maxData = Math.max(other.getData(), currNode.getData());

				if (maxData == other.getData()) {
					min = currNode;
					heapMax = other;
				} else {
					min = other;
					heapMax = currNode;
				}

				// remove min element from list
				min.getright().setleft(min.getleft());
				min.getleft().setright(min.getright());

				// Make the min child of heapMax by combining pairwise
				min.setright(min);
				min.setleft(min);

				// combine list call to move the child of OLD heapMax to the main root list
				if (null == heapMax.getChild()) {
					// heapMax.child is null, result is min
					heapMax.setChild(min);
				} else if (null != min) {
					// combine the lists if both heapMax.child and min is not null
					FibonacciNode maxright;
					maxright = heapMax.getChild().getright();
					heapMax.getChild().setright(min.getright());
					heapMax.getChild().getright().setleft(heapMax.getChild());
					min.setright(maxright);
					min.getright().setleft(min);

					// pointer to larger node is assigned to heapMax.child
					int findGreater = Math.max(heapMax.getChild().getData(), min.getData());
					if (findGreater != heapMax.getChild().getData())
						heapMax.setChild(min);
				}

				min.setParent(heapMax);
				min.setChildCutVal(false);
				heapMax.setDegree(heapMax.getDegree() + 1);

				currNode = heapMax;
			}
			if (!(heapMax.getData() <= currNode.getData()))
				continue;

			// Update the heapMax element
			heapMax = currNode;

		}
		return maxNode;
	}
}

/*
 * Creating a new FibonacciNode Variable data will store hash tag count stored
 * in this node. Variable key will store associated value of the string with this
 * node. Node left and right keeps the track of the left node and right
 * node
 */
class FibonacciNode {

	private String key; // name of the hashtag
	private int degree = 0; // stores the current degree of the FibonacciNode
	private int data; // Count associated with each hashtag

	private FibonacciNode left; // Points to the left sibling of the FibonacciNode
	private FibonacciNode right; // Points to the right sibling of the FibonacciNode
	private FibonacciNode child; // Points to child FibonacciNode 
	private FibonacciNode parent; // Points to parent FibonacciNode

	private boolean childCutVal = false; // tells whether sub tree of this node has been cut before or not

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}

	public FibonacciNode getleft() {
		return left;
	}

	public void setleft(FibonacciNode left) {
		this.left = left;
	}

	public FibonacciNode getright() {
		return right;
	}

	public void setright(FibonacciNode right) {
		this.right = right;
	}

	public FibonacciNode getChild() {
		return child;
	}

	public void setChild(FibonacciNode child) {
		this.child = child;
	}

	public FibonacciNode getParent() {
		return parent;
	}

	public void setParent(FibonacciNode parent) {
		this.parent = parent;
	}

	public boolean isChildCutVal() {
		return childCutVal;
	}

	public void setChildCutVal(boolean childCutVal) {
		this.childCutVal = childCutVal;
	}

	FibonacciNode(String keyValue, int value) {
		this.right = this;
		this.left = this;
		this.data = value;
		this.key = keyValue;
	}
}