import java.util.Scanner;

public class MiniCodeExplainer {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Welcome message
        System.out.println("========================================");
        System.out.println("   Welcome to Mini Code Explainer");
        System.out.println("========================================");
        System.out.println("Enter your code below.");
        System.out.println("Type 'END' on a new line to finish.\n");
        
        // Read multi-line code input
        StringBuilder codeInput = new StringBuilder();
        int lineCount = 0;
        String line;
        
        while (true) {
            line = scanner.nextLine();
            
            if (line.equals("END")) {
                break;
            }
            
            codeInput.append(line).append("\n");
            lineCount++;
        }
        
        // Check if any code was entered
        if (lineCount == 0) {
            System.out.println("\nNo code entered. Exiting.");
            scanner.close();
            return;
        }
        
        // Convert to string and convert to lowercase for analysis
        String code = codeInput.toString();
        String codeLower = code.toLowerCase();
        
        // Print explanation heading
        System.out.println("\n--- Explanation ---");
        
        // Track if any pattern was found
        boolean patternFound = false;
        
        // Check for conditions
        if (codeLower.contains("if")) {
            System.out.println("✓ This code uses a CONDITION");
            patternFound = true;
        }
        
        // Check for loops
        if (codeLower.contains("for") || codeLower.contains("while")) {
            System.out.println("✓ This code uses a LOOP");
            patternFound = true;
        }
        
        // Check for functions or variables
        if (codeLower.contains("int") || codeLower.contains("void") || codeLower.contains("string")) {
            System.out.println("✓ This code may contain FUNCTIONS or VARIABLES");
            patternFound = true;
        }
        
        // Check for switch statement (optional requirement)
        if (codeLower.contains("switch")) {
            System.out.println("✓ This code uses SWITCH case");
            patternFound = true;
        }
        
        // If no patterns found
        if (!patternFound) {
            System.out.println("→ No major logic structures detected");
        }
        
        // Print summary
        System.out.println("\n--- Summary ---");
        System.out.println("Total lines of code: " + lineCount);
        
        if (patternFound) {
            System.out.println("Status: Code contains multiple logic structures.");
        } else {
            System.out.println("Status: This appears to be simple or declarative code.");
        }
        
        System.out.println("\n========================================");
        System.out.println("           Analysis Complete");
        System.out.println("========================================");
        
        scanner.close();
    }
}
