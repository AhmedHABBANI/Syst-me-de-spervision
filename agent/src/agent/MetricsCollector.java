package agent;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class MetricsCollector {
    private final com.sun.management.OperatingSystemMXBean osBean;

    public MetricsCollector() {
        OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
        this.osBean = (com.sun.management.OperatingSystemMXBean) bean;
    }

    /**
     * Collecte un échantillon de métriques système
     */
    public MetricSample collect() {
        long timestamp = System.currentTimeMillis();
        double cpuUsage = getSystemCpuUsage();     // ✅ CPU SYSTEME
        double memoryUsage = getSystemMemoryUsage(); // ✅ RAM SYSTEME
        double diskUsage = getDiskUsage();

        return new MetricSample(timestamp, cpuUsage, memoryUsage, diskUsage);
    }

    /**
     * CPU SYSTEME GLOBAL (%)
     */
    private double getSystemCpuUsage() {
        double load = osBean.getCpuLoad(); // Changed from getSystemCpuLoad()
        if (load < 0) {
            return 0.0;
        }
        return load * 100.0;
    }

    /**
     * RAM SYSTEME (%)
     */
    private double getSystemMemoryUsage() {
        long total = osBean.getTotalMemorySize(); // Changed from getTotalPhysicalMemorySize()
        long free = osBean.getFreeMemorySize();   // Changed from getFreePhysicalMemorySize()
        if (total == 0) {
            return 0.0;
        }
        return ((double) (total - free) / total) * 100.0;
    }

    /**
     * DISQUE SYSTEME (%)
     */
    private double getDiskUsage() {
        File root = new File("C:\\");
        long total = root.getTotalSpace();
        long free = root.getUsableSpace();
        if (total == 0) {
            return 0.0;
        }
        return ((double) (total - free) / total) * 100.0;
    }
}