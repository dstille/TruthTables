table = {}

def create_exps(str_in):
    global exps_to_eval
    exps_to_eval = find_parentheses(original) + [original]
    create_table(find_vars(original))

def find_parentheses(str_in):
    l_left_index = []
    l_simple_parentheses = []
    l_complex_parentheses = []
    for i in range(len(str_in)):
        if str_in[i] == "(": l_left_index += [i]
        if str_in[i] == ")":
            if "(" in str_in[l_left_index[-1] + 1:]:
                l_complex_parentheses += [str_in[l_left_index.pop(-1): i + 1]]
            else:
                l_simple_parentheses += [str_in[l_left_index.pop(-1): i + 1]]
    return l_simple_parentheses + l_complex_parentheses

def find_vars(str_in):
    return [i for i in "abcdefghijklmnopqrstuvwyz" if i in str_in]

def create_table(lst_in):
    num_var = len(lst_in)
    num_rows = 2**num_var
    for var_num in range(num_var):
        column = []
        row_num = 0
        while(row_num < num_rows):
            for row in range(2**(num_var - var_num - 1)):
                column += [True]
                row_num += 1
            for row in range(2**(num_var - var_num - 1)):
                column += [False]
                row_num += 1
        table[lst_in[var_num]] = column

def find_left_arg(op_index, str_in):
    for i in range(0, op_index):
        if str_in[i: op_index] in table.keys():
            return str_in[i: op_index]

def find_right_arg(op_index, str_in):
    for i in range(len(str_in), op_index, - 1):
        if str_in[op_index + 1: i] in table.keys():
            return str_in[op_index + 1: i]

def evaluate(col_A, col_B, op, str_in):
    str_in = "(" + str_in + ")"
    table[str_in] = []
    for (a, b) in zip(table[col_A], table[col_B]):
         if op == "&":
             table[str_in] += [a and b]
         elif op == "V":
             table[str_in] += [a or b]
         elif op == "<->":
             table[str_in] += [a == b]
         elif op == "=>":
             table[str_in] += [False if a and not b else True]


def eval_not(col, str_in):
    table[str_in] = [not i for i in table[col]]

def print_table():
   for column in table:
       print(f"{column}", end = " ")
   print()
   for column in table:
       for i in range(len(column)): print("-", end = "")
       print("-", end = "")
   print()
   for row in range(len(table["p"])):
       for column in table:
           spaces = len(column) - 1
           for i in range(int(spaces/2)):
               print(" ", end = "")
               spaces -= 1
           print("T" if table[column][row] else "F", end = " ")
           while spaces > 0:
               print(" ", end = "")
               spaces -= 1
       print()



original = input("Enter expression: ")
create_exps(original)
operators = ["~", "&", "V", "=>", "<->"]
right_arg = ""
left_arg = ""
for exp in exps_to_eval:
    for op in operators:
        for i in range(len(exp)):
            if exp[i: i + len(op)] == op:
                right_arg = find_right_arg(i + len(op) - 1, exp)
                if op != "~":
                    left_arg = find_left_arg(i, exp)
                    evaluate(left_arg, right_arg, op, left_arg + op + right_arg)
                else:
                     eval_not(right_arg, op + right_arg)
print(table)
print_table()