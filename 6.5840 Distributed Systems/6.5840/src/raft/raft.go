package raft

//
// this is an outline of the API that raft must expose to
// the service (or tester). see comments below for
// each of these functions for more details.
//
// rf = Make(...)
//   create a new Raft server.
// rf.Start(command interface{}) (index, term, isleader)
//   start agreement on a new log entry
// rf.GetState() (term, isLeader)
//   ask a Raft for its current term, and whether it thinks it is leader
// ApplyMsg
//   each time a new entry is committed to the log, each Raft peer
//   should send an ApplyMsg to the service (or tester)
//   in the same server.
//

import (
	"6.5840/labgob"
	"bytes"
	"math/rand"

	"sort"
	"sync"
	"sync/atomic"
	"time"

	//	"6.5840/labgob"
	"6.5840/labrpc"
)

// as each Raft peer becomes aware that successive log entries are
// committed, the peer should send an ApplyMsg to the service (or
// tester) on the same server, via the applyCh passed to Make(). set
// CommandValid to true to indicate that the ApplyMsg contains a newly
// committed log entry.
//
// in part 3D you'll want to send other kinds of messages (e.g.,
// snapshots) on the applyCh, but set CommandValid to false for these
// other uses.
type ApplyMsg struct {
	CommandValid bool
	Command      interface{}
	CommandIndex int

	// For 3D:
	SnapshotValid bool
	Snapshot      []byte
	SnapshotTerm  int
	SnapshotIndex int
}

const (
	Leader = iota
	Candidate
	Follower
)

type Entry struct {
	Term    int
	Command interface{}
}

type PeerQueue struct {
	ch chan bool
	mu sync.Mutex
}

type SnapshotData struct {
	LastIncludedIndex int
	LastIncludedTerm  int
	Data              []byte
}

// A Go object implementing a single Raft peer.
type Raft struct {
	mu        sync.Mutex          // Lock to protect shared access to this peer's state
	peers     []*labrpc.ClientEnd // RPC end points of all peers
	persister *Persister          // Object to hold this peer's persisted state
	me        int                 // this peer's index into peers[]
	dead      int32               // set by Kill()
	applyCh   chan ApplyMsg
	commitCh  chan int
	// Your data here (3A, 3B, 3C).
	// Look at the paper's Figure 2 for a description of what
	// state a Raft server must maintain.
	peerQueues []PeerQueue

	nextIndex   []int
	matchIndex  []int
	commitIndex int

	actorType   int
	nextTimeout time.Time

	/* persisted */
	log         []Entry
	snapshot    SnapshotData
	currentTerm int
	votedFor    int
}

func (rf *Raft) getLogIdx(idx int) int {
	return idx - (rf.snapshot.LastIncludedIndex + 1)
}

func (rf *Raft) getLogLength() int {
	return rf.getLastLogIndex() + 1
}

func (rf *Raft) getLastLogIndex() int {
	return len(rf.log) + rf.snapshot.LastIncludedIndex
}

func (rf *Raft) getLastLogTerm() int {
	if len(rf.log) > 0 {
		return rf.log[len(rf.log)-1].Term
	} else {
		return rf.snapshot.LastIncludedTerm
	}
}

// return currentTerm and whether this server
// believes it is the leader.
func (rf *Raft) GetState() (int, bool) {
	rf.mu.Lock()
	defer rf.mu.Unlock()

	// Your code here (2A).
	return rf.currentTerm, rf.actorType == Leader
}

// save Raft's persistent state to stable storage,
// where it can later be retrieved after a crash and restart.
// see paper's Figure 2 for a description of what should be persistent.
// before you've implemented snapshots, you should pass nil as the
// second argument to persister.Save().
// after you've implemented snapshots, pass the current snapshot
// (or nil if there's not yet a snapshot).
func (rf *Raft) persist() {
	// Your code here (3C).
	// Example:
	w := new(bytes.Buffer)
	e := labgob.NewEncoder(w)
	e.Encode(rf.currentTerm)
	e.Encode(rf.votedFor)
	e.Encode(rf.log)
	e.Encode(rf.snapshot.LastIncludedIndex)
	e.Encode(rf.snapshot.LastIncludedTerm)
	raftstate := w.Bytes()
	rf.persister.Save(raftstate, rf.snapshot.Data)
}

// restore previously persisted state.
func (rf *Raft) readPersist(data []byte) {
	if data == nil || len(data) < 1 { // bootstrap without any state?
		return
	}
	// Your code here (3C).
	// Example:
	r := bytes.NewBuffer(data)
	d := labgob.NewDecoder(r)

	var currentTerm int
	var votedFor int
	var log []Entry
	var snapshotLastIncludedIndex int
	var snapshotLastIncludedTerm int
	if d.Decode(&currentTerm) != nil || d.Decode(&votedFor) != nil || d.Decode(&log) != nil ||
		d.Decode(&snapshotLastIncludedIndex) != nil || d.Decode(&snapshotLastIncludedTerm) != nil {
		panic("Error decoding persisted state")
	} else {
		rf.currentTerm = currentTerm
		rf.votedFor = votedFor
		rf.log = log
		rf.snapshot.LastIncludedIndex = snapshotLastIncludedIndex
		rf.snapshot.LastIncludedTerm = snapshotLastIncludedTerm
	}
}

// the service says it has created a snapshot that has
// all info up to and including index. this means the
// service no longer needs the log through (and including)
// that index. Raft should now trim its log as much as possible.
func (rf *Raft) Snapshot(index int, snapshot []byte) {
	rf.mu.Lock()
	defer rf.mu.Unlock()

	if rf.snapshot.LastIncludedIndex >= (index - 1) {
		return
	}

	DPrintf("Server %d snapshotting at index %d\n", rf.me, index-1)
	idx := rf.getLogIdx(index - 1)
	rf.snapshot.LastIncludedIndex = index - 1
	rf.snapshot.LastIncludedTerm = rf.log[idx].Term
	rf.snapshot.Data = snapshot
	rf.log = rf.log[idx+1:]
	rf.persist()
}

// example RequestVote RPC arguments structure.
// field names must start with capital letters!
type RequestVoteArgs struct {
	// Your data here (3A, 3B).
	Term         int
	CandidateId  int
	LastLogIndex int
	LastLogTerm  int
}

// example RequestVote RPC reply structure.
// field names must start with capital letters!
type RequestVoteReply struct {
	Term        int
	VoteGranted bool
}

// example RequestVote RPC handler.
func (rf *Raft) RequestVote(args *RequestVoteArgs, reply *RequestVoteReply) {
	rf.mu.Lock()
	defer rf.mu.Unlock()
	if rf.killed() {
		return
	}

	reply.Term = rf.currentTerm

	if args.Term < rf.currentTerm {
		reply.VoteGranted = false
		return
	}

	if args.Term > rf.currentTerm {
		rf.changeType(Follower, args.Term, -1)
	}

	if rf.votedFor != -1 {
		reply.VoteGranted = rf.votedFor == args.CandidateId
		//if reply.VoteGranted {
		//	rf.updateTimeMarker()
		//}
		return
	}

	if args.LastLogTerm < rf.getLastLogTerm() || (args.LastLogTerm == rf.getLastLogTerm() && args.LastLogIndex < rf.getLastLogIndex()) {
		reply.VoteGranted = false
		return
	}

	rf.votedFor = args.CandidateId
	rf.persist()
	reply.VoteGranted = true
	rf.updateTimeMarker()
}

// example code to send a RequestVote RPC to a server.
// server is the index of the target server in rf.peers[].
// expects RPC arguments in args.
// fills in *reply with RPC reply, so caller should
// pass &reply.
// the types of the args and reply passed to Call() must be
// the same as the types of the arguments declared in the
// handler function (including whether they are pointers).
//
// The labrpc package simulates a lossy network, in which servers
// may be unreachable, and in which requests and replies may be lost.
// Call() sends a request and waits for a reply. If a reply arrives
// within a timeout interval, Call() returns true; otherwise
// Call() returns false. Thus Call() may not return for a while.
// A false return can be caused by a dead server, a live server that
// can't be reached, a lost request, or a lost reply.
//
// Call() is guaranteed to return (perhaps after a delay) *except* if the
// handler function on the server side does not return.  Thus there
// is no need to implement your own timeouts around Call().
//
// look at the comments in ../labrpc/labrpc.go for more details.
//
// if you're having trouble getting RPC to work, check that you've
// capitalized all field names in structs passed over RPC, and
// that the caller passes the address of the reply struct with &, not
// the struct itself.
func (rf *Raft) sendRequestVote(server int, args *RequestVoteArgs, reply *RequestVoteReply) bool {
	ok := rf.peers[server].Call("Raft.RequestVote", args, reply)
	return ok
}

type AppendEntriesArgs struct {
	Term         int
	LeaderId     int
	PrevLogIndex int
	PrevLogTerm  int
	Entries      []Entry
	LeaderCommit int
}

type AppendEntriesReply struct {
	Term    int
	Success bool

	LastLogTerm    int
	FirstTermIndex int
	LogLength      int
}

const ElectionTimeMinimum int64 = 300
const ElectionTimeRange int64 = 300

func (rf *Raft) isTimedOut() bool {
	return time.Now().After(rf.nextTimeout)
}

func (rf *Raft) updateTimeMarker() {
	rf.nextTimeout = time.Now().Add(time.Duration(ElectionTimeMinimum+rand.Int63n(ElectionTimeRange)) * time.Millisecond)
}

func min(a, b int) int {
	if a < b {
		return a
	}
	return b
}

func max(a, b int) int {
	if a > b {
		return a
	}
	return b
}

func (rf *Raft) sendCommit(msg ApplyMsg) {
	for rf.killed() == false {
		select {
		case rf.applyCh <- msg:
			return
		case <-time.After(100 * time.Millisecond):
			continue
		}
	}
}

func (rf *Raft) sendCommits() {
	lastApplied := 0
	for rf.killed() == false {
		nextCommit, ok := <-rf.commitCh
		if !ok {
			return
		}

		if nextCommit < lastApplied {
			continue
		}

		rf.mu.Lock()

		logIdx := rf.getLogIdx(lastApplied)

		if logIdx < 0 {
			msg := ApplyMsg{}
			msg.SnapshotValid = true
			msg.Snapshot = rf.snapshot.Data
			msg.SnapshotIndex = rf.snapshot.LastIncludedIndex + 1
			msg.SnapshotTerm = rf.snapshot.LastIncludedTerm
			rf.mu.Unlock()

			rf.sendCommit(msg)

			lastApplied = msg.SnapshotIndex
			continue
		}

		entries := make([]Entry, nextCommit-lastApplied+1)
		endIdx := rf.getLogIdx(nextCommit + 1)
		copy(entries, rf.log[logIdx:endIdx])

		rf.mu.Unlock()
		for i := 0; i < len(entries); i++ {
			msg := ApplyMsg{}
			msg.CommandValid = true
			msg.CommandIndex = lastApplied + i + 1 // log is 1 indexed
			msg.Command = entries[i].Command
			rf.sendCommit(msg)
		}
		lastApplied = nextCommit + 1
	}
}

func (rf *Raft) updateCommitIndex(newValue int) {
	oldCommit := rf.commitIndex
	rf.commitIndex = min(newValue, rf.getLastLogIndex())
	if rf.commitIndex != oldCommit {
		if rf.actorType == Leader {
			rf.forceSend()
		}
		select {
		case <-rf.commitCh:
			rf.commitCh <- rf.commitIndex
			break
		default:
			rf.commitCh <- rf.commitIndex
		}
	}
}

func (rf *Raft) changeType(newType int, newTerm int, newVotedFor int) {
	rf.actorType = newType

	if rf.currentTerm != newTerm || rf.votedFor != newVotedFor {
		rf.currentTerm = newTerm
		rf.votedFor = newVotedFor
		rf.persist()
	}
	rf.updateTimeMarker()
}

func (rf *Raft) AppendEntries(args *AppendEntriesArgs, reply *AppendEntriesReply) {
	rf.mu.Lock()
	defer rf.mu.Unlock()

	if rf.killed() {
		return
	}

	reply.Term = rf.currentTerm

	if args.Term < rf.currentTerm {
		reply.Success = false
		return
	}

	rf.changeType(Follower, args.Term, args.LeaderId)

	DPrintf("Server %d got append from %d\n", rf.me, args.LeaderId)

	// don't even have the previous log
	reply.LogLength = rf.getLogLength()
	if args.PrevLogIndex >= rf.getLogLength() {
		reply.LastLogTerm = -1
		reply.FirstTermIndex = -1

		rf.updateTimeMarker()
		reply.Success = false
		return
	}

	logIdx := rf.getLogIdx(args.PrevLogIndex)

	if logIdx >= 0 && rf.log[logIdx].Term != args.PrevLogTerm {
		reply.LastLogTerm = rf.log[logIdx].Term
		var index int
		for index = logIdx; index >= 0 && rf.log[index].Term == reply.LastLogTerm; index-- {
		}
		reply.FirstTermIndex = index + 1

		rf.updateTimeMarker()
		reply.Success = false
		return
	}

	reply.Success = true

	entriesStart := 0

	if logIdx < 0 {
		entriesStart = -logIdx - 1
	}

	for i := entriesStart; i < len(args.Entries); i++ {
		idx := i + logIdx + 1
		if idx >= len(rf.log) || rf.log[idx].Term != args.Entries[i].Term {
			rf.log = append(rf.log[:idx], args.Entries[i:]...)
			break
		}
	}

	rf.persist()
	rf.updateTimeMarker()
	rf.updateCommitIndex(args.LeaderCommit)
}

// the service using Raft (e.g. a k/v server) wants to start
// agreement on the next command to be appended to Raft's log. if this
// server isn't the leader, returns false. otherwise start the
// agreement and return immediately. there is no guarantee that this
// command will ever be committed to the Raft log, since the leader
// may fail or lose an election. even if the Raft instance has been killed,
// this function should return gracefully.
//
// the first return value is the index that the command will appear at
// if it's ever committed. the second return value is the current
// term. the third return value is true if this server believes it is
// the leader.
func (rf *Raft) Start(command interface{}) (int, int, bool) {
	rf.mu.Lock()
	defer rf.mu.Unlock()

	if rf.actorType != Leader || rf.killed() {
		return -1, -1, false
	}

	rf.log = append(rf.log, Entry{rf.currentTerm, command})
	rf.persist()
	index := rf.getLogLength() // log is 1 indexed
	term := rf.currentTerm

	DPrintf("Server %d term[%d] starting agreement for command %v at index %d\n", rf.me,
		rf.currentTerm, command, index)
	rf.forceSend()

	return index, term, true
}

// the tester doesn't halt goroutines created by Raft after each test,
// but it does call the Kill() method. your code can use killed() to
// check whether Kill() has been called. the use of atomic avoids the
// need for a lock.
//
// the issue is that long-running goroutines use memory and may chew
// up CPU time, perhaps causing later tests to fail and generating
// confusing debug output. any goroutine with a long-running loop
// should call killed() to check whether it should stop.
func (rf *Raft) Kill() {
	rf.mu.Lock()
	defer rf.mu.Unlock()
	atomic.StoreInt32(&rf.dead, 1)
	DPrintf("[TEST] Server %d killed\n", rf.me)
	close(rf.commitCh)
}

func (rf *Raft) killed() bool {
	z := atomic.LoadInt32(&rf.dead)
	return z == 1
}

const HeartBeatInterval = time.Duration(150) * time.Millisecond

func (rf *Raft) updateLeaderCommit() {
	currentLogMatch := make([]int, 0)
	for i := 0; i < len(rf.peers); i++ {
		if i != rf.me {
			idx := rf.getLogIdx(rf.matchIndex[i])
			if (rf.matchIndex[i] > rf.commitIndex) && (idx >= 0 && rf.log[idx].Term == rf.currentTerm) {
				currentLogMatch = append(currentLogMatch, rf.matchIndex[i])
			}
		} else if rf.getLastLogTerm() == rf.currentTerm {
			currentLogMatch = append(currentLogMatch, rf.getLastLogIndex())
		}
	}

	DPrintf("Leader %d updating commit index (current %d) matchIndex %v currentLogMatch %v\n", rf.me, rf.commitIndex, rf.matchIndex, currentLogMatch)
	if len(currentLogMatch) < len(rf.peers)/2+1 {
		return
	}

	sort.Sort(sort.Reverse(sort.IntSlice(currentLogMatch)))
	DPrintf("Leader %d updating commit index to %d matchIndex %v currentLogMatch %v", rf.me, currentLogMatch[len(rf.peers)/2], rf.matchIndex, currentLogMatch)
	rf.updateCommitIndex(currentLogMatch[len(rf.peers)/2])
}

func (rf *Raft) findLastIndex(term int) int {
	if term <= rf.snapshot.LastIncludedTerm {
		return 0
	}

	for i := len(rf.log) - 1; i >= 0; i-- {
		if rf.log[i].Term == term {
			return i + rf.snapshot.LastIncludedIndex + 1
		}
	}

	return -1
}

func (rf *Raft) sendAppendEntry(server int, args *AppendEntriesArgs) {
	reply := AppendEntriesReply{}

	if !rf.peers[server].Call("Raft.AppendEntries", args, &reply) {
		DPrintf("Server %d append entries reply lost for %d\n", rf.me, server)
		rf.mu.Lock()
		rf.forceSendSingle(server)
		rf.mu.Unlock()
		return
	}

	DPrintf("Server %d got append entries reply from %d: %v\n", rf.me, server, reply)

	rf.mu.Lock()
	defer rf.mu.Unlock()

	if rf.killed() {
		return
	}

	if reply.Term > rf.currentTerm {
		rf.changeType(Follower, reply.Term, -1)
		return
	}

	if rf.actorType != Leader || rf.currentTerm != args.Term {
		return
	}

	if reply.Success {
		oldIdx := rf.nextIndex[server]
		rf.nextIndex[server] = max(oldIdx, args.PrevLogIndex+len(args.Entries)+1)
		if rf.nextIndex[server] > oldIdx {
			DPrintf("Server %d nextIndex[%d] updated from %d to %d, ARGS: %v\n", rf.me, server, oldIdx, rf.nextIndex[server], args)
		}
		rf.matchIndex[server] = rf.nextIndex[server] - 1
		rf.updateLeaderCommit()
		return
	}

	if rf.nextIndex[server] > 0 {
		//DPrintf("FAIL | Leader [%d] nextIndex[%d] old: %d", rf.me, server, rf.nextIndex[server])
		if reply.LastLogTerm == -1 {
			rf.nextIndex[server] = reply.LogLength
		} else if lastIdx := rf.findLastIndex(reply.LastLogTerm); lastIdx != -1 {
			rf.nextIndex[server] = lastIdx + 1
		} else {
			rf.nextIndex[server] = reply.FirstTermIndex
		}
		//DPrintf(" new: %d\n", rf.nextIndex[server])
		rf.forceSendSingle(server)
	}
}

func (rf *Raft) forceSendSingle(dest int) {
	if rf.killed() {
		return
	}
	select {
	case <-rf.peerQueues[dest].ch:
		rf.peerQueues[dest].ch <- true
		break
	default:
		rf.peerQueues[dest].ch <- true
	}
}

func (rf *Raft) forceSend() {
	for i := 0; i < len(rf.peers); i++ {
		if i != rf.me {
			rf.forceSendSingle(i)
		}
	}
}

const MaxEntriesPerAppend = 200

func (rf *Raft) sendHeartbeat(dest int) {
	if rf.actorType != Leader {
		return
	}

	logIdx := rf.getLogIdx(rf.nextIndex[dest])

	if logIdx < 0 {
		DPrintf("Server %d sending install snapshot to %d\n", rf.me, dest)
		go rf.sendInstallSnapshot(dest, &InstallSnapshotArgs{
			rf.currentTerm,
			rf.me,
			rf.snapshot,
		})
		return
	}

	entries := rf.log[logIdx:]

	if len(entries) > MaxEntriesPerAppend {
		entries = entries[:MaxEntriesPerAppend]
	}

	entriesCopy := make([]Entry, len(entries))
	copy(entriesCopy, entries)

	commitIndex := rf.commitIndex
	currentTerm := rf.currentTerm

	var prevLogTerm int

	if logIdx > 0 {
		prevLogTerm = rf.log[logIdx-1].Term
	} else {
		prevLogTerm = rf.snapshot.LastIncludedTerm
	}

	DPrintf("Server %d sending append entries to %d\n", rf.me, dest)
	go rf.sendAppendEntry(dest, &AppendEntriesArgs{
		currentTerm,
		rf.me,
		rf.nextIndex[dest] - 1,
		prevLogTerm,
		entriesCopy,
		commitIndex})
	return
}

func (rf *Raft) startHeartbeats() {
	for i := 0; i < len(rf.peers); i++ {
		if i != rf.me {
			go rf.heartbeat(i, rf.currentTerm)
		}
	}
}

func (rf *Raft) heartbeat(dest int, term int) {
	for rf.killed() == false {
		rf.mu.Lock()
		if rf.actorType != Leader || rf.currentTerm != term || rf.killed() {
			rf.mu.Unlock()
			return
		}
		now := time.Now()
		rf.sendHeartbeat(dest)
		rf.mu.Unlock()
		diff := time.Now().Sub(now)
		if diff < HeartBeatInterval {
			select {
			case <-rf.peerQueues[dest].ch:
			case <-time.After(HeartBeatInterval - diff):
			}
		}
	}
}

type InstallSnapshotArgs struct {
	Term     int
	LeaderId int
	Snapshot SnapshotData
}

type InstallSnapshotReply struct {
	Term int
}

func (rf *Raft) sendInstallSnapshot(dest int, args *InstallSnapshotArgs) {
	reply := InstallSnapshotReply{}

	if !rf.peers[dest].Call("Raft.InstallSnapshot", args, &reply) {
		DPrintf("Server %d install snapshot reply lost for %d\n", rf.me, dest)
		rf.mu.Lock()
		rf.forceSendSingle(dest)
		rf.mu.Unlock()
		return
	}

	DPrintf("Server %d got install snapshot reply from %d: %v\n", rf.me, dest, reply)

	rf.mu.Lock()
	defer rf.mu.Unlock()

	if rf.killed() {
		return
	}

	if reply.Term > rf.currentTerm {
		rf.changeType(Follower, reply.Term, -1)
		return
	}

	if rf.actorType != Leader || rf.currentTerm != args.Term {
		return
	}

	rf.nextIndex[dest] = max(rf.nextIndex[dest], rf.snapshot.LastIncludedIndex+1)
	rf.matchIndex[dest] = rf.nextIndex[dest] - 1
	rf.updateLeaderCommit()
}

func (rf *Raft) InstallSnapshot(args *InstallSnapshotArgs, reply *InstallSnapshotReply) {
	rf.mu.Lock()
	defer rf.mu.Unlock()

	if rf.killed() {
		return
	}

	reply.Term = rf.currentTerm

	if args.Term < rf.currentTerm {
		return
	}

	rf.changeType(Follower, args.Term, args.LeaderId)

	if args.Snapshot.LastIncludedIndex <= rf.snapshot.LastIncludedIndex {
		return
	}

	if args.Snapshot.LastIncludedIndex >= rf.getLastLogIndex() {
		rf.log = make([]Entry, 0)
	} else {
		if idx := rf.getLogIdx(args.Snapshot.LastIncludedIndex); rf.log[idx].Term == args.Snapshot.LastIncludedTerm {
			rf.log = rf.log[idx+1:]
		} else {
			rf.log = make([]Entry, 0)
		}
	}

	rf.snapshot = args.Snapshot

	rf.persist()
	rf.updateTimeMarker()
	if rf.commitIndex < args.Snapshot.LastIncludedIndex {
		rf.updateCommitIndex(args.Snapshot.LastIncludedIndex)
	}
}

func (rf *Raft) startElection() {
	rf.changeType(Candidate, rf.currentTerm+1, rf.me)

	votes := make([]int, 0)
	okVotes := 1
	localTerm := rf.currentTerm
	lastLogTerm := rf.getLastLogTerm()
	lastLogIndex := rf.getLastLogIndex()

	DPrintf("ELECTION ::: Server %d starting election for term %d\n", rf.me, rf.currentTerm)
	for i := 0; i < len(rf.peers); i++ {
		if i != rf.me {
			server := i
			go func() {
				for rf.killed() == false {
					reply := &RequestVoteReply{}
					ok := rf.sendRequestVote(server, &RequestVoteArgs{localTerm, rf.me, lastLogIndex, lastLogTerm}, reply)
					DPrintf("Server %d got vote reply from %d: %v\n", rf.me, server, reply)

					rf.mu.Lock()
					if rf.actorType != Candidate || rf.currentTerm != localTerm || rf.killed() {
						rf.mu.Unlock()
						break
					}

					DPrintf("Server %d got vote reply from %d: %v\n", rf.me, server, ok && reply.VoteGranted)
					if ok {

						if reply.Term > rf.currentTerm {
							rf.changeType(Follower, reply.Term, -1)
						}

						rf.updateTimeMarker()

						if !reply.VoteGranted {
							rf.mu.Unlock()
							break
						}

						okVotes++
						votes = append(votes, server)
						if okVotes >= len(rf.peers)/2+1 {
							DPrintf("ELECTED:::::: Server %d elected as leader for term %d with votes from %v, log length: %d, last log term %d\n", rf.me, rf.currentTerm, votes, lastLogIndex, lastLogTerm)
							rf.actorType = Leader
							for j := 0; j < len(rf.peers); j++ {
								rf.nextIndex[j] = rf.getLogLength()
								rf.matchIndex[j] = -1
							}
							rf.startHeartbeats()
						}

						rf.mu.Unlock()
						break
					}
					rf.mu.Unlock()
					time.Sleep(10 * time.Millisecond)
				}
			}()
		}
	}
}

func (rf *Raft) ticker() {
	for rf.killed() == false {

		rf.mu.Lock()
		if rf.actorType != Leader && rf.isTimedOut() && rf.killed() == false {
			//DPrintf("Server %d timed out\n", rf.me)
			rf.startElection()
		}
		rf.mu.Unlock()

		ms := 50
		time.Sleep(time.Duration(ms) * time.Millisecond)
	}
}

// the service or tester wants to create a Raft server. the ports
// of all the Raft servers (including this one) are in peers[]. this
// server's port is peers[me]. all the servers' peers[] arrays
// have the same order. persister is a place for this server to
// save its persistent state, and also initially holds the most
// recent saved state, if any. applyCh is a channel on which the
// tester or service expects Raft to send ApplyMsg messages.
// Make() must return quickly, so it should start goroutines
// for any long-running work.
func Make(peers []*labrpc.ClientEnd, me int,
	persister *Persister, applyCh chan ApplyMsg) *Raft {
	rf := &Raft{}
	rf.peers = peers
	rf.peerQueues = make([]PeerQueue, len(peers))
	rf.commitCh = make(chan int, 1)
	for i := 0; i < len(peers); i++ {
		rf.peerQueues[i].ch = make(chan bool, 1)
		rf.peerQueues[i].ch <- true
	}
	rf.persister = persister
	rf.me = me
	rf.applyCh = applyCh
	// Your initialization code here (3A, 3B, 3C).

	// initialize from state persisted before a crash
	rf.nextIndex = make([]int, len(peers))
	rf.matchIndex = make([]int, len(peers))

	for i := 0; i < len(peers); i++ {
		rf.matchIndex[i] = -1
	}

	rf.log = make([]Entry, 0)
	rf.commitIndex = -1

	rf.actorType = Follower
	rf.currentTerm = 0
	rf.votedFor = -1
	rf.snapshot.LastIncludedTerm = -1
	rf.snapshot.LastIncludedIndex = -1
	rf.snapshot.Data = nil

	rf.snapshot.Data = persister.ReadSnapshot()
	rf.readPersist(persister.ReadRaftState())
	rf.updateTimeMarker()

	DPrintf("Server %d starting with log length %d\n", rf.me, len(rf.log))
	// start ticker goroutine to start elections
	rf.startHeartbeats()
	go rf.ticker()
	go rf.sendCommits()
	return rf
}
