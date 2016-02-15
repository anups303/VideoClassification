# VideoClassification
Genre Classification for User-generated Video

Text classification:

Prerequisites:

1. Prepare caption file in srt extension in format - <YouTube video id>.srt
2. For videos without captions, create empty file in same format.

Procedure:

Run CollateInfo.java on caption files once to add video title information to srt files
Run TextClassificationTester.java for individual srt files or for all together

Audio Classification:

Prerequisites:

1. Extract wav files from videos being tested.
2. Extract MFCC features and its derivatives from wav files using jAudio, get output as .arff files

Run AddClassAttribute.java to add a class attribute (if needed)
Run AudioClassificationTester.java for individual arff files or all together

Other files:

TextClassifier.java for training text classifier
AudioClassifier.java for training audio classifier
