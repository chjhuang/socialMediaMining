package ucas.dataMining.datapreprocessing;

import java.util.ArrayList;
import java.util.List;

import ucas.dataMining.dao.Node;
import ucas.dataMining.dao.Relation;
import ucas.dataMining.dataAccess.DataFactory;
import Jama.Matrix;

public class DataPreProcessing {
	DataFactory df = new DataFactory();
	public static Matrix getAdjacenceMatrix(List<Relation> edges)
	{
		int n = edges.size();
		Matrix adjacenceMatrix = new Matrix(n,n);
		
		for(int i=0;i<n;i++)
		{
			Relation edge = edges.get(i);
			int source = edge.getSource();
			int target = edge.getTarget();
			//无向图将两个节点的都赋值,这个可能会出问题
			(adjacenceMatrix.getArray())[source][target] = 1;
			(adjacenceMatrix.getArray())[target][source] = 1;
		}
		return adjacenceMatrix;
		
	}
	
	public static double[] getClassValues(List<Node> nodes)
	{
		double[] classValues = null;
		classValues = new double[nodes.size()];
		for(int i=0;i<nodes.size();i++)
		{
			double classValue = (double)nodes.get(i).classValue;
			classValues[i] = classValue;
		}
		return classValues;
	}
	//
	
	public static List<Node> getJSONNodes(List<String> nodenames,double[] values)
	{
		List<Node> nodes = new ArrayList<Node>();
		for(int i=0;i<nodenames.size();i++)
		{
			Node node;
			if(values[i]==1.0)
			{
				node = new Node(nodenames.get(i), 1);
			}else if(values[i]==0.0)
			{
				node = new Node(nodenames.get(i), 0);
			}else
			{
				node = new Node(nodenames.get(i), -1);
			}
			nodes.add(node);
		}
		
		return nodes;
	}
	
//	public static List<Relation> getRelations(List<String> nodes)
//	{
//		List<Relation> edges = new ArrayList<Relation>();
//		int n = nodes.size();
//		for(int i=0;i<n;i++)
//		{
//			for(int j=i+1;j<n;j++)
//			{
//				if(DataFactory.isAdgjancent(nodes.get(i),nodes.get(j)))
//				{
//					//1 if adjancent
//					Relation edge = new Relation(i,j,"connected");
//					edges.add(edge);
//				}else
//				{
//					//0 if disadjancent
//					continue;
//				}
//			}
//			
//		}
//		return edges;
//	}
//	
	public static List<Relation> getRelations(Matrix adMa)
	{
		List<Relation> edges = new ArrayList<Relation>();
		double[][] array = adMa.getArray();
		for(int i=0;i<array.length;i++)
		{
			for(int j=i+1;j<array.length;j++)
			{
				if(array[i][j]==1)
				{
					//1 if adjancent
					Relation edge = new Relation(i,j,"connected");
					edges.add(edge);
				}else
				{
					//0 if disadjancent
					continue;
				}
			}
			
		}
		return edges;
	}
}
