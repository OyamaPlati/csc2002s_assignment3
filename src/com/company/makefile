JFLAGS = -g
JC = javac

.SUFFIXES: .java .class
.java.class:
        $(JC) $(JFLAGS) $*.java

CLASSES = \ Vector.java \ CloudData.java \ PrevailingWind.java \ CloudClassification.java \ CloudDataParallelAnalysis.j>
default: classes

classes: $(CLASSES:.java=.class)

clean:
        $(RM) *.class
