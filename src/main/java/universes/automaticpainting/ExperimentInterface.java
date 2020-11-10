package universes.automaticpainting;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class ExperimentInterface {
    private volatile List<Double> loggedUPS;
    private volatile List<Double> loggedCPUUse;

    public ExperimentInterface() {
        this.loggedUPS = new ArrayList<>();
        this.loggedCPUUse = new ArrayList<>();
    }

    public void logUPS(double value) {
        loggedUPS.add(value);
        System.out.println("UPS: " + value);
        try {
            this.logCPUUse();
        }
        catch(Exception e ) {
            System.out.println("Error while acquiring CPU use: " + e.getMessage());
        }
    }

    public void logCPUUse() throws Exception {
        double cpuUse = this.getProcessCpuLoad();
        if (cpuUse != Double.NaN) {
            System.out.println("CPU: " + cpuUse);
            loggedCPUUse.add(cpuUse);
            reportLatestEfficiency();
        }
    }


    private static double getProcessCpuLoad() throws Exception {

        MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
        ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

        if (list.isEmpty()) return Double.NaN;

        Attribute att = (Attribute)list.get(0);
        Double value  = (Double)att.getValue();

        if (value == -1.0)      return Double.NaN;
        return ((int)(value * 1000) / 10.0);
    }

    private void reportLatestEfficiency() {
        System.out.println("Efficiency: " + (loggedUPS.get(loggedUPS.size()-1)/loggedCPUUse.get(loggedCPUUse.size()-1)));
    }

    public List<Double> getLoggedCPUUse() { return loggedCPUUse; }

    public List<Double> getLoggedUPS() { return loggedUPS; }
}
