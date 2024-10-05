import math
import operator as op


Symbol = str
Number = (int, float)

def add(*args):
    res = 0
    for i in args:
        res += i
    return res

def sub(*args):
    res = args[0]
    for i in args[1:]:
        res -= i
    return res

def mul(*args):
    res = 1
    for i in args:
        res *= i
    return res

def div(*args):
    res = args[0]
    for i in args[1:]:
        res //= i
    return res

def minimum(*args):
    # print("args, ", args)
    res = args[0]
    for i in args:
        res = min(res, i)
    return res

def maximum(*args):
    res = args[0]
    for i in args:
        res = max(res, i)
    return res

def do(proc, args):
    # print(proc)
    if(proc == add or proc == sub or proc == mul or proc == div or
    proc == minimum or proc == maximum):
        return proc(*args)
    else:
        res = []
        for i in args:
            res.append(proc(i))
        return res


def standard_env():
    env = dict()
    env.update(vars(math))  # sin, cos, sqrt, pi, ...
    env.update({
        '+': add, '-': sub,
        '*': mul, '/': div,
        '>': op.gt, '<': op.lt,
        '#t': True, '#f': False,
        '>=': op.ge, '<=': op.le, '=': op.eq, "!=": op.is_not,
        'abs':     abs,
        'max':     maximum,
        'min':     minimum,
        'expt':    pow,

        'length':  len,
        'car':     lambda x: x[0],
        'cdr':     lambda x: x[1:],
        'append':  op.add,
        'cons':    lambda x,y: [x] + y,
        'list':    lambda *x: list(x),
        'list?':   lambda x: isinstance(x, list),
        'apply':   lambda proc, args: proc(*args),
        'map':     lambda fn, ls: list(map(fn, ls)),

        'equal?':  op.eq,
        'zero?':    lambda x: x == 0,
        'not':     op.not_,
        'null?':   lambda x: x == [],
        'number?': lambda x: isinstance(x, Number),
        'symbol?': lambda x: isinstance(x, Symbol),

        "display" : print
    })
    return env