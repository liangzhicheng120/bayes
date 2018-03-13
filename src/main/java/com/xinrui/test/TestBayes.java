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
		ArrayList<String> testData1 = (ArrayList<String>) HanLP.extractKeyword("据了解，2017年，北京市将加大自住房供应力度，将新增1.5万套自住房用地，而近期就有近一半自住房用地有了着落。", 10);
		ArrayList<String> testData2 = (ArrayList<String>) HanLP.extractKeyword("小明语文考试考了100分,非常高兴,也非常兴奋,结果被老爸打了一顿,郁闷了一天。", 10);
		for (String string : testData2) {
			System.out.println(string);
		}
		// 输出预测结果
		System.out.println(Bayes.predictClassify(model, testData1));
		System.out.println(Bayes.predictClassify(model, testData2));
	}
}
