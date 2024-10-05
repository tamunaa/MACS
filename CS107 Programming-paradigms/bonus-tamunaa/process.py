def __tokenize(s):
    s = s.replace('(', ' ( ')
    s = s.replace(')', ' ) ')
    s = s.split()
    return s

def __change_list_form(s :list):
    l = 0
    while l < len(s):
        if(s[l] != "'"):
            l = l+1
            continue
        ind = l
        i = ind+1
        counter = 0
        while True:
            if(s[i] == '('):
                counter += 1
            elif(s[i] == ')'):
                counter -= 1
            i += 1
            if(counter == 0):break

        s[ind] = 'quote'
        s.insert(ind, '(')
        s.insert(i, ')')
    return s

def __parse_rev(tokens):
    token = tokens.pop()
    if token == '(':
        lst = []
        while tokens[-1] != ')':
            lst.append(__parse_rev(tokens))
        tokens.pop()
        return lst
    else:
        try:
            return int(token)
        except ValueError:
            try:
                return float(token)
            except ValueError:
                return token


def process(s):
    s = __tokenize(s)

    s = __change_list_form(s)

    # print("tekenized", s)
    s.reverse()
    return __parse_rev(s)
