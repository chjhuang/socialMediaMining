package ucas.dataMining.regression;

import Jama.Matrix;



public class ClassficationWithNI {
	/*
	 * 基于网络信息的分类
	 * 但是如果网络十分庞大的话会导致程序迭代层次过多，解决这种大网络的分类预测需要制定一定的规则
	 * 现在先实现小网络里的
	 */
	private static int VALUE_CHOICE=2;
	private static int ITERATION=10;
	
	public static double[] train(Matrix adjacenceMatrix,double[] classValue)
	{
		//新建邻接矩阵（带属性权值）的备份，在备份上进行操作
		Matrix copy = new Matrix(adjacenceMatrix.getArray());
		
		//对classValue赋初值
		for(int i=0;i<classValue.length;i++)
		{
			if(classValue[i]!=1.0 && classValue[i]!=0.0)
			{
				classValue[i] = (double) 1/VALUE_CHOICE;
			}
		}
		//迭代计算新的classValue数值
		for(int i=0;i<ITERATION;i++)
		{
			for(int j=0;j<classValue.length;j++)
			{
				if(classValue[j]!=1.0 && classValue[j]!=0.0)
				{
					double temp = 0.0;
					int count = 0;
					for(int k=0;k<classValue.length;k++)
					{
						temp+=classValue[k]*copy.getArray()[j][k];
						count += copy.getArray()[j][k];
					}
					classValue[j] = temp/count;
				}
				else
					continue;
			}
		}
		
		//根据计算所得的classValue值估测未标记节点的分类
		for(int i=0;i<classValue.length;i++)
		{
			
			if(classValue[i]!=1.0 && classValue[i]!=0.0)
			{
				if(classValue[i]>0.5)
				{
					classValue[i] = 1.0;
				}
				else
				{
					classValue[i] = 0.0;
				}
				
			}
		}
		return classValue;
	}
	
}
