package kvraft

import (
	"6.5840/raft"
	"sync"
)

const (
	OK             = "OK"
	ErrNoKey       = "ErrNoKey"
	ErrWrongLeader = "ErrWrongLeader"
)

type Err string

type OppArgs struct {
	Key       string
	Value     string
	Op        string
	ClientId  int64
	RequestId int64
}

type OppReply struct {
	Err   Err
	Value string
}

type OperationType string

const (
	OperationTypeGet    = "Get"
	OperationTypePut    = "Put"
	OperationTypeAppend = "Append"
)

type Op struct {
	Type      string
	Key       string
	Value     string
	ClientId  int64
	RequestId int64
}

type KVServer struct {
	mu           sync.Mutex
	me           int
	rf           *raft.Raft
	applyCh      chan raft.ApplyMsg
	dead         int32
	maxraftstate int

	db          map[string]string
	lastApplied map[int64]int64
	notifyChs   map[int]chan OpResult
	persister   *raft.Persister
}

type OpResult struct {
	Op    Op
	Value string
	Err   Err
}
