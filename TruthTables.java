import javax.swing.*;
import java.util.*;
public class TruthTables {
    public static void main(String[] args){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter expression:");
        Table table = new Table(keyboard.nextLine());
        table.buildTable();
    }
}

class Table{

    String original;
    String expression;
    Stack<String> variableList  = new Stack<String>();
    ArrayList<String> expressionsToEvaluate = new ArrayList<>();
    Stack<String> arguments = new Stack<String>();
    Stack<String> parentheses = new Stack<String>();
    Stack<String> complexParentheses = new Stack<String>();
    ArrayList<ArrayList<String> >tableTruthValues = new ArrayList<ArrayList<String> >();
    String input;
    int numVariables;
    int numRows;

    Table(String original) {
        this.original = original;
    }

    void buildTable() {
        searchForVariables();
        searchForParentheses();
        stackExpressions();
        for(int i = 0; i < expressionsToEvaluate.size(); i++) {
            input = expressionsToEvaluate.get(i);
            if(input.contains("(")) {
                expressionsToEvaluate.remove(original);
                expressionsToEvaluate.add(original.replace(expressionsToEvaluate.get(i), tableTruthValues.size() - 1 + ""));
            }
            orderOfOps();
        }
        printTable();
    }

    void orderOfOps() {
        String [] operators = new String [] {"~", "^", "V", "=>", "<=>"};
        String [] expression;
        int beginIndex;
        int endIndex;
        for(int i = 0; i < operators.length; i++) {
            while(input.contains(operators[i])) {
                expression = findExpression(operators[i]);
                if(operators[i].equals("~"))
                    beginIndex = input.indexOf(operators[i]);
                else
                    beginIndex = input.indexOf(operators[i]) - 1;
                endIndex = input.indexOf(operators[i]) + operators[i].length() + 1;
                String snip = input.substring(beginIndex, endIndex);
                input = input.replace(snip, indexAsString());
                System.out.println(input);

                switch (expression [1]) {
                    case "~" : tableTruthValues.add(notTruthValues(expression)); break;
                    case "^" : tableTruthValues.add(andTruthValues(expression)); break;
                    case "V" : tableTruthValues.add(orTruthValues(expression)); break;
                    case "=>" : tableTruthValues.add(implies(expression)); break;
                    case "<=>" : tableTruthValues.add(biconditional(expression)); break;
                    }
            }
        }
    }

    String [] findExpression(String operator) {
        int columnB = input.indexOf(operator) + operator.length();
        String rightArgument = input.charAt(columnB) + "";
        if(Character.isDigit(input.charAt(columnB))) {
            columnB = Character.getNumericValue(input.charAt(columnB));
            rightArgument = tableTruthValues.get(columnB).get(0);
        }
        if(operator.equals("~"))
            return new String [] {operator + rightArgument, operator, rightArgument};
        else {
            int columnA = input.indexOf(operator) - 1;
            String leftArgument = input.charAt(columnA) + "";
            if (Character.isDigit(input.charAt(columnA))) {
                columnA = Character.getNumericValue(input.charAt(columnA));
                leftArgument = tableTruthValues.get(columnA).get(0);
            }
            return new String[]{leftArgument + operator + rightArgument, operator, leftArgument, rightArgument};
        }
    }

    void searchForParentheses() {
        Stack<Integer> leftParentheses = new Stack<Integer>();
        int leftIndex;
        for(int index = 0; index < original.length(); index++)
            if(original.charAt(index) == '(')
                leftParentheses.push(index);
            else if(original.charAt(index) == ')') {
                leftIndex = leftParentheses.pop();
                String snip = original.substring(leftIndex, index + 1);
                if(!parentheses.contains(snip)) {
                    if (original.lastIndexOf('(') > leftIndex)
                        complexParentheses.push(snip);
                    else
                        parentheses.push(snip);
                }
            }
        System.out.println(parentheses);
        System.out.println(complexParentheses);
    }

    void stackExpressions() {
        expressionsToEvaluate.add(original);
        for(int index = 0; index < parentheses.size(); index++)
            expressionsToEvaluate.add(parentheses.pop());
    }

    void searchForVariables() {
        for(char letter = 'a'; letter <= 'z'; letter++)
            if (original.contains("" + letter))
                variableList.add("" + letter);
        System.out.println(variableList);
        numVariables = variableList.size();
        assignVariableTruthValues();
    }

    String indexAsString() {
        return tableTruthValues.size() + "";
    }

    void assignVariableTruthValues() {
        numRows = (int) Math.pow(2, numVariables);
        for(int varNum = 1; varNum <= numVariables; varNum++) {
            ArrayList<String> variableTruthValues = new ArrayList<String>();
            variableTruthValues.add(variableList.get(varNum - 1));
            int row = 0;
            do {
                for (int current = 0; current < (int) Math.pow(2, numVariables - varNum); current++, row++)
                    variableTruthValues.add("T");
                for (int current = 0; current < (int) Math.pow(2, numVariables - varNum); current++, row++)
                    variableTruthValues.add("F");
            } while(row < numRows);
            tableTruthValues.add(variableTruthValues);
        }

        for (int i = 0; i <= numRows; i++) {
            for (int j = 0; j < tableTruthValues.size(); j++) {
                System.out.print(tableTruthValues.get(j).get(i) + " ");
            }
            System.out.println();
        }
    }
    void printTable() {
        for (int i = 0; i <= numRows; i++) {
            for (int j = 0; j < tableTruthValues.size(); j++) {
                int spaces = tableTruthValues.get(j).get(0).length()/2;
                for(int k = 0; k < spaces && i > 0; k++)
                    System.out.print(" ");
                System.out.print(tableTruthValues.get(j).get(i) + " ");
                for(int k = 0; k < spaces && i > 0; k++)
                    System.out.print(" ");
            }
            System.out.println();

        }
    }

    void evaluate(String [] expression) {

    }
    ArrayList<String> andTruthValues(String [] expression) {
        int indexA = 0;
        int indexB = 0;
        ArrayList<String> values = new ArrayList<String>();
        values.add(expression [0]);
        for(int i = 0; i < tableTruthValues.size(); i++) {
            if (tableTruthValues.get(i).get(0).equals(expression[2]))
                indexA = i;
            if (tableTruthValues.get(i).get(0).equals(expression[3]))
                indexB = i;
        }
        for(int row = 1; row <= numRows; row++) {
            if(tableTruthValues.get(indexA).get(row).equals("T") && tableTruthValues.get(indexB).get(row).equals("T"))
                values.add("T");
            else
                values.add("F");
        }
        return values;
    }

    ArrayList<String> orTruthValues(String [] expression) {
        int indexA = 0;
        int indexB = 0;
        ArrayList<String> values = new ArrayList<String>();
        values.add(expression [0]);
        for(int i = 0; i < tableTruthValues.size(); i++) {
            if (tableTruthValues.get(i).get(0).equals(expression[2]))
                indexA = i;
            if (tableTruthValues.get(i).get(0).equals(expression[3]))
                indexB = i;
        }
        for(int row = 1; row <= numRows; row++) {
            if(tableTruthValues.get(indexA).get(row).equals("T") || tableTruthValues.get(indexB).get(row).equals("T"))
                values.add("T");
            else
                values.add("F");
        }
        return values;
    }

    ArrayList<String> implies(String [] expression) {
        int indexA = 0;
        int indexB = 0;
        ArrayList<String> values = new ArrayList<String>();
        values.add(expression [0]);
        for(int i = 0; i < numVariables; i++) {
            if (tableTruthValues.get(i).get(0).equals(expression [2]))
                indexA = i;
            if (tableTruthValues.get(i).get(0).equals(expression [3]))
                indexB = i;
        }
        for(int row = 1; row <= numRows; row++) {
            if(tableTruthValues.get(indexA).get(row).equals("T") && tableTruthValues.get(indexB).get(row).equals("F"))
                values.add("F");
            else
                values.add("T");
        }
        return values;
    }

    ArrayList<String> biconditional(String [] expression) {
        int indexA = 0;
        int indexB = 0;
        ArrayList<String> values = new ArrayList<String>();
        values.add(expression [0]);
        for(int i = 0; i < numVariables; i++) {
            if (tableTruthValues.get(i).get(0).equals(expression [2]))
                indexA = i;
            if (tableTruthValues.get(i).get(0).equals(expression [3]))
                indexB = i;
        }
        for(int row = 1; row <= numRows; row++) {
            if(tableTruthValues.get(indexA).get(row).equals(tableTruthValues.get(indexB).get(row)))
                values.add("T");
            else
                values.add("F");
        }
        return values;
    }

    ArrayList<String> notTruthValues(String [] expression) {
        ArrayList<String> values = new ArrayList<String>();
        values.add(expression [0]);
        for(int i = 0; i < tableTruthValues.size(); i++)
            if(tableTruthValues.get(i).get(0).equals(expression [2]))
                for(int row = 1; row <= numRows; row++)
                    if(tableTruthValues.get(i).get(row).equals("T"))
                        values.add("F");
                    else
                        values.add("T");
        return values;
    }

}

