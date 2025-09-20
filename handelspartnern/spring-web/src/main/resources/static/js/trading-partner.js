// Trading Partner JavaScript Functions

function selectPartner(partnerId) {
    // Highlight selected card
    document.querySelectorAll('.partner-card').forEach(card => {
        card.classList.remove('selected');
    });

    event.currentTarget.classList.add('selected');

    // Load partner details
    loadPartnerDetails(partnerId);
}

function loadPartnerDetails(partnerId) {
    // This would typically make an AJAX request to get partner details
    // For now, we'll show a placeholder
    const detailsDiv = document.getElementById('partnerDetails');

    detailsDiv.innerHTML = `
        <div class="partner-detail-card">
            <div class="detail-header">
                <h3>Partner Details</h3>
                <div class="detail-actions">
                    <button class="btn btn-secondary">Bearbeiten</button>
                </div>
            </div>
            <div class="detail-content">
                <p>Details für Partner ID: ${partnerId}</p>
                <p>Details werden hier geladen...</p>
            </div>
        </div>
    `;
}

function showAddPartnerModal() {
    document.getElementById('addPartnerModal').style.display = 'block';
}

function hideAddPartnerModal() {
    document.getElementById('addPartnerModal').style.display = 'none';
    document.getElementById('addPartnerForm').reset();
}

// Search and Filter Functions
document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('searchInput');
    const typeFilter = document.getElementById('typeFilter');
    const statusFilter = document.getElementById('statusFilter');

    if (searchInput) {
        searchInput.addEventListener('input', filterPartners);
    }

    if (typeFilter) {
        typeFilter.addEventListener('change', filterPartners);
    }

    if (statusFilter) {
        statusFilter.addEventListener('change', filterPartners);
    }

    // Close modal when clicking outside
    window.addEventListener('click', function (event) {
        const modal = document.getElementById('addPartnerModal');
        if (event.target === modal) {
            hideAddPartnerModal();
        }
    });
});

function filterPartners() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const typeFilter = document.getElementById('typeFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;

    const cards = document.querySelectorAll('.partner-card');

    cards.forEach(card => {
        const name = card.querySelector('.partner-name').textContent.toLowerCase();
        const type = card.querySelector('.partner-type').classList.toString();
        const status = card.querySelector('.partner-status').classList.toString();

        const matchesSearch = name.includes(searchTerm);
        const matchesType = !typeFilter || type.includes(typeFilter.toLowerCase());
        const matchesStatus = !statusFilter || status.includes(statusFilter.toLowerCase());

        if (matchesSearch && matchesType && matchesStatus) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}