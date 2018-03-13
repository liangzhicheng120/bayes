package com.xinrui.test;

import java.util.ArrayList;

import com.hankcs.hanlp.HanLP;
import com.xinrui.util.Bayes;

public class TestBayes {
	public static void main(String[] args) throws Exception {
		// 获取当前工程存放位置
		String path = TestBayes.class.getResource("").getPath();
		String classPath = path.substring(0, path.indexOf("/com/xinrui"));
		// 模型文件存放位置
		String modelName = classPath + "/model/classify_model.txt";
		ArrayList<ArrayList<String>> model = Bayes.read(modelName);
		// 抽取10个关键词组成一个元祖
		ArrayList<String> testData = (ArrayList<String>) HanLP
				.extractKeyword(
						"时值“大海贼时代”，为了寻找传说中海贼王罗杰所留下的大秘宝“ONE PIECE”，无数海贼扬起旗帜，互相争斗。有一个梦想成为海盗的少年叫路飞，他因误食“恶魔果实”而成为了橡皮人，在获得超人能力的同时付出了一辈子无法游泳的代价。十年后，路飞为实现与因救他而断臂的香克斯的约定而出海，他在旅途中不断寻找志同道合的伙伴，开始了以成为海贼王为目标的伟大的冒险旅程[9]  ",
						15);
		// 输出预测结果
		System.out.println(Bayes.predictClassify(model, testData));
	}
}
