#include "vector.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <search.h>

void VectorNew(vector *v, int elemSize, VectorFreeFunction freeFn, int initialAllocation){
    assert(elemSize > 0 && initialAllocation >= 0);

    if(initialAllocation == 0)initialAllocation = 1;

    v->elem_size = elemSize;
    v->freefn = freeFn;
    v->init_len = initialAllocation;
    v->log_len = 0;
    v->len = initialAllocation;
    v->base = malloc(initialAllocation * elemSize);
    assert(v->base != NULL);
}

void VectorDispose(vector *v){
    if(v->freefn != NULL){
        for(int i = 0; i < v->log_len; i++){
            void* ptr = VectorNth(v, i);
            v->freefn(ptr);
        }
    }
    free(v->base);
}

int VectorLength(const vector *v){ 
    return v->log_len;
}

void *VectorNth(const vector *v, int position){
    assert(position >= 0 && position < v->log_len);
    void* ptr = (void*)((char*)v->base + v->elem_size * position);
    
    return ptr; 
}

void VectorReplace(vector *v, const void *elemAddr, int position){
    assert(position >= 0 && position < v->log_len);

    void* ptr = VectorNth(v, position);
    if(v->freefn != NULL)v->freefn(ptr);

    memcpy(ptr, elemAddr, v->elem_size);
}

void grow(vector *v){
    v->len *= 2;
    //v->len += v->init_len; //nelia dzalian ase
    v->base = realloc(v->base, v->len * v->elem_size);
    assert(v->base!=NULL);
}

void VectorInsert(vector *v, const void *elemAddr, int position){
    assert(position >= 0 && position <= v->log_len);
    if(v->log_len == v->len)grow(v);

    if(position == v->log_len){
        VectorAppend(v, elemAddr);
        return;
    }

    void* ins_pos = VectorNth(v, position);
    char* nxt_pos = (void*)((char*)ins_pos + v->elem_size);

    memmove(nxt_pos, ins_pos, (v->log_len - position)*(v->elem_size));
    memcpy(ins_pos, elemAddr, v->elem_size);

    v->log_len++;
}

void VectorAppend(vector *v, const void *elemAddr){
    if(v->log_len == v->len)grow(v);

    char* address = (char*)v->base + v->log_len * v->elem_size;

    memcpy(address, elemAddr, v->elem_size);
    v->log_len++;
}

void VectorDelete(vector *v, int position){
    assert(v->log_len >= 0 && position < v->log_len);

    int len = (v->log_len - position -1)*v->elem_size;
    void* ptr = VectorNth(v, position);
    char* nxt = (char*)ptr + v->elem_size;
    if(v->freefn != NULL)v->freefn(ptr);
   
    memcpy(ptr, nxt, len);
    if(v->freefn != NULL && position != v->log_len)v->freefn((char*)v->base + v->log_len * v->elem_size);
    v->log_len--;
}

void VectorSort(vector *v, VectorCompareFunction compare){
    qsort(v->base, v->log_len, v->elem_size, compare);
}

void VectorMap(vector *v, VectorMapFunction mapFn, void *auxData){
    assert(mapFn != NULL);
    for(int i = 0; i < v->log_len; i++) mapFn((char*)v->base + i * v->elem_size, auxData);
    
}

static const int kNotFound = -1;

int VectorSearch(const vector *v, const void *key, VectorCompareFunction searchFn, int startIndex, bool isSorted) {
    assert(startIndex >= 0 && startIndex <= v->log_len);
    assert(searchFn != NULL);

    void* start_pos = (void*)((char*)v->base + startIndex * v->elem_size);
    size_t num_memb = v->log_len - startIndex;
    char* address;

    if(isSorted){
        address = (char*)bsearch(key, start_pos, num_memb, v->elem_size, searchFn);
    }else{
        address = (char*)lfind(key, start_pos, &num_memb, v->elem_size, searchFn);
    }
    
    if(address != NULL){
        int ind = (address - (char*)v->base)/v->elem_size;
        return ind;
    }

    return kNotFound;
}
