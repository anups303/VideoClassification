package cavd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.Decorate;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.meta.RotationForest;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.TextDirectoryLoader;
import weka.core.tokenizers.AlphabeticTokenizer;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.supervised.attribute.AttributeSelection;

/*
 * Used to train text classifier for videos. Change input folder in main method. Output is the saved classifier model
 */

public class TextClassifier {
	
	Instances dataRaw;
	FilteredClassifier fc;
	MultiFilter filter;
	StringToWordVector stringFilter;
	AlphabeticTokenizer tokenizer;
	AttributeSelection attributeRank;
	InfoGainAttributeEval attribEval;
	Ranker ranker;
	Decorate cl1;
	RandomForest cl4;
	SMO cl5;
	NaiveBayesMultinomial cl3;
	RotationForest cl2;
	Vote classifier;
	
	public void loadData(String dir) {
		try {
			TextDirectoryLoader loader = new TextDirectoryLoader();
			loader.setDirectory(new File(dir));
			dataRaw = loader.getDataSet();
			//convert dataset into .arff (if necessary)
			/*ArffSaver saver = new ArffSaver();
			saver.setInstances(dataRaw);
			saver.setFile(new File("./src/trainingDataset.arff"));
			saver.writeBatch();*/
		} catch(IOException e) {
			System.err.println(e.getCause()+" "+e.getMessage());
		}
	}
	
	public void evaluate() {
//		Creation of filtered classifier here
		try {
			dataRaw.setClassIndex(1);
			stringFilter = new StringToWordVector();
			stringFilter.setIDFTransform(true);
			stringFilter.setTFTransform(true);
			stringFilter.setLowerCaseTokens(true);
			stringFilter.setOutputWordCounts(true);
			tokenizer = new AlphabeticTokenizer();
			stringFilter.setTokenizer(tokenizer);
			stringFilter.setUseStoplist(true);
			stringFilter.setWordsToKeep(20000);
			attributeRank = new AttributeSelection();
			attribEval = new InfoGainAttributeEval();
			ranker = new Ranker();
			ranker.setThreshold(0);
			attributeRank.setEvaluator(attribEval);
			attributeRank.setSearch(ranker);
			filter = new MultiFilter();
			filter.setFilters(new Filter[]{stringFilter, attributeRank});
			fc = new FilteredClassifier();
			cl1 = new Decorate();
			cl2 = new RotationForest();
			cl3 = new NaiveBayesMultinomial();
			cl4 = new RandomForest();
			cl5 = new SMO();
			cl4.setNumTrees(300);
			cl1.setClassifier(cl4);
			cl2.setClassifier(cl4);
			classifier = new Vote();
//			Setting base classifiers
			classifier.setClassifiers(new Classifier[]{cl1,cl2,cl3,cl4,cl5});
			fc.setClassifier(classifier);
			fc.setFilter(filter);
			Evaluation eval = new Evaluation(dataRaw);
//			Training done on 10-fold cross validation
			eval.crossValidateModel(fc, dataRaw, 10, new Random(1));
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toMatrixString());
			System.out.println(eval.toClassDetailsString());
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void learn() {
//		Train classifier upon dataset
		try {
			dataRaw.setClassIndex(1);
			fc.buildClassifier(dataRaw);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void saveModel(String filename) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
			out.writeObject(fc);
			out.close();
		} catch(IOException e) {
			System.err.println(e.getCause()+" "+e.getMessage());
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		TextClassifier learner = new TextClassifier();
//		Change source folder for loading srt files
		learner.loadData("./src/training/captions/");
		learner.evaluate();
		learner.learn();
		learner.saveModel("./src/Vote.model");
	}

}
