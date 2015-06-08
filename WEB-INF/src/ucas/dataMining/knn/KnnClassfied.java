package ucas.dataMining.knn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;

import ucas.dataMining.dao.dataProcess;
class sortEntry{
	int index;
	double value;
}

class mycmp implements Comparator<sortEntry>  
{  

  public int compare(sortEntry A, sortEntry B)   
  {  
        if(A.value<B.value)
        	return -1;
        else if(A.value>B.value)
        	return 1;
        else
        	return 0;
  }  
} 


public class KnnClassfied {

/**
 * *******************返回排序之后的索引****************************
 * @param array
 * @return
 */
	int[] argeSort(double []array){
		int size=array.length;
		sortEntry[] se=new sortEntry[size];
		for(int i=0;i<size;i++){
			se[i]=new sortEntry();
			se[i].index=i;
			se[i].value=array[i];
		}
		Arrays.sort(se,0,size,new mycmp());
		int []result=new int[size];
		for(int i=0;i<se.length;i++)
			result[i]=se[i].index;
		return result;
	}
/**
 * ***********************对x进行分类******************************
 * @param x
 * @param dataSet 数据集
 * @param label  数据集标签
 * @param k  knn中k的数目
 * @return    k中最多的类
 */
public	String classify(double[]x,double[][]dataSet,String []label,int k){
		int size=dataSet.length;
		int n=x.length;
		double []dist=new double[size];
		for(int i=0;i<size;i++){
			double sum=0.0;
			for(int j=0;j<n;j++){
				sum+=Math.pow(dataSet[i][j]-x[j],2);
			}
			dist[i]=Math.pow(sum,0.5);
			}
	    int []sortIndex=argeSort(dist);
	    HashMap<String,Integer> map=new HashMap<String,Integer>();
	    for(int i=0;i<k;i++){
	    	
	    	Integer clabel=map.get(label[sortIndex[i]]);
	    	if(clabel==null)
	    		clabel=1;
	    	else
	    		clabel++;
	    	map.put(label[sortIndex[i]], clabel);
	    }
	    
	    ArrayList<Entry<String,Integer>> l = new ArrayList<Entry<String,Integer>>(map.entrySet()); 
	    Collections.sort(l, new Comparator<Map.Entry<String, Integer>>() {     
			   public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {     
			                return (o2.getValue() - o1.getValue());     }     });   
	    
	    
	    return l.get(0).getKey();
	}

/**
 * ***********************交叉验证模型的错误率*********************
 * @param dataSet
 * @param label
 * @param k
 */
public  double[] cross_validation(double[][]dataSet,String []label,int k){
	    double[] return_result=new double[4];
    	Random r=new Random();
    	int size=dataSet.length;
    	int n=dataSet[0].length;
    	int testSize=(int)(size*0.1);
    	HashSet<Integer> testSet=new HashSet<Integer>();
    	while(testSet.size()<testSize)
    		testSet.add(r.nextInt(size));
    	double [][]testData=new double[testSize][n];
    	double [][]trainData=new double[size-testSize][n];
    	String []testLabel=new String[testSize];
    	String []trainLabel=new String[size-testSize];
    	int count1,count2;
    	count1=count2=0;
    	for(int i=0;i<size;i++){
    		if(testSet.contains(i)){
    			for(int j=0;j<n;j++)
    				testData[count2][j]=dataSet[i][j];
    			testLabel[count2++]=label[i];
    		}else{
    			for(int j=0;j<n;j++)
    				trainData[count1][j]=dataSet[i][j];
    			trainLabel[count1++]=label[i];
    		}
    	}
    	int error=0;
    	String result=null;
    	for(int i=0;i<testSize;i++){
    		result=classify(testData[i],trainData,trainLabel,k);
    		if(!result.equals(testLabel[i]))
    			error++;
    	}
    	
    	System.out.println("错误率"+" "+(double)error/testSize);
    	return_result[0]=size-testSize;
    	return_result[1]=testSize;
    	return_result[2]=testSize-error;
    	return_result[3]=(double)error/testSize;
    	return return_result;
    	
    }
   
	public static void  main(String []args)throws Exception{

		dataProcess d=new dataProcess();
		d.process("irisData.txt");
		KnnClassfied knn=new KnnClassfied();
		//交叉验证
		knn.cross_validation(d.getDataSet(), d.getLabel(), 3);
		Scanner sc = new Scanner(System.in);
        while(true){
		System.out.println("请输入要分类的特征");
		String name = sc.nextLine();
		String []list=name.split(",");
		double []feature=new double[list.length];
		for(int i=0;i<list.length;i++)
			feature[i]=(Double.parseDouble(list[i])-d.getMin()[i])/(d.getMax()[i]-d.getMin()[i]);
		System.out.println(knn.classify(feature,d.getDataSet(),d.getLabel(),10));
		}
	}
}
