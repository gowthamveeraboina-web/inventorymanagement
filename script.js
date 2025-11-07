document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("inventoryForm");
    const tableBody = document.querySelector("#inventoryTable tbody");
    const editIndex = document.getElementById("editIndex");

    let inventory = JSON.parse(localStorage.getItem("inventory")) || [];

    function renderTable() {
        tableBody.innerHTML = "";
        inventory.forEach((item, index) => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${item.productName}</td>
                <td>${item.sku}</td>
                <td>${item.category}</td>
                <td>${item.quantity}</td>
                <td>${item.supplier}</td>
                <td>${item.price}</td>
                <td>${item.location}</td>
                <td>
                    <button class="action-btn edit-btn" onclick="editItem(${index})">Edit</button>
                    <button class="action-btn delete-btn" onclick="deleteItem(${index})">Delete</button>
                </td>
            `;
            tableBody.appendChild(row);
        });
    }

    window.editItem = (index) => {
        const item = inventory[index];
        document.getElementById("productName").value = item.productName;
        document.getElementById("sku").value = item.sku;
        document.getElementById("category").value = item.category;
        document.getElementById("quantity").value = item.quantity;
        document.getElementById("supplier").value = item.supplier;
        document.getElementById("price").value = item.price;
        document.getElementById("location").value = item.location;
        editIndex.value = index;
    };

    window.deleteItem = (index) => {
        if (confirm("Are you sure you want to delete this item?")) {
            inventory.splice(index, 1);
            localStorage.setItem("inventory", JSON.stringify(inventory));
            renderTable();
        }
    };

    form.addEventListener("submit", (e) => {
        e.preventDefault();
        const newItem = {
            productName: document.getElementById("productName").value.trim(),
            sku: document.getElementById("sku").value.trim(),
            category: document.getElementById("category").value.trim(),
            quantity: parseInt(document.getElementById("quantity").value),
            supplier: document.getElementById("supplier").value.trim(),
            price: parseFloat(document.getElementById("price").value),
            location: document.getElementById("location").value.trim(),
        };

        if (inventory.some(item => item.sku === newItem.sku && editIndex.value === "")) {
            alert("Error: Duplicate SKU found. Please use a unique SKU.");
            return;
        }

        if (editIndex.value === "") {
            inventory.push(newItem);
        } else {
            inventory[editIndex.value] = newItem;
            editIndex.value = "";
        }

        localStorage.setItem("inventory", JSON.stringify(inventory));
        form.reset();
        renderTable();
    });

    renderTable();
});
