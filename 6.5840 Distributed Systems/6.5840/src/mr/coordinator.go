package mr

import (
	"errors"
	"log"
	"net"
	"net/http"
	"net/rpc"
	"os"
	"sync"
	"time"
)

type TaskType int

const (
	MapTask TaskType = iota
	ReduceTask
)

type Task struct {
	TaskType  TaskType
	FileName  string
	TaskIndex int
	NReduce   int
	NMap      int
	BeginTime time.Time
	WorkerID  int
}

type taskQueue struct {
	taskList []Task
	mutex    sync.Mutex
}

func (tq *taskQueue) taskQueueSize() int {
	tq.mutex.Lock()
	defer tq.mutex.Unlock()
	return len(tq.taskList)
}

func (tq *taskQueue) PushTask(task Task) {
	tq.mutex.Lock()
	defer tq.mutex.Unlock()
	tq.taskList = append(tq.taskList, task)
}

func (tq *taskQueue) PopTask() (Task, error) {
	tq.mutex.Lock()
	defer tq.mutex.Unlock()
	if len(tq.taskList) == 0 {
		return Task{}, errors.New("No Tasks available")
	}
	task := tq.taskList[0]
	tq.taskList = tq.taskList[1:]
	return task, nil
}

func (tq *taskQueue) RemoveTask(taskIndex int) {
	tq.mutex.Lock()
	defer tq.mutex.Unlock()
	for i, task := range tq.taskList {
		if task.TaskIndex == taskIndex {
			tq.taskList = append(tq.taskList[:i], tq.taskList[i+1:]...)
			break
		}
	}
}

func (t *Task) setTime() {
	t.BeginTime = time.Now()
}

func (t *Task) TimeInterval() time.Duration {
	return time.Since(t.BeginTime)
}

func (t *Task) TimeExceeded() bool {
	return t.TimeInterval() > time.Second*10
}

type Coordinator struct {
	fileNames []string

	idleMapTask     taskQueue
	assignedMapTask taskQueue

	idleReduceTask     taskQueue
	assignedReduceTask taskQueue

	nReduce   int
	nMap      int
	mu        sync.Mutex
	completed bool
}

func (c *Coordinator) AssignTask(args *WorkerArgs, reply *TaskReply) error {
	c.mu.Lock()
	defer c.mu.Unlock()

	if c.idleMapTask.taskQueueSize() > 0 {
		task, err := c.idleMapTask.PopTask()
		if err != nil {
			return err
		}
		task.setTime()
		c.assignedMapTask.PushTask(task)
		reply.Task = task
		return nil
	}

	if c.allMapTasksCompleted() && c.idleReduceTask.taskQueueSize() > 0 {
		task, err := c.idleReduceTask.PopTask()
		if err != nil {
			return err
		}
		task.setTime()
		c.assignedReduceTask.PushTask(task)
		reply.Task = task
		return nil
	}

	return errors.New("no Tasks available")
}

func (c *Coordinator) CompleteTask(args *CompleteTaskArgs, reply *CompleteTaskReply) error {
	c.mu.Lock()
	defer c.mu.Unlock()

	if args.TaskType == MapTask {
		c.assignedMapTask.RemoveTask(args.TaskIndex)
		if c.allMapTasksCompleted() && c.idleReduceTask.taskQueueSize() == 0 {
			for i := 0; i < c.nReduce; i++ {
				task := Task{
					TaskType:  ReduceTask,
					TaskIndex: i,
					NReduce:   c.nReduce,
					NMap:      c.nMap,
				}
				c.idleReduceTask.PushTask(task)
			}
		}
	} else if args.TaskType == ReduceTask {
		c.assignedReduceTask.RemoveTask(args.TaskIndex)
	}

	if c.allMapTasksCompleted() && c.allReduceTasksCompleted() {
		c.completed = true
	}
	return nil
}

func (c *Coordinator) allMapTasksCompleted() bool {
	return c.idleMapTask.taskQueueSize() == 0 && c.assignedMapTask.taskQueueSize() == 0
}

func (c *Coordinator) allReduceTasksCompleted() bool {
	return c.idleReduceTask.taskQueueSize() == 0 && c.assignedReduceTask.taskQueueSize() == 0
}

func (c *Coordinator) monitorTimeouts() {
	for {
		time.Sleep(time.Second * 5)
		c.mu.Lock()

		// Handle map tasks
		var expiredMapTasks []Task
		newAssignedMapTasks := make([]Task, 0, len(c.assignedMapTask.taskList))
		for _, task := range c.assignedMapTask.taskList {
			if task.TimeExceeded() {
				expiredMapTasks = append(expiredMapTasks, task)
			} else {
				newAssignedMapTasks = append(newAssignedMapTasks, task)
			}
		}
		c.assignedMapTask.taskList = newAssignedMapTasks
		for _, task := range expiredMapTasks {
			c.idleMapTask.PushTask(task)
		}

		// Handle reduce tasks
		var expiredReduceTasks []Task
		newAssignedReduceTasks := make([]Task, 0, len(c.assignedReduceTask.taskList))
		for _, task := range c.assignedReduceTask.taskList {
			if task.TimeExceeded() {
				expiredReduceTasks = append(expiredReduceTasks, task)
			} else {
				newAssignedReduceTasks = append(newAssignedReduceTasks, task)
			}
		}
		c.assignedReduceTask.taskList = newAssignedReduceTasks
		for _, task := range expiredReduceTasks {
			c.idleReduceTask.PushTask(task)
		}

		c.mu.Unlock()
	}
}

func (c *Coordinator) Done() bool {
	c.mu.Lock()
	defer c.mu.Unlock()
	return c.completed
}

func (c *Coordinator) server() {
	rpc.Register(c)
	rpc.HandleHTTP()
	sockname := coordinatorSock()
	os.Remove(sockname)
	l, e := net.Listen("unix", sockname)
	if e != nil {
		log.Fatal("listen error:", e)
	}
	go http.Serve(l, nil)
}

func MakeCoordinator(files []string, nReduce int) *Coordinator {
	c := Coordinator{
		fileNames: files,
		nReduce:   nReduce,
		nMap:      len(files),
		completed: false,
	}

	for i, file := range files {
		task := Task{
			TaskType:  MapTask,
			FileName:  file,
			TaskIndex: i,
			NReduce:   nReduce,
			NMap:      len(files),
		}
		c.idleMapTask.PushTask(task)
	}

	c.server()
	go c.monitorTimeouts()

	return &c
}
