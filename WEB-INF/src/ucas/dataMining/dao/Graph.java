package ucas.dataMining.dao;

import java.util.List;

import ucas.dataMining.dataAccess.DataFactory;

public class Graph {
	public static  int MAXINT=65535;
class Node{
	int id;
	Edge first;
}
class Edge{
	int v;
	double weight;
	Edge next;
public Edge(int v,double weight){
	this.v=v;
	this.weight=weight;
}
}
	int nodeNumber=943;
	int edgeNumber;
	Node[] nodeList=new Node[nodeNumber];
//构造函数
public Graph(){
	for(int i=0;i<nodeNumber;i++)
		nodeList[i]=new Node();
}

//初始化图的结构
public void init(List<SimilarUser>susers){
	edgeNumber=susers.size();
	for(SimilarUser su:susers){
		int u=Integer.parseInt(su.getId1());
		int v=Integer.parseInt(su.getId2());
		Edge e=new Edge(u,su.getSocre());
		link(nodeList[v],e);
		Edge e2=new Edge(v,su.getSocre());
		link(nodeList[u],e2);
		
	}

}
//连接边到定点u
public void link(Node u,Edge v){
	if(u.first==null)
		u.first=v;
	else{
		v.next=u.first;
		u.first=v;
		}
}
//dijkstra算法找v到其它节点距离
public int[] getDist(int u){
	
	int []dist=new int[nodeNumber];
	boolean []s=new boolean[nodeNumber];
	for(int i=0;i<nodeNumber;i++){
		dist[i]=MAXINT;
		s[i]=false;
	}
	Edge e=nodeList[u].first;
	while(e!=null){
		int v=e.v;
		dist[v]=1;
		e=e.next;
		s[v]=true;
	}
	dist[u]=0;s[u]=true;
	for(int i=1;i<nodeNumber;i++){
		int midDist=MAXINT;
		int v=u;
		for(int j=0;j<nodeNumber;j++)
			if(!s[j]&&dist[j]<midDist)
				midDist=dist[v=j];
		s[v]=true;
		if(midDist==MAXINT)
			return dist;
		for(int j=0;j<nodeNumber;j++){
			e=nodeList[v].first;
			while(e!=null){
				int midNode=e.v;
				if(!s[midNode]&&(dist[v]+1)<dist[midNode])
					dist[midNode]=dist[v]+1;
				e=e.next;
			}
		}
	}
	return dist;
}
public static void main(String []args){
	Graph g=new Graph();
	DataFactory.Init();
	DataFactory.doSimilar(DataFactory.getAllUsers(), 20,0.6);
	List<SimilarUser> su=DataFactory.getSimilarUser();
	g.init(su); 
	g.getDist(1);
}
}
