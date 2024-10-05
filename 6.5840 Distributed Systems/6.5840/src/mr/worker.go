package mr

import (
	"encoding/json"
	"fmt"
	"hash/fnv"
	"io/ioutil"
	"log"
	"net/rpc"
	"os"
	"sort"
	"time"
)

type KeyValue struct {
	Key   string
	Value string
}

type ByKey []KeyValue

func (a ByKey) Len() int           { return len(a) }
func (a ByKey) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a ByKey) Less(i, j int) bool { return a[i].Key < a[j].Key }

func ihash(key string) int {
	h := fnv.New32a()
	h.Write([]byte(key))
	return int(h.Sum32() & 0x7fffffff)
}

func Worker(mapf func(string, string) []KeyValue,
	reducef func(string, []string) string) {

	for {
		task, err := requestTask()
		if err != nil {
			log.Printf("Error requesting task: %v", err)
			time.Sleep(time.Second)
			continue
		}

		switch task.TaskType {
		case MapTask:
			doMap(mapf, task)
		case ReduceTask:
			doReduce(reducef, task)
		default:
			log.Printf("Unknown task type")
			time.Sleep(time.Second)
		}
	}
}

func requestTask() (Task, error) {
	args := WorkerArgs{}
	reply := TaskReply{}
	err := call("Coordinator.AssignTask", &args, &reply)
	if err != nil {
		return Task{}, err
	}
	return reply.Task, nil
}

func doMap(mapf func(string, string) []KeyValue, task Task) {
	filename := task.FileName
	file, err := os.Open(filename)
	if err != nil {
		log.Fatalf("cannot open %v", filename)
	}
	content, err := ioutil.ReadAll(file)
	if err != nil {
		log.Fatalf("cannot read %v", filename)
	}
	file.Close()

	kva := mapf(filename, string(content))

	intermediateFiles := make([]*os.File, task.NReduce)
	encoders := make([]*json.Encoder, task.NReduce)

	for i := 0; i < task.NReduce; i++ {
		oname := fmt.Sprintf("mr-%d-%d", task.TaskIndex, i)
		ofile, _ := os.Create(oname)
		intermediateFiles[i] = ofile
		encoders[i] = json.NewEncoder(ofile)
	}

	for _, kv := range kva {
		index := ihash(kv.Key) % task.NReduce
		err := encoders[index].Encode(&kv)
		if err != nil {
			log.Fatalf("cannot encode %v", kv)
		}
	}

	for _, f := range intermediateFiles {
		f.Close()
	}

	callCompleteTask(MapTask, task.TaskIndex)
}

func doReduce(reducef func(string, []string) string, task Task) {
	intermediate := []KeyValue{}
	for i := 0; i < task.NMap; i++ {
		filename := fmt.Sprintf("mr-%d-%d", i, task.TaskIndex)
		file, err := os.Open(filename)
		if err != nil {
			log.Fatalf("cannot open %v", filename)
		}
		dec := json.NewDecoder(file)
		for {
			var kv KeyValue
			if err := dec.Decode(&kv); err != nil {
				break
			}
			intermediate = append(intermediate, kv)
		}
		file.Close()
	}

	sort.Sort(ByKey(intermediate))

	tempFile, err := ioutil.TempFile("", "mr-out-temp-")
	if err != nil {
		log.Fatalf("cannot create temp file: %v", err)
	}
	tempFilename := tempFile.Name()

	i := 0
	for i < len(intermediate) {
		j := i + 1
		for j < len(intermediate) && intermediate[j].Key == intermediate[i].Key {
			j++
		}
		values := []string{}
		for k := i; k < j; k++ {
			values = append(values, intermediate[k].Value)
		}
		output := reducef(intermediate[i].Key, values)

		fmt.Fprintf(tempFile, "%v %v\n", intermediate[i].Key, output)

		i = j
	}

	tempFile.Close()

	finalFilename := fmt.Sprintf("mr-out-%d", task.TaskIndex)
	err = os.Rename(tempFilename, finalFilename)
	if err != nil {
		log.Fatalf("cannot rename temp file: %v", err)
	}

	callCompleteTask(ReduceTask, task.TaskIndex)
}

func callCompleteTask(taskType TaskType, taskIndex int) {
	args := CompleteTaskArgs{TaskType: taskType, TaskIndex: taskIndex}
	reply := CompleteTaskReply{}
	call("Coordinator.CompleteTask", &args, &reply)
}

func call(rpcname string, args interface{}, reply interface{}) error {
	sockname := coordinatorSock()
	c, err := rpc.DialHTTP("unix", sockname)
	if err != nil {
		log.Fatal("dialing:", err)
	}
	defer c.Close()

	err = c.Call(rpcname, args, reply)
	if err == nil {
		return nil
	}

	fmt.Println(err)
	return err
}
