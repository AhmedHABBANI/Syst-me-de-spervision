package server;

public class AgentStateMonitor implements Runnable {

    private static final long TTL_MS = 20_000;
    private final AgentRegistry registry;

    public AgentStateMonitor(AgentRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void run() {
        while (true) {
            long now = System.currentTimeMillis();

            registry.getAgents().values().forEach(agent -> {
                if (agent.getState() != AgentState.OFFLINE &&
                        now - agent.getLastSeen() > TTL_MS) {

                    registry.markOffline(agent.getAgentId());
                    System.out.println("Agent OFFLINE → " + agent.getAgentId());
                }
            });

            try {
                Thread.sleep(5_000);
            } catch (InterruptedException ignored) {}
        }
    }
}
