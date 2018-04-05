package ca.mcgill.ecse321.treeple;

public class InvalidInputException extends Exception {
    public InvalidInputException(String error) {
        super(error);
    }
}
