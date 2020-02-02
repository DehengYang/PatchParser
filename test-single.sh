#!/bin/bash

# install gumtree
cd gumtree
mvn compile
mvn install -Dmaven.test.skip=true

# package patchparser
cd ../Parser
mvn compile
mvn dependency:copy-dependencies
mvn package -Dmaven.test.skip=true
mv -f target/Parser-0.0.1-SNAPSHOT.jar target/dependency/

# proj
bugProjList=(Chart Closure Lang Math Mockito Time)
oriProjList=(jfreechart closure-compiler commons-lang commons-math mockito joda-time)
bugNumberList=(26 133 65 106 38 27) 

# execute in Parser/ folder
#for bugProj in ${bugProjList[@]}
for((i=0;i<${#bugProjList[@]};i++))
do 
	bugProj=${bugProjList[i]}
	oriProj=${oriProjList[i]}
	bugNumber=${bugNumberList[i]}

	# download all bugs for a given program (e.g., Chart -> Chart 1 to 26) 
	java -cp "target/dependency/*" -Xmx2g edu.lu.uni.serval.Main -d4j /home/dale/ALL_APR_TOOLS/d4j-repo/ -bugProj $bugProj -oriProj $oriProj -id 1 -singleBug true
	java -cp "target/dependency/*" -Xmx2g edu.lu.uni.serval.Main2 -d4j /home/dale/ALL_APR_TOOLS/d4j-repo/ -bugProj $bugProj -oriProj $oriProj -id 1
	break 

<<C1
	# run for each bug
	for id in $(seq 1 $bugNumber) 
	do
		java -cp "target/dependency/*" -Xmx2g edu.lu.uni.serval.Main2 -d4j /home/dale/ALL_APR_TOOLS/d4j-repo/ -bugProj $bugProj -oriProj $oriProj -id $id 
	done

	# mv data
	mv ../data ../data-$bugProj
C1


done














