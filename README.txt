To compile the Addon on Windows:
 - Put the whole project in a folder named "QTasteAddOn_AbsCon".
 - Create a batch file next to this folder with the following content:
     pushd QTasteAddOn_AbsCon
     call mvn clean install assembly:single
     popd
  
Then, copy the file  "QTasteAddOn_AbsCon/target/AbsCon-deploy.jar" in the pluggin repository of QTaste.
