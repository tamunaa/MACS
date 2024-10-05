package kvsrv

// Put or Append
type PutAppendArgs struct {
	Key       string
	Value     string
	UserID    int32
	RequestID int32
}

type PutAppendReply struct {
	Value string
}

type GetArgs struct {
	Key       string
	UserID    int32
	RequestID int32
}

type GetReply struct {
	Value string
}
