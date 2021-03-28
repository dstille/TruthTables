import java.util.*;
public class TruthTables {
    public static void main(String[] args){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("This program builds a truth table for an entered logical expression containing" +
                "\nlower case letters for propositions and logical operators.");
        System.out.println("The operators are: " +
                "\n~ = not\n^ = and\nV = or\n=> = implies\n<-> = biconditional (iff)");
        System.out.println("Enter expression:");
        TablePlan tablePlan = new TablePlan(keyboard.nextLine());
        tablePlan.buildPlan();
    }
}

class TablePlan{

    String original;
    Table table;
    ArrayList<String> variableList  = new ArrayList<>();
    ArrayList<String> expressionsToEvaluate = new ArrayList<>();
    ArrayList<String> evaluatedExpressions = new ArrayList<>();
    Stack<String> parentheses = new Stack<>();
    Stack<String> complexParentheses = new Stack<>();
    int numVariables;
    int numRows;

    TablePlan(String original) {
        this.original = original;
    }

    void buildPlan() {
        table = new Table();
        searchForVariables();
        searchForParentheses();
        stackExpressions();
        for(int i = 0; i < expressionsToEvaluate.size(); i++)
            orderOfOps(expressionsToEvaluate.get(i));
        table.printTable();
    }

    void orderOfOps(String expression) {
        String [] operators = new String [] {"~", "^", "V", "=>", "<->"};
        int leftArg = 0;
        int rightArg = 0;
        String operator;
        String header;
        for(int i = 0; i < operators.length; i++) {
            operator = operators[i];
            for(int j = 0; j < expression.length() - operator.length() && expression.contains(operator); j++) {
                if(( expression.substring(j, j + operator.length()).equals(operator))) {
                    rightArg = findRightArgIndex(j + operator.length(), expression);
                    if (operator != "~") {
                        leftArg = findLeftArgIndex(j - 1, expression);
                        header = table.getTable(leftArg, 0) + operator + table.getTable(rightArg, 0);
                        if(expression.equals("(" + header + ")"))
                            header = expression;
                        if(!evaluatedExpressions.contains(header) && !evaluatedExpressions.contains("(" + header + ")")) {
                            table.column(leftArg, rightArg, header, operator);
                            evaluatedExpressions.add(header);
                        }
                    }
                    else {
                        header = operator + table.getTable(rightArg, 0);
                        if(expression.equals("(" + header + ")"))
                            header = expression;
                        if(!evaluatedExpressions.contains(header) && !evaluatedExpressions.contains("(" + header + ")")) {
                            table.column(rightArg, header);
                            evaluatedExpressions.add(header);
                        }
                    }
                }
            }
        }
        evaluatedExpressions.remove(evaluatedExpressions.size() - 1);
        evaluatedExpressions.add(expression);
    }

    int findRightArgIndex(int argIndex, String expression) {
        String snip;
        for(int i = expression.length() - 1; i >= argIndex; i--) {
            snip = expression.substring(argIndex, i + 1);
            if(evaluatedExpressions.contains(snip))
                return evaluatedExpressions.lastIndexOf(snip) + numVariables;
        }
        return variableList.indexOf(expression.charAt(argIndex) + "");
    }

    int findLeftArgIndex(int argIndex, String expression) {
        String snip;
        for(int i = 0; i <= argIndex; i++) {
            snip = expression.substring(i, argIndex + 1);
            if(evaluatedExpressions.contains(snip))
                return evaluatedExpressions.lastIndexOf(snip) + numVariables;
        }
        return variableList.indexOf(expression.charAt(argIndex) + "");
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
    }

    void stackExpressions() {
        for(int index = 0; index < parentheses.size() && !expressionsToEvaluate.contains(parentheses.get(index)); index++)
            expressionsToEvaluate.add(parentheses.get(index));
        for(int index = 0; index < complexParentheses.size() && !expressionsToEvaluate.contains(complexParentheses.get(index)); index++)
            expressionsToEvaluate.add(complexParentheses.get(index));
        if(!expressionsToEvaluate.contains(original))
            expressionsToEvaluate.add(original);
    }

    void searchForVariables() {
        for(char letter = 'a'; letter <= 'z'; letter++)
            if (original.contains("" + letter))
                variableList.add("" + letter);
        numVariables = variableList.size();
        table.assignVariableTruthValues(variableList);
    }

}

class Table{

    ArrayList<String> values;
    ArrayList<ArrayList<String>> truthTable = new ArrayList<ArrayList<String>>();
    ArrayList<String> variableList;
    int numRows;
    int numVariables;

    public String getTable(int truthColumn, int row) { return truthTable.get(truthColumn).get(row); }

    void assignVariableTruthValues(ArrayList<String> variableList) {
        this.variableList = variableList;
        numVariables = variableList.size();
        numRows = (int) Math.pow(2, numVariables);
        for(int varNum = 1; varNum <= numVariables; varNum++) {
            values = new ArrayList<>();
            values.add(variableList.get(varNum - 1));
            int row = 0;
            do {
                for (int current = 0; current < (int) Math.pow(2, numVariables - varNum); current++, row++)
                    values.add("T");
                for (int current = 0; current < (int) Math.pow(2, numVariables - varNum); current++, row++)
                    values.add("F");
            } while(row < numRows);
            truthTable.add(values);
        }
    }

    public void column(int rightArgIndex, String header) {
        values = new ArrayList<String>();
        values.add(header);
        for(int row = 1; row <= numRows; row++)
            if(getTable(rightArgIndex, row).equals("T"))
                values.add("F");
            else
                values.add("T");
        truthTable.add(values);
    }

    public void column(int leftArgIndex, int rightArgIndex, String header, String operator) {
        values = new ArrayList<>();
        String leftValue;
        String rightValue;
        values.add(header);
        for(int row = 1; row <= numRows; row++) {
            leftValue = getTable(leftArgIndex, row);
            rightValue = getTable(rightArgIndex, row);
            if(operator.equals("^"))
                if(leftValue.equals("T") && rightValue.equals("T"))
                    values.add("T");
                else
                    values.add("F");
            else if(operator.equals("V"))
                if(leftValue.equals("T") || rightValue.equals("T"))
                    values.add("T");
                else
                    values.add("F");
            else if(operator.equals("=>"))
                if(leftValue.equals("T") && rightValue.equals("F"))
                    values.add("F");
                else
                    values.add("T");
            else if(operator.equals("<->"))
                if(leftValue.equals(rightValue))
                    values.add("T");
                else
                    values.add("F");
        }
        truthTable.add(values);
    }

    void printTable() {
        for (int i = 0; i <= numRows; i++) {
            for (int j = 0; j < truthTable.size(); j++) {
                int spaces = getTable(j, 0).length() - getTable(j, i).length();
                for(int k = spaces/2; spaces >= k; spaces--)
                    System.out.print(" ");
                System.out.print(getTable(j, i));
                while(spaces-- >= 0)
                    System.out.print(" ");
                System.out.print(" ");
            }
            System.out.println();
        }
    }

}