package ucas.dataMining.decisionTree;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class DecisionTreeTest {
	private static DecisionTree makeOne() {
		return new DecisionTree();
	}

	private static DecisionTree makeOutlookTree() {
		try {
			return makeOne()
					.setAttributeNames(new String[] { "Outlook", "Temperature", "Humidity", "Wind" })
					.addInstance(new String[] { "Sunny", "Hot", "High", "Weak" }, "NO")
					.addInstance(new String[] { "Sunny", "Hot", "High", "Strong" }, "NO")
					.addInstance(new String[] { "Overcast", "Hot", "High", "Weak" }, "YES")
					.addInstance(new String[] { "Rain", "Mild", "High", "Weak" }, "YES")
					.addInstance(new String[] { "Rain", "Cool", "Normal", "Weak" }, "YES")
					.addInstance(new String[] { "Rain", "Cool", "Normal", "Strong" }, "NO")
					.addInstance(new String[] { "Overcast", "Cool", "Normal", "Strong" }, "YES")
					.addInstance(new String[] { "Sunny", "Mild", "High", "Weak" }, "NO")
					.addInstance(new String[] { "Sunny", "Cool", "Normal", "Weak" }, "YES")
					.addInstance(new String[] { "Rain", "Mild", "Normal", "Weak" }, "YES")
					.addInstance(new String[] { "Sunny", "Mild", "Normal", "Strong" }, "YES")
					.addInstance(new String[] { "Overcast", "Mild", "High", "Strong" }, "YES")
					.addInstance(new String[] { "Overcast", "Hot", "Normal", "Weak" }, "YES")
					.addInstance(new String[] { "Rain", "Mild", "High", "Strong" }, "NO");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return makeOne();
		}
	}

	public static void main(String args[]) {
		DecisionTree decisionTree = makeOutlookTree();
		
		Map<String, String> case1 = new HashMap<String, String>();
		case1.put("Outlook", "Overcast");
		case1.put("Temperature", "Hot");
		case1.put("Humidity", "High");
		case1.put("Wind", "Strong");
		
		try {
			System.out.println(decisionTree.classify(case1));
		} catch (UnknownDecisionException e) {
			System.out.println("?");
		}
		
		
	}
}
