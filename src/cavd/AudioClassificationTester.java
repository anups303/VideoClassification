package cavd;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import weka.classifiers.trees.LADTree;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class AudioClassificationTester {
	Instances data;
//	LADTree ladTree;
	RandomForest rForest;
	public void loadData(String filename) {
		try {
			DataSource source = new DataSource(filename);
			data = source.getDataSet();
			if(data.classIndex() == -1)
				data.setClassIndex(data.numAttributes()-1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void loadModel(String filename) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
//			ladTree = (LADTree)in.readObject();
			rForest = (RandomForest)in.readObject();
			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public double[] classify(int showProb) {
		double[] pred = null;
		try {
			double predict = rForest.classifyInstance(data.instance(0));
			System.out.println("Audio predicted class: "+data.classAttribute().value((int)predict));
//			double[] pred = ladTree.distributionForInstance(data.firstInstance());
			pred = rForest.distributionForInstance(data.firstInstance());
			if(showProb == 1) {
				System.out.println("Probability from audio file:");
				for(int i=0;i<pred.length;i++)
					System.out.println(data.classAttribute().value(i)+":"+Double.toString(pred[i]));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return pred;
	}
	public static void main(String[] args) throws Exception {
		AudioClassificationTester tester = new AudioClassificationTester();
		tester.loadData("./src/testing/test.arff");
		tester.loadModel("./src/audClassifier.model");
		tester.classify(0);
	}
}
