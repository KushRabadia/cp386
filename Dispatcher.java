import java.io.BufferedReader;
//all notation comma is for readFile
//import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
	static int[] processes_table[]; // active
	static int previous_time = 0;
	static int current_time = 0;

	public static void main(String[] args) {
		class PCB {// single process and it blocks
			int id;// process_number
			String status;// "ready"/"running"/"block"/"exit"
			int status_start_time; // when does the status start

			int total_time_running = 0;
			int total_time_block = 0;
			int total_time_ready = 0;

			public PCB(int id, String status, int status_start_time) {
				this.id = id;
				this.status = status;
				this.status_start_time = status_start_time;
			}

			public void updateTime(String newStatus, int currentTime) {
				if (this.status == "ready") {
					this.total_time_ready += currentTime - this.status_start_time;
				}
				if (this.status == "running") {
					this.total_time_running += currentTime - this.status_start_time;
				}

				if (this.status == "block") {
					this.total_time_block += currentTime - this.status_start_time;
				}
				this.status = newStatus;
				this.status_start_time = currentTime;
			}

			public String toString() {
				return this.id + " " + this.total_time_running + " " + this.total_time_ready + " " + this.total_time_block;
			}
		}

		class ProcessTable {
			ArrayList<PCB> readyQueue = new ArrayList<PCB>();
			Map<Integer, ArrayList<PCB>> blockDict = new HashMap<Integer, ArrayList<PCB>>();// use dictionary {1:[2,3]}
			PCB running;
			PCB zero_process;
			ArrayList<PCB> exitList = new ArrayList<PCB>();

			int find_min() {
				int min = this.exitList.get(0).id;
				for (int i = 0; i < this.exitList.size(); i++) {
					if (this.exitList.get(i).id < min) {
						min = this.exitList.get(i).id;
					}
				}
				return min;
			}
		}
		BufferedReader reader;
		String command;
		ProcessTable table = new ProcessTable();
		table.zero_process = new PCB(0, "running", current_time);
		table.running = table.zero_process;
		try {
//			reader = new BufferedReader(new FileReader("test.txt"));
			reader = new BufferedReader(new InputStreamReader(System.in));
			String line = reader.readLine();
			// read each line in text
//			while (line !=null) {
			while (!line.equals("")) {
				String[] splitor = line.split("\\W+"); // ex:100 C 1

				current_time = Integer.parseInt(splitor[0]);
				command = splitor[1];
				switch (command) {
				case "C":// create process
					int proNum = Integer.parseInt(splitor[2]);// new process number
					PCB newProcess = new PCB(proNum, "ready", current_time);
					// if CPU free, put newProcess created direct to running
					if (table.running.id == 0) {
						table.zero_process.updateTime("ready", current_time);
						newProcess.updateTime("running", current_time);
						table.running = newProcess;
					} else {// if CPU not free, put newProcess created to readyQueue
						table.readyQueue.add(newProcess);
					}
					break;

				case "E":// exit process
					table.running.updateTime("exit", current_time);

					table.exitList.add(table.running);
					if (table.readyQueue.size() == 0) {// no process and zero process comes
						table.zero_process.updateTime("running", current_time);
						table.running = table.zero_process;
					} else {
						table.running = table.readyQueue.remove(0);
						table.running.updateTime("running", current_time);
					}
					break;

				case "R":
					int rblock = Integer.parseInt(splitor[2]);
					// create key if it is not in dictionary
					if (table.blockDict.get(rblock) == null) {
						table.blockDict.put(rblock, new ArrayList<PCB>());
					}

					table.running.updateTime("block", current_time);
					table.blockDict.get(rblock).add(table.running);

					// cpu is free now
					if (table.readyQueue.size() != 0) {
						table.running = table.readyQueue.remove(0);
						table.running.updateTime("running", current_time);
					} else {
						table.zero_process.updateTime("running", current_time);
						table.running = table.zero_process;
					}
					break;

				case "I":
					int blocked_num = Integer.parseInt(splitor[3]);
					int block = Integer.parseInt(splitor[2]);
					int i = 0;
					while (table.blockDict.get(block).get(i).id != blocked_num) {
						i++;
					}
					PCB outBlock = table.blockDict.get(block).remove(i);
					outBlock.updateTime("ready", current_time);
					if (table.running.id == 0) {
						table.running.updateTime("ready", current_time);
						outBlock.updateTime("running", current_time);
						table.running = outBlock;
					} else {
						table.readyQueue.add(outBlock);
					}
					break;

				case "T": // interrupt process
					if (table.readyQueue.size() != 0 && table.running.id != 0) {
						// swapping element in readyQueue and running
						table.running.updateTime("ready", current_time);
						table.readyQueue.add(table.running);

						table.running = table.readyQueue.remove(0);
						table.running.updateTime("running", current_time);
					}
					if (table.readyQueue.size() != 0 && table.running.id == 0) {
						// 0 process renew running time
						System.out.println(current_time + " " + table.running.status_start_time);
						table.zero_process.updateTime("ready", current_time);
						System.out.println(table.zero_process.total_time_running);
						// move process from readyQueue to running
						table.running = table.readyQueue.remove(0);
						table.running.updateTime("running", current_time);
					}
					// there are no change in else situation
					break;
				}
				previous_time = current_time;
				line = reader.readLine();
			}
			reader.close();

			System.out.println(0 + " " + table.zero_process.total_time_running);
			while(table.exitList.size()!=0) {
				int min = table.find_min();
				for (int i = 0;i < table.exitList.size(); i++) {
					if (table.exitList.get(i).id == min) {
						System.out.println(table.exitList.remove(i));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
