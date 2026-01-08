package agent;

import java.io.File;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class MetricsCollector {

    private final OperatingSystemMXBean osBean;

    public MetricsCollector() {
        this.osBean = (OperatingSystemMXBean)
                ManagementFactory.getOperatingSystemMXBean();
    }

    /**
     * Collecte un échantillon de métriques système
     */
    public MetricSample collect() {

        long timestamp = System.currentTimeMillis();

        double cpuUsage = getCpuUsage();
        double memoryUsage = getMemoryUsage();
        double diskUsage = getDiskUsage();

        return new MetricSample(timestamp, cpuUsage, memoryUsage, diskUsage);
    }

    /**
     * Utilisation CPU en pourcentage
     */
    private double getCpuUsage() {
        double load = osBean.getProcessCpuLoad();
        if (load < 0) {
            return 0.0;
        }
        return load * 100.0;
    }

    /**
     * Utilisation mémoire en pourcentage
     */
    private double getMemoryUsage() {
        long total = Runtime.getRuntime().maxMemory();
        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        if (total == 0) {
            return 0.0;
        }

        return ((double) used / total) * 100.0;
    }

    /**
     * Utilisation disque (partition racine) en pourcentage
     */
    private double getDiskUsage() {
        File root = new File("/");

        long total = root.getTotalSpace();
        long free = root.getUsableSpace();

        if (total == 0) {
            return 0.0;
        }

        return ((double) (total - free) / total) * 100.0;
    }
}
