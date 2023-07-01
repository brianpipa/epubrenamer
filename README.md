# EpubRenamer
Renames epubs to a consistent format.
Default format is The_Title-by-Firstname_Lastname.epub
Latest runnable version can be found at https://github.com/brianpipa/epubrenamer/releases

## Building
`mvn clean package` to build it.

## Running
Once you have built it, run it like so:  
```
> java -jar target/epubrenamer-0.0.1-SNAPSHOT-jar-with-dependencies.jar  
EpubRenamer https://github.com/brianpipa/epubrenamer  
Missing required option: f  
usage:
	 -af,--authorFirst             Filename will have author then title.
	                               Default is title then author
	 -alnf,--authorLastNameFirst   Author name is lastname then firstname.
	                               Default is firstName lastName
	 -f,--folder <arg>             epub folder
	 -ps,--pieceSeparator <arg>    characters to separate title from author.
	                               Default is -by-
	 -ws,--wordSeparator <arg>     Character used to separate words, default
```    
You must specify the folder to look for epubs. Everything else is optional.

An valid run would look like:
```
>java -jar target/epubrenamer-0.0.1-SNAPSHOT-jar-with-dependencies.jar -f "/media/bpipa/250GBInternal/Shared ebooks/Brian/read"
```
