#include <assert.h>
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <curl/curl.h>

#include "bool.h"
#include "html-utils.h"
#include "streamtokenizer.h"
#include "url.h"

#include "hashset.h"

static void Welcome(const char *welcomeTextFileName);
static void BuildIndices(const char *feedsFileName, hashset* stop_words, hashset* data, hashset* seen);
static void ProcessFeed(const char *remoteDocumentName, hashset* stop_words, hashset* data, hashset* seen);
static void PullAllNewsItems(FILE *dataStream, hashset* stop_words, hashset* data, hashset* seen);
static bool GetNextItemTag(streamtokenizer *st);
static void ProcessSingleNewsItem(streamtokenizer *st, hashset* stop_words, hashset* data, hashset* seen);
static void ExtractElement(streamtokenizer *st, const char *htmlTag,
                           char dataBuffer[], int bufferLength);
static void ParseArticle(const char *articleTitle,
                         const char *articleDescription,
                         const char *articleURL, 
                         hashset* stop_words, hashset* data, hashset* seen);
static void ScanArticle(streamtokenizer *st, const char *articleTitle,
                        const char *unused, const char *articleURL,
                        hashset* stop_words, hashset* data, hashset* seen);
static void QueryIndices(hashset* stop_words, hashset* data);
static void ProcessResponse(const char *word,  hashset* stop_words, hashset* data);
static bool WordIsWellFormed(const char *word);

static const char *const kWelcomeTextFile = "data/welcome.txt";
static const char *const kDefaultFeedsFile = "data/test.txt";
static const char *const kFilePrefix = "file://";
static const char *const kTextDelimiters =
    " \t\n\r\b!@$%^*()_+={[}]|\\'\":;/?.>,<~`";
static const char *const kNewLineDelimiters = "\r\n";

static const signed long kHashMultiplier = -1664117991L;
static const int num_buckets = 1717;


static const char* file_address = "data/stop-words.txt";

void fill_stop_words(hashset* stop_words){
  FILE* infile;
  streamtokenizer tk;
  char buffer[1024];

  infile = fopen(file_address, "r");
  STNew(&tk, infile, kNewLineDelimiters, true);

  while (STNextToken(&tk, buffer, sizeof(buffer))){
    char* str = strdup(buffer);
    HashSetEnter(stop_words, &str);
  }
  
  STDispose(&tk);
  fclose(infile);
}

typedef struct{
  char* title;
  char* url;
  int word_counter;
}nd;

typedef struct{
  char* word;
  vector* nds;
}node;

static int strHashFn(const void *address, int numBuckets){            
  int i;
  unsigned long hashcode = 0;
  const char *s = *(char **) address;
  for (i = 0; i < strlen(s); i++)  
    hashcode = hashcode * kHashMultiplier + tolower(s[i]);  
  
  return hashcode % numBuckets;                                
}

static int strcmpFn(const void *str1, const void *str2) {
  return strcasecmp(*(char**) str1, *(char**) str2);
}

static void freestrFn(void *address) {
  free(*(char**) address);
}

static int nodeHashFn(const void *address, int numBuckets){   
  char* str = ((node*)address)->word;
  return strHashFn(&str, num_buckets);                             
}

static int nodecmpFn(const void* node1, const void* node2){
  char* w1 = ((node*)node1)->word;
  char* w2 = ((node*)node2)->word;

  return strcasecmp(w1, w2);
}

static void nodeFreeFn(void* node1){
  node* ptr = ((node*)node1);
  free(ptr->word);
  VectorDispose(ptr->nds);
  free(ptr->nds);
}

static void ndFreeFn(void* nd1){
  nd* ptr = (nd*)nd1;
  free(ptr ->title);
  free(ptr ->url);
}
static int ndstFn(const void* nd1, const void* nd2){
  nd* ptr1 = (nd*)nd1;
  nd* ptr2 = (nd*)nd2;

  return (ptr2->word_counter) - (ptr1->word_counter);
}

int main(int argc, char **argv) {
  setbuf(stdout, NULL);
  curl_global_init(CURL_GLOBAL_DEFAULT);
  Welcome(kWelcomeTextFile);

  hashset stop_words;
  HashSetNew(&stop_words, sizeof(char*), num_buckets, strHashFn, strcmpFn, freestrFn);
  fill_stop_words(&stop_words);
 
  hashset data;
  HashSetNew(&data, sizeof(node), num_buckets, nodeHashFn, nodecmpFn, nodeFreeFn);

  hashset seen;
  HashSetNew(&seen, sizeof(char *), num_buckets, strHashFn, strcmpFn, freestrFn);


  BuildIndices((argc == 1) ? kDefaultFeedsFile : argv[1], &stop_words, &data, &seen);
  QueryIndices(&stop_words, &data);
  HashSetDispose(&stop_words);
  HashSetDispose(&data);
  HashSetDispose(&seen);
  curl_global_cleanup();
  return 0;
}

size_t SavePage(char *ptr, size_t size, size_t nmemb, void *data) {
  return fprintf((FILE *)data, "%s", ptr);
}

static FILE *RemoveCData(const char *tmpFile) {
  FILE *inp = fopen(tmpFile, "rb");
  fseek(inp, 0, SEEK_END);
  long fsize = ftell(inp);
  fseek(inp, 0, SEEK_SET); /* same as rewind(f); */
  char *contents = malloc(fsize + 1);
  long read = fread(contents, 1, fsize, inp);
  assert(fsize == read);
  fclose(inp);
  FILE *out = fopen(tmpFile, "w");
  bool inside_cdata = false;
  for (int i = 0; i < fsize; ++i) {
    if (strncasecmp(contents + i, "<![CDATA[", strlen("<![CDATA[")) == 0) {
      inside_cdata = true;
      i += strlen("<![CDATA[") - 1;
    } else if (inside_cdata && strncmp(contents + i, "]]>", 3) == 0) {
      inside_cdata = false;
      i += 2;
    } else {
      fprintf(out, "%c", contents[i]);
    }
  }
  fclose(out);
  free(contents);
  return fopen(tmpFile, "r");
}

static FILE *FetchURL(const char *path, const char *tmpFile) {
  FILE *tmpDoc = fopen(tmpFile, "w");
  CURL *curl;
  CURLcode res;
  curl = curl_easy_init();
  curl_easy_setopt(curl, CURLOPT_VERBOSE, 0L);
  curl_easy_setopt(curl, CURLOPT_URL, path);
  curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L);
  curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, SavePage);
  curl_easy_setopt(curl, CURLOPT_WRITEDATA, tmpDoc);
  res = curl_easy_perform(curl);
  fclose(tmpDoc);
  curl_easy_cleanup(curl);
  if (res != CURLE_OK) {
    return NULL;
  }
  return RemoveCData(tmpFile);
}

static void Welcome(const char *welcomeTextFileName) {
  FILE *infile;
  streamtokenizer st;
  char buffer[1024];

  infile = fopen(welcomeTextFileName, "r");
  assert(infile != NULL);

  STNew(&st, infile, kNewLineDelimiters, true);
  while (STNextToken(&st, buffer, sizeof(buffer))) {
    printf("%s\n", buffer);
  }

  printf("\n");
  STDispose(&st); 
  fclose(infile);
}

static void mpst(void *add, void *auxData) {
  node* it = (node*) add;
  VectorSort(it->nds, ndstFn);
}

static void BuildIndices(const char *feedsFileName, hashset* stop_words, hashset* data, hashset* seen) {
  FILE *infile;
  streamtokenizer st;
  char remoteFileName[1024];

  infile = fopen(feedsFileName, "r");
  assert(infile != NULL);
  STNew(&st, infile, kNewLineDelimiters, true);
  while (STSkipUntil(&st, ":") !=
         EOF) { // ignore everything up to the first selicolon of the line
    STSkipOver(
        &st,
        ": "); // now ignore the semicolon and any whitespace directly after it
    STNextToken(&st, remoteFileName, sizeof(remoteFileName));
    ProcessFeed(remoteFileName, stop_words, data, seen);
  }
  HashSetMap(data, mpst, NULL); 
  STDispose(&st);
  fclose(infile);
  printf("\n");
}

static void ProcessFeedFromFile(char *fileName, hashset* stop_words, hashset* data, hashset*seen) {
  FILE *infile;
  streamtokenizer st;
  char articleDescription[1024];
  articleDescription[0] = '\0';
  infile = fopen((const char *)fileName, "r");
  assert(infile != NULL);
  STNew(&st, infile, kTextDelimiters, true);
  ScanArticle(&st, (const char *)fileName, articleDescription,
              (const char *)fileName, stop_words,  data, seen);
  STDispose(&st); 
  fclose(infile);
}

static void ProcessFeed(const char *remoteDocumentName, hashset* stop_words, hashset* data, hashset* seen) {

  if (!strncmp(kFilePrefix, remoteDocumentName, strlen(kFilePrefix))) {
    ProcessFeedFromFile((char *)remoteDocumentName + strlen(kFilePrefix), stop_words, data, seen);
    return;
  }

  FILE *tmpFeed = FetchURL(remoteDocumentName, "tmp_feed");
  PullAllNewsItems(tmpFeed, stop_words, data, seen);
  fclose(tmpFeed);
}

static void PullAllNewsItems(FILE *dataStream, hashset* stop_words, hashset* data, hashset* seen) {
  streamtokenizer st;
  STNew(&st, dataStream, kTextDelimiters, false);
  while (GetNextItemTag(
      &st)) { 
    ProcessSingleNewsItem(&st, stop_words, data, seen);
  }

  STDispose(&st);
}


static const char *const kItemTagPrefix = "<item";
static bool GetNextItemTag(streamtokenizer *st) {
  char htmlTag[1024];
  while (GetNextTag(st, htmlTag, sizeof(htmlTag))) {
    if (strncasecmp(htmlTag, kItemTagPrefix, strlen(kItemTagPrefix)) == 0) {
      return true;
    }
  }
  return false;
}


static const char *const kItemEndTag = "</item>";
static const char *const kTitleTagPrefix = "<title";
static const char *const kDescriptionTagPrefix = "<description";
static const char *const kLinkTagPrefix = "<link";

static void ProcessSingleNewsItem(streamtokenizer *st, hashset* stop_words, hashset* data, hashset* seen) {
  char htmlTag[1024];
  char articleTitle[1024];
  char articleDescription[1024];
  char articleURL[1024];
  articleTitle[0] = articleDescription[0] = articleURL[0] = '\0';

  while (GetNextTag(st, htmlTag, sizeof(htmlTag)) &&
         (strcasecmp(htmlTag, kItemEndTag) != 0)) {
    if (strncasecmp(htmlTag, kTitleTagPrefix, strlen(kTitleTagPrefix)) == 0) {
      ExtractElement(st, htmlTag, articleTitle, sizeof(articleTitle));
    }
    if (strncasecmp(htmlTag, kDescriptionTagPrefix,
                    strlen(kDescriptionTagPrefix)) == 0)
      ExtractElement(st, htmlTag, articleDescription,
                     sizeof(articleDescription));
    if (strncasecmp(htmlTag, kLinkTagPrefix, strlen(kLinkTagPrefix)) == 0)
      ExtractElement(st, htmlTag, articleURL, sizeof(articleURL));
  }

  if (strncmp(articleURL, "", sizeof(articleURL)) == 0)
    return;
  ParseArticle(articleTitle, articleDescription, articleURL, stop_words, data, seen);
}

static void ExtractElement(streamtokenizer *st, const char *htmlTag,
                           char dataBuffer[], int bufferLength) {
  assert(htmlTag[strlen(htmlTag) - 1] == '>');
  if (htmlTag[strlen(htmlTag) - 2] == '/')
    return; 
  STNextTokenUsingDifferentDelimiters(st, dataBuffer, bufferLength, "<");
  RemoveEscapeCharacters(dataBuffer);
  if (dataBuffer[0] == '<')
    strcpy(dataBuffer, "");
  STSkipUntil(st, ">");
  STSkipOver(st, ">");
}

static void ParseArticle(const char *articleTitle,
                         const char *articleDescription,
                         const char *articleURL, hashset* stop_words, hashset* data, hashset* seen) {
  FILE *tmpDoc = FetchURL(articleURL, "tmp_doc");
  if (tmpDoc == NULL) {
    printf("Unable to fetch URL: %s\n", articleURL);
    return;
  }
  printf("Scanning \"%s\"\n", articleTitle);
  streamtokenizer st;
  STNew(&st, tmpDoc, kTextDelimiters, false);
  ScanArticle(&st, articleTitle, articleDescription, articleURL,stop_words, data, seen);
  STDispose(&st);
  fclose(tmpDoc);
}

bool is_seen(const char *title, const char *url, hashset* seen) {
  char* t = strdup(title);
  char* u = strdup(url);
  
  if (HashSetLookup(seen, &t) != NULL || HashSetLookup(seen, &u) != NULL) {
    free(t);
    free(u);  
    return false;
  } 
  free(t);
  free(u);
  return true;
}

static void ScanArticle(streamtokenizer *st, const char *articleTitle,
                        const char *unused, const char *articleURL, 
                        hashset* stop_words, hashset* data, 
                        hashset*seen) {
  int numWords = 0;
  char word[1024];
  char longestWord[1024] = {'\0'};


  if (!is_seen(articleTitle, articleURL, seen)) return;
  char* u = strdup(articleURL);
  char* t = strdup(articleTitle);
  HashSetEnter(seen, &u);
  HashSetEnter(seen, &t);

  while (STNextToken(st, word, sizeof(word))) {
    if (strcasecmp(word, "<") == 0) {
      SkipIrrelevantContent(st); 
    } else {
      RemoveEscapeCharacters(word);
      char* cp = strdup(word);
      
      if (WordIsWellFormed(cp)) {
        if(HashSetLookup(stop_words, &cp) != NULL){
          free(cp);
          continue;
        } 
      
        node* ptr = HashSetLookup(data, &cp); // vnaxot ukve damatebuli maqvs tu ara eg sityva
        if(ptr == NULL){
          nd newNd;
          newNd.title = strdup(articleTitle);
          newNd.url = strdup(articleURL);
          newNd.word_counter = 1;

          node newNode;

          vector* vec = malloc(sizeof(vector));
          VectorNew(vec, sizeof(nd), ndFreeFn, 1717); 
          VectorAppend(vec, &newNd);

          newNode.nds = vec;
          newNode.word = strdup(word);
      
          HashSetEnter(data, &newNode);
        }else{
          //jer vipovot eg nd
          nd cur;
          cur.title = strdup(articleTitle);
          cur.url = strdup(articleURL);

          int ind = VectorSearch(ptr->nds, &cur, nodecmpFn, 0, false);
          free(cur.title);
          free(cur.url);

          if(ind == -1){
            nd newNode;
            newNode.title =  strdup(articleTitle);
            newNode.url = strdup(articleURL);
            newNode.word_counter = 1;
            VectorAppend(ptr->nds, &newNode);
          }else{
            ((nd*)(VectorNth(ptr->nds, ind)))->word_counter++;
          }
        }
        numWords++;
        if (strlen(word) > strlen(longestWord))
          strcpy(longestWord, word);
      }
      free(cp);
    }
  }

  printf("\tWe counted %d well-formed words [including duplicates].\n",
         numWords);
  printf("\tThe longest word scanned was \"%s\".", longestWord);
  if (strlen(longestWord) >= 15 && (strchr(longestWord, '-') == NULL))
    printf(" [Ooooo... long word!]");
  printf("\n");
}

static void QueryIndices( hashset *stop_words, hashset *data) {
  char response[1024];
  while (true) {
    printf("Please enter a single search term [enter to break]: ");
    fgets(response, sizeof(response), stdin);
    response[strlen(response) - 1] = '\0';
    if (strcasecmp(response, "") == 0)
      break;
    ProcessResponse(response,stop_words,data);
  }
}


static void ProcessResponse(const char *word, hashset* stop_words, hashset* data) {
  if(!WordIsWellFormed(word)){
    printf("\tWe won't be allowing words like \"%s\" into our set of indices.\n",word);
    return;
  }

  if(HashSetLookup(stop_words, &word) != NULL){
    printf("Too common a word to be taken seriously. Try something more specific.\n");
    return;
  }

  node* ptr = HashSetLookup(data, &word);

  if(ptr == NULL){
    printf("None of today's news articles contain the word \"%s\".\n", word);
    return;
  }

  int article_len = VectorLength(ptr->nds);

  for(int i = 0; i < article_len; i++){
    nd* cur = VectorNth(ptr ->nds, i);

    char* title = cur->title;
    char* url = cur->url;
    int num = cur->word_counter;

    char* t = "time";
    if(cur->word_counter > 1)t = "times";

    printf("%d.) \"%s\" [search term occurs %d %s]\n\"%s\"\n", (i + 1), title, num, t, url);
  }
}

static bool WordIsWellFormed(const char *word) {
  int i;
  if (strlen(word) == 0)
    return true;
  if (!isalpha((int)word[0]))
    return false;
  for (i = 1; i < strlen(word); i++)
    if (!isalnum((int)word[i]) && (word[i] != '-'))
      return false;

  return true;
}
