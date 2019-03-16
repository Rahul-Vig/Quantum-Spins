import java.lang.*;
import java.io.*;

class Multithreading implements Runnable {
    public static int count = 1;
    int numSpins;
    double B;
    double C;
    double temp;
    public double NM_pair[][];
    public int NM = 15;

    public Multithreading(int numSpins, double B, double C, double temp) {
        this.numSpins = numSpins;
        this.B = B;
        this.C = C;
        this.temp = temp;
    }

    public double calculateMeanMPS() {
        double sum = 0;
        for (int i = 0; i < NM; i++) {
            sum += NM_pair[i][0];
        }

        return sum / NM;
    }

    public double calculateMeanPCPS() {
        double sum = 0;
        for (int i = 0; i < NM; i++) {
            sum += NM_pair[i][1];
        }

        return sum / NM;
    }

    @Override
    public void run() {
        try {
            // Displaying the thread that is running
            System.out.println("Thread " + count++ + " is running");
            // System.out.println(numSpins + " " + B + " " + C + " " + tempRange);
            NM_pair = new double[NM][2];

            if (B < 0) {
                B = -B;
            }
            Metropolis threadObject = new Metropolis(numSpins, B, C, temp);

            for (int i = 0; i < NM; i++) {
                NM_pair[i] = threadObject.metropolisAlgorithm(numSpins, B, C, temp);
            }

            try {
                File file = new File("mpsAverages.txt");
                if (file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getName(), true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(String.valueOf(calculateMeanMPS()) + "\n");
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                File pcps = new File("pcpsAverages.txt");
                if (pcps.exists()) {
                    pcps.createNewFile();
                }

                FileWriter fw1 = new FileWriter(pcps.getName(), true);
                BufferedWriter bw1 = new BufferedWriter(fw1);
                bw1.write(calculateMeanPCPS() + "\n");
                bw1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught");
        }
    }

}