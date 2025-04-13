del *.class

javac *.java
java RaceManyThreads -U
java RaceManyThreads -M 20 -N 4
java RaceManyThreads -M 30 -N 5

del *.class