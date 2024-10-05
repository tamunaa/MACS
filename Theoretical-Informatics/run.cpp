#include <bits/stdc++.h>
using namespace std;

map<int, map<char, vector<int>>> mp;
map<int, map<char, bool>> can_accept;
map<int, bool> accepting;


void read(){
    int n, a, t;
    cin>>n>>a>>t;

    for(int i = 0; i < a; i++){
        int id; cin>>id;
        // cout << "id " << id << endl;
        accepting[id] = true;
    }

    for(int i = 0; i < n; i++){
        int num; cin>>num;
        
        for(int j = 0; j < num; j++){
            char c; 
            int to;
            cin>>c;
            cin>>to;
            
            mp[i][c].push_back(to);
            can_accept[i][c] = can_accept[i][c] || accepting[to];
            // cout << "can_accept[i][c] " << i << " " << c << " " << can_accept[i][c] << endl<<endl;  
        }
    }
}

void calc(){
    string str; cin>>str;
    read();
    // cout << "daamtavra kitxva" << endl;

    string ans(str.size(), 'N');

    vector<int> cur_states;
    cur_states.push_back(0);

    for(int i = 0; i < str.size(); i++){
        char c = str[i];

        for(auto a : cur_states){
            if(can_accept[a][c]){
                ans[i] = 'Y';
            }
        }

        vector<int> nxt_states;

        for(auto a : cur_states){
            vector<int> nxt = mp[a][c];

            for(auto b : nxt){
                nxt_states.push_back(b);
            }
        }

        cur_states = nxt_states;
    }

    cout << ans << endl;
}

int main(){
    calc();

    return 0;
}