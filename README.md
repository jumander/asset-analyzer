# asset-analyzer
An application for viewing assets and test investment strategies.
![alt text](https://github.com/zetez/asset-analyzer/blob/master/asset-analyzer.png?raw=true)


## Installation for IntelliJ
1. Clone this repository
2. Download a ZIP bundle for your platform of lwjgl 3.0.0 or later from https://www.lwjgl.org/download
3. Extract the ZIP bundle into the folder asset-analyzer/lib/
4. In IntelliJ create a new project from asset-analyzer/pom.xml
5. Go to File -> Project Structure -> Project Settings -> Libraries
6. Click New Project Library -> Java and select jsoup-1.10.1.jar in asset-analyzer/lib/
7. Click New Project Library -> Java and select all the *.jar files from the extracted ZIP bundle in asset-analyzer/lib/
8. Run the main function in asset-analyzer/src/main/java/assetAnalyzer/AssetAnalyzer.java
