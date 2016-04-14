#!/bin/sh
java -classpath lib/gson-2.2.2.jar:lib/org.eclipse.egit.github.core-2.1.5.jar:bin/ research.experiment.datacollectiontools.DownloadDataSets $1 $2
