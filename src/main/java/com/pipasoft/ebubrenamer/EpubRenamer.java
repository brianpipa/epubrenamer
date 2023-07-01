package com.pipasoft.ebubrenamer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jazzlib.ZipException;
import net.sf.jazzlib.ZipFile;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

public class EpubRenamer {

	private static final String DEFAULT_WORD_SEPARATOR = "_";
	private static final String DEFAULT_PIECE_SEPARATOR = "-by-";
	
	private static String wordSeparator = DEFAULT_WORD_SEPARATOR;
	private static String pieceSeparator = DEFAULT_PIECE_SEPARATOR;
	
	private static boolean authorLastnameFirst = false;
	private static boolean authorFirst = false;
	private static String directory;
	
	
	public static void main(String[] args) {
		System.out.println("EpubRenamer https://github.com/brianpipa/epubrenamer");
		
		parseOptions(args);
		System.out.println("-------------------------");
		System.out.println("directory="+directory);
		System.out.println("wordSeparator="+wordSeparator);
		System.out.println("pieceSeparator="+pieceSeparator);
		System.out.println("authorLastnameFirst="+authorLastnameFirst);
		System.out.println("authorFirst="+authorFirst);
		System.out.println("-------------------------");
		//System.exit(0);
		
		Logger log = LoggerFactory.getLogger(EpubRenamer.class);
		log.debug("TESTING");
		
		int renamedCount = 0;
		try {
			List<String> fileList = findFiles(Paths.get(directory));

			System.out.println("Found "+fileList.size()+" epubs in "+directory);
			for (String epubFile : fileList) {
				Book book = getBook(epubFile);
				List<Author> authors = book.getMetadata().getAuthors();
				String authorString = getAuthorString(authors);
				String renameString;;
				if (authorFirst) {
					renameString = authorString +pieceSeparator+ book.getMetadata().getFirstTitle() ;					
				} else {
					renameString = book.getMetadata().getFirstTitle() + pieceSeparator + authorString;
				}
				renameString = renameString.replace(" ", wordSeparator);
				//System.out.println("renaming "+epubFile +" to ["+renameString+"]");

				Path source = Paths.get(epubFile);
				if (Files.exists(source.resolveSibling(renameString + ".epub"))) {
					//System.err.println("SKIPPING "+renameString +"-- already exists");
				} else {
					System.out.println("renaming to ["+renameString+".epub] from "+epubFile);
					Files.move(source, source.resolveSibling(renameString + ".epub"));
					renamedCount++;
				}				
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(renamedCount+" epubs renamed");
	}

	private static Book getBook(String path) throws ZipException, IOException {
		return new EpubReader().readEpubLazy(new ZipFile(path), "UTF-8");
	}

	private static String getAuthorString(List<Author> authors) {
		StringBuilder sb = new StringBuilder();
		int count = 1;
		for (Author author : authors) {
			if (count > 1) {
				sb.append(" and ");
			}
			if (authorLastnameFirst) {
				sb.append(author.getLastname() + "," + author.getFirstname());
			} else {
				sb.append(author.getFirstname() + " " + author.getLastname());	
			}
			
			count++;
		}
		return sb.toString();
	}

	public static List<String> findFiles(Path path) throws IOException {

		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path must be a directory!");
		}

		List<String> result;

		try (Stream<Path> walk = Files.walk(path)) {
			result = walk.filter(p -> !Files.isDirectory(p)).map(p -> p.toString())
					.filter(f -> f.toLowerCase().endsWith("epub")).collect(Collectors.toList());
		}

		return result;
	}

	/**
	 * parses command line options
	 * 
	 * @param args commandline args
	 */	
	private static void parseOptions(String[] args) {
        Options options = new Options();

        Option f = new Option("f", "folder", true, "epub folder");
        f.setRequired(true);
        options.addOption(f);

        Option ws = new Option("ws", "wordSeparator", true, "Character used to separate words, default is "+DEFAULT_WORD_SEPARATOR);
        ws.setRequired(false);
        options.addOption(ws);        
        
        Option ps = new Option("ps", "pieceSeparator", true, "characters to separate title from author. Default is "+DEFAULT_PIECE_SEPARATOR);
        options.addOption(ps);
                
        Option alnf = new Option("alnf", "authorLastNameFirst", false, "Author name is lastname then firstname. Default is firstName lastName");
        options.addOption(alnf);

        Option af = new Option("af", "authorFirst", false, "Filename will have author then title. Default is title then author");
        options.addOption(af);        
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp(" ", options);
            System.exit(1);
        }

        if (cmd.hasOption("f")) {
        	directory =  cmd.getOptionValue("f");        	
        	if (!directory.endsWith(File.separator)) {
        		directory += File.separator;
        	}
        	if (!Files.exists(Paths.get(directory)) ||!( new File(directory)).isDirectory()) {
        		System.err.println(directory+" does not exist or is not a folder");
        		System.exit(1);
        	}        	
        }
        
        if (cmd.hasOption("ws")) {
        	wordSeparator = cmd.getOptionValue("ws");
        }
        
        if (cmd.hasOption("ps")) {
        	pieceSeparator = cmd.getOptionValue("ps");
        }        

        if (cmd.hasOption("alnf")) {
        	authorLastnameFirst = true;
        }
        if (cmd.hasOption("af")) {
        	authorFirst = true;
        }
        
	}

}
