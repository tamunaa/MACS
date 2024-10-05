// client.go
package kvraft

import (
	"6.5840/labrpc"
	"crypto/rand"
	"math/big"
	"sync"
	"sync/atomic"
	"time"
)

type Clerk struct {
	servers   []*labrpc.ClientEnd
	leaderId  int
	clientId  int64
	requestId int64
	mu        sync.Mutex
}

func nrand() int64 {
	max := big.NewInt(int64(1) << 62)
	bigx, _ := rand.Int(rand.Reader, max)
	x := bigx.Int64()
	return x
}

func MakeClerk(servers []*labrpc.ClientEnd) *Clerk {
	ck := new(Clerk)
	ck.servers = servers
	ck.clientId = nrand()
	ck.requestId = 0
	ck.leaderId = 0
	return ck
}

func (ck *Clerk) Get(key string) string {
	args := OppArgs{
		Key:       key,
		ClientId:  ck.clientId,
		RequestId: atomic.AddInt64(&ck.requestId, 1),
	}

	for {
		for i := 0; i < len(ck.servers); i++ {
			serverId := (ck.leaderId + i) % len(ck.servers)
			reply := OppReply{}
			ok := ck.servers[serverId].Call("KVServer.Get", &args, &reply)

			if ok && reply.Err == OK {
				ck.leaderId = serverId
				return reply.Value
			}
			if ok && reply.Err == ErrNoKey {
				return ""
			}
		}
		time.Sleep(100 * time.Millisecond)
	}
}

func (ck *Clerk) PutAppend(key string, value string, op string) {
	args := OppArgs{
		Key:       key,
		Value:     value,
		Op:        op,
		ClientId:  ck.clientId,
		RequestId: atomic.AddInt64(&ck.requestId, 1),
	}

	for {
		for i := 0; i < len(ck.servers); i++ {
			serverId := (ck.leaderId + i) % len(ck.servers)
			reply := OppReply{}
			ok := ck.servers[serverId].Call("KVServer.PutAppend", &args, &reply)

			if ok && reply.Err == OK {
				ck.leaderId = serverId
				return
			}
		}
		time.Sleep(100 * time.Millisecond)
	}
}

func (ck *Clerk) Put(key string, value string) {
	ck.PutAppend(key, value, "Put")
}

func (ck *Clerk) Append(key string, value string) {
	ck.PutAppend(key, value, "Append")
}
