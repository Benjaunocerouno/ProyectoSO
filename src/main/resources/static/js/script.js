// src/main/resources/static/js/script.js

document.addEventListener('DOMContentLoaded', function() {
    const canvas = document.getElementById('graficaVistas');
    
    // Verificamos que los datos no sean null y el canvas exista
    if (canvas && typeof dataLibrosReporte !== 'undefined' && dataLibrosReporte.length > 0) {
        const labels = dataLibrosReporte.map(l => l.titulo);
        const dataVistas = dataLibrosReporte.map(l => l.vistas);

        new Chart(canvas.getContext('2d'), {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Lecturas totales',
                    data: dataVistas,
                    backgroundColor: '#0d6efd',
                    borderRadius: 5
                }]
            },
            options: { responsive: true, maintainAspectRatio: false }
        });
    }
});

function confirmarEliminacion(id) {
    if (confirm('¿Estás seguro de que deseas retirar este libro? Dejará de ser visible en el catálogo.')) {
        // Redirige al controlador
        window.location.href = '/libros/eliminar/' + id;
    }
}