package com.xinrui.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.hankcs.hanlp.HanLP;

/**
 * 贝叶斯计算器主体类
 */
public class Bayes {

	private static Logger logger = Logger.getLogger(Bayes.class);

	/**
	 * 将原训练元组按类别划分
	 * 
	 * @param datas
	 *            训练元组
	 * @return Map<类别，属于该类别的训练元组>
	 */
	public static Map<String, ArrayList<ArrayList<String>>> classifyByCategory(ArrayList<ArrayList<String>> datas) {
		if (datas == null) {
			return null;
		}

		Map<String, ArrayList<ArrayList<String>>> map = new HashMap<String, ArrayList<ArrayList<String>>>();
		ArrayList<String> singleTrainning = null;
		String classificaion = "";
		for (int i = 0; i < datas.size(); i++) {
			singleTrainning = datas.get(i);
			classificaion = singleTrainning.get(0);
			singleTrainning.remove(0);
			if (map.containsKey(classificaion)) {
				map.get(classificaion).add(singleTrainning);
			} else {
				ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
				list.add(singleTrainning);
				map.put(classificaion, list);
			}
		}

		return map;
	}

	/**
	 * 在训练数据的基础上预测测试元组的类别
	 * 
	 * @param datas
	 *            训练元组
	 * @param testData
	 *            测试元组
	 * @return 测试元组的类别
	 */
	public static String predictClassify(ArrayList<ArrayList<String>> datas, ArrayList<String> testData) {

		if (datas == null || testData == null) {
			return null;
		}

		int maxPIndex = -1;
		Map<String, ArrayList<ArrayList<String>>> map = classifyByCategory(datas);
		Object[] classes = map.keySet().toArray();
		double maxProbability = 0.0;
		for (int i = 0; i < map.size(); i++) {
			double p = 0.0;
			for (int j = 0; j < testData.size(); j++) {
				p += calProbabilityClassificationInKey(map, classes[i].toString(), testData.get(j));
			}
			if (p > maxProbability) {
				maxProbability = p;
				maxPIndex = i;
			}
		}

		return maxPIndex == -1 ? "其他" : classes[maxPIndex].toString();
	}

	/**
	 * 在训练数据的基础上预测测试元组的类别
	 * 
	 * @param testData
	 *            测试元组
	 * @return 测试元组的类别
	 * @throws Exception
	 */
	public String predictClassify(ArrayList<String> testData, String mId) throws Exception {
		return predictClassify(read(mId), testData);
	}

	/**
	 * 某一特征值在某一分类上的概率分布[ P(key|Classify) ]
	 * 
	 * @param classify
	 *            某一分类特征向量集
	 * @param value
	 *            某一特征值
	 * @return 概率分布
	 */
	private static double calProbabilityKeyInClassification(ArrayList<ArrayList<String>> classify, String value) {
		if (classify == null || StringUtils.isEmpty(value)) {
			return 0.0;
		}
		int totleKeyCount = 0;
		int foundKeyCount = 0;
		ArrayList<String> featureVector = null; // 分类中的某一特征向量
		for (int i = 0; i < classify.size(); i++) {
			featureVector = classify.get(i);
			for (int j = 0; j < featureVector.size(); j++) {
				totleKeyCount++;
				if (featureVector.get(j).equalsIgnoreCase(value)) {
					foundKeyCount++;
				}
			}
		}
		return totleKeyCount == 0 ? 0.0 : 1.0 * foundKeyCount / totleKeyCount;
	}

	/**
	 * 获得某一分类的概率 [ P(Classify) ]
	 * 
	 * @param classes
	 *            分类集合
	 * @param classify
	 *            某一特定分类
	 * @return 某一分类的概率
	 */
	private static double calProbabilityClassification(Map<String, ArrayList<ArrayList<String>>> map, String classify) {
		if (map == null | StringUtils.isEmpty(classify)) {
			return 0;
		}
		Object[] classes = map.keySet().toArray();
		int totleClassifyCount = 0;
		for (int i = 0; i < classes.length; i++) {
			totleClassifyCount += map.get(classes[i].toString()).size();
		}
		return 1.0 * map.get(classify).size() / totleClassifyCount;
	}

	/**
	 * 获得关键词的总概率
	 * 
	 * @param map
	 *            所有分类的数据集
	 * @param key
	 *            某一特征值
	 * @return 某一特征值在所有分类数据集中的比率
	 */
	private static double calProbabilityKey(Map<String, ArrayList<ArrayList<String>>> map, String key) {
		if (map == null || StringUtils.isEmpty(key)) {
			return 0;
		}
		int foundKeyCount = 0;
		int totleKeyCount = 0;
		Object[] classes = map.keySet().toArray();
		for (int i = 0; i < map.size(); i++) {
			ArrayList<ArrayList<String>> classify = map.get(classes[i]);
			ArrayList<String> featureVector = null; // 分类中的某一特征向量
			for (int j = 0; j < classify.size(); j++) {
				featureVector = classify.get(j);
				for (int k = 0; k < featureVector.size(); k++) {
					totleKeyCount++;
					if (featureVector.get(k).equalsIgnoreCase(key)) {
						foundKeyCount++;
					}
				}
			}
		}
		return totleKeyCount == 0 ? 0.0 : 1.0 * foundKeyCount / totleKeyCount;
	}

	/**
	 * 计算在出现key的情况下，是分类classify的概率 [ P(Classify | key) ]
	 * 
	 * @param map
	 *            所有分类的数据集
	 * @param classify
	 *            某一特定分类
	 * @param key
	 *            某一特定特征
	 * @return P(Classify | key)
	 */
	private static double calProbabilityClassificationInKey(Map<String, ArrayList<ArrayList<String>>> map, String classify, String key) {
		ArrayList<ArrayList<String>> classifyList = map.get(classify);
		double pkc = calProbabilityKeyInClassification(classifyList, key); // p(key|classify)
		double pc = calProbabilityClassification(map, classify); // p(classify)
		double pk = calProbabilityKey(map, key); // p(key)
		return pk == 0 ? 0 : pkc * pc / pk; // p(classify | key)
	}

	/**
	 * 读取训练文档中的训练数据 并进行封装
	 * 
	 * @param filePath
	 *            训练文档的路径
	 * @return 训练数据集
	 * @throws Exception
	 */
	public static ArrayList<ArrayList<String>> read(String clzss) throws Exception {
		ArrayList<String> singleTrainning = null;
		ArrayList<ArrayList<String>> trainningSet = new ArrayList<ArrayList<String>>();
		List<String> datas = new ArrayList<String>(FileUtils.readLines(new File(clzss), Charsets.UTF_8));
		if (datas.size() == 0) {
			logger.error("[" + "模型文件加载错误" + "]" + clzss);
			throw new Exception("模型文件加载错误!");
		}
		for (int i = 0; i < datas.size(); i++) {
			String[] characteristicValues = datas.get(i).split(" ");
			singleTrainning = new ArrayList<String>();
			for (int j = 0; j < characteristicValues.length; j++) {
				if (StringUtils.isNotEmpty(characteristicValues[j])) {
					singleTrainning.add(characteristicValues[j]);
				}
			}
			trainningSet.add(singleTrainning);
		}
		return trainningSet;
	}

	/**
	 * 
	 * @param fileName
	 *            训练文件
	 * @param size
	 *            关键词个数
	 */
	public static void trainBayes(String fileName, String mId, int size) {
		try {
			Bayes bayes = new Bayes();
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			int total = 0;
			int right = 0;
			long start = System.currentTimeMillis();
			while ((line = reader.readLine()) != null) {
				ArrayList<String> testData = (ArrayList<String>) HanLP.extractKeyword(line, size);
				String classification = bayes.predictClassify(testData, mId);
				if (classification.equals(fileName.split("\\.")[0])) {
					right += 1;
				}
				System.out.print("\n分类：" + classification);
				total++;
			}
			reader.close();
			long end = System.currentTimeMillis();
			System.out.println("正确分类：" + right);
			System.out.println("总行数：" + total);
			System.out.println("正确率：" + MathUtil.div(right, total, 4) * 100 + "%");
			System.out.println("程序运行时间： " + (end - start) / 1000 + "s");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
