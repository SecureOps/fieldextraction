package com.secureops.fieldextraction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class TextParser {

    private static final String OPTION_INPUT_FILE = "input_file";
    private static final String OPTION_INPUT_DESC = "File to parse (absolute path). Use special string 'STDIN' to read from STDIN instead";
    private static final String OPTION_CONFIGURATION_FILE = "properties_file";
    private static final String OPTION_CONFIGURATION_DESC = "Properties file to use (absolute path)";
    private static final String OPTION_OUTPUT_DIRECTORY = "output_dir";
    private static final String OPTION_OUTPUT_FILE = "output_file";
    private static final String OPTION_OUTPUT_DESC = "output directory (absolute path) or output file. * Conflicting options.";
    private static final String OPTION_EXTRACTOR_NAME = "extractor_name";
    private static final String OPTION_EXTRACTOR_NAME_DESC = "Field key that contains the extractor name designation";

    private File inputFile = null;
    private File outputDir = null;
    private File outputFile = null;
    private String confFile = "fieldextraction.properties";
    private String noMatchFileName = "noMatch.txt";
    private String extractorNameString = "extractor_name";

    private Map<String, PrintWriter> outputs = new HashMap<String, PrintWriter>();

    private FieldExtractor extractor;

    @SuppressWarnings("static-access")
    static Options buildOptions() throws IllegalArgumentException {
        Options o = new Options();
        o.addOption(OptionBuilder.hasArg().isRequired()
                .withArgName(OPTION_INPUT_FILE)
                .withDescription(OPTION_INPUT_DESC).create(OPTION_INPUT_FILE));
        o.addOption(OptionBuilder.hasOptionalArg()
                .withArgName(OPTION_OUTPUT_DIRECTORY)
                .withDescription(OPTION_OUTPUT_DESC)
                .create(OPTION_OUTPUT_DIRECTORY));
        o.addOption(OptionBuilder.hasOptionalArg()
                .withArgName(OPTION_OUTPUT_FILE)
                .withDescription(OPTION_OUTPUT_DESC).create(OPTION_OUTPUT_FILE));
        o.addOption(OptionBuilder.hasOptionalArg()
                .withArgName(OPTION_CONFIGURATION_FILE)
                .withDescription(OPTION_CONFIGURATION_DESC)
                .create(OPTION_CONFIGURATION_FILE));
        o.addOption(OptionBuilder.hasOptionalArg()
                .withArgName(OPTION_EXTRACTOR_NAME)
                .withDescription(OPTION_EXTRACTOR_NAME_DESC)
                .create(OPTION_EXTRACTOR_NAME));
        return o;
    }

    private void printHelp(String reason, Options opts) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("TextParser", "", opts, reason);
        // sometimes when you put 'reason' in the formatter it returns a
        // java.lang.StringIndexOutOfBoundsException: String index out of range.
        // It seems to be related to this https://issues.apache.org/jira/browse/CLI-8
        // but cleaning special chars, line separators does not necessarily helps
        // TODO need some more investigation
    }

    private void parseCommandLine(String[] args) throws ParseException,
            ConfigurationException {
        Options opts = buildOptions();
        CommandLineParser parser = new BasicParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(opts, args);
            String fileName = line.getOptionValue(OPTION_INPUT_FILE);

            // Check if the input file exists, if not, throw exception
            File inFile = new File(fileName);
            if ((!fileName.equals("STDIN"))
                    && (!inFile.exists() && !inFile.canRead())) {
                throw new ParseException("File " + fileName + " not found");
            }
            this.inputFile = inFile;

            // One, and only one of these 2 parameters must be provided
            String outFile = line.getOptionValue(OPTION_OUTPUT_FILE);
            String outDir = line.getOptionValue(OPTION_OUTPUT_DIRECTORY);
            if ((outFile == null && outDir == null)
                    || (outFile != null && outDir != null)) {
                throw new ConfigurationException(
                        "ERROR: One, and ONLY ONE of output_file and output_dir parameters must be provided");
            }

            if (outDir != null)
                this.outputDir = new File(outDir);
            if (outFile != null)
                this.outputFile = new File(outFile);

            String conf = line.getOptionValue(OPTION_CONFIGURATION_FILE, null);
            if (conf != null) {
                this.confFile = conf;
            }
            // Parse the configuration file
            this.extractor = FieldExtractorConfigLoader.loadConf(this.confFile);

            this.extractorNameString = line.getOptionValue(
                    OPTION_EXTRACTOR_NAME, this.extractorNameString);

        } catch (ParseException exp) {
            this.printHelp(exp.getMessage(), opts);
            System.exit(1);
        } catch (SecurityException exp) {
            this.printHelp(exp.getMessage(), opts);
            System.exit(1);
        } catch (ConfigurationException exp) {
            this.printHelp(exp.getMessage(), opts);
            System.exit(1);
        }
    }

    /*
     * Creates a file in the output folder with the extractor name and puts csv and k=v into it
     */
    public PrintWriter createFile(String outFileName) throws IOException {
        File outFile = new File(this.outputDir, outFileName);
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
                outFile, false)));
        this.outputs.put(outFileName, out);
        return out;
    }

    /*
     * Closes all the files we opened
     */
    public void closeOutputFiles() {
        for (String keyValue : this.outputs.keySet()) {
            PrintWriter writer = this.outputs.get(keyValue);
            writer.flush();
            writer.close();
        }
        this.outputs.clear();
    }

    /*
     * Process output
     */
    public void processOutput(ExtractorResult result, String logLine)
            throws IOException {
        String outputFileName = null;
        Map<String, String> matches = null;
        PrintWriter out;

        try {
            // Output to directory
            if (this.outputDir != null) {
                // Create output directory, is possible
                this.outputDir.mkdirs();
                if (!this.outputDir.isDirectory() && !this.outputDir.canWrite()) {
                    this.printHelp(
                            "ERROR: Output Directory "
                                    + this.outputDir.getName()
                                    + " is not a directory or you don't have the permissions.",
                            buildOptions());
                    System.exit(1);
                }

                // If there is a result we set the filname using extractor name,
                // otherwise the record will go to 'noMatch' file and we create
                // a new HashMap with "message" containing the logLine
                if (result != null) {
                    outputFileName = result.getTags().get(
                            this.extractorNameString);
                    matches = result.getMatches();
                } else {
                    outputFileName = noMatchFileName;
                    matches = new HashMap<String, String>();
                    matches.put("message", logLine);
                }
            }
            // Output to a File
            else if (this.outputFile != null) {
                outputFileName = this.outputFile.getPath();
                // If there is a result we add results to the map, otherwise we
                // create a new HashMap with "message" containing the logLine
                if (result != null) {
                    matches = result.getMatches();
                } else {
                    matches = new HashMap<String, String>();
                    matches.put("message", logLine);
                }
            }

            //
            if (matches != null && !matches.isEmpty()) {
                // Creating a writer for the output file
                out = this.outputs.get(outputFileName);
                if (out == null) {
                    out = this.createFile(outputFileName);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                out.append(objectMapper.writeValueAsString(matches));
                out.println();
            }
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run(String[] args) throws Exception {
        buildOptions();
        this.parseCommandLine(args);
        int count = 0;
        int countLimit = 100;

        if (this.inputFile.toString().equals("STDIN")) {
            // READ FROM STDIN
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    System.in));
            String line;
            while ((line = br.readLine()) != null) {
                count++;
                if ((count % countLimit) == 0) {
                    System.out.println("Processing event #" + count);
                }
                ExtractorResult result = this.extractor.extract(line.trim());
                this.processOutput(result, line);
            }
            this.closeOutputFiles();
        } else {
            // READ FROM A FILE
            LineIterator it = FileUtils.lineIterator(this.inputFile, "UTF-8");
            try {
                while (it.hasNext()) {
                    count++;
                    if ((count % countLimit) == 0) {
                        System.out.println("Processing event #" + count);
                    }
                    String line = it.nextLine();
                    ExtractorResult result = this.extractor.extract(line.trim());
                    this.processOutput(result, line);
                }
            } finally {
                this.closeOutputFiles();
                LineIterator.closeQuietly(it);
            }
        }
        System.out.println("Processed " + count + " events.");

    }

    public static void main(String[] args) throws Exception {
        TextParser p = new TextParser();
        p.run(args);
    }

}
