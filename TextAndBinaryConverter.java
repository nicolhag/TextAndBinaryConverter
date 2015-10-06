import java.util.Arrays;
import java.util.Scanner;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

class TextAndBinaryConverter{
    public static void main(String[] args) {
        Handler handler = new Handler(args);
    }
}

class Handler{
    private Converter converter;

    public Handler(String[] args){
        converter = new Converter();
        printProgramDescription();
        handleArguments(args);
        printProgramExitMessage();
    }

    private void printProgramDescription(){
        System.out.print("\n\n\n\n\n\n\n\n************************************************************************************************************");
        System.out.println("\n\t WELCOME TO THE TEXT/BINARY-CONVERTER!\n");
        System.out.println("\tTo the program, you may give two different kinds of input:\n");
        System.out.println("\t1. A text to be converted into binary code\n");
        System.out.println("\t2. A binary code to be converted into pure text (separated by whitespaces)\n\n");
        System.out.println("\tThe input argument can therefore be the following:\n");
        System.out.println("\t1. A text or binary code to be converted - all arguments will be looked at as ONE string (n number of arguments)\n");
        System.out.println("\t2. A name of a text-file that contains text/binary code\n");
        System.out.println("\t2. NO arguments - which will open for browsing a file for input to the program\n\n");
        System.out.println("\tTLDR: \n");
        System.out.println("\tUsage: BinaryToTextConverter [text/binary code/txt-file] \n");

        System.out.println("************************************************************************************************************\n\n");
    }

    private void handleArguments(String[] args){
        if (argumentsGiven(args)){
            if (isTextFileName(args[0])){
                String fileContent = readFromFile(new File(args[0]));
                handleInputString(fileContent);
            } else {
                handleInputFromArguments(args);
            }
        } else {
            collectInputFromFile();
        }
    }

    private boolean argumentsGiven(String[] args){
        return (args.length != 0);
    }

    private void handleInputFromArguments(String[] input){
        String fullString = createFullStringOfArguments(input);
        handleInputString(fullString);
    }

    private void collectInputFromFile(){
        Scanner terminalInput = new Scanner(System.in);
        System.out.println("No arguments given. Do you want to browse for a text-file as input? (y/n)");
        char answer = terminalInput.nextLine().charAt(0);
        if (answer == 'y'){
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT-files", "txt");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                    String fileContent = readFromFile (chooser.getSelectedFile());
                    handleInputString(fileContent);
            }
        } else {
            System.out.println("Usage: BinaryToTextConverter [text/binary code/txt-file]");
        }
    }

    private void handleInputString(String input){
        if (isBinary(input)){
            System.out.println("Reading binary string...");
            String[] legalBinaryString = ensureFullBinaryStringLength(input.split(" "));
            String text = converter.convertFromBinaryToText(legalBinaryString);
            printNicelyWithMessage("The binary code in text is", text);
        } else {
            System.out.println("Reading pure text...");
            String binary = converter.convertFromTextToBinary(input);
            printNicelyWithMessage("The text-message in binary is", binary);
        }
    }

    private String readFromFile(File thisFile){
        Scanner scanner = null;
        try{
            scanner = new Scanner(thisFile);
        } catch (FileNotFoundException e){
            System.out.println("Did not find the file with the name: '" + thisFile.getName() +"'");
        }

        String toReturn = "";
        while (scanner.hasNextLine()){
            toReturn += scanner.nextLine();
        }
        return toReturn;
    }

    private void writeResultToFile(){
        PrintWriter writer = null;
        try{
            writer = new PrintWriter("result.txt");
            writer.println(converter.getResult());
            writer.close();
        } catch (Exception e){
            System.out.println("Error writing to file");
        }
    }

    private String[] ensureFullBinaryStringLength(String[] binaryStrings){
        final int FULL_BINARY_STRING_LENGTH = 8;

        for (int i = 0; i < binaryStrings.length; i++){
            int numberOfBinaryDigits = binaryStrings[i].length();
            String currentBinaryString = binaryStrings[i];

            if (numberOfBinaryDigits < FULL_BINARY_STRING_LENGTH){
                int zeroesToAdd = FULL_BINARY_STRING_LENGTH-numberOfBinaryDigits;
                binaryStrings[i] = addLeadingZerosTo(currentBinaryString, zeroesToAdd);
            }
        }
        return binaryStrings;
    }

    private String addLeadingZerosTo(String binaryString, int numberOfZeroes){
        String zerosToAdd = "";
        for (int j = 0; j < numberOfZeroes; j++){
            zerosToAdd += "0";
        }
        return (zerosToAdd + binaryString);
    }

    private String createFullStringOfArguments(String[] args){
        String toReturn = "";

        for (int i = 0; i < args.length; i++){
            toReturn += " " + args[i];
        }
        return toReturn;
    }

    private void printProgramExitMessage(){
        System.out.println("\n\nThank you for using the BinaryToTextConverter.");
        System.out.println("Would you like to print the result to the file \"result.txt\"? (y/n) \n");
        Scanner terminalInput = new Scanner(System.in);
        char answer = terminalInput.nextLine().charAt(0);
        if (answer == 'y'){
            writeResultToFile();
        }
    }

    private void printNicelyWithMessage(String message, String resultOfConversion){
        System.out.print("\n\n************************************************************************");
        System.out.println("\n " + message + ":\n");
        System.out.println("\t" + resultOfConversion + "\n");
        System.out.println("************************************************************************\n\n");
    }

    private boolean isBinary(String firstArgument){
        char firstChar = firstArgument.trim().charAt(0);
        return (firstChar == '0' || firstChar == '1');
    }

    private boolean isTextFileName(String potentialFileName){
        return potentialFileName.endsWith(".txt") || potentialFileName.endsWith(".text");
    }
}

class Converter{
    private String result;

    public String getResult(){
        return result;
    }
    private void setResult(String result){
        this.result = result;
    }

    public String convertFromTextToBinary(String text){
        char[] splitted = text.toCharArray();
        String message = "";
        for (int i = 0; i < splitted.length; i++){
            int asciiNumber = (int) splitted[i];
            for (int k = 7; k >= 0; k--){
                int sizeOfPlace = (int) Math.pow(2, k);
                if (sizeOfPlace > asciiNumber){
                    message += "0";
                } else {
                    message += "1";
                    asciiNumber -= sizeOfPlace;
                }
            }
            message += " ";
        }
        setResult(message);
        return message;
    }

    public String convertFromBinaryToText(String[] binary){
        String message = "";
        for (int i = 0; i < binary.length; i++){
            int asciiNumber = 0;
            int k = 0;
            for (int j = 7; j >= 0; j--){
                if (binary[i].charAt(j) == '1'){
                    asciiNumber += (int) Math.pow(2, k);
                }
                k++;
            }
            message += (char) asciiNumber;
        }
        setResult(message);
        return message;
    }
}
