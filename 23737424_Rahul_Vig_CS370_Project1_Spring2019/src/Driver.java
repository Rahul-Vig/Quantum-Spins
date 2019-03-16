import java.util.*;
import java.io.*;

public class Driver {

	public static void main(String[] args) {
		// Once a global mean for MPS and global mean for PCPS is found, they are stored
		// in mRange and cRange respectively
		// The values are stored in the array for each iteration of the temperature.
		double mRange[] = new double[10]; // This will display the temp and <M> values to plot 10 points
		double cRange[] = new double[10]; // This will display the temp and <C> values to plot 10 points.
		Scanner input = new Scanner(System.in);
		System.out.print("Enter the number of spins in a configuration: ");
		int numSpins = input.nextInt();
		System.out.print("Enter a value for B: ");
		double B = input.nextDouble();
		System.out.print("Enter a value for C: ");
		double C = input.nextDouble();
		// System.out.print("Enter a value for the temperature range: ");
		// double temp = input.nextDouble();
		input.close();
		int x = 0; // this keeps track of the index for mRange and cRange
		for (double temp = 0.1; temp <= 1.9; temp += 0.2) { // this for loop controls the temp increment per iteration.
			// Right now the loop is going from 0.1->1.9 plotting 10 points. This may be
			// modified
			try {
				File mps = new File("mpsAverages.txt");
				if (!mps.exists()) {
					mps.createNewFile();
				}

				FileWriter fw = new FileWriter(mps.getName(), false);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("");
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				File pcps = new File("pcpsAverages.txt");
				if (!pcps.exists()) {
					pcps.createNewFile();
				}

				FileWriter fw1 = new FileWriter(pcps.getName(), false);
				BufferedWriter bw1 = new BufferedWriter(fw1);
				bw1.write("");
				bw1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			int numThreads = 1000;
			Runnable r[] = new Runnable[numThreads];
			Thread threads[] = new Thread[numThreads];
			// number of threads
			for (int i = 0; i < numThreads; i++) {
				r[i] = new Multithreading(numSpins, B, C, temp);
				threads[i] = new Thread(r[i]);
				threads[i].start();
			}

			for (int i = 0; i < numThreads; i++) {
				try {
					threads[i].join();
				} catch (Exception e) {
				}
			}

			System.out.println("End of Thread Execution");

			double m[] = new double[numThreads];
			Scanner scan;
			File fr = new File("mpsAverages.txt");
			try {
				scan = new Scanner(fr);
				int i = 0;
				while (scan.hasNextDouble() && i < numThreads) {
					m[i] = scan.nextDouble();
					i++;
				}

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			System.out.print("The Global Mean for <m> is: ");
			double globalMeanMPS = 0;
			for (int i = 0; i < numThreads; i++) {
				globalMeanMPS += m[i];
			}
			globalMeanMPS = globalMeanMPS / numThreads;
			mRange[x] = globalMeanMPS;
			System.out.println(globalMeanMPS);

			double c[] = new double[numThreads];
			Scanner scan1;
			File fr1 = new File("pcpsAverages.txt");
			try {
				scan1 = new Scanner(fr1);
				int i = 0;
				while (scan1.hasNextDouble() && i < numThreads) {
					c[i] = scan1.nextDouble();
					i++;
				}

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			System.out.print("The Global Mean for <c> is: ");
			double globalMeanPCPS = 0;
			for (int i = 0; i < numThreads; i++) {
				globalMeanPCPS += c[i];
			}
			globalMeanPCPS = globalMeanPCPS / numThreads;
			cRange[x] = globalMeanPCPS;

			System.out.println(globalMeanPCPS);

			if (B == 0) {
				double cpTheory = (Math.exp(C / temp) - Math.exp(-C / temp))
						/ (Math.exp(C / temp) + Math.exp(-C / temp));
				System.out.println("<c> Theory: " + cpTheory);

				double relativeError;
				double relativeErrorSum = 0;
				for (int i = 0; i < numThreads; i++) {
					relativeErrorSum += ((c[i] - cpTheory) / cpTheory);
				}

				relativeError = relativeErrorSum / numThreads;
				System.out.println("Relative Error: " + relativeError);

				if (Math.abs(relativeError) <= 0.02) {
					System.out.println("Optimal Relative Error Found");
				} else {
					System.out.println("Unoptimal Relative Error Found, Please Change NM or NF");
				}

				double variance;
				double varianceSum = 0;
				for (int i = 0; i < numThreads; i++) {
					varianceSum += Math.pow((((c[i] - cpTheory) / cpTheory) - relativeError), 2);
				}

				variance = varianceSum / numThreads;

				System.out.println("Variance: " + variance);

				if (Math.abs(variance) <= 0.02) {
					System.out.println("Optimal Variance Found");
				} else {
					System.out.println("Unoptimal Variance Found, Please Change NM or NF");
				}
			}

			if (C == 0) {
				double mTheory = (Math.exp(B / temp) - Math.exp(-B / temp))
						/ (Math.exp(B / temp) + Math.exp(-B / temp));
				System.out.println("<m> Theory: " + mTheory);
				double cpTheory = mTheory * mTheory;

				double relativeError;
				double relativeErrorSum = 0;
				for (int i = 0; i < numThreads; i++) {
					relativeErrorSum += ((c[i] - cpTheory) / cpTheory);
				}

				relativeError = relativeErrorSum / numThreads;
				System.out.println("Relative Error: " + relativeError);

				if (Math.abs(relativeError) <= 0.02) {
					System.out.println("Optimal Relative Error Found");
				} else {
					System.out.println("Unoptimal Relative Error Found, Please Change NM or NF");
				}

				double variance;
				double varianceSum = 0;
				for (int i = 0; i < numThreads; i++) {
					varianceSum += Math.pow((((c[i] - cpTheory) / cpTheory) - relativeError), 2);
				}

				variance = varianceSum / numThreads;

				System.out.println("Variance: " + variance);

				if (Math.abs(variance) <= 0.02) {
					System.out.println("Optimal Variance Found");
				} else {
					System.out.println("Unoptimal Variance Found, Please Change NM or NF");
				}
			}

			x++;

		}

		System.out.println("temp\t<M>\t\t<CP>");
		double t = 0.1;
		for (int i = 0; i < 10; i++) {
			System.out.printf("%.2f" + "\t" + "%.8f" + "\t" + "%.8f\n", t, mRange[i], cRange[i]);
			t += 0.2;
		}

	}
}
