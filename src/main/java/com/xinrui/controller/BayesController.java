package com.xinrui.controller;

import java.util.ArrayList;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hankcs.hanlp.HanLP;
import com.xinrui.test.TestBayes;
import com.xinrui.util.Bayes;

@Controller
@EnableAutoConfiguration
public class BayesController {

	@RequestMapping("/classify")
	@ResponseBody
	public String classify(String text) throws Exception {
		// 获取当前工程存放位置
		String path = TestBayes.class.getResource("").getPath();
		String classPath = path.substring(0, path.indexOf("/com/xinrui"));
		// 模型文件存放位置
		String modelName = classPath + "/model/classify_model.txt";
		ArrayList<ArrayList<String>> model = Bayes.read(modelName);
		// 抽取10个关键词组成一个元祖
		ArrayList<String> testData = (ArrayList<String>) HanLP.extractKeyword(text, 10);
		// 输出结果
		String result = Bayes.predictClassify(model, testData);
		
		return result;
	}

}
