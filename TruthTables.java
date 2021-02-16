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
    ArrayList<String> variableList  = new ArrayList<>();
    ArrayList<String> expressionsToEvaluate = new ArrayList<>();
    ArrayList<String> evaluatedExpressions = new ArrayList<>();
    Stack<String> parentheses = new Stack<>();
    Stack<String> complexParentheses = new Stack<>();
    ArrayList<ArrayList<String> >tableTruthValues = new ArrayList<>();
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
            orderOfOps(input);
        }
        printTable();
    }

    void orderOfOps(String expression) {
        String [] operators = new String [] {"~", "^", "V", "=>", "<->"};
        int leftArg = 0;
        int rightArg = 0;
        String operator;
        System.out.println("evaluating exp: " + expression);
        for(int i = 0; i < operators.length; i++) {
            System.out.println("Evaluating for op: " + operators[i]);
            operator = operators[i];
            for(int j = 0; j < expression.length() - operator.length() && expression.contains(operator); j++) {
                if(( expression.substring(j, j + operator.length()).equals(operator))) {
                    rightArg = findRightArgIndex(j + operator.length());
                    if (operator != "~")
                        leftArg = findLeftArgIndex(j - 1);
                    switch (operator) {
                        case "~":
                            tableTruthValues.add(notTruthValues(rightArg));
                            break;
                        case "^":
                            tableTruthValues.add(andTruthValues(leftArg, rightArg));
                            break;
                        case "V":
                            tableTruthValues.add(orTruthValues(leftArg, rightArg));
                            break;
                        case "=>":
                            tableTruthValues.add(implies(leftArg, rightArg));
                            break;
                        case "<->":
                            tableTruthValues.add(biconditional(leftArg, rightArg));
                            break;
                    }
                    evaluatedExpressions.add(tableTruthValues.get(tableTruthValues.size() - 1).get(0));
                }
            }
        }
        evaluatedExpressions.remove(evaluatedExpressions.size() - 1);
        evaluatedExpressions.add(expression);
        System.out.println(evaluatedExpressions);
    }

    int findRightArgIndex(int argIndex) {
        String snip;
        System.out.println(argIndex);
        for(int i = input.length() - 1; i >= argIndex; i--) {
            snip = input.substring(argIndex, i + 1);
            System.out.println(snip);
            if(evaluatedExpressions.contains(snip))
                return evaluatedExpressions.lastIndexOf(snip) + numVariables;
        }
        return variableList.indexOf(input.charAt(argIndex) + "");
    }

    int findLeftArgIndex(int argIndex) {
        String snip;
        System.out.println(argIndex);
        for(int i = 0; i <= argIndex; i++) {
            snip = input.substring(i, argIndex + 1);
            System.out.println(snip);
            if(evaluatedExpressions.contains(snip))
                return evaluatedExpressions.lastIndexOf(snip) + numVariables;
        }
        return variableList.indexOf(input.charAt(argIndex) + "");
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
                    if (original.lastIndexOf('(') > leftIndex && original.lastIndexOf(')') == index)
                        complexParentheses.push(snip);
                    else
                        parentheses.push(snip);
                }
            }
        System.out.println(parentheses);
        System.out.println(complexParentheses);
    }

    void stackExpressions() {
        for(int index = 0; index < parentheses.size(); index++)
            expressionsToEvaluate.add(parentheses.get(index));
        expressionsToEvaluate.add(original);
        System.out.println("exps to eval: " + expressionsToEvaluate);
    }

    void searchForVariables() {
        for(char letter = 'a'; letter <= 'z'; letter++)
            if (original.contains("" + letter))
                variableList.add("" + letter);
        System.out.println(variableList);
        numVariables = variableList.size();
        assignVariableTruthValues();
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
    }

    void printTable() {
        for(int j = 0; j < tableTruthValues.size(); j++)
            System.out.print(tableTruthValues.get(j).get(0) + " ");
        System.out.println();
        for (int i = 1; i <= numRows; i++) {
            for (int j = 0; j < tableTruthValues.size(); j++) {
                int spaces = tableTruthValues.get(j).get(0).length() + 1;
                for(int k = 1; k < spaces/2; k++, spaces--)
                    System.out.print(" ");
                System.out.print(tableTruthValues.get(j).get(i));
                spaces--;
                for(int k = 0; k < spaces; k++, spaces--)
                    System.out.print(" ");
                if(j >= numVariables)
                    System.out.print(" ");
            }
            System.out.println();

        }
    }

    ArrayList<String> andTruthValues(int leftArg, int rightArg) {
        String expression = tableTruthValues.get(leftArg). get(0) + "^" + tableTruthValues.get(rightArg).get(0);
        ArrayList<String> values = new ArrayList<String>();
        values.add(expression);
        for(int row = 1; row <= numRows; row++) {
            if(tableTruthValues.get(leftArg).get(row).equals("T") && tableTruthValues.get(rightArg).get(row).equals("T"))
                values.add("T");
            else
                values.add("F");
        }
        return values;
    }

    ArrayList<String> orTruthValues(int leftArg, int rightArg) {
        String expression = tableTruthValues.get(leftArg). get(0) + "V" + tableTruthValues.get(rightArg).get(0);
        ArrayList<String> values = new ArrayList<String>();
        values.add(expression);
        for(int row = 1; row <= numRows; row++) {
            if(tableTruthValues.get(leftArg).get(row).equals("T") || tableTruthValues.get(rightArg).get(row).equals("T"))
                values.add("T");
            else
                values.add("F");
        }
        return values;
    }

    ArrayList<String> implies(int leftArg, int rightArg) {
        ArrayList<String> values = new ArrayList<String>();
        String expression = tableTruthValues.get(leftArg).get(0) + "=>" + tableTruthValues.get(rightArg).get(0);
        values.add(expression);
        for(int row = 1; row <= numRows; row++) {
            if(tableTruthValues.get(leftArg).get(row).equals("T") && tableTruthValues.get(rightArg).get(row).equals("F"))
                values.add("F");
            else
                values.add("T");
        }
        return values;
    }

    ArrayList<String> biconditional(int leftArg, int rightArg) {
        ArrayList<String> values = new ArrayList<String>();
        String expression = tableTruthValues.get(leftArg).get(0) + "<->" + tableTruthValues.get(rightArg).get(0);
        values.add(expression);
        for(int row = 1; row <= numRows; row++) {
            if(tableTruthValues.get(leftArg).get(row).equals(tableTruthValues.get(rightArg).get(row)))
                values.add("T");
            else
                values.add("F");
        }
        return values;
    }

    ArrayList<String> notTruthValues(int rightArg) {
        ArrayList<String> values = new ArrayList<String>();
        String expression = "~" + tableTruthValues.get(rightArg).get(0);
        values.add(expression);
        for(int row = 1; row <= numRows; row++)
            if(tableTruthValues.get(rightArg).get(row).equals("T"))
                values.add("F");
            else
                values.add("T");
        return values;
    }

}

