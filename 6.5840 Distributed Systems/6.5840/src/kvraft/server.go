// server.go
package kvraft

import (
	"6.5840/labgob"
	"6.5840/labrpc"
	"6.5840/raft"
	"bytes"
	"log"
	"sync/atomic"
	"time"
)

const (
	operationTimeout = 500 * time.Millisecond
)

func (kv *KVServer) handleWait(index int, args *OppArgs, reply *OppReply) {
	ch := kv.createNotifyChan(index)
	defer kv.deleteNotifyChan(index)

	select {
	case result := <-ch:
		if result.Op.ClientId == args.ClientId && result.Op.RequestId == args.RequestId {
			reply.Value = result.Value
			reply.Err = result.Err
		} else {
			reply.Err = ErrWrongLeader
		}
	case <-time.After(operationTimeout):
		reply.Err = ErrWrongLeader
	}
}

func (kv *KVServer) Get(args *OppArgs, reply *OppReply) {
	if kv.killed() {
		reply.Err = ErrWrongLeader
		return
	}

	op := Op{
		Type:      OperationTypeGet,
		Key:       args.Key,
		ClientId:  args.ClientId,
		RequestId: args.RequestId,
	}

	index, _, isLeader := kv.rf.Start(op)
	if !isLeader {
		reply.Err = ErrWrongLeader
		return
	}
	kv.handleWait(index, args, reply)
}

func (kv *KVServer) PutAppend(args *OppArgs, reply *OppReply) {
	if kv.killed() {
		reply.Err = ErrWrongLeader
		return
	}

	op := Op{
		Type:      args.Op,
		Key:       args.Key,
		Value:     args.Value,
		ClientId:  args.ClientId,
		RequestId: args.RequestId,
	}

	index, _, isLeader := kv.rf.Start(op)
	if !isLeader {
		reply.Err = ErrWrongLeader
		return
	}

	kv.handleWait(index, args, reply)
}

func (kv *KVServer) createNotifyChan(index int) chan OpResult {
	kv.mu.Lock()
	defer kv.mu.Unlock()

	ch := make(chan OpResult, 1)
	kv.notifyChs[index] = ch
	return ch
}

func (kv *KVServer) deleteNotifyChan(index int) {
	kv.mu.Lock()
	defer kv.mu.Unlock()
	delete(kv.notifyChs, index)
}

func (kv *KVServer) applier() {
	for !kv.killed() {
		msg := <-kv.applyCh

		if msg.CommandValid {
			kv.mu.Lock()
			op := msg.Command.(Op)
			result := OpResult{Op: op, Err: OK}

			if op.RequestId <= kv.lastApplied[op.ClientId] {
				// Duplicate request
				if op.Type == OperationTypeGet {
					result.Value = kv.db[op.Key]
					if _, exists := kv.db[op.Key]; !exists {
						result.Err = ErrNoKey
					}
				}
			} else {
				switch op.Type {
				case OperationTypePut:
					kv.db[op.Key] = op.Value
				case OperationTypeAppend:
					kv.db[op.Key] += op.Value
				case OperationTypeGet:
					result.Value = kv.db[op.Key]
					if _, exists := kv.db[op.Key]; !exists {
						result.Err = ErrNoKey
					}
				}
				kv.lastApplied[op.ClientId] = op.RequestId
			}

			if ch, ok := kv.notifyChs[msg.CommandIndex]; ok {
				ch <- result
			}

			kv.maybeCreateSnapshot(msg.CommandIndex)

			kv.mu.Unlock()
		} else if msg.SnapshotValid {
			kv.readSnapshot(msg.Snapshot)
		}
	}
}

func (kv *KVServer) maybeCreateSnapshot(index int) {
	if kv.maxraftstate == -1 || kv.persister.RaftStateSize() <= kv.maxraftstate {
		return
	}

	snapshot := kv.createSnapshot()
	kv.rf.Snapshot(index, snapshot)
}

func (kv *KVServer) createSnapshot() []byte {
	w := new(bytes.Buffer)
	e := labgob.NewEncoder(w)
	e.Encode(kv.db)
	e.Encode(kv.lastApplied)
	return w.Bytes()
}

func (kv *KVServer) readSnapshot(snapshot []byte) {
	kv.mu.Lock()
	defer kv.mu.Unlock()

	if snapshot == nil || len(snapshot) < 1 {
		return
	}

	r := bytes.NewBuffer(snapshot)
	d := labgob.NewDecoder(r)

	var db map[string]string
	var lastApplied map[int64]int64

	if d.Decode(&db) != nil || d.Decode(&lastApplied) != nil {
		kv.LogError("Failed to decode snapshot")
		return
	}

	kv.db = db
	kv.lastApplied = lastApplied
}

func (kv *KVServer) Kill() {
	atomic.StoreInt32(&kv.dead, 1)
	kv.rf.Kill()
}

func (kv *KVServer) killed() bool {
	z := atomic.LoadInt32(&kv.dead)
	return z == 1
}

func StartKVServer(servers []*labrpc.ClientEnd, me int, persister *raft.Persister, maxraftstate int) *KVServer {
	labgob.Register(Op{})

	kv := new(KVServer)
	kv.me = me
	kv.maxraftstate = maxraftstate
	kv.persister = persister

	kv.db = make(map[string]string)
	kv.lastApplied = make(map[int64]int64)
	kv.notifyChs = make(map[int]chan OpResult)

	kv.applyCh = make(chan raft.ApplyMsg)
	kv.rf = raft.Make(servers, me, persister, kv.applyCh)

	kv.readSnapshot(persister.ReadSnapshot())
	go kv.applier()

	return kv
}

const Debug = false

func (kv *KVServer) LogError(format string, args ...interface{}) {
	if Debug {
		log.Printf("Server %d [ERROR]: "+format, append([]interface{}{kv.me}, args...)...)
	}
}

func (kv *KVServer) LogInfo(format string, args ...interface{}) {
	if Debug {
		log.Printf("Server %d [INFO]: "+format, append([]interface{}{kv.me}, args...)...)
	}
}
