from env import *
from process import *

global_env = standard_env()


class create_function(object):
    def __init__(self, parms, body, env):
        self.parms, self.body, self.env = parms, body, env

    def __call__(self, *args):
        for i in range(len(args)):
            self.env[self.parms[i]] = args[i]
        res = eval(self.body, self.env)
        return res


def eval(x, env):
    if isinstance(x, Symbol):
        res = env[x]
        return res

    elif isinstance(x, Number):
        return x

    operation, *args = x
    if operation == "quote":
        return args[0]

    elif operation == 'if':
        test_case = args[0]
        test_case_res = eval(test_case, env)
        if test_case_res:
            return eval(args[1], env)
        else:
            return eval(args[2], env)

    elif operation == 'and':
        for i in args:
            if not eval(i, env):
                return False
        return True

    elif operation == 'or':
        for i in args:
            if eval(i, env):
                return True

    elif operation == 'defun':
        variable, *value = args
        value = eval(*value, env)
        env[variable] = value

    elif operation == 'define':
        function_name, *function_args = args[0]
        function_body = args[1]
        env[function_name] = create_function(function_args, function_body, env)

    elif operation == 'lambda':
        (function_args, function_body) = args
        return create_function(function_args, function_body, env)

    else:
        cp = env.copy()
        function = eval(operation, cp)
        if function in cp: function = cp[function]
        arguments = list()
        for i in range(len(args)):
            arguments.append(eval(args[i], cp))

        res = function(*arguments)
        return res


def main():
    while True:
        try:
            s = input('list> ')
            if s == '-1': break
        except EOFError:
            break
        if not s:
            continue

        parsed = process(s)
        val = eval(parsed, global_env)
        if val != None: print(val)


main()
