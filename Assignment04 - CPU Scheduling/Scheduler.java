import java.util.*;

// Source code
public class Scheduler {
	int timer;
	ArrayList<Process> listOfProcesses;
	ArrayList<Process> readyQueue;
	ArrayList<Process> endProcesses;
	int timeQuantum;
	int contextSwitch; // small number (less than half the timeQuantum).. ex:
						// 0.1 * timeQuantum
	Process cpu;
	int counter;

	public Scheduler(ArrayList<Process> listOfProcesses, int timeQuantum) {
		this.timeQuantum = timeQuantum;
		this.listOfProcesses = listOfProcesses;
	}

	public void fcfs() {
			timer = 0;
			contextSwitch = 0;
			cpu = null;
			readyQueue = new ArrayList<>(listOfProcesses);
			endProcesses = new ArrayList<>();

			// Sort the processes according to arrivalTime
			// If B has a later arrival time, A comes before B
			readyQueue.sort((a, b) -> a.arrivalTime - b.arrivalTime);

			for (Process p : readyQueue) {
				p.responseTime = timer - p.arrivalTime; // Set process's responseTime
				timer += p.burstTime; // Update timer according to the burstTime
				p.completionTime = timer; // Add it to endProcesses
				contextSwitch ++; // Update contextSwitch by 1
				endProcesses.add(p); // Add it to endProcesses
			}
        System.out.println("-----------------FCFS----------------");
		Utilities myUtility = new Utilities(endProcesses, contextSwitch, timer);
		myUtility.calUtilities();
	}

	public void rr() {
		timer = 0;
		contextSwitch = 0;
		cpu = null;
		readyQueue = new ArrayList<>();
		endProcesses = new ArrayList<>();

		while (!readyQueue.isEmpty() || !listOfProcesses.isEmpty() || cpu != null) {
			// add to readyQueue
			for (int i = 0; i < listOfProcesses.size(); i++) {
				if (listOfProcesses.get(i).arrivalTime == timer) {
					readyQueue.add(listOfProcesses.remove(i));
				}
			}

			// add to cpu
			if (cpu == null) {
				cpu = readyQueue.remove(0);
			}

			counter++;
			cpu.serviceTime++;

			// If this process is new to be allocated to cpu
			if (cpu.serviceTime == 1)
				cpu.responseTime = timer - cpu.arrivalTime; // Set its responseTime
			
			// If the process is completed by comparing cpu.serviceTime == cpu.burstTime
			if (cpu.serviceTime == cpu.burstTime) {
				cpu.completionTime = timer; // Set process's completionTime
				endProcesses.add(cpu); // Add it to endProcesses
				cpu = null; // Clear up cpu (cpu = null)
				contextSwitch ++; // Increase contextSwitch by 1
				counter = 0; // Reset counter to 0
			}
			// Else if the process uses up the timeQuantum
			else if (counter == timeQuantum) {
				readyQueue.add(cpu); // Put it back to readyQueue
				cpu = null; // Clear cpu (cpu = null)
				contextSwitch ++; // Increase contextSwitch by 1
				counter = 0; // Reset counter to 0
			}

			timer++; // real time : cpu time

		}
        System.out.println("-----------------RR------------------");
		Utilities myUtility = new Utilities(endProcesses, contextSwitch, timer);
		myUtility.calUtilities();

	}
} 
