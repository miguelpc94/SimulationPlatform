package App;

import gui.SimulationGraphicsInterface;
import universes.automaticpainting.AutomaticPainting;
import universes.automaticpainting.ExperimentInterface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Experiment40x16HeatMap {

    static final double RUN_END_TIME = 60000;

    static final String EXPERIMENT_NAME = "W[1-16]P[2k-80k]";
    static int numberOfWorkers[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
    static int populationSizes[] = { 2000, 4000 ,6000, 8000, 10000,
                                     12000, 14000, 16000, 18000, 20000, 22000, 24000, 26000, 28000, 30000, 32000,
                                     34000, 36000, 38000, 40000, 42000, 44000, 46000, 48000, 50000, 52000, 54000,
                                     56000, 58000, 60000, 62000, 64000, 66000, 68000, 70000, 72000, 74000, 76000,
                                     78000, 80000};

    static class HeatMap {
        int[] numberOfWorkers;
        int[] populationSizes;
        Map<Integer,Map<Integer,Double>> values;

        final char END_OF_LINE = '\n';
        final char TOKEN = '\"';
        final char SEPARATOR = ',';


        public HeatMap(int[] numberOfWorkers, int[] populationSizes) {
            this.numberOfWorkers = numberOfWorkers;
            this.populationSizes = populationSizes;
            this.values = new HashMap<>();
        }

        public void addHeatCell(int numberOfWorkers, int populationSize, double value) {
            if (values.containsKey(numberOfWorkers)) {
                values.get(numberOfWorkers).put(populationSize, value);
            }
            else {
                Map<Integer,Double> populationMap = new HashMap<>();
                populationMap.put(populationSize,value);
                values.put(numberOfWorkers,populationMap);
            }
        }

        public Double getHeatCell(int numberOfWorkers, int populationSize) {
            return values.getOrDefault(numberOfWorkers, new HashMap<>()).getOrDefault(populationSize,Double.NaN);
        }

        private String generateCSVHeader() {
            String output = ""+TOKEN+TOKEN+SEPARATOR;
            for (int numberOfWorker:numberOfWorkers) {
                output+=""+TOKEN+numberOfWorker+TOKEN+SEPARATOR;
            }
            output += END_OF_LINE;
            return output;
        }

        private String generateCSVLine(int populationSize) {
            String output = TOKEN+Integer.toString(populationSize)+TOKEN+SEPARATOR;
            for (int numberOfWorker:numberOfWorkers) {
                Double value = getHeatCell(numberOfWorker,populationSize);
                String cellValue = value == Double.NaN ? "" : Double.toString(value);
                output+=TOKEN+cellValue+TOKEN+SEPARATOR;
            }
            output += END_OF_LINE;
            return output;
        }

        private List<String> generateCSVString() {
            List<String> output = new ArrayList<>();
            output.add(generateCSVHeader());
            for (int populationSize:populationSizes) {
                output.add(generateCSVLine(populationSize));
            }

            return output;
        }

        public void writeToCSV(String filePath) {
            List<String> csv = generateCSVString();
            File file = new File(filePath);
            FileWriter csvFile = null;
            try {
                csvFile = new FileWriter(file);
                for (String line:csv) {
                    csvFile.write(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                //close resources
                try {
                    csvFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static double mean (List<Double> values)
    {
        double total = 0;

        for ( int i= 0;i < values.size(); i++)
        {
            double currentNum = values.get(i);
            total+= currentNum;
        }
        return total/values.size();
    }

    private static List<Double> calculateMeanAndStandardDeviation(List<Double> values) {
        List<Double> result = new ArrayList<>();

        double mean = mean(values);
        double temp = 0;
        for (int i = 0; i < values.size(); i++)
        {
            double val = values.get(i);
            double squrDiffToMean = Math.pow(val - mean, 2);
            temp += squrDiffToMean;
        }

        double meanOfDiffs = temp / (double) (values.size());

        result.add(mean);
        result.add(Math.sqrt(meanOfDiffs));
        return result;

    }

    public static void main(String[] args) {

        HeatMap upsMeanHeatMap = new HeatMap(numberOfWorkers, populationSizes);
        HeatMap upsDeviationHeatMap = new HeatMap(numberOfWorkers, populationSizes);
        HeatMap cpuMeanHeatMap = new HeatMap(numberOfWorkers, populationSizes);
        HeatMap cpuDeviationHeatMap = new HeatMap(numberOfWorkers, populationSizes);

        for (int numberOfWorker:numberOfWorkers) {
            for (int populationSize:populationSizes) {
                double startTime = System.currentTimeMillis();
                SimulationGraphicsInterface sgi = new SimulationGraphicsInterface(1920,1000);
                ExperimentInterface experimentInterface = new ExperimentInterface();
                AutomaticPainting automaticPainting = new AutomaticPainting();
                automaticPainting.setUniverseName("unw"+numberOfWorker+"p"+populationSize);
                automaticPainting.setNumberOfWorkers(numberOfWorker);
                automaticPainting.setPopulationSize(populationSize);
                automaticPainting.setSimulationGraphicsInterface(sgi);
                automaticPainting.setSeed(42);
                automaticPainting.setExperimentInterface(experimentInterface);
                automaticPainting.start();
                double now = System.currentTimeMillis();
                while((now-startTime)<RUN_END_TIME) now = System.currentTimeMillis();
                automaticPainting.apocalipse();
                while(automaticPainting.isAlive());
                List<Double> upsData = calculateMeanAndStandardDeviation(experimentInterface.getLoggedUPS());
                upsMeanHeatMap.addHeatCell(numberOfWorker,populationSize,upsData.get(0));
                upsDeviationHeatMap.addHeatCell(numberOfWorker,populationSize,upsData.get(1));
                List<Double> cpuData = calculateMeanAndStandardDeviation(experimentInterface.getLoggedCPUUse());
                cpuMeanHeatMap.addHeatCell(numberOfWorker,populationSize,cpuData.get(0));
                cpuDeviationHeatMap.addHeatCell(numberOfWorker,populationSize,cpuData.get(1));
                upsMeanHeatMap.writeToCSV(EXPERIMENT_NAME+"UPSMean.csv");
                upsDeviationHeatMap.writeToCSV(EXPERIMENT_NAME+"UPSDeviation.csv");
                cpuMeanHeatMap.writeToCSV(EXPERIMENT_NAME+"CPUMean.csv");
                cpuDeviationHeatMap.writeToCSV(EXPERIMENT_NAME+"CPUDeviation.csv");
            }
        }

        System.out.println("\n\n\n-=-=-=-= Experiment " + EXPERIMENT_NAME+" has ended =-=-=-=-\n\n\n");
    }
}
