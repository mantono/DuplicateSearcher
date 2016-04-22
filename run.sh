#!/bin/sh
java -classpath lib/gson-2.2.2.jar:lib/org.eclipse.egit.github.core-2.1.5.jar:lib/edu.mit.jwi_2.4.0.jar:lib/snowball-stemmer-1.3.0.581.1.jar:bin/ duplicatesearcher.DuplicateSearcher $1 $2
