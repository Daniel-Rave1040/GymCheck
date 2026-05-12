const API_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {
    // --- Tabs Logic ---
    const btnTabOwner = document.getElementById('btnTabOwner');
    const btnTabClient = document.getElementById('btnTabClient');
    const viewOwner = document.getElementById('viewOwner');
    const viewClient = document.getElementById('viewClient');

    btnTabOwner.addEventListener('click', () => {
        btnTabOwner.classList.add('active'); btnTabClient.classList.remove('active');
        viewOwner.style.display = 'grid'; viewClient.style.display = 'none';
        cargarCaja(); cargarRutinas();
    });

    btnTabClient.addEventListener('click', () => {
        btnTabClient.classList.add('active'); btnTabOwner.classList.remove('active');
        viewClient.style.display = 'grid'; viewOwner.style.display = 'none';
        cargarRutinas();
    });

    // --- DUEÑO: Caja ---
    window.cargarCaja = async () => {
        try {
            const res = await fetch(`${API_URL}/pagos/caja/hoy`);
            const data = await res.json();
            document.getElementById('cajaHoy').textContent = `$${data.totalVentas || 0}`;
        } catch (e) { console.error('Error cargando caja', e); }
    };

    // --- DUEÑO: Vender Membresía ---
    document.getElementById('formVenta').addEventListener('submit', async (e) => {
        e.preventDefault();
        const idCliente = document.getElementById('vIdCliente').value;
        const idMembresia = document.getElementById('vIdMembresia').value;
        const msg = document.getElementById('ventaMsg');

        try {
            const res = await fetch(`${API_URL}/membresias/comprar?idCliente=${idCliente}&idMembresia=${idMembresia}`, { method: 'POST' });
            if(res.ok) {
                msg.textContent = '¡Venta registrada con éxito!'; msg.className = 'msg success';
                cargarCaja(); // Actualiza caja automáticamente
            } else { throw new Error(); }
        } catch (error) {
            msg.textContent = 'Error: Verifica los IDs'; msg.className = 'msg error';
        }
    });

    // --- AMBOS: Rutinas ---
    window.cargarRutinas = async () => {
        try {
            const res = await fetch(`${API_URL}/rutinas`);
            const rutinas = await res.json();
            const listAdmin = document.getElementById('listaRutinasAdmin');
            const listClient = document.getElementById('listaRutinasCliente');
            listAdmin.innerHTML = ''; listClient.innerHTML = '';

            rutinas.forEach(r => {
                // Para admin (con boton borrar)
                listAdmin.innerHTML += `
                    <li class="list-item">
                        <div><h4>${r.nombre}</h4><p>${r.descripcion}</p></div>
                        <button class="danger-btn" onclick="borrarRutina(${r.idRutina})">X</button>
                    </li>`;
                // Para cliente (solo vista)
                listClient.innerHTML += `
                    <li class="list-item">
                        <div><h4>${r.nombre}</h4><p>${r.descripcion}</p></div>
                    </li>`;
            });
        } catch (e) { console.error('Error cargando rutinas', e); }
    };

    document.getElementById('formRutina').addEventListener('submit', async (e) => {
        e.preventDefault();
        const nombre = document.getElementById('rNombre').value;
        const desc = document.getElementById('rDesc').value;
        await fetch(`${API_URL}/rutinas`, {
            method: 'POST', headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ nombre, descripcion: desc })
        });
        document.getElementById('rNombre').value = ''; document.getElementById('rDesc').value = '';
        cargarRutinas();
    });

    window.borrarRutina = async (id) => {
        await fetch(`${API_URL}/rutinas/${id}`, { method: 'DELETE' });
        cargarRutinas();
    };

    // --- CLIENTE: Consultar Membresía ---
    document.getElementById('formConsulta').addEventListener('submit', async (e) => {
        e.preventDefault();
        const idCliente = document.getElementById('cIdCliente').value;
        const resBox = document.getElementById('resultadoConsulta');
        const estado = document.getElementById('cEstado');
        const dias = document.getElementById('cDias');

        try {
            const res = await fetch(`${API_URL}/membresias/cliente/${idCliente}/dias`);
            const data = await res.json();
            resBox.style.display = 'block';
            estado.textContent = data.estado;
            estado.className = data.estado;
            dias.textContent = data.diasRestantes;
        } catch (e) { alert('Error consultando'); }
    });

    // Inits
    cargarCaja();
    cargarRutinas();
});
