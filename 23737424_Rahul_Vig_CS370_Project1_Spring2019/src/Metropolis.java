
//Rahul Vig
//Project 1

import java.util.*;
import java.lang.*;
import java.util.concurrent.ThreadLocalRandom;

public class Metropolis {
	private int n;
	private double B;
	private double C;
	private double temp;
	private int currConfig[];
	private int newConfig[];
	private double mps;
	private double pcps;

	public Metropolis(int n, double B, double C, double temp) {
		this.n = n;
		this.B = B;
		this.C = C;
		this.temp = temp;
		this.currConfig = new int[n];
		if (B > 0) {
			Arrays.fill(currConfig, 1);
		} else {
			Arrays.fill(currConfig, -1);
		}

	}

	public static double MagnetizationPerSpin(int sigma[], int n) {

		double sum = 0;

		for (int i = 0; i < n; i++) {
			sum += sigma[i];
		}
		return sum / n;

	}

	public static double PairCorrelationPerSpin(int sigma[], int n) {
		double correlation = sigma[0] * sigma[n - 1];

		for (int i = 1; i < n; i++) {
			correlation += sigma[i - 1] * sigma[i];
		}

		return correlation / n;
	}

	public static double EnergyOfConfiguration(double B, double C, int sigma[], int n) {
		double energy = 0;
		int j;
		for (int i = 0; i < n; i++) {
			j = i + 1;

			if (j >= n) {
				j = j % n;
			}

			energy += ((B * sigma[i]) + (C * sigma[i] * sigma[j]));

		}
		if ((energy * -1) == (-0.0)) {
			return 0;
		} else {
			return (energy * -1);
		}
	}

	public static double EnergyOfConfigurationDifference(double B, double C, int sigma1[], int sigma2[], int n) {
		double energy = 0;
		int j;
		for (int i = 0; i < n; i++) {
			j = i + 1;

			if (j >= n) {
				j = j % n;
			}

			energy -= (((B * sigma2[i]) + (C * sigma2[i] * sigma2[j]))
					- ((B * sigma1[i]) + (C * sigma1[i] * sigma1[j])));

		}

		if ((energy) == (-0.0)) {
			return 0;
		} else {
			return energy;
		}

	}

	public double[] metropolisAlgorithm(int n, double B, double C, double T) {
		// Using the metropolis algorithm we are going to compute <m> and <cp>
		// We must first establish an initial configuration, we will call this our
		// currConfig and will be an array of ints of with set {1, -1}
		// The constructor already fills the currConfig array to all 1's
		// so we just need to check the case that C < 0
		if (C < 0) {
			for (int i = 0; i < n; i++) {
				if (i % 2 == 1) {
					currConfig[i] = -1;
				}
			}
		}

		this.newConfig = currConfig.clone();

		for (int k = 0; k < n * 20; k++) {

			// System.out.println();
			// Randomly generate an index to change a random spin in the configuration
			int randomIndex = (int) (ThreadLocalRandom.current().nextDouble(0, n));
			// Debugging Purposes
			// System.out.println("Random Index: " + randomIndex);
			// Flip the spin at the randomly generated index to the opposite of what it is
			// By doing this we can create a new configuration of spins
			this.newConfig[randomIndex] = (this.newConfig[randomIndex] == 1) ? -1 : 1;

			// Now that we've created a new configuration, the energy difference between the
			// two
			// configurations is stored in the variable energyDifference
			double energyDifference = EnergyOfConfigurationDifference(B, C, currConfig, newConfig, n);
			// System.out.println("Energy Difference: " + energyDifference);

			// Now we make the decision to use our new configuration or not
			if (energyDifference <= 0) {
				currConfig = newConfig.clone();
				// System.out.println("Update accepted");
			} else {
				double p = Math.exp((-energyDifference) / T);
				// System.out.println(p);
				double r = ThreadLocalRandom.current().nextDouble(0, 1);
				// System.out.println(r);
				if (r < p) {
					currConfig = newConfig.clone();
					// System.out.println("Update accepted");
				} else {
					// System.out.println("Update rejected");
					newConfig = currConfig.clone();
				}
			}

			// for (int i = 0; i < n; i++) {
			// System.out.print(currConfig[i] + " ");
			// }
			// System.out.println();
			// for (int i = 0; i < n; i++) {
			// System.out.print(newConfig[i] + " ");
			// }
			// System.out.println();
		}

		this.mps = MagnetizationPerSpin(currConfig, n);
		this.pcps = PairCorrelationPerSpin(currConfig, n);
		double averages[] = new double[2];
		// System.out.println("MPS Average: " + this.mps);
		// System.out.println("PCPS Average: " + this.pcps);
		averages[0] = this.mps;
		averages[1] = this.pcps;
		return averages;

	}
}
