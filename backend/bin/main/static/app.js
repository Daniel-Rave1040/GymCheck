const API_URL = '/api'; // ✅ Ruta relativa — funciona en cualquier entorno

document.addEventListener('DOMContentLoaded', () => {
    // --- Tabs Logic ---
    const btnTabOwner = document.getElementById('btnTabOwner');
    const btnTabClient = document.getElementById('btnTabClient');
    const viewOwner = document.getElementById('viewOwner');
    const viewClient = document.getElementById('viewClient');

    btnTabOwner.addEventListener('click', () => {
        btnTabOwner.classList.add('active'); btnTabClient.classList.remove('active');
        viewOwner.style.display = 'grid'; viewClient.style.display = 'none';
        cargarCaja(); cargarRutinas(); cargarPaises(); cargarSedes(); cargarTiposMembresia(); cargarEntrenadores();
    });

    btnTabClient.addEventListener('click', () => {
        btnTabClient.classList.add('active'); btnTabOwner.classList.remove('active');
        viewClient.style.display = 'grid'; viewOwner.style.display = 'none';
        cargarRutinas(); cargarEntrenadores();
    });

    // --- DUEÑO: Caja ---
    window.cargarCaja = async () => {
        try {
            const res = await fetch(`${API_URL}/pagos/caja/resumen`);
            const data = await res.json();

            const fmt = v => `$${Number(v || 0).toLocaleString('es-CO')}`;

            document.getElementById('cajaHoy').textContent = fmt(data.totalHoy);
            document.getElementById('cajaSemana').textContent = fmt(data.totalSemana);
            document.getElementById('cajaMes').textContent = fmt(data.totalMes);

            const m = data.conteoMetodos || {};
            document.getElementById('conteoEfectivo').textContent = `${m.Efectivo || 0} pagos`;
            document.getElementById('conteoTarjeta').textContent = `${m.Tarjeta || 0} pagos`;
            document.getElementById('conteoTransferencia').textContent = `${m.Transferencia || 0} pagos`;
        } catch (e) { console.error('Error cargando caja', e); }
    };

    // --- DUEÑO: Vender Membresía (Con Autocompletado) ---
    const inputNombre = document.getElementById('vNombreCliente');
    const inputDoc = document.getElementById('vDocCliente');
    const autocompleteList = document.getElementById('autocomplete-list');
    let timer;

    inputNombre.addEventListener('input', function () {
        clearTimeout(timer);
        const val = this.value;
        autocompleteList.innerHTML = '';
        if (!val) return;

        timer = setTimeout(async () => {
            try {
                // ✅ encodeURIComponent para manejar caracteres especiales en el nombre
                const res = await fetch(`${API_URL}/clientes/buscar?nombre=${encodeURIComponent(val)}`);
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
            } catch (e) { console.error(e); }
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

        // ✅ Validar que el cliente fue seleccionado del autocompletado
        if (!inputDoc.value) {
            msg.textContent = 'Por favor selecciona un cliente del autocompletado.';
            msg.className = 'msg error';
            return;
        }

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
            if (res.ok) {
                const data = await res.json();
                msg.textContent = `¡Venta registrada! Expira el ${data.fechaFin}`;
                msg.className = 'msg success';
                document.getElementById('formVenta').reset();
                cargarCaja();
            } else {
                throw new Error();
            }
        } catch (error) {
            msg.textContent = 'Error al registrar la venta.';
            msg.className = 'msg error';
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
                listAdmin.innerHTML += `
                    <li class="list-item">
                        <div><h4>${r.nombre}</h4><p>${r.descripcion}</p></div>
                        <button class="danger-btn" onclick="borrarRutina(${r.idRutina})">X</button>
                    </li>`;
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

        // ✅ Verificar que el POST fue exitoso antes de recargar
        try {
            const res = await fetch(`${API_URL}/rutinas`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nombre, descripcion: desc })
            });
            if (res.ok) {
                document.getElementById('rNombre').value = '';
                document.getElementById('rDesc').value = '';
                cargarRutinas();
            } else {
                alert('Error al guardar la rutina en el servidor.');
            }
        } catch (e) {
            console.error(e);
            alert('Error de conexión al guardar la rutina.');
        }
    });

    // ✅ Verificar respuesta antes de recargar lista
    window.borrarRutina = async (id) => {
        if (!confirm('¿Está seguro de eliminar esta rutina?')) return;
        try {
            const res = await fetch(`${API_URL}/rutinas/${id}`, { method: 'DELETE' });
            if (res.ok) cargarRutinas();
            else alert('Error al eliminar la rutina.');
        } catch (e) {
            console.error(e);
            alert('Error de conexión al eliminar la rutina.');
        }
    };

    // --- CLIENTE: Consultar Membresía por cédula ---
    document.getElementById('formConsulta').addEventListener('submit', async (e) => {
        e.preventDefault();
        // ✅ Ahora usa cédula en vez de ID interno
        const documento = document.getElementById('cDocumentoCliente').value.trim();
        const resBox = document.getElementById('resultadoConsulta');
        const estado = document.getElementById('cEstado');
        const dias = document.getElementById('cDias');

        try {
            const res = await fetch(`${API_URL}/membresias/cliente/documento/${encodeURIComponent(documento)}/dias`);
            if (!res.ok) {
                resBox.style.display = 'block';
                estado.textContent = 'NO ENCONTRADO';
                estado.className = 'VENCIDO';
                dias.textContent = '0';
                return;
            }
            const data = await res.json();
            resBox.style.display = 'block';
            estado.textContent = data.estado;
            estado.className = data.estado;
            dias.textContent = data.diasRestantes;
        } catch (e) { alert('Error consultando. Verifica tu número de cédula.'); }
    });

    // --- DUEÑO: Gestión de Sedes ---
    const selectPais = document.getElementById('sPais');
    const selectCiudad = document.getElementById('sCiudad');
    const btnToggleNuevoPais = document.getElementById('btnToggleNuevoPais');
    const btnToggleNuevaCiudad = document.getElementById('btnToggleNuevaCiudad');
    const quickAddPais = document.getElementById('quickAddPais');
    const quickAddCiudad = document.getElementById('quickAddCiudad');
    const inputNuevoPais = document.getElementById('nuevoPaisNombre');
    const inputNuevoCiudad = document.getElementById('nuevoCiudadNombre');
    const btnGuardarPais = document.getElementById('btnGuardarPais');
    const btnGuardarCiudad = document.getElementById('btnGuardarCiudad');
    const formSede = document.getElementById('formSede');
    const sedeMsg = document.getElementById('sedeMsg');
    const listaSedes = document.getElementById('listaSedesAdmin');

    window.cargarPaises = async () => {
        try {
            const res = await fetch(`${API_URL}/paises`);
            const paises = await res.json();
            const selectedVal = selectPais.value;
            selectPais.innerHTML = '<option value="" disabled selected>Selecciona País</option>';
            paises.forEach(p => {
                const opt = document.createElement('option');
                opt.value = p.idPais;
                opt.textContent = p.nombre;
                selectPais.appendChild(opt);
            });
            if (selectedVal) selectPais.value = selectedVal;
        } catch (e) { console.error('Error cargando países', e); }
    };

    window.cargarCiudades = async (idPais) => {
        try {
            const res = await fetch(`${API_URL}/ciudades/pais/${idPais}`);
            const ciudades = await res.json();
            const selectedVal = selectCiudad.value;
            selectCiudad.innerHTML = '<option value="" disabled selected>Selecciona Ciudad</option>';
            ciudades.forEach(c => {
                const opt = document.createElement('option');
                opt.value = c.idCiudad;
                opt.textContent = c.nombre;
                selectCiudad.appendChild(opt);
            });
            selectCiudad.disabled = false;
            btnToggleNuevaCiudad.disabled = false;
            if (selectedVal) selectCiudad.value = selectedVal;
        } catch (e) { console.error('Error cargando ciudades', e); }
    };

    window.cargarSedes = async () => {
        try {
            const res = await fetch(`${API_URL}/sedes`);
            const sedes = await res.json();
            listaSedes.innerHTML = '';

            const selectSedeEntrenador = document.getElementById('eIdSede');
            if (selectSedeEntrenador) {
                selectSedeEntrenador.innerHTML = '<option value="" disabled selected>Selecciona Sede</option>';
                sedes.forEach(s => {
                    const opt = document.createElement('option');
                    opt.value = s.idSede;
                    opt.textContent = s.nombre;
                    selectSedeEntrenador.appendChild(opt);
                });
            }

            sedes.forEach(s => {
                listaSedes.innerHTML += `
                    <li class="list-item">
                        <div>
                            <h4>${s.nombre}</h4>
                            <p>${s.direccion} - ${s.ciudad ? s.ciudad.nombre : ''} (${s.ciudad && s.ciudad.pais ? s.ciudad.pais.nombre : ''})</p>
                        </div>
                        <button class="danger-btn" onclick="borrarSede(${s.idSede})">X</button>
                    </li>`;
            });
        } catch (e) { console.error('Error cargando sedes', e); }
    };

    window.cargarTiposMembresia = async () => {
        try {
            const res = await fetch(`${API_URL}/membresias`);
            const membresias = await res.json();
            const selectMembresia = document.getElementById('vIdMembresia');
            selectMembresia.innerHTML = '<option value="" disabled selected>Selecciona Membresía</option>';
            membresias.forEach(m => {
                const opt = document.createElement('option');
                opt.value = m.idMembresia;
                opt.textContent = `${m.tipo} ($${m.precio.toLocaleString('es-CO')})`;
                selectMembresia.appendChild(opt);
            });
        } catch (e) {
            console.error('Error cargando tipos de membresía', e);
            const selectMembresia = document.getElementById('vIdMembresia');
            selectMembresia.innerHTML = '<option value="" disabled selected>Error al cargar membresías</option>';
        }
    };

    selectPais.addEventListener('change', () => {
        const idPais = selectPais.value;
        if (idPais) {
            cargarCiudades(idPais);
        } else {
            selectCiudad.innerHTML = '<option value="" disabled selected>Selecciona Ciudad (Elige País primero)</option>';
            selectCiudad.disabled = true;
            btnToggleNuevaCiudad.disabled = true;
        }
    });

    btnToggleNuevoPais.addEventListener('click', () => {
        quickAddPais.style.display = quickAddPais.style.display === 'none' ? 'flex' : 'none';
        inputNuevoPais.focus();
    });

    btnToggleNuevaCiudad.addEventListener('click', () => {
        if (!selectPais.value) return;
        quickAddCiudad.style.display = quickAddCiudad.style.display === 'none' ? 'flex' : 'none';
        inputNuevoCiudad.focus();
    });

    btnGuardarPais.addEventListener('click', async () => {
        const nombre = inputNuevoPais.value.trim();
        if (!nombre) return;
        try {
            const res = await fetch(`${API_URL}/paises`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nombre })
            });
            if (res.ok) {
                const nuevoPais = await res.json();
                inputNuevoPais.value = '';
                quickAddPais.style.display = 'none';
                await cargarPaises();
                selectPais.value = nuevoPais.idPais;
                cargarCiudades(nuevoPais.idPais);
                sedeMsg.textContent = 'País guardado correctamente.';
                sedeMsg.className = 'msg success';
            } else {
                sedeMsg.textContent = 'Error al guardar el país en el servidor.';
                sedeMsg.className = 'msg error';
            }
        } catch (e) {
            console.error(e);
            sedeMsg.textContent = 'Error de conexión al guardar el país.';
            sedeMsg.className = 'msg error';
        }
    });

    btnGuardarCiudad.addEventListener('click', async () => {
        const nombre = inputNuevoCiudad.value.trim();
        const idPais = selectPais.value;
        if (!nombre || !idPais) return;
        try {
            const res = await fetch(`${API_URL}/ciudades`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nombre, pais: { idPais: parseInt(idPais) } })
            });
            if (res.ok) {
                const nuevaCiudad = await res.json();
                inputNuevoCiudad.value = '';
                quickAddCiudad.style.display = 'none';
                await cargarCiudades(idPais);
                selectCiudad.value = nuevaCiudad.idCiudad;
                sedeMsg.textContent = 'Ciudad guardada correctamente.';
                sedeMsg.className = 'msg success';
            } else {
                sedeMsg.textContent = 'Error al guardar la ciudad en el servidor.';
                sedeMsg.className = 'msg error';
            }
        } catch (e) {
            console.error(e);
            sedeMsg.textContent = 'Error de conexión al guardar la ciudad.';
            sedeMsg.className = 'msg error';
        }
    });

    formSede.addEventListener('submit', async (e) => {
        e.preventDefault();
        const nombre = document.getElementById('sNombre').value;
        const direccion = document.getElementById('sDireccion').value;
        const idCiudad = selectCiudad.value;

        if (!nombre || !direccion || !idCiudad) return;

        try {
            const res = await fetch(`${API_URL}/sedes`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    nombre,
                    direccion,
                    ciudad: { idCiudad: parseInt(idCiudad) }
                })
            });
            if (res.ok) {
                sedeMsg.textContent = '¡Sede registrada exitosamente!';
                sedeMsg.className = 'msg success';
                formSede.reset();
                selectCiudad.disabled = true;
                btnToggleNuevaCiudad.disabled = true;
                cargarSedes();
            } else {
                throw new Error();
            }
        } catch (error) {
            sedeMsg.textContent = 'Error al registrar la sede.';
            sedeMsg.className = 'msg error';
        }
    });

    // ✅ Verificar respuesta antes de recargar lista
    window.borrarSede = async (id) => {
        if (!confirm('¿Está seguro de eliminar esta sede?')) return;
        try {
            const res = await fetch(`${API_URL}/sedes/${id}`, { method: 'DELETE' });
            if (res.ok) cargarSedes();
            else alert('Error al eliminar la sede.');
        } catch (e) {
            console.error(e);
            alert('Error de conexión al eliminar la sede.');
        }
    };

    // --- DUEÑO: Gestión de Entrenadores ---
    window.cargarEntrenadores = async () => {
        try {
            const res = await fetch(`${API_URL}/entrenadores`);
            const entrenadores = await res.json();
            const listAdmin = document.getElementById('listaEntrenadoresAdmin');
            const listClient = document.getElementById('listaEntrenadoresCliente');

            if (listAdmin) listAdmin.innerHTML = '';
            if (listClient) listClient.innerHTML = '';

            entrenadores.forEach(ent => {
                const sedeNombre = ent.sede ? ent.sede.nombre : 'Sin Sede';
                if (listAdmin) {
                    listAdmin.innerHTML += `
                        <li class="list-item">
                            <div>
                                <h4>${ent.nombre}</h4>
                                <p>Especialidad: ${ent.especialidad} | Sede: ${sedeNombre}</p>
                            </div>
                            <button class="danger-btn" onclick="borrarEntrenador(${ent.idEntrenador})">X</button>
                        </li>`;
                }
                if (listClient) {
                    listClient.innerHTML += `
                        <li class="list-item">
                            <div>
                                <h4>${ent.nombre}</h4>
                                <p>Especialidad: ${ent.especialidad} | Sede: ${sedeNombre}</p>
                            </div>
                        </li>`;
                }
            });
        } catch (e) { console.error('Error cargando entrenadores', e); }
    };

    document.getElementById('formEntrenador').addEventListener('submit', async (e) => {
        e.preventDefault();
        const msg = document.getElementById('entrenadorMsg');
        const nombre = document.getElementById('eNombre').value.trim();
        const especialidad = document.getElementById('eEspecialidad').value.trim();
        const idSede = parseInt(document.getElementById('eIdSede').value);

        if (!nombre || !especialidad || !idSede) return;

        try {
            const res = await fetch(`${API_URL}/entrenadores`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nombre, especialidad, sede: { idSede } })
            });
            if (res.ok) {
                msg.textContent = '¡Entrenador contratado exitosamente!';
                msg.className = 'msg success';
                document.getElementById('formEntrenador').reset();
                cargarEntrenadores();
            } else {
                throw new Error();
            }
        } catch (error) {
            msg.textContent = 'Error al contratar al entrenador.';
            msg.className = 'msg error';
        }
    });

    // ✅ Verificar respuesta antes de recargar lista
    window.borrarEntrenador = async (id) => {
        if (!confirm('¿Está seguro de despachar/eliminar a este entrenador?')) return;
        try {
            const res = await fetch(`${API_URL}/entrenadores/${id}`, { method: 'DELETE' });
            if (res.ok) cargarEntrenadores();
            else alert('Error al eliminar el entrenador.');
        } catch (e) {
            console.error(e);
            alert('Error de conexión al eliminar el entrenador.');
        }
    };

    // Inits
    cargarCaja();
    cargarRutinas();
    cargarPaises();
    cargarSedes();
    cargarTiposMembresia();
    cargarEntrenadores();
});