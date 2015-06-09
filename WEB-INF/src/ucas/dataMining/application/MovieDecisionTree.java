package ucas.dataMining.application;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ucas.dataMining.dao.User;
import ucas.dataMining.dataAccess.DataFactory;
import ucas.dataMining.decisionTree.DecisionTree;
import ucas.dataMining.decisionTree.UnknownDecisionException;
import ucas.dataMining.util.FileIOUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class MovieDecisionTree implements Runnable{
	public String movieId;
	public MovieDecisionTree()
	{
		
	}
	public MovieDecisionTree(String movieId)
	{
		this.movieId = movieId;
	}
	public void trainAndSave(String movieId,String savePath) {
		DecisionTree dt = new DecisionTree();
		List<User> users = DataFactory.getAllUsers();
		//构建决策树
		
		dt.setAttributeNames(new String[]{"Age","Gender","Occupation","ZipCode"});
		
		for(int i=0;i<users.size();i++)
		{
			Integer rating = users.get(i).getRatings().get(movieId);
//			System.out.println("电影评分"+rating);
			//找到用户对电影的评分
			if(rating!=null)
			{
				//根据评分确定是否推荐
				String choice = "";
				if(rating>=3)choice = "YES";
				else if(rating<3) choice = "NO";
				dt.addInstance(new String[]{users.get(i).getAge()+"",users.get(i).getGender(),
						users.get(i).getOccupation(),users.get(i).getZipcode()}, choice);
			}else{
				continue;
			}
		}

		// 开始生成json
		JSONObject jsonBuilder = new JSONObject();
		JSONArray yes = new JSONArray();
		JSONArray no = new JSONArray();
		JSONArray unkonwn = new JSONArray();

		try {

			for (User user : users) {
				Map<String, String> cases = new HashMap<String, String>();
				cases.put("Age", user.getAge() + "");
				cases.put("Gender", user.getGender());
				cases.put("Occupation", user.getOccupation());
				cases.put("ZipCode", user.getZipcode());
				JSONObject item = new JSONObject();
				item.put("id", (Integer.parseInt(user.getId())));
				String decision = dt.classify(cases);
				if (decision.equals("YES")) {
					yes.add(item);
				} else if (decision.equals("NO")) {
					no.add(item);
				} else
					unkonwn.add(item);
			}
			jsonBuilder.put("YES", yes);
			jsonBuilder.put("NO", no);
			jsonBuilder.put("UNKNOWN", unkonwn);

		} catch (UnknownDecisionException e) {
			System.out.println("?");
		}

		// 写json文件
		try {
			FileIOUtil.writeToFile(jsonBuilder.toJSONString(),
					savePath);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void run() {
		System.out.println("开始进行决策树分类");
		String savePath = FileIOUtil.rootPath+"\\json\\decisionTree.json";
		this.trainAndSave(movieId,savePath);
		
		Flags.decisionTree = true;
		System.out.println("决策树分类结束");
	}

}
