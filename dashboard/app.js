console.log("app.js chargé");

window.addEventListener("DOMContentLoaded", () => {

    console.log("DOM prêt");

    // =========================
    // CONFIG
    // =========================
    const API = "http://127.0.0.1:8080";
    const AGENT_ID = "agent-1";

    // =========================
    // DOM
    // =========================
    const agentsDiv = document.getElementById("agents");
    const alertsUl = document.getElementById("alerts");

    const canvas = document.getElementById("cpuChart");
    const ctx = canvas.getContext("2d");

    canvas.width = canvas.offsetWidth;
    canvas.height = 300;

    // =========================
    // STATE FRONT
    // =========================
    const agents = {};
    const cpuHistory = [];

    // =========================
    // DRAW CPU CHART
    // =========================
    function drawCpuChart() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        if (cpuHistory.length < 2) return;

        // fond
        ctx.fillStyle = "#f9f9f9";
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        // axes
        ctx.strokeStyle = "#ccc";
        ctx.lineWidth = 1;
        ctx.beginPath();
        ctx.moveTo(40, 10);
        ctx.lineTo(40, canvas.height - 30);
        ctx.lineTo(canvas.width - 10, canvas.height - 30);
        ctx.stroke();

        const maxCpu = 100;
        const usableHeight = canvas.height - 60;
        const usableWidth = canvas.width - 60;
        const stepX = usableWidth / (cpuHistory.length - 1);

        ctx.beginPath();
        ctx.strokeStyle = "#e74c3c";
        ctx.lineWidth = 2;

        cpuHistory.forEach((v, i) => {
            const x = 40 + i * stepX;
            const y = canvas.height - 30 - (v / maxCpu) * usableHeight;

            if (i === 0) ctx.moveTo(x, y);
            else ctx.lineTo(x, y);
        });

        ctx.stroke();
    }

    // =========================
    // AGENT CARD
    // =========================
    function updateAgentCard(agent) {
        let card = agents[agent.agentId];

        if (!card) {
            card = document.createElement("div");
            card.className = "agent";
            agents[agent.agentId] = card;
            agentsDiv.appendChild(card);
        }

        card.className = `agent ${agent.state.toLowerCase()}`;
        card.innerHTML = `
            <h3>${agent.agentId}</h3>
            <strong>${agent.state}</strong>
            <small>Dernière activité : ${new Date(agent.lastSeen).toLocaleTimeString()}</small>
        `;
    }

    // =========================
    // LOAD AGENTS INIT
    // =========================
    fetch(`${API}/agents`)
        .then(r => r.json())
        .then(list => {
            console.log("Agents init :", list);
            list.forEach(updateAgentCard);
        })
        .catch(err => console.error("Erreur chargement agents", err));

    // =========================
    // SSE
    // =========================
    const evt = new EventSource(`${API}/events`);

    evt.onopen = () => console.log("SSE connecté");
    evt.onerror = e => console.error("Erreur SSE", e);

    evt.addEventListener("agent_state", e => {
        const data = JSON.parse(e.data);
        console.log("SSE agent_state :", data);

        updateAgentCard({
            agentId: data.agentId,
            state: data.state,
            lastSeen: Date.now()
        });
    });

    evt.addEventListener("alert", e => {
        const alert = JSON.parse(e.data);
        console.log("SSE alert :", alert);

        const li = document.createElement("li");
        li.textContent = `[${alert.level}] ${alert.agentId} - ${alert.metric} = ${alert.value}`;
        alertsUl.prepend(li);
    });

    // =========================
    // CPU HISTORY (RÉEL)
    // =========================
    setInterval(() => {
        fetch(`${API}/metrics/${AGENT_ID}`)
            .then(r => r.json())
            .then(m => {
                if (m.cpu !== undefined) {
                    cpuHistory.push(m.cpu);
                    if (cpuHistory.length > 30) cpuHistory.shift();
                    drawCpuChart();
                }
            })
            .catch(() => {});
    }, 2000);

});
