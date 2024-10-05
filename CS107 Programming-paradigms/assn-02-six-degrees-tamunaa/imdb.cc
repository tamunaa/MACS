using namespace std;
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <unistd.h>
#include "imdb.h"
#include <string.h>
#include <iostream>

const char *const imdb::kActorFileName = "actordata";
const char *const imdb::kMovieFileName = "moviedata";

imdb::imdb(const string& directory){
  const string actorFileName = directory + "/" + kActorFileName;
  const string movieFileName = directory + "/" + kMovieFileName;
  
  actorFile = acquireFileMap(actorFileName, actorInfo);
  movieFile = acquireFileMap(movieFileName, movieInfo);
}

bool imdb::good() const{
  return !( (actorInfo.fd == -1) || 
	    (movieInfo.fd == -1) ); 
}

struct Node{
  char* st;
  char* p;
};


int cmp_function(const void* a_struct, const void * b){
  char* start_ptr = ((Node*)a_struct)->st;

  char* player1 = ((Node*)a_struct)->p;
  char* player2 = start_ptr + (*(int*)b);

  return strcmp(player1, player2);
}

string ptr_to_string(char* ptr){
  string res;
  while(*ptr != '\0'){
    res += (*ptr);
    ptr++;
  }
  return res;
}

bool imdb::getCredits(const string& player, vector<film>& films) const { 
  void* start = (int*)actorFile + 1;
  int num_of_actors = *((int*)actorFile);
  int elemSize = sizeof(int);

  Node nd;
  nd.st = (char*)actorFile;
  nd.p = (char*)player.c_str(); 

  int* actor_dis = (int*)bsearch(&nd, start, num_of_actors, elemSize, cmp_function);

  if (actor_dis == NULL) return false;

  char* cur_ptr = (char*)actorFile + *(int*)actor_dis;

  int input_len = 0;
  input_len += (player.size()+1);
  if(player.size() % 2 == 0)input_len++;

  short movieCount = *(short*)(cur_ptr + input_len); 
  input_len += sizeof(short);
  
  if (input_len % 4 != 0) input_len += 2;
  
  int *movie_ptr = (int*)(cur_ptr + input_len);

  for (short i = 0; i < movieCount; i++) {
    string name = (char*)movieFile + *movie_ptr;
    char* year_address = (char*)movieFile + *(movie_ptr) + name.size();
    year_address++;
    short year = 1900 + *(year_address);
    film f; f.title = name; f.year = year;
    films.push_back(f);
    movie_ptr++;
  }
  return true; 
}


struct rame{
  film f;
  char* st;
};


int cmp_function_num2(const void* a_struct, const void * b){
  char* start = ((rame*)a_struct) -> st;
  film f = ((rame*)a_struct)->f;
  int sec_id = *(int*)b;
  char* sec_add = start + sec_id;
  string movie_name = ptr_to_string(sec_add);
  while (*sec_add != '\0')sec_add++;
  while (*sec_add == '\0')sec_add++;
  short y = *(sec_add) + 1900;
  film fl; fl.title = movie_name; fl.year = y;
  if(f == fl)return 0;
  if(f < fl)return -1;
  return 1;
}


bool imdb::getCast(const film& movie, vector<string>& players) const {
  void* start = (int*)movieFile + 1;
  int num_of_movies = *((int*)movieFile);
  int elemSize = sizeof(int);  
  rame nd; nd.f = movie; nd.st = (char*)movieFile;

  void* dis = bsearch(&nd, start, num_of_movies, elemSize, cmp_function_num2);
  if (dis == NULL) return false;

  char* ptr = (char*)movieFile + *(int*)dis;
  string str = ptr_to_string(ptr);

  int info_len = 0;
  info_len += (str.size()+2);
  if(str.size() % 2 == 1)info_len++;
  ptr += info_len;

  short actor_num = *(short*)(ptr); 
  
  info_len += 2; ptr += 2;
  if (info_len % 4 != 0) {
    info_len += 2;
    ptr += 2;  
  }
  
  int* vinme = (int*)ptr;
  for (short i = 0; i < actor_num; ++i) {
    char* address = (char*)actorFile + *vinme;
    players.push_back(ptr_to_string(address));
    vinme++;
  }
  return true;
}

imdb::~imdb(){
  releaseFileMap(actorInfo);
  releaseFileMap(movieInfo);
}

// ignore everything below... it's all UNIXy stuff in place to make a file look like
// an array of bytes in RAM.. 
const void *imdb::acquireFileMap(const string& fileName, struct fileInfo& info){
  struct stat stats;
  stat(fileName.c_str(), &stats);
  info.fileSize = stats.st_size;
  info.fd = open(fileName.c_str(), O_RDONLY);
  return info.fileMap = mmap(0, info.fileSize, PROT_READ, MAP_SHARED, info.fd, 0);
}

void imdb::releaseFileMap(struct fileInfo& info){
  if (info.fileMap != NULL) munmap((char *) info.fileMap, info.fileSize);
  if (info.fd != -1) close(info.fd);
}
