JC = javac
JCFLAGS = -g -Xlint -cp .:lanterna-2.1.6.jar:jopt-simple-4.5.jar
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JCFLAGS) $<

SFSOURCES = SumSequential.java SumThreaded.java \
	      FactorSequential.java FactorThreaded.java

LIFESOURCES = GameOfLifeSequential.java GameOfLife.java

default: sf life

sf: $(SFSOURCES:.java=.class)

life: $(LIFESOURCES:.java=.class)

GameOfLifeSequential.class: lanterna-2.1.6.jar jopt-simple-4.5.jar patterns
GameOfLife.class: lanterna-2.1.6.jar jopt-simple-4.5.jar patterns

lanterna-2.1.6.jar:
	wget https://lanterna.googlecode.com/files/lanterna-2.1.6.jar

jopt-simple-4.5.jar:
	wget http://central.maven.org/maven2/net/sf/jopt-simple/jopt-simple/4.5/jopt-simple-4.5.jar

patterns:
	wget http://www.conwaylife.com/patterns/all.zip
	unzip -j -d patterns all.zip *.cells
	$(RM) all.zip

clean:
	$(RM) *.class

reallyclean: clean
	$(RM) lanterna-2.1.6.jar
	$(RM) jopt-simple-4.5.jar
	$(RM) -r patterns