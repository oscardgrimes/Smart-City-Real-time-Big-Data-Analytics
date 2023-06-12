import sys

from sklearn import svm
import joblib

#Load pickle files
svmFog = joblib.load('C:\Python27\svmFog.pkl')
svmHaze = joblib.load('C:\Python27\svmHaze.pkl')

#Make predictions
def analyzeData(data):
    list = data.split(",")
    fog_predict = svmFog.predict([list])
    haze_predict = svmHaze.predict([list])
    return [str(fog_predict[0]), str(haze_predict[0])]



if __name__ == "__main__":
    print(analyzeData(sys.argv[1]))

