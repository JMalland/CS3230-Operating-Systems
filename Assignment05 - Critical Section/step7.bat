del *.class
javac *.java

java RaceManyThreads -M 10 -N 2
java RaceManyThreads -M 10 -N 3
java RaceManyThreads -M 20 -N 4
java RaceManyThreads -M 20 -N 5
java RaceManyThreads -M 50 -N 5

del *.class