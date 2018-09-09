FamTree
=======

FamTree is a tool for printing out famliy trees from gedcom data. I wanted
to print out a big tree for a famliy reunion and couldn't find another
tool to do this.  The primary use of this tool is to print out a tree of
descendants from a single individual.

If you want to include pictures in your printouts, you will have to
export your GEDCOM from Famliy Tree Maker.  This will embed references
to media as paths to local files on your disk. FTM also will use a custom
GEDCOM _PHOTO tag to indicate which media file is the primary photo
of the person.  Currently this tool can't handle GEDCOM files exported
directly from ancestry.com.

I printed some descendant trees that were about 10 ft wide by using FamTree
to create a large PDF and printing the PDF from acrobat reader in poster
form.

Download
--------
A zip of the latest released version is [here](https://github.com/sappling/famtree/releases).

How To Build
------------
This project include a [gradle](http://gradle.org) build file and wrapper.
Use the provided gradlew file to create a project for your IDE or a
command line runner.  You will need to have a Java 8 JRE or JDK installed and your
JAVA_HOME environment variable set with a path to your JRE.

Run "gradlew idea" to generate a project for [IntelliJ Idea](https://www.jetbrains.com/idea/).
Run "gradlew eclipse" to generate a project for [Eclipse](https://eclipse.org/ide/).

Run "gradlew dist" to build a runnable sample in the build/install/rallyx
directory and a zip in build/distributions.

Running
-------
Use the script bin/famtree.bat or bin/famtree to run the tool.


#### Command Line Arguments

```
usage: famtree [-gen <numgen>] [-help] [-id <ID>] [-in <filename>]
       [-layout <type>] [-names] [-out <filename>] [-stop <ID>] [-type
       <type>]
 -gen <numgen>     maximum number of generations
 -help             Show commmand line usage
 -id <ID>          ID number of top ancestor
 -in <filename>    specifies gedcom input file
 -layout <type>    Type of layout: descendant (default) or paternalline
 -names            List names and IDs in input GEDCOM file
 -out <filename>   specifies input file
 -stop <ID>        Person to stop at
 -type <type>      Type of output: svg or pdf
```

People are identified in your gedcom file with an ID such as:
```
0 @I201@ INDI
1 NAME Lisa Franklin /Doty/
```
When using the "id" or "stop" arguments that take an ID parameter, it is the id between the @ signs on the
INDI line.  For example,
`famtree -in famdata.ged -id I201` would
print out a descendant chart for Lisa Doty.

When creating a chart, I found that in some cases, the resulting chart
was just too wide.  In these cases you may want to use the `-stop` parameter
to remove one branch from the chart and print another chart for that person's
descendants.


License
-------
This application is licensed under the [MIT License](https://opensource.org/licenses/MIT)

