#include <bits/stdc++.h>
using namespace std;

int counter = 0;

struct transition;

typedef struct node{
    int id = counter++;
    bool isStartNode = false;
    bool isEndNode = false;
    vector<transition*> transitions;
    node(bool isStart, bool isEnd){
        isStartNode = isStart;
        isEndNode = isEnd;
    }
}node;


typedef struct transition{
    node* from;
    node* to;
    char symbol;
    transition(node* from, node* to, char symbol){
        this->from = from;
        this->to = to;
        this->symbol = symbol;
    }

    bool operator<(const transition& other) const {
        return from->id < other.from->id;
    }

    bool operator>(const transition& other)const{
        return from->id > other.from->id;
    }
}transition;

typedef struct nfa{
    bool isEpsilonNfa = false;
    node* startNode;
    set<node*> endNodes;
}nfa;



nfa add_nfas(nfa nfa1, nfa nfa2){
    if(nfa1.isEpsilonNfa || nfa2.isEpsilonNfa){
        if(nfa1.isEpsilonNfa){
            return nfa2;
        }
        if(nfa2.isEpsilonNfa){
            return nfa1;
        }
    }

    node* secondStart = nfa2.startNode;

    vector<transition*> endTransitions = secondStart->transitions;
    set<node*> firstEnds = nfa1.endNodes;

    // cout << "start Id " << nfa1.startNode->id << endl;

    for(auto nd : firstEnds){
        for(auto t : endTransitions){
            if(!secondStart->isEndNode){
                nd->isEndNode = false;
            }
            // cout << "chaemata "<< nd->id << " " << t->to->id << " " << t->symbol << endl;
            transition* trans = new transition(nd, t->to, t->symbol);
            nd->transitions.push_back(trans);
        }
    }
    set<node*> secondEndNodes = nfa2.endNodes;

    if(!secondStart->isEndNode){
        nfa1.endNodes.clear();
    }

    for(auto nd : secondEndNodes){
        nfa1.endNodes.insert(nd);
    }

    if(nfa1.endNodes.count(secondStart)){
        // nfa1.endNodes.erase(secondStart);
    }

    return nfa1;
}

// void print_end_nodes(nfa cp){
//     cout<< "------------------" << endl;
//     for(auto a : cp.endNodes){
//         cout << a->id << endl;
//     }
//     cout << endl;
// }
nfa nfa_or(nfa nfa1, nfa nfa2){
    if(nfa1.isEpsilonNfa || nfa2.isEpsilonNfa){
        // cout << "shemodis " << endl;
        if(nfa1.isEpsilonNfa){
            nfa2.startNode->isEndNode = true;
            return nfa2;
        }
        if(nfa2.isEpsilonNfa){
            nfa1.startNode->isEndNode = true;
            return nfa1;
        }
    }


    node* secondStart = nfa2.startNode;
    vector<transition*> transitionsFromStartNode = secondStart->transitions;

    for(auto t : transitionsFromStartNode){
        transition* newTransition = new transition(nfa1.startNode, t->to, t->symbol);
        nfa1.startNode->transitions.push_back(newTransition);
    }


    if(secondStart->isEndNode){
        nfa1.startNode->isEndNode = true;
        nfa1.endNodes.insert(nfa1.startNode);
    }

    // print_end_nodes(nfa1);

    for(auto nd : nfa2.endNodes){
        nfa1.endNodes.insert(nd);
    }
    
    // print_end_nodes(nfa1);

    return nfa1;
}


nfa nfa_to_nfa_star(nfa nfacp){
    // cout << "fifqi gaaketa" << endl;
    if(nfacp.isEpsilonNfa){
        return nfacp;
    }

    vector<transition*> transitionsFromStartNode = nfacp.startNode->transitions;

    set<node*> endNodes = nfacp.endNodes;

    for(auto nd : endNodes){
        for(auto t : transitionsFromStartNode){
            transition* newTransition = new transition(nd, t->to, t->symbol);
            nd->transitions.push_back(newTransition);
        }
    }
    nfacp.endNodes.insert(nfacp.startNode);
    nfacp.startNode->isEndNode = true;
    return nfacp;
}



nfa symbol_accepting_nfa(char c){
    if(c == ' '){
        nfa res;
        node* nd = new node(true, true);
        nd->isEndNode = true;
        res.isEpsilonNfa = true;
        res.startNode = nd;
        return res;
    }

    node* startNode = new node(true, false);
    node* endNode = new node(false, true);

    transition* trans = new transition(startNode, endNode, c);

    startNode->transitions.push_back(trans);

    nfa res;
    res.startNode = startNode;
    res.endNodes.insert(endNode);

    return res;
}


bool is_symbol(char c){
    return (c >= 'a' && c <= 'z') or (c >= '0' && c <= '9') or (c == ' ');
}


string add_dots(string str){
    string res = "";

    for(int i = 0; i < str.size()-1; i++){
        if(is_symbol(str[i]) && is_symbol(str[i+1])){
            res += str[i];
            res += '.';
            continue;
        }

        if(is_symbol(str[i]) && (str[i+1] == '(')){
            res +=str[i];
            res += '.';
            continue;
        }
        
        if((str[i] == '*' || str[i] == ')') && (is_symbol(str[i+1]) || str[i+1] == '(')){
            res += str[i];
            res += '.';
            continue;
        }
        res += str[i];
    }
    res += str[str.size() - 1];

    return res;    
}

string regex_to_postfix(string str){
    str = add_dots(str);

    string post = "";
    stack<char> st;
    map<char, int> mp = {{'(', 1}, {'.', 3}, {'*', 4}, {'|', 2}};

    for(char c : str){
        if(is_symbol(c)){
            post += c;
        }else if(c == '('){
            st.push('(');
        }else if(c == ')'){

            while (st.size() != 0 && st.top() != '('){
                char front = st.top();
                st.pop();
                post += front;
            }
            st.pop();
        }else{
            while (st.size() != 0 && mp[st.top()] >= mp[c]){
                char front = st.top();
                st.pop();
                post += front;
            }
            st.push(c);
        }
    }

    while (st.size() != 0){
        post += st.top();
        st.pop();
    }
    
    return post;
}


nfa expression_to_nfa(string str){
    str = regex_to_postfix(str);

    stack<nfa> st;

    for(int i = 0; i < str.size(); i++){
        char c = str[i];
        
        if(c == '|'){
            nfa nfa1 = st.top();
            st.pop();
            nfa nfa2 = st.top();
            st.pop();

            nfa toPush = nfa_or(nfa2, nfa1);
            st.push(toPush);
        }else if(c == '*'){
            nfa front =st.top();
            st.pop();
            st.push(nfa_to_nfa_star(front));
        }else if(c == '.'){
            nfa nfa1 = st.top();
            st.pop();
            nfa nfa2 = st.top();
            st.pop();

            st.push(add_nfas(nfa2, nfa1));
        }else{
            nfa newNfa = symbol_accepting_nfa(c);
            st.push(newNfa);
        }
    }

    nfa result_nfa = st.top();

    return result_nfa;
}

string change_epsilons(string str){
    string res = "";

    for(int i = 0; i < str.size()-1; i++){
        if(str[i] == '(' && str[i+1] == ')'){
            res += ' ';
            i++;
            continue;
        }
        res += str[i];
    }
    if(str.size() >= 2 && str[str.size() - 2] != '(')res += str[str.size() - 1];
    return res;
}

void count_all(node* start, set<node*>& node_set, set<transition*>& transition_set){
    node_set.insert(start);
    for(auto a : start->transitions){
        if(!transition_set.count(a)){
            transition_set.insert(a);
            count_all(a->to, node_set, transition_set);
        }
    }
}

void print_my_way(nfa cp){
    set<node*> node_set;
    set<transition*> transition_set;

    count_all(cp.startNode, node_set, transition_set);

    cout << "startNode " << cp.startNode->id << endl;

    for(auto a : transition_set){
        cout << a->from->id << " " << a->to->id << " " << a->symbol << endl;
    }

}

int result_node_num;
int result_transition_num;
map<int, vector<pair<char, int>>> transition_map;
map<int, int> mp;

void scale_down(nfa cp){
    set<node*> node_set;
    set<transition*> transition_set;

    
    count_all(cp.startNode, node_set, transition_set);

    result_node_num = node_set.size();
    result_transition_num = transition_set.size();
    
    
    set<int> st;
    for(auto a : node_set){
        st.insert(a->id);
    }

    int id = 0;
    for(auto a : st){
        mp[a] = id++;
    }

    for(auto a : transition_set){
        pair<char, int> to_push = {a->symbol, mp[a->to->id]};
        transition_map[mp[a->from->id]].push_back(to_push);
    }
}

void print_properly(nfa cp){
    scale_down(cp);

    cout << result_node_num << " " << cp.endNodes.size() << " " << result_transition_num << endl;

    for(auto a : cp.endNodes){
        cout << mp[a->id] << " ";
    }
    cout << endl;

    for(int i = 0; i < result_node_num; i++){

        auto a = transition_map[i];
        cout << transition_map[i].size() << " ";

        for(auto b : transition_map[i]){
            cout << b.first << " " << b.second << " ";
        }
        cout << endl;
    }

}

int main(){
    string str; cin>>str;
    nfa res = expression_to_nfa(str);
    print_properly(res);
    // print_my_way(res);

    return 0;
}