package raft

import (
	"fmt"
	"log"
	"os"
)

type LogLevel int

const (
	DEBUG LogLevel = iota
	INFO
	WARN
	ERROR
)

type RaftLogger struct {
	nodeId int
	logger *log.Logger
	level  LogLevel
	debug  bool
}

func NewRaftLogger(nodeId int, debug bool) *RaftLogger {
	return &RaftLogger{
		nodeId: nodeId,
		logger: log.New(os.Stdout, "", log.Lmicroseconds),
		level:  DEBUG,
		debug:  debug,
	}
}

func (rl *RaftLogger) printf(level LogLevel, function string, format string, v ...interface{}) {
	if !rl.debug {
		return
	}
	if level >= rl.level {
		msg := fmt.Sprintf(format, v...)
		rl.logger.Printf("[%s][Node %d][%s] %s", level.String(), rl.nodeId, function, msg)
	}
}

func (rl *RaftLogger) Debug(function string, format string, v ...interface{}) {
	if !rl.debug {
		return
	}
	rl.printf(DEBUG, function, format, v...)
}

func (rl *RaftLogger) Info(function string, format string, v ...interface{}) {
	if !rl.debug {
		return
	}
	rl.printf(INFO, function, format, v...)
}

func (rl *RaftLogger) Warn(function string, format string, v ...interface{}) {
	if !rl.debug {
		return
	}
	rl.printf(WARN, function, format, v...)
}

func (rl *RaftLogger) Error(function string, format string, v ...interface{}) {
	if !rl.debug {
		return
	}
	rl.printf(ERROR, function, format, v...)
}

func (l LogLevel) String() string {
	switch l {
	case DEBUG:
		return "DEBUG"
	case INFO:
		return "INFO"
	case WARN:
		return "WARN"
	case ERROR:
		return "ERROR"
	default:
		return "UNKNOWN"
	}
}
