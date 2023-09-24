# EpubRenamer
Renames epubs to a consistent format.
Default format is The_Title-by-Firstname_Lastname.epub
Latest runnable version can be found at https://github.com/brianpipa/epubrenamer/releases

## Building
`mvn clean package` to build it.

## Running
Once you have built it, run it like so to see the options:  
```
> java -jar PATH/TO/epubrenamer.jar  --help
EpubRenamer https://github.com/brianpipa/epubrenamer  
usage:
	 -af,--authorFirst             Filename will have author then title.
	                               Default is title then author
	 -alnf,--authorLastNameFirst   Author name is lastname then firstname.
	                               Default is firstName lastName
	 -f,--folder <arg>             epub folder, defaults to current didirectory if not specified
	 -ps,--pieceSeparator <arg>    characters to separate title from author.
	                               Default is -by-
	 -ws,--wordSeparator <arg>     Character used to separate words, default
```    

A valid run might look like:
```
>java -jar target/epubrenamer-1.0-with-dependencies.jar -f "path/to/epub/folder"
```
