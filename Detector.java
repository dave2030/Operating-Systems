package assignment1;

import java.util.*;

import assignment1.Dispatcher.Process;

public class Detector {

	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		Queue<Integer> process = new LinkedList<Integer>();
		int processes = keyboard.nextInt();
		int resources = keyboard.nextInt();
		int len = processes * 2 + 1;
		int matrix[][] = new int[len][resources];
		for (int row = 0; row < len; row++) {
			for (int col = 0; col < resources; col++) {
				matrix[row][col] = keyboard.nextInt();
			}
		}

		int trap_m[] = new int[resources];// unallocated matrix for requests
		int max_process = -1;
		boolean process_flag[] = new boolean[processes]; // flag to keep track for processes and their sum of requests
		boolean done[] = new boolean[processes]; // list to keep track of process that is finished executing
		boolean deadlock = false; // deadlock checker
		int temp_sum = 0;

		for (int i = 0; i < processes; i++)
			done[i] = false;

		for (int i = 0; i < resources; i++)
			trap_m[i] = matrix[len - 1][i];

		for (int i = 0; i < processes; i++) {// sums requests
			for (int j = 0; j < resources; j++)
				temp_sum += matrix[i][j];

			if (temp_sum == 0)
				process_flag[i] = true;
			else
				process_flag[i] = false;
			temp_sum = 0;

		}

		int count = 0;
		do {

			do {

				for (int i = 0; i < processes; i++) {
					deadlock = true;
					if (process_flag[i] == false) {
						for (int j = 0; j < resources; j++) {// checks to see if the unallocated matrix has enough to
																// fulfill requests
							if (matrix[i][j] > trap_m[j]) {
								process_flag[i] = false;
								deadlock = true;
								break;
							} else {
								process_flag[i] = true;
								deadlock = false;
							}
						}
					}

					if (process_flag[i] == true && done[i] == false) { // When the flag is true, we have enough
																		// resources to execute
						// allocate to process
						count += 1;
						System.out.print(i + 1 + " ");// prints process completed
						for (int k = 0; k < resources; k++) {
							trap_m[k] += matrix[i + processes][k];// allocate the new resources
						}

						done[i] = true;// process is done
						deadlock = false;

					}

				}
				if (count == processes)// break out since we're done executing
					break;

			} while (deadlock == false);

			if (count == processes)
				break;
			int max_allocated = 0;
			System.out.println();
			for (int i = 0; i < processes; i++) {
				if (process_flag[i] == false) {

					System.out.print(i + 1 + " ");// checks to see which processes are deadlocked

					temp_sum = 0;
					for (int j = 0; j < resources; j++)// finds max allocated resources and terminates that process
						temp_sum += matrix[i + processes][j];
					if (temp_sum > max_allocated) {
						max_process = i;
						max_allocated = temp_sum;
					}
				}
			}
			System.out.println();
			if (max_process != -1) {
				process_flag[max_process] = true;
				done[max_process] = true;

				for (int k = 0; k < resources; k++)// allocates max resource
					trap_m[k] += matrix[max_process + processes][k];

				System.out.println(max_process + 1 + " ");// prints the terminated process
				count += 1;
				deadlock = false;

			}

		} while (count < processes);// second condition for loop

	}

}
