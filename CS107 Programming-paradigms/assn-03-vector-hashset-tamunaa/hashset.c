#include "hashset.h"
#include <assert.h>
#include <stdlib.h>
#include <string.h>

void HashSetNew(hashset *h, int elemSize, int numBuckets, HashSetHashFunction hashfn, HashSetCompareFunction comparefn, HashSetFreeFunction freefn){
	assert(numBuckets > 0);
	assert(elemSize > 0);
	assert(hashfn != NULL);
	assert(comparefn != NULL);

	void* base = malloc(numBuckets * sizeof(vector));	
	h->base = base;
	h->hashfn = hashfn;
	h->freefn = freefn;
	h->cmpfn = comparefn;
	h->elem_size = elemSize;
	h->num_buckets = numBuckets;
	h->log_len = 0;

	vector* ptr = h->base;
	for(int i = 0; i < numBuckets; i++){
		VectorNew((void*)ptr, elemSize, freefn, 64);
		ptr++;
	}
}

void HashSetDispose(hashset *h){
	for(int i = 0; i < h->num_buckets; i++) VectorDispose(&h->base[i]);
	
	free(h->base);
}

int HashSetCount(const hashset *h){
	return h->log_len;
}

void HashSetMap(hashset *h, HashSetMapFunction mapfn, void *auxData){
	assert(mapfn != NULL);
	for(int i = 0; i < h->num_buckets; i++) VectorMap(&h->base[i], mapfn, auxData);
}

void HashSetEnter(hashset *h, const void *elemAddr){
	assert(elemAddr != NULL);
	int hash = h->hashfn(elemAddr, h->num_buckets);
	assert(hash >= 0 && hash < h->num_buckets);

	int ind = VectorSearch(&h->base[hash], elemAddr, h->cmpfn, 0, 0);
	
	if(ind != -1){
		VectorReplace(&h->base[hash], elemAddr, ind);
	}else{
		VectorAppend(&h->base[hash], elemAddr);
		h->log_len++;
	}	
}

void *HashSetLookup(const hashset *h, const void *elemAddr){
	assert(elemAddr != NULL);
	int hash = h->hashfn(elemAddr, h->num_buckets);
	assert(hash >= 0 && hash < h->num_buckets);

	int ind = VectorSearch(&h->base[hash], elemAddr, h->cmpfn, 0, 0);
	if(ind == -1)return NULL;
	return (void*)(VectorNth(&h->base[hash], ind)); 
}
