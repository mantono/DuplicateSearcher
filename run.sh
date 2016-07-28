#!/bin/sh
java -ea -d64 -Xcomp -Xfuture -Xms1G -Xmx10G -XX:+AggressiveOpts -classpath "bin:lib/*" dsv2.DuplicateSearcher $@
