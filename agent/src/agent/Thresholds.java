package agent;

public class Thresholds {

    public static final double CPU_WARN = 70.0;
    public static final double CPU_CRITICAL = 85.0;

    public static final double MEM_WARN = 75.0;
    public static final double MEM_CRITICAL = 90.0;

    public static final double DISK_WARN = 80.0;
    public static final double DISK_CRITICAL = 95.0;

    private Thresholds() {
        // utilitaire
    }
}
