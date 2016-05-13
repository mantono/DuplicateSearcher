#!/bin/sh
java -ea -d64 -Xcomp -Xfuture -Xms1G -Xmx16G -XX:+AggressiveOpts -classpath lib/gson-2.2.2.jar:lib/org.eclipse.egit.github.core-2.1.5.jar:lib/edu.mit.jwi_2.4.0.jar:lib/snowball-stemmer-1.3.0.581.1.jar:lib/jwi-2.2.3.jar:bin/ research.experiment.Experiment -t 1.5 -b 1.0 -l 0.5 -c 0.5 -d 0.5 -a 0.5
