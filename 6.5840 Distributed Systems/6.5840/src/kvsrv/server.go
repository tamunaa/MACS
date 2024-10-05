package kvsrv

import (
	"log"
	"sync"
)

const Debug = false

func DPrintf(format string, a ...interface{}) (n int, err error) {
	if Debug {
		log.Printf(format, a...)
	}
	return
}

type KVServer struct {
	mu sync.Mutex

	kvas               map[string]string
	lastRequestPerUser map[int32]LastRequest
}

type LastRequest struct {
	RequestID int32
	//Value     string
	Length int32
}

func (kv *KVServer) Get(args *GetArgs, reply *GetReply) {
	kv.mu.Lock()
	defer kv.mu.Unlock()
	reply.Value = kv.kvas[args.Key]
}

func (kv *KVServer) Put(args *PutAppendArgs, reply *PutAppendReply) {
	kv.mu.Lock()
	defer kv.mu.Unlock()
	//fmt.Println("[PUT][BEFORE]", kv.kvas)
	//fmt.Println("[PUT][ARGS]", args)

	if lastReq, ok := kv.lastRequestPerUser[args.UserID]; ok && args.RequestID == lastReq.RequestID {
		//reply.Value = lastReq.Value
		reply.Value = kv.kvas[args.Key][0:lastReq.Length]
		return
	}

	kv.kvas[args.Key] = args.Value
	reply.Value = kv.kvas[args.Key]
	kv.lastRequestPerUser[args.UserID] = LastRequest{RequestID: args.RequestID,
		//Value: reply.Value
		Length: int32(len(reply.Value)),
	}
	//fmt.Println("[PUT][AFTER]", kv.kvas)
	//fmt.Println("[PUT][REPLY]", reply)
}

func (kv *KVServer) Append(args *PutAppendArgs, reply *PutAppendReply) {
	kv.mu.Lock()
	defer kv.mu.Unlock()

	//fmt.Println("[APPEND][BEFORE]", kv.kvas)
	//fmt.Println("[APPEND][ARGS]", args)
	if lastReq, ok := kv.lastRequestPerUser[args.UserID]; ok && args.RequestID == lastReq.RequestID {
		//reply.Value = lastReq.Value
		reply.Value = kv.kvas[args.Key][0:lastReq.Length]
		return
	}

	reply.Value = kv.kvas[args.Key]
	kv.kvas[args.Key] += args.Value
	kv.lastRequestPerUser[args.UserID] = LastRequest{RequestID: args.RequestID,
		//Value: reply.Value
		Length: int32(len(reply.Value)),
	}
	//fmt.Println("[APPEND][AFTER]", kv.kvas)
	//fmt.Println("[APPEND][REPLY]")
}

func StartKVServer() *KVServer {
	kv := new(KVServer)
	kv.kvas = make(map[string]string)
	kv.lastRequestPerUser = make(map[int32]LastRequest)

	return kv
}
