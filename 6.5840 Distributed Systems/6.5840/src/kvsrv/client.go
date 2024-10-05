package kvsrv

import (
	"6.5840/labrpc"
	"sync/atomic"
)
import "crypto/rand"
import "math/big"

type Clerk struct {
	server *labrpc.ClientEnd
	ID     int32
	SeqNum int32
}

func MakeClerk(server *labrpc.ClientEnd) *Clerk {
	return &Clerk{
		server: server,
		ID:     int32(nrand()),
		SeqNum: 0,
	}
}

func nrand() int64 {
	max := big.NewInt(int64(1) << 62)
	bigx, _ := rand.Int(rand.Reader, max)
	x := bigx.Int64()
	return x
}

// fetch the current value for a key.
// returns "" if the key does not exist.
// keeps trying forever in the face of all other errors.
//
// you can send an RPC with code like this:
// ok := ck.server.Call("KVServer.Get", &args, &reply)
//
// the types of args and reply (including whether they are pointers)
// must match the declared types of the RPC handler function's
// arguments. and reply must be passed as a pointer.
func (ck *Clerk) Get(key string) string {
	args := GetArgs{
		Key:       key,
		UserID:    ck.ID,
		RequestID: atomic.AddInt32(&ck.SeqNum, 1),
	}
	reply := GetReply{}
	for {
		if ck.server.Call("KVServer.Get", &args, &reply) {
			return reply.Value
		}
	}
}

// shared by Put and Append.
//
// you can send an RPC with code like this:
// ok := ck.server.Call("KVServer."+op, &args, &reply)
//
// the types of args and reply (including whether they are pointers)
// must match the declared types of the RPC handler function's
// arguments. and reply must be passed as a pointer.
func (ck *Clerk) PutAppend(key string, value string, op string) string {
	args := PutAppendArgs{
		Key:       key,
		Value:     value,
		UserID:    ck.ID,
		RequestID: atomic.AddInt32(&ck.SeqNum, 1),
	}
	reply := PutAppendReply{}

	for {
		if ck.server.Call("KVServer."+op, &args, &reply) {
			//fmt.Println("KEY", key, "VALUE", value)
			//fmt.Println("[CLIENT][RETURNING]", reply.Value, "[ONARGS]", args)
			return reply.Value
		}
	}
}

func (ck *Clerk) Put(key string, value string) {
	ck.PutAppend(key, value, "Put")
}

// Append value to key's value and return that value
func (ck *Clerk) Append(key string, value string) string {
	return ck.PutAppend(key, value, "Append")
}
