<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%
	request.setCharacterEncoding("UTF-8");
%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <script src="<%=basePath%>jscss/echarts.js"></script>
	<!-- ECharts单文件引入 -->
    <script src="<%=basePath%>build/dist/echarts.js"></script>
    <script src="<%=basePath%>jscss/jquery.js"></script>
	<link rel="stylesheet" type="text/css" href="jscss/style.css"/>
    <title>dataMining</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page"> 
  </head>
  
	<body>
		<div id="menu">
			<ul>
				<li><a href="home.html">Home</a></li>
				<li><a href="tfidf.html">TF-IDF</a></li>
				<li><a href="decisionTree.html">DecisionTree</a></li>
				<li><a href="classficationWithNetworkInformation.html">Network</a></li>
				<li><a href="simpleLinearRegression.html">Regression</a></li>
				<li><a href="bayes.jsp">NBC</a></li>
				<li><a href="knn.jsp">KNN</a></li>
				<li><a href="http://localhost:8000/njza/">KMeans</a></li>
				<li><a href="application.html">Application</a></li>
			</ul>
		</div>
		<div id="title">Naive Bayes Classifier</div>
		<div style="width: 100%; height: 630px;">
		<div id="left_container" style="width: 20%;">
			<div id="information_zone" class="info_zone">
				<h3 class="zone_title">Input Test Data(Bayes):</h3>
				<form id="data">
					<div class="attributeName">花萼长度: <input type="text" name='length1' value="5.0" class="attributeValue" style="width:100px;"></div>
					<div class="attributeName">花萼高度: <input type="text" name='height1' value="3.3" class="attributeValue" style="width:100px;"></div>
					<div class="attributeName">花瓣长度: <input type="text" name='length2' value="1.4" class="attributeValue" style="width:100px;"></div>
					<div class="attributeName">花瓣高度: <input type="text" name='height2' value="0.2" class="attributeValue" style="width:100px;"></div>
					<!--
					花萼高度: <input type="text" name='height1' value="3.3" style="width:100px"><br/>
					花瓣长度: <input type="text" name='length2' value="1.4" style="width:100px"><br/>
					花瓣高度: <input type="text" name='height2' value="0.2" style="width:100px"><br/>-->
					<div class="attributeName">实际结果: 
						<select name="result" style="height: 20px; border-radius: 0.5em;">
							<option value="setosa" selected="selected">山鸢尾</option>
							<option value="versicolor">变色鸢尾</option>
							<option value="virginica">维吉尼亚鸢尾</option>
						</select>
					</div>
					<input type="button" class="button" value="确定" id="test" style="margin: 10px 0 -15px 35px;">
				</form>
			</div>
			
			<div id="prediction_zone" class="info_zone" style="padding-top: 10px;">预测结果:</div><br>
			<div style="margin-left: 10px;">
				<form>
					<input type="button" class="button" value="交叉验证" id="cross_button"/>
					<div style="height:15px"></div>
					<div id="cross_validation"></div>
				</form>
			</div>
		</div>
		
		<div id="right_container" style="width: 76%;">
			<div display="inline">
				<div id="main" style="height:94%;width:50%;float:left;"></div>
				<div id="main2" style="height:94%;width:50%;float:left;"></div>
			</div>
		</div>
		</div>
		<div id="footer">
			<p>Copyright (c) 2015. Design by Group 17. </p>
		</div>
<script type="text/javascript">
	var setosa=[];
	var versicolor=[];
	var virginica=[];
	var myChart,option;
	var myChart2,option2;
	var setosa2=[];
	var versicolor2=[];
	var virginica2=[];	
		
	$(function (){
		//预测测试样例，并返回结果
		$("#test").click(function(){
		$("#prediction_zone").html("预测结果:"+"<font color='red'>...</font>");
		length1=$("#data input[name='length1']").val();
		height1=$("#data input[name='height1']").val();
		length2=$("#data input[name='length2']").val();
		height2=$("#data input[name='height2']").val();
		data1=[];
		data1.push(length1,height1);
		data2=[];
		data2.push(length2,height2);
  
		myChart.addData([
		[
			3,        // 系列索引
			data1, // 新增数据
			false,     // 新增数据是否从队列头部插入
			true
		]
		]);
		myChart.setSeries(myChart.getSeries());
		myChart.refresh();
    
		myChart2.addData([
        [
            3,        // 系列索引
           data2, // 新增数据
            false,     // 新增数据是否从队列头部插入
            true
        ]
        
		]);
		myChart2.setSeries(myChart2.getSeries());
		myChart2.refresh();
    
		$.ajax({
			type:"POST",
			url: "<%=basePath%>Bayes",
			data:{
				method:"predict",
				length1:length1,
				height1:height1,
				length2:length2,
				height2:height2
			},
			success:function(result){
					if(result=="Iris-setosa")
						result="山鸢尾";
					else if(result=="Iris-versicolor")
						result="变色鸢尾";
					else
						result="维吉尼亚鸢尾";
     
			$("#prediction_zone").html("预测结果:"+"<font color='red'>"+result+"</font>");
			}
		});      
	});

	//交叉测试时的结果
	$("#cross_button").click(function(){
	$.ajax({
		type:"POST",
		url: "<%=basePath%>Bayes",
		data:{method:"cross_validation"},
		success:function(data){
		list=data.split(',');
		$("#cross_validation").html("训练样例:"+list[0]+" 测试样例:"+list[1]+
			"  正确数:"+list[2]+"  错误率"+list[3]);
		}
		});

	});

	$.ajax({
		type: "POST",
		url: "<%=basePath%>data/irisData.txt",
		data: "text",
		success: function(msg){
			list=msg.split('\n');
			for(var i=0;i<50;i++){
				strline=list[i].split(',');
				setosa.push([strline[0],strline[1]]);
				setosa2.push([strline[2],strline[3]]);
			}
		   
			for(var i=50;i<100;i++){
				strline=list[i].split(',');
				versicolor.push([strline[0],strline[1]]);
				versicolor2.push([strline[2],strline[3]]);
			}
		   
			for(var i=100;i<150;i++){
				strline=list[i].split(',');
				virginica.push([strline[0],strline[1]]);
				virginica2.push([strline[2],strline[3]]);
			} 
		   
			myChart.setOption(option); 
			myChart2.setOption(option2);            
		}
	});
      
});

    // 路径配置
    require.config({
        paths: {
            echarts: '<%=basePath%>/build/dist'
        }
    });
    // 使用
    require(
        [
            'echarts',
            'echarts/chart/scatter' // 使用柱状图就加载bar模块，按需加载
        ],
        function (ec) {
            // 基于准备好的dom，初始化echarts图表
            myChart = ec.init(document.getElementById('main')); 
            myChart2 = require('echarts').init(document.getElementById('main2')); 
                
        option = {
			title : {
			text: 'iris-鸢尾花类',
			subtext: '花萼长度和宽度'
			},
			tooltip : {
				trigger: 'axis',
				showDelay : 0,
			formatter : function (params) {
				if (params.value.length > 1) {
					return params.seriesName + ' :<br/>'
						+ params.value[0]+'cm '  
						+ params.value[1]+'cm ';
				}
				else {
					return params.seriesName + ' :<br/>'
						+ params.name + ' : '
						+ params.value+'cm ';
				}
			},  
			axisPointer:{
				show: true,
				type : 'cross',
				lineStyle: {
					type : 'dashed',
					width : 1
				}
			}
		},
		legend: {
			data:['山鸢尾','变色鸢尾','维吉尼亚鸢尾','test']
		},
		toolbox: {
			show : true
		},
		xAxis : [
        {
            type : 'value',
            scale:true,
            axisLabel : {
                formatter: '{value} cm'
            }
        }
		],
		yAxis : [
        {
            type : 'value',
            scale:true,
            axisLabel : {
                formatter: '{value} cm'
            }
        }
		],
		series : [
        {
            name:'山鸢尾',
            type:'scatter',
            data: setosa,
            markPoint : {
                data : [
                    {type : 'max', name: '最大值'},
                    {type : 'min', name: '最小值'}
                ]
            },
            markLine : {
                data : [
                    {type : 'average', name: '平均值'}
                ]
            }
        },
        {
            name:'变色鸢尾',
            type:'scatter',
            data: versicolor,
            markPoint : {
                data : [
                    {type : 'max', name: '最大值'},
                    {type : 'min', name: '最小值'}
                ]
            },
            markLine : {
                data : [
                    {type : 'average', name: '平均值'}
                ]
            }
        },
        {
            name:'维吉尼亚鸢尾',
            type:'scatter',
            data: virginica,
            markPoint : {
                data : [
                    {type : 'max', name: '最大值'},
                    {type : 'min', name: '最小值'}
                ]
            },
            markLine : {
                data : [
                    {type : 'average', name: '平均值'}
                ]
            }
        },
        {
            name:'test',
            type:'scatter',
            data:[]
        }
		]
		};                   

		option2 = {
			title : {
			text: 'iris-鸢尾花类',
			subtext: '花瓣长度和宽度'
			},
		tooltip : {
			trigger: 'axis',
			showDelay : 0,
			formatter : function (params) {
            if (params.value.length > 1) {
                return params.seriesName + ' :<br/>'
                   + params.value[0]+'cm '  
                   + params.value[1]+'cm ';
            }
            else {
                return params.seriesName + ' :<br/>'
                   + params.name + ' : '
                   + params.value+'cm ';
            }
        },  
        axisPointer:{
            show: true,
            type : 'cross',
            lineStyle: {
                type : 'dashed',
                width : 1
            }
        }
    },
    legend: {
        data:['山鸢尾','变色鸢尾','维吉尼亚鸢尾','test']
    },
    toolbox: {
        show : true
    },
    xAxis : [
        {
            type : 'value',
            scale:true,
            axisLabel : {
                formatter: '{value} cm'
            }
        }
    ],
    yAxis : [
        {
            type : 'value',
            scale:true,
            axisLabel : {
                formatter: '{value} cm'
            }
        }
    ],
    series : [
        {
            name:'山鸢尾',
            type:'scatter',
            data: setosa2,
            markPoint : {
                data : [
                    {type : 'max', name: '最大值'},
                    {type : 'min', name: '最小值'}
                ]
            },
            markLine : {
                data : [
                    {type : 'average', name: '平均值'}
                ]
            }
        },
        {
            name:'变色鸢尾',
            type:'scatter',
            data: versicolor2,
            markPoint : {
                data : [
                    {type : 'max', name: '最大值'},
                    {type : 'min', name: '最小值'}
                ]
            },
            markLine : {
                data : [
                    {type : 'average', name: '平均值'}
                ]
            }
        },
        {
            name:'维吉尼亚鸢尾',
            type:'scatter',
            data: virginica2,
            markPoint : {
                data : [
                    {type : 'max', name: '最大值'},
                    {type : 'min', name: '最小值'}
                ]
            },
            markLine : {
                data : [
                    {type : 'average', name: '平均值'}
                ]
            }
        },
        {
            name:'test',
            type:'scatter',
            data:[]
        }
    ]
};   
                
                
}
        );
        
</script>
</body>
</html>
