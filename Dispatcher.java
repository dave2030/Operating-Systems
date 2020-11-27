//Created by Shyam Dave 180332030 
package assignment1;
import java.util.*;

public class Dispatcher {

	public static class Process {
		char type;
		int num_of_runs;
		boolean is_running = false;
		int pid;
		int run_time;
		int blocked_time;
		int ready_time;
		boolean is_Ready = false;
		boolean is_Blocked = false;
		boolean is_Done;
		int time_left;

		public Process(int pid, char type) {
			this.type = type;
			this.pid = pid;
			run_time = 0;
			blocked_time = 0;
			ready_time = 0;
			if (type == 'S') {
				time_left = 200;
				num_of_runs = 1;
			} else if (type == 'C') {
				time_left = 1000;
				num_of_runs = 1;
			} else
				time_left = 50; // but we care about 150, 100, 50
			num_of_runs = 7; // 4 sets of 50ms + 3 block periods
		}

		public void decrement_counter() {
			time_left -= 50;// every iteration
			if (time_left == 0) {
				if (type == 'S' || type == 'C') {
					is_Ready = false;
					is_running = false;
					is_Done = true;
				} else {
					if (num_of_runs == 1) {
						is_Blocked = false;
						is_Ready = false;
						is_Done = true;
					} else {
						if (is_running) {
							is_Ready = false;
							is_Blocked = true;
							is_running = false;
							time_left = 1000;
						} else if (is_Blocked) {
							is_Ready = true;
							is_Blocked = false;
							time_left = 50;
						}
						num_of_runs--;
					}

				}
			}
		}

		public void inc_ready_time() {
			ready_time += 50;
		}

		public void inc_blocked_time() {
			blocked_time += 50;
		}

		public void inc_run_time() {
			run_time += 50;
		}

		public boolean is_Done() {
			return is_Done;
		}

		public boolean is_Blocked() {
			return is_Blocked;
		}

		public boolean is_Ready() {
			return is_Ready;
		}

	}

	public static class Controller {
		public int time = 0;
		public Queue<Process> ready = new LinkedList<Process>();
		public ArrayList<Process> blocked = new ArrayList<Process>();
		public ArrayList<Process> buck = new ArrayList<Process>();
		public Queue<Process> new_p = new LinkedList<Process>();
		public ArrayList<Process> done = new ArrayList<Process>();
		public Process running;
		public boolean end_simulation = false;
		Process idle = new Process(0, 'X'); // don't care about type for IDLE

		public void run(String s) {
			Process init = new Process(1, s.charAt(0));
			running = init;
			init.is_running = true;
			int string_counter = 2;
			//System.out.println(init.pid + " arrived at time " + time);
			do {
				tick();

				if (string_counter <= s.length() && time % 100 == 0) {// new process enters
					Process next = new Process(string_counter, s.charAt(string_counter - 1));
					//System.out.println(string_counter + " arrived at time " + time);
					string_counter++;
					if (running.equals(idle)) {
						running = next;
						running.is_running = true;
					} else {
						ready.add(next);
					}
				}
			} while (string_counter < s.length() || end_simulation == false);
		}

		private void tick() {
			time += 50;

			running.inc_run_time(); // "running" will only ever point to ONE PROCESS AT A TIME
			if (!running.equals(idle))
				idle.inc_ready_time();
			running.decrement_counter();

			for (Process p : ready) {
				p.inc_ready_time();
			}
			// System.out.println(" size " + blocked.size());
			for (int i = 0; i < blocked.size(); i++) {
				Process p = blocked.get(i);
				p.inc_blocked_time();
				if (i == 0) {
					p.decrement_counter();
				}
				

				if (p.is_Ready()) {
					Process k = blocked.remove(i);
					
					if (running.equals(idle)) {
						running = p;
						p.is_running = true;

					} else {
						ready.add(k);
					}
				}

			}

			if (running.is_Blocked()) {
				//System.out.println(running.pid + " added to BLOCKED at time " + time);
				running.is_running = false;
				blocked.add(running);
				if(blocked.size()>1)
					running.inc_blocked_time();
				if (ready.size() > 0) {
					running = ready.remove();
					running.is_running = true;
				} else
					running = idle;
			}
			if (running.equals(idle)) {
				if (ready.size() > 0) {
					running = ready.remove();
					running.is_running = true;
				}
			}

			if (running.is_Done()) {
				//System.out.println(running.pid + " is DONE at time " + time + " BLOCKED SIZE: " + blocked.size()+ " READY SIZE: " + ready.size());

				done.add(running);
				if (ready.size() > 0) {
					running = ready.remove();
					running.is_running = true;
				} else if (blocked.size() > 0) {
					running = idle;
				} else {
					done.add(idle);
					end_simulation = true;
				}
			}
		}

		public void print_info() {

			Process a[] = new Process[done.size()];
			for (Process p : done)
				a[p.pid] = p;
			for (int i = 0; i < a.length; i++) {
				if (i == 0)
					System.out.println(a[i].pid + " " + a[i].run_time + "  " + a[i].ready_time);
				else
					System.out.println(
							a[i].pid + " " + a[i].run_time + "  " + a[i].ready_time + "  " + a[i].blocked_time);
			}
		}
	}

	public static void main(String[] args) {
		Controller cpu = new Controller();
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter process: ");
		String c = sc.nextLine();
		cpu.run(c);
		cpu.print_info();
	}
}