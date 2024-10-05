#include <vector>
#include <list>
#include <set>
#include <string>
#include <iostream>
#include <iomanip>
#include "imdb.h"
#include "path.h"
#include <queue>
using namespace std;

/**
 * Using the specified prompt, requests that the user supply
 * the name of an actor or actress.  The code returns
 * once the user has supplied a name for which some record within
 * the referenced imdb existsif (or if the user just hits return,
 * which is a signal that the empty string should just be returned.)
 *
 * @param prompt the text that should be used for the meaningful
 *               part of the user prompt.
 * @param db a reference to the imdb which can be used to confirm
 *           that a user's response is a legitimate one.
 * @return the name of the user-supplied actor or actress, or the
 *         empty string.
 */

static string promptForActor(const string& prompt, const imdb& db)
{
  string response;
  while (true) {
    cout << prompt << " [or <enter> to quit]: ";
    getline(cin, response);
    if (response == "") return "";
    vector<film> credits;
    if (db.getCredits(response, credits)) return response;
    cout << "We couldn't find \"" << response << "\" in the movie database. "
	 << "Please try again." << endl;
  }
}

/**
 * Serves as the main entry point for the six-degrees executable.
 * There are no parameters to speak of.
 *
 * @param argc the number of tokens passed to the command line to
 *             invoke this executable.  It's completely ignored
 *             here, because we don't expect any arguments.
 * @param argv the C strings making up the full command line.
 *             We expect argv[0] to be logically equivalent to
 *             "six-degrees" (or whatever absolute path was used to
 *             invoke the program), but otherwise these are ignored
 *             as well.
 * @return 0 if the program ends normally, and undefined otherwise.
 */

void rame(string& source, string& target, const imdb& db){
  set<string> seen_actors;
  set<film> seen_films;

  queue<path> qu;
  path partial(source);
  qu.push(partial);
  
  while(qu.size() != 0 && qu.front().getLength() <= 5){
    path cur = qu.front(); qu.pop();
    string last_actor = cur.getLastPlayer();
    vector<film> movies;
    db.getCredits(last_actor, movies);
    
    for(film f : movies){

      if(seen_films.find(f) == seen_films.end()){
        seen_films.insert(f);
        vector<string> cast;
        db.getCast(f, cast);

        for(string p : cast){
          if(seen_actors.find(p) == seen_actors.end()){
            path to_add = cur;
            to_add.addConnection(f, p);
            if(p == target){
              cout << to_add << endl;
              return;
            }
            qu.push(to_add);
            seen_actors.insert(p);
          }
        }
      }
    }
  }
  cout << "No path between those two people could be found." << endl;
}




int main(int argc, const char *argv[]){
  imdb db(determinePathToData(argv[1])); // inlined in imdb-utils.h
  if (!db.good()) {
    cout << "Failed to properly initialize the imdb database." << endl;
    cout << "Please check to make sure the source files exist and that you have permission to read them." << endl;
    return 1;
  }
  
  while (true) {
    string source = promptForActor("Actor or actress", db);
    if (source == "") break;
    string target = promptForActor("Another actor or actress", db);
    if (target == "") break;
    if (source == target) {
      cout << "Good one.  This is only interesting if you specify two different people." << endl;
    } else {
      // replace the following line by a call to your generateShortestPath routine... 
      //cout << endl << "No path between those two people could be found." << endl << endl;

      rame(source, target, db);
    }
  }
  
  cout << "Thanks for playing!" << endl;
  return 0;
}

