package io.github.stackphy;

import io.github.stackphy.export.CodePhyExporter;
import io.github.stackphy.parser.StackPhyException;
import io.github.stackphy.runtime.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Command-line utility to parse a StackPhy file and export it to CodePhy JSON.
 */
public class ExportCommand {
    
    /**
     * Entry point for the export command.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            printUsage();
            return;
        }
        
        String inputFilePath = args[0];
        String outputFilePath = args.length == 2 ? args[1] : inputFilePath.replaceFirst("\\.[^.]+$", "") + ".json";
        
        File inputFile = new File(inputFilePath);
        
        if (!inputFile.exists()) {
            System.err.println("Input file not found: " + inputFilePath);
            System.exit(1);
        }
        
        try {
            // Parse the StackPhy file
            StackPhyParser parser = new StackPhyParser();
            Environment environment = parser.parseFile(inputFile);
            
            // Export to CodePhy JSON
            CodePhyExporter exporter = new CodePhyExporter(environment);
            String json = exporter.exportToJson();
            
            // Write to output file
            try (PrintWriter out = new PrintWriter(new FileWriter(outputFilePath))) {
                out.print(json);
            }
            
            System.out.println("Model successfully exported to: " + outputFilePath);
            
        } catch (IOException e) {
            System.err.println("Error reading/writing file: " + e.getMessage());
            System.exit(1);
        } catch (StackPhyException e) {
            System.err.println("Error parsing model: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Prints usage information.
     */
    private static void printUsage() {
        System.out.println("Usage: stackphy-export <input.sp> [output.json]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  <input.sp>     StackPhy model file to export");
        System.out.println("  [output.json]  Output JSON file (defaults to input filename with .json extension)");
    }
}
