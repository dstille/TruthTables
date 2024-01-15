import re, counts

d = {}

def find_parentheses(s: str):
    lft_idxs, sums, simple_par, complex_par = [], [], [], []
    for idx, char in enumerate(s):
        if char == "(": lft_idxs += [idx]
        if char == ")":
            substr = s[lft_idxs.pop(): idx + 1]
            if substr[1:4] == 'SUM': sums += [substr] 
            elif "(" in substr[1:]: complex_par += [substr]   
            else: simple_par += [substr]
    return sums + simple_par + complex_par

def find_left_arg(s):
    for i in range(len(s)):
        substr = s[i:]
        if substr in d:
            return substr
    return s        
    
def find_right_arg(s):
    for i in range(len(s), -1, -1):
        substr = s[:i]
        if substr in d: 
            return substr
    return s    

def find_3fields(s):
    print(s)
    return re.split(r"[,E\)]", s)[1:4]

def evaluate(op, args):
     if op == "SUM":
         begin, end, term = args
         return f"SUM,{begin},{end},{term}", summation(begin, end, term)
     lftarg, rghtarg = args   
     if op == "^": return lftarg + op + rghtarg, d[lftarg]**d[rghtarg]
     if op == "*": return lftarg + op + rghtarg, d[lftarg]*d[rghtarg]
     if op == "/": return lftarg + op + rghtarg, d[lftarg]/d[rghtarg]
     if op == "+": return lftarg + op + rghtarg, d[lftarg]+d[rghtarg]
     if op == "-": return lftarg + op + rghtarg, d[lftarg]-d[rghtarg]
     if op == "=": return lftarg, d[rghtarg]

def find_nums(s):
    nums = re.findall('\d+', s)
    if nums:
        for num in nums:
            d[num] = int(num)

def get_expressions(s):
    find_nums(s) 
    return find_parentheses(s) + [s]           

def parse(s):
    evaluated = s
    for exp in get_expressions(s):
        operators = find_ops(exp)
        for op, sides in operators.items():
            for left, right in sides:
                if op == "SUM":
                    args = find_3fields(right)
                    print(args)  
                else:    
                    args = [find_left_arg(left)] + [find_right_arg(right)]
                evaluated, value = evaluate(op, args) 
                if f'({evaluated})'== exp: evaluated = f'({evaluated})'
                d[evaluated] = value
    return d[evaluated], evaluated

def find_ops(s):
    d = {}
    for i in range(len(s)):
        for op in ['SUM', '^', '*', '/', '+', '-', '=']:
            substr = s[i: i + len(op)]
            if substr == op:
                sides = (s[:i], s[i + len(op):])
                d[op] = d[op] + [sides] if op in d else [sides]           
    return d

def get_subs(s):
    mtch = re.search(r'(.*)\[(.*)\]', s) 
    ky = mtch.group(1)
    idx = mtch.group(2)
    print(ky, idx)
    return d[ky[d[idx]]]


def summation(orig, end, term):
    start, curr = parse(orig)
    sum = 0
    for idx in range(start, d[end] + 1):
        d[curr] = idx
        sum += parse(term)[0]  
    return 
    
def summation_E(var, coll_name, term):
    collection = d[coll_name]
    d[var] = coll_name
    sum = 0
    for elem in collection:
        print('elem:', elem)
        d['i'] = elem
        print(d)
        sum += get_subs(term)
        print(sum)
    return sum    



def resolve(s):
    parse(s)
    return d[s]    



if __name__ == "__main__":
    wc = counts.get_dict()
    d['W'] = wc
    exp = '(SUM,i=1,10,2*i)'
    exp1 = '(SUM,wEW,w[i])'
    fields = find_3fields(exp1)
    print(fields)
    sum = summation_E(*fields)
    print(sum)
    
    


