package cavd;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.LADTree;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class AudioClassifier {
	Instances data;
	LADTree ladTree;
	RandomForest rForest;
	public void loadDataset(String filename) {
		try {
			DataSource source = new DataSource(filename);
			data = source.getDataSet();
			if(data.classIndex() == -1)
				data.setClassIndex(data.numAttributes()-1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void evaluate() {
		try {
//			ladTree = new LADTree();
			rForest = new RandomForest();
			Evaluation eval = new Evaluation(data);
//			eval.crossValidateModel(ladTree, data, 10, new Random(1));
			eval.crossValidateModel(rForest, data, 10, new Random(1));
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toMatrixString());
			System.out.println(eval.toClassDetailsString());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void learn() {
		try {
//			ladTree.buildClassifier(data);
			rForest.buildClassifier(data);
//			System.out.println(ladTree);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void saveModel(String filename) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
//			out.writeObject(ladTree);
			out.writeObject(rForest);
			out.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception {
		AudioClassifier audClass = new AudioClassifier();
		audClass.loadDataset("./src/training/audio/final.arff");
		audClass.evaluate();
		audClass.learn();
		audClass.saveModel("./src/audClassifier.model");
	}
}
