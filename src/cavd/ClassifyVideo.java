package cavd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.Decorate;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.TextDirectoryLoader;
import weka.core.tokenizers.AlphabeticTokenizer;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.supervised.attribute.AttributeSelection;

public class ClassifyVideo {
	
	Instances dataRaw;
	FilteredClassifier fc;
	MultiFilter filter;
	
	public void loadData(String dir) {
		try {
			TextDirectoryLoader loader = new TextDirectoryLoader();
			loader.setDirectory(new File(dir));
			dataRaw = loader.getDataSet();
			//convert dataset into .arff (if necessary)
			/*ArffSaver saver = new ArffSaver();
			saver.setInstances(dataRaw);
			saver.setDestination(new File("./src/trainingDataset.arff"));
			saver.writeBatch();*/
		} catch(IOException e) {
			System.err.println(e.getCause()+" "+e.getMessage());
		}
	}
	
	public void evaluate() {
		try {
			dataRaw.setClassIndex(1);
			StringToWordVector stringFilter = new StringToWordVector();
			stringFilter.setIDFTransform(true);
			stringFilter.setTFTransform(true);
			stringFilter.setLowerCaseTokens(true);
			stringFilter.setOutputWordCounts(true);
			AlphabeticTokenizer tokenizer = new AlphabeticTokenizer();
			stringFilter.setTokenizer(tokenizer);
			stringFilter.setUseStoplist(true);
			stringFilter.setWordsToKeep(20000);
			AttributeSelection attributeRank = new AttributeSelection();
			InfoGainAttributeEval attribEval = new InfoGainAttributeEval();
			Ranker ranker = new Ranker();
			ranker.setThreshold(0);
			attributeRank.setEvaluator(attribEval);
			attributeRank.setSearch(ranker);
			filter = new MultiFilter();
			filter.setFilters(new Filter[]{stringFilter, attributeRank});
			fc = new FilteredClassifier();
			Decorate classifier = new Decorate();
			RandomForest base = new RandomForest();
			classifier.setClassifier(base);
			fc.setClassifier(classifier);
//			fc.setClassifier(base);
			fc.setFilter(filter);
			Evaluation eval = new Evaluation(dataRaw);
			eval.crossValidateModel(fc, dataRaw, 10, new Random(1));
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void learn() {
		try {
			dataRaw.setClassIndex(1);
			StringToWordVector stringFilter = new StringToWordVector();
			stringFilter.setIDFTransform(true);
			stringFilter.setTFTransform(true);
			stringFilter.setLowerCaseTokens(true);
			stringFilter.setOutputWordCounts(true);
			AlphabeticTokenizer tokenizer = new AlphabeticTokenizer();
			stringFilter.setTokenizer(tokenizer);
			stringFilter.setUseStoplist(true);
			stringFilter.setWordsToKeep(20000);
			AttributeSelection attributeRank = new AttributeSelection();
			InfoGainAttributeEval attribEval = new InfoGainAttributeEval();
			Ranker ranker = new Ranker();
			ranker.setThreshold(0);
			attributeRank.setEvaluator(attribEval);
			attributeRank.setSearch(ranker);
			filter = new MultiFilter();
			filter.setFilters(new Filter[]{stringFilter, attributeRank});
			fc = new FilteredClassifier();
			Decorate classifier = new Decorate();
			RandomForest base = new RandomForest();
			classifier.setClassifier(base);
			fc.setClassifier(classifier);
//			fc.setClassifier(base);
			fc.setFilter(filter);
			fc.buildClassifier(dataRaw);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void saveModel(String filename) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("./src/fClassifier.model"));
			out.writeObject(fc);
			out.close();
		} catch(IOException e) {
			System.err.println(e.getCause()+" "+e.getMessage());
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		
//		System.out.println(eval.toSummaryString());
//		System.out.println(eval.toClassDetailsString());
//		System.out.println(fc);
		
		ClassifyVideo learner = new ClassifyVideo();
		learner.loadData("./src/training/");
		learner.evaluate();
		learner.learn();
		learner.saveModel("./src/fClassifier.model");
	}

}
