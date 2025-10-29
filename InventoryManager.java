import java.time.LocalDateTime;
import java.util.*;

class Item {
    private String name;
    private String category;
    private int quantity;
    private String supplier;
    private double price;
    private String location;

    public Item(String name, String category, int quantity, String supplier, double price, String location) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.supplier = supplier;
        this.price = price;
        this.location = location;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public String toString() {
        return String.format(
            "{\"name\":\"%s\", \"category\":\"%s\", \"quantity\":%d, \"supplier\":\"%s\", \"price\":%.2f, \"location\":\"%s\"}",
            name, category, quantity, supplier, price, location
        );
    }
}

class AuditEntry {
    private String action;
    private String sku;
    private LocalDateTime timestamp;
    private String details;

    public AuditEntry(String action, String sku, String details) {
        this.action = action;
        this.sku = sku;
        this.timestamp = LocalDateTime.now();
        this.details = details;
    }

    @Override
    public String toString() {
        return String.format(
            "{\"action\":\"%s\", \"sku\":\"%s\", \"timestamp\":\"%s\", \"details\":%s}",
            action, sku, timestamp, details
        );
    }
}

public class InventoryManager {
    private Map<String, Item> inventory = new HashMap<>();
    private List<AuditEntry> auditTrail = new ArrayList<>();
    private List<Item> deletedItems = new ArrayList<>();
    private Map<String, String> users = new HashMap<>();
    private Scanner scanner;

    public InventoryManager(Scanner scanner) {
        this.scanner = scanner;
        users.put("admin", "password123");
    }

    public boolean authenticate(String username, String password) {
        return users.getOrDefault(username, "").equals(password);
    }

    public void addItem(String sku, String name, String category, int quantity, String supplier, double price, String location) throws Exception {
        if (inventory.containsKey(sku))
            throw new Exception("Duplicate SKU: " + sku + " already exists.");

        if (name.isEmpty() || category.isEmpty() || supplier.isEmpty() || location.isEmpty())
            throw new Exception("All text fields are required.");

        if (quantity < 0 || price < 0)
            throw new Exception("Quantity and price must be non-negative.");

        Item item = new Item(name, category, quantity, supplier, price, location);
        inventory.put(sku, item);
        auditTrail.add(new AuditEntry("add", sku, item.toString()));
        System.out.println("✅ Item " + sku + " added successfully.");
    }

    public void updateItem(String sku, Map<String, Object> updates) throws Exception {
        if (!inventory.containsKey(sku))
            throw new Exception("Item " + sku + " not found.");

        Item item = inventory.get(sku);
        String oldDetails = item.toString();

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            switch (key) {
                case "name": item.setName((String) value); break;
                case "category": item.setCategory((String) value); break;
                case "quantity":
                    int qty = (Integer) value;
                    if (qty < 0) throw new Exception("Quantity must be non-negative.");
                    item.setQuantity(qty);
                    break;
                case "supplier": item.setSupplier((String) value); break;
                case "price":
                    double pr = (Double) value;
                    if (pr < 0) throw new Exception("Price must be non-negative.");
                    item.setPrice(pr);
                    break;
                case "location": item.setLocation((String) value); break;
                default: throw new Exception("Invalid field: " + key);
            }
        }

        auditTrail.add(new AuditEntry("update", sku, "{\"old\":" + oldDetails + ", \"new\":" + item.toString() + "}"));
        System.out.println("✅ Item " + sku + " updated successfully.");
    }

    public void deleteItem(String sku) throws Exception {
        if (!inventory.containsKey(sku))
            throw new Exception("Item " + sku + " not found.");

        System.out.print("Are you sure you want to delete " + sku + "? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (!response.equals("yes")) {
            System.out.println("❌ Deletion cancelled.");
            return;
        }

        Item deletedItem = inventory.remove(sku);
        deletedItems.add(deletedItem);
        auditTrail.add(new AuditEntry("delete", sku, deletedItem.toString()));
        System.out.println("🗑️ Item " + sku + " deleted successfully.");
    }

    public void viewInventory() {
        if (inventory.isEmpty()) {
            System.out.println("No items in inventory.");
            return;
        }
        inventory.forEach((sku, item) -> System.out.println(sku + ": " + item));
    }

    public void viewAuditTrail() {
        if (auditTrail.isEmpty()) {
            System.out.println("No audit records found.");
            return;
        }
        auditTrail.forEach(System.out::println);
    }

    public void viewDeletedItems() {
        if (deletedItems.isEmpty()) {
            System.out.println("No deleted items.");
            return;
        }
        deletedItems.forEach(System.out::println);
    }

    public void syncWithExternalSystem(String systemName) {
        System.out.println("Syncing with " + systemName + "... (Simulated)");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InventoryManager manager = new InventoryManager(scanner);

        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (!manager.authenticate(username, password)) {
            System.out.println("Authentication failed.");
            return;
        }

        while (true) {
            System.out.println("\n=== Inventory Management System ===");
            System.out.println("1. Add Item");
            System.out.println("2. Update Item");
            System.out.println("3. Delete Item");
            System.out.println("4. View Inventory");
            System.out.println("5. View Audit Trail");
            System.out.println("6. View Deleted Items");
            System.out.println("7. Sync with External System");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        System.out.print("SKU: ");
                        String sku = scanner.nextLine();
                        System.out.print("Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Category: ");
                        String category = scanner.nextLine();
                        System.out.print("Quantity: ");
                        int qty = Integer.parseInt(scanner.nextLine());
                        System.out.print("Supplier: ");
                        String supplier = scanner.nextLine();
                        System.out.print("Price: ");
                        double price = Double.parseDouble(scanner.nextLine());
                        System.out.print("Location: ");
                        String location = scanner.nextLine();
                        manager.addItem(sku, name, category, qty, supplier, price, location);
                        break;
                    case 2:
                        System.out.print("SKU to update: ");
                        String skuUpdate = scanner.nextLine();
                        Map<String, Object> updates = new HashMap<>();
                        System.out.print("Update name? (y/n): ");
                        if (scanner.nextLine().equalsIgnoreCase("y")) {
                            System.out.print("New name: ");
                            updates.put("name", scanner.nextLine());
                        }
                        System.out.print("Update category? (y/n): ");
                        if (scanner.nextLine().equalsIgnoreCase("y")) {
                            System.out.print("New category: ");
                            updates.put("category", scanner.nextLine());
                        }
                        System.out.print("Update quantity? (y/n): ");
                        if (scanner.nextLine().equalsIgnoreCase("y")) {
                            System.out.print("New quantity: ");
                            updates.put("quantity", Integer.parseInt(scanner.nextLine()));
                        }
                        System.out.print("Update supplier? (y/n): ");
                        if (scanner.nextLine().equalsIgnoreCase("y")) {
                            System.out.print("New supplier: ");
                            updates.put("supplier", scanner.nextLine());
                        }
                        System.out.print("Update price? (y/n): ");
                        if (scanner.nextLine().equalsIgnoreCase("y")) {
                            System.out.print("New price: ");
                            updates.put("price", Double.parseDouble(scanner.nextLine()));
                        }
                        System.out.print("Update location? (y/n): ");
                        if (scanner.nextLine().equalsIgnoreCase("y")) {
                            System.out.print("New location: ");
                            updates.put("location", scanner.nextLine());
                        }
                        manager.updateItem(skuUpdate, updates);
                        break;
                    case 3:
                        System.out.print("SKU to delete: ");
                        String skuDel = scanner.nextLine();
                        manager.deleteItem(skuDel);
                        break;
                    case 4:
                        manager.viewInventory();
                        break;
                    case 5:
                        manager.viewAuditTrail();
                        break;
                    case 6:
                        manager.viewDeletedItems();
                        break;
                    case 7:
                        System.out.print("System name: ");
                        manager.syncWithExternalSystem(scanner.nextLine());
                        break;
                    case 8:
                        System.out.println("Exiting system...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Error: " + e.getMessage());
            }
        }
    }
}
