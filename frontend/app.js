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

    // --- DUEÑO: Vender Membresía (Con Autocompletado) ---
    const inputNombre = document.getElementById('vNombreCliente');
    const inputDoc = document.getElementById('vDocCliente');
    const autocompleteList = document.getElementById('autocomplete-list');
    let timer;

    inputNombre.addEventListener('input', function() {
        clearTimeout(timer);
        const val = this.value;
        autocompleteList.innerHTML = '';
        if (!val) return;
        
        timer = setTimeout(async () => {
            try {
                const res = await fetch(`${API_URL}/clientes/buscar?nombre=${val}`);
                const clientes = await res.json();
                
                clientes.forEach(c => {
                    const li = document.createElement('li');
                    li.textContent = `${c.nombre} (Doc: ${c.documento})`;
                    li.addEventListener('click', () => {
                        inputNombre.value = c.nombre;
                        inputDoc.value = c.documento;
                        autocompleteList.innerHTML = '';
                    });
                    autocompleteList.appendChild(li);
                });
            } catch(e) { console.error(e); }
        }, 300); // 300ms debounce
    });

    // Cerrar lista si hace clic fuera
    document.addEventListener('click', function (e) {
        if (e.target !== inputNombre) {
            autocompleteList.innerHTML = '';
        }
    });

    document.getElementById('formVenta').addEventListener('submit', async (e) => {
        e.preventDefault();
        const msg = document.getElementById('ventaMsg');
        
        const payload = {
            nombreCliente: inputNombre.value,
            documentoCliente: inputDoc.value,
            idMembresia: parseInt(document.getElementById('vIdMembresia').value),
            meses: parseInt(document.getElementById('vMeses').value),
            metodoPago: document.getElementById('vMetodoPago').value
        };

        try {
            const res = await fetch(`${API_URL}/membresias/comprar`, { 
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if(res.ok) {
                const data = await res.json();
                msg.textContent = `¡Venta registrada! Expira el ${data.fechaFin}`; 
                msg.className = 'msg success';
                
                // Limpiar form
                document.getElementById('formVenta').reset();
                cargarCaja(); // Actualiza caja
            } else { throw new Error(); }
        } catch (error) {
            msg.textContent = 'Error al registrar la venta.'; msg.className = 'msg error';
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
