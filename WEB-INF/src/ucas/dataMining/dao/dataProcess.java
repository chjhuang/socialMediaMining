package ucas.dataMining.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class dataProcess{

double [][] dataSet;
String []label;
double []min;
double []max;
int size;
int n;

/**
 *******************获得文件行数********************** 
 */
int getLine(String filename)throws Exception{
	File f=new File(filename);
	BufferedReader r=new BufferedReader(new FileReader(f));
	int count=0;
	String line=null;
	while((line=r.readLine())!=null)
		count++;
	r.close();
	return count;
}

/**
 * ******************将数据从文件中读入内存
 */
public void loadData(String filename) {
	try{
	File f=new File(filename);
	BufferedReader r=new BufferedReader(new FileReader(f));
	String line;
	int lineCount=getLine(filename);
	size=lineCount;
	label=new String[lineCount];
	line=r.readLine();
	String []list=line.split(",");
	n=list.length-1;
	dataSet=new double[lineCount][list.length-1];
	int count=0;
	min=new double[n];
	max=new double[n];
	for(int i=0;i<list.length-1;i++){
		dataSet[count][i]=Double.parseDouble(list[i]);
	    min[i]=max[i]=dataSet[count][i];	
	}
	label[count]=list[list.length-1];
	
	while((line=r.readLine())!=null){
		count++;
		list=line.split(",");
		for(int i=0;i<list.length-1;i++){
			dataSet[count][i]=Double.parseDouble(list[i]);
		    if(dataSet[count][i]<min[i])
		    	min[i]=dataSet[count][i];
		    if(dataSet[count][i]>max[i])
		    	max[i]=dataSet[count][i];
		}
		label[count]=list[list.length-1];
		
	}
	r.close();
	}catch(Exception e){e.printStackTrace();}
	
}

/**
 * ******************规范化为0~1之间**************************
 */
void autoNorm(){
	for(int i=0;i<size;i++)
		for(int j=0;j<n;j++){
			dataSet[i][j]=(dataSet[i][j]-min[j])/(max[j]-min[j]);
		}
			
}

/**
 * *******************打包预处理********************************
 * @param filename
 * @throws Exception
 */
public void process(String filename)throws Exception{
	loadData(filename);
	autoNorm();
}



public double[][] getDataSet() {
	return dataSet;
}

public String[] getLabel() {
	return label;
}


public double[] getMin() {
	return min;
}

public void setMin(double[] min) {
	this.min = min;
}

public double[] getMax() {
	return max;
}

public void setMax(double[] max) {
	this.max = max;
}

public static void main(String []args) throws Exception{
	dataProcess d=new  dataProcess();
	d.loadData("irisData.txt");
	int count=d.getLine("irisData.txt");
	d.autoNorm();
	for(int i=0;i<count;i++){
		for(int j=0;j<4;j++)
			System.out.print(d.dataSet[i][j]+" ");
		System.out.println(d.label[i]);
	}
	for(int i=0;i<d.n;i++)
		System.out.println(d.max[i]+" "+d.min[i]);
}
}
