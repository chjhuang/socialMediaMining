package ucas.dataMining.bayes;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import ucas.dataMining.dao.dataProcess;




class bayesEntry{
	String name;
	double value;
}

class bayesCmp implements Comparator<bayesEntry>  
{  

  public int compare(bayesEntry A, bayesEntry B)   
  {  
        if(A.value>B.value)
        	return -1;
        else if(A.value<B.value)
        	return 1;
        else
        	return 0;
  }  
} 
public class naiveBayes {


HashMap<String,Integer>priori;
HashMap<String,Integer>featureMap;
HashMap<String,Double>featureNumber;
int sizeBig;

/*
 *****************获得alist的前size个数的平均数************
 */
    double getAverage(double[]alist,int size){
 
    	double sum=0.0;
    	for(int i=0;i<size;i++)
    		sum+=alist[i];
    	return sum/size;
    }
    
/**
 * @param alist
 * @param size
 * @param aver
 * @return  方差
 */
    double getVar(double[]alist,int size,double aver){
    	double sum=0.0;
    	for(int i=0;i<size;i++)
    		sum+=(alist[i]-aver)*(alist[i]-aver);
    	return sum/(size-1);
    }

/**
 * **************获得先验概率***********************
 * @param label
 */
    
    void getPriori(String []label){
    	priori=new HashMap<String,Integer>();
    	int size=label.length;
    	String labelName;
    	Integer counts;
    	for(int i=0;i<size;i++){
    		labelName=label[i];
    		counts=priori.get(labelName);
    		if(counts==null)
    			counts=1;
    		else
    			counts++;
    		priori.put(labelName, counts);
    	}
    	
    	
    }
/**
 * **********************训练模型，获得条件概率***********************
 * @param train
 * @param label
 * @param number
 */
public  void train(double[][]train,String []label,boolean[]number){
		int size=train.length;
		sizeBig=size;
		int n=train[0].length;
		getPriori(label);
		
		featureMap=new HashMap<String,Integer>();
		featureNumber=new HashMap<String,Double>();
		for(int j=0;j<n;j++){
			if(number[j]){
			   
			    for(Map.Entry<String,Integer>entry:priori.entrySet()){
			    	int m1=0;
			    	double[]lists=new double[size];
			    	for(int i=0;i<size;i++){
			    	     if(label[i].equals(entry.getKey()))
			    	    	 lists[m1++]=train[i][j];
			    	}
			    	
			    	double ave=getAverage(lists,m1);
			    	double avar=getVar(lists,m1,ave);
			    	String in=entry.getKey()+","+j+",";
			    	featureNumber.put(in+"ave",ave);
			    	featureNumber.put(in+"var",avar);
			    }
				
			}else{
				String labels;
				Integer counts;
				for(int i=0;i<size;i++){
					labels=label[i]+","+j+","+train[i][j];
					counts=featureMap.get(labels);
					if(counts==null)
						counts=1;
					else
						counts++;
					featureMap.put(labels, counts);
					
					
					labels=label[i]+","+j;
					counts=featureMap.get(labels);
					if(counts==null)
						counts=1;
					else
						counts++;
					featureMap.put(labels, counts);
				}
			}
			
			}
		
		
		 System.out.println("bayes is trainning");
	}

/**
 * *********************判别给点x最可能所属的类别*******************
 * @param x
 * @param number
 * @return
 */
public	String classify(double[]x,boolean[]number){
		int n=x.length;
		int count=priori.size();
		bayesEntry[] result=new bayesEntry[count];
		int k=0;
		for(Map.Entry<String, Integer> entry:priori.entrySet()){
			result[k]=new bayesEntry();
			result[k].name=entry.getKey();
			double midresult=0.0;
			midresult+=Math.log((double)entry.getValue()/sizeBig);
            for(int i=0;i<n;i++){
            	if(number[i]){
            		String strn=entry.getKey()+","+i+",";
            		double ave=featureNumber.get(strn+"ave");
            		double var=featureNumber.get(strn+"var");
            		double p=(1/(Math.sqrt(2*Math.PI*var)))*Math.pow(Math.E,-(x[i]-ave)*(x[i]-ave)/(2*var));
            		midresult+=Math.log(p);
            	}else{
            		String strall=entry.getKey()+","+i;
            		int all=featureMap.get(strall);
            		String strc=strall+","+x[i];
            		Integer c=featureMap.get(strc);
            	    
            	    if(c==null){
            	    	midresult+=Math.log((double)1/(all+1));
            	    }
            	    else
            		midresult+=Math.log((double)c/all);
            	}
            }
            result[k++].value=midresult;
       }
		Arrays.sort(result,0,k,new bayesCmp());
		return result[0].name;
	}
	
/**
 * **********************十字交叉验证，返回错误率************************
 * @param dataSet
 * @param label
 * @param num
 */
	
public	double[] cross_validation(double[][]dataSet,String []label,boolean[]num){
	    double[]return_result=new double[4];
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
    	train(trainData,trainLabel,num);
    	for(int i=0;i<testSize;i++){
    		result=classify(testData[i],num);
    		if(!result.equals(testLabel[i]))
    			error++;
    	}
    	return_result[0]=size-testSize;
    	return_result[1]=testSize;
    	return_result[2]=testSize-error;
    	return_result[3]=(double)error/testSize;
    	System.out.println("错误率"+" "+(double)error/testSize);
    	return return_result;
    }
	
	
	public static void main(String []args)throws Exception{
		dataProcess d=new dataProcess();
		d.loadData(".\\data\\irisData.txt");
		naiveBayes bayes=new naiveBayes();
		boolean[]number={true,true,true,true};
		bayes.cross_validation(d.getDataSet(),d.getLabel(),number);
		
		bayes.train(d.getDataSet(), d.getLabel(),number);

		Scanner sc = new Scanner(System.in);
        while(true){
        	System.out.println("请输入要分类的特征");
		String name = sc.nextLine();
		String []list=name.split(",");
		double []feature=new double[list.length];
		for(int i=0;i<list.length;i++)
			feature[i]=Double.parseDouble(list[i]);
		System.out.println(bayes.classify(feature, number));
		}
	}
	
}



