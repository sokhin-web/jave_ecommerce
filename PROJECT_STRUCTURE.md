# JavaFX Admin Panel - Project Structure

## Complete File Structure

```
javafx-admin-panel/
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── adminpanel/
│                   ├── DashboardApp.java      # Main application entry point
│                   ├── DataStore.java         # In-memory data storage (Singleton)
│                   ├── User.java              # User model class
│                   ├── Category.java          # Category model class
│                   ├── Product.java           # Product model class
│                   ├── Cart.java              # Cart model class
│                   └── CartItem.java          # CartItem model class
│
├── pom.xml                                    # Maven project configuration
├── run.sh                                     # Shell script to compile and run
└── README.md                                  # Project documentation

```

## Class Descriptions

### 1. DashboardApp.java (Main Application)
**Purpose**: Main JavaFX application class that creates the UI and handles user interactions.

**Key Components**:
- **Header**: Blue top bar with "Admin Panel" title
- **Sidebar**: Navigation menu with buttons for:
  - Dashboard
  - User
  - Category
  - Product
  - User cart
  - Setting

- **Content Area**: Dynamic area that changes based on selected menu item

**Main Methods**:
- `start()`: Application entry point, sets up the UI
- `createHeader()`: Creates the top header bar
- `createSidebar()`: Creates the left navigation sidebar
- `showDashboard()`: Displays dashboard with statistics
- `showUserManagement()`: Shows user management screen
- `showCategoryManagement()`: Shows category management screen
- `showProductManagement()`: Shows product management screen
- `showUserCartManagement()`: Shows cart management screen
- `createUserTable()`, `createCategoryTable()`, etc.: Create data tables
- Various dialog methods for CRUD operations

### 2. DataStore.java (Data Management)
**Purpose**: Singleton class that manages all application data in ArrayLists.

**Data Collections**:
- `ArrayList<User> users`
- `ArrayList<Category> categories`
- `ArrayList<Product> products`
- `ArrayList<Cart> carts`
- `ArrayList<CartItem> cartItems`

**Key Methods**:
- `getInstance()`: Returns the singleton instance
- `initializeSampleData()`: Populates initial sample data
- CRUD methods for each entity type (add, get, delete, getById, etc.)
- `updateCartTotalPrice()`: Calculates and updates cart totals

**Sample Data Included**:
- 5 Users
- 5 Categories
- 8 Products
- 3 Carts
- 4 Cart Items

### 3. User.java (Model)
**Fields**:
- `int id` (auto-increment)
- `String firstName`
- `String lastName`
- `String image` (emoji or icon)
- `String email`
- `String password`
- `String position` (Admin, Manager, Customer)
- `String address`
- `String phone`
- `LocalDateTime createAt`

**Methods**:
- Getters and Setters for all fields
- `getFullName()`: Returns concatenated first and last name

### 4. Category.java (Model)
**Fields**:
- `int id` (auto-increment)
- `String name`
- `String description`
- `String icon` (emoji or icon)

**Methods**:
- Getters and Setters for all fields

### 5. Product.java (Model)
**Fields**:
- `int id` (auto-increment)
- `String name`
- `String description`
- `String image` (emoji or icon)
- `double price`
- `int categoryId` (foreign key reference)
- `LocalDateTime createAt`

**Methods**:
- Getters and Setters for all fields

### 6. Cart.java (Model)
**Fields**:
- `int id` (auto-increment)
- `int userId` (foreign key reference)
- `String status` (Active, Completed, Cancelled)
- `double totalPrice` (calculated from cart items)

**Methods**:
- Getters and Setters for all fields

### 7. CartItem.java (Model)
**Fields**:
- `int id` (auto-increment)
- `int cartId` (foreign key reference)
- `int productId` (foreign key reference)
- `double price` (price at time of adding to cart)
- `int quantity`

**Methods**:
- Getters and Setters for all fields
- `getTotal()`: Returns price * quantity

## Data Flow

```
User Interaction → DashboardApp UI → DataStore Methods → ArrayList Operations
                                                              ↓
                                    Model Objects ← Data Retrieved
                                           ↓
                              UI Updated with New Data
```

## UI Layout Structure

```
┌─────────────────────────────────────────────────┐
│  Header (Admin Panel)                           │
├──────────┬──────────────────────────────────────┤
│          │                                       │
│ Sidebar  │        Content Area                  │
│          │                                       │
│ - Dash   │  [Dynamic content based on selection]│
│ - User   │                                       │
│ - Cat    │  - Tables                            │
│ - Prod   │  - Forms                             │
│ - Cart   │  - Statistics                        │
│          │  - Search bars                       │
│ - Settings│                                      │
│          │                                       │
└──────────┴──────────────────────────────────────┘
```

## Features by Screen

### Dashboard
- 4 Statistics cards showing counts
- Recent users table (first 5 users)

### User Management
- Search bar to filter users
- Create button to add new users
- Table with columns: ID, First Name, Last Name, Email, Phone, Address, Position
- Action buttons: View (👁), Edit (✏️), Delete (🗑)

### Category Management
- Search bar to filter categories
- Create button to add new categories
- Table with columns: ID, Name, Description, Icon
- Action buttons: View, Edit, Delete

### Product Management
- Search bar to filter products
- Create button to add new products
- Table with columns: ID, Name, Description, Image, Price, Category
- Action buttons: View, Edit, Delete

### User Cart Management
- Search bar to filter carts by user name
- Create button to add new carts
- Table with columns: ID, User, Status, Total Price, Items Count
- Action buttons: View, Edit, Delete

## Dialog Forms

### User Form (Create/Edit)
- First Name (TextField)
- Last Name (TextField)
- Email (TextField)
- Password (TextField)
- Position (TextField)
- Address (TextField)
- Phone (TextField)
- Image (TextField with default emoji)

### Category Form (Create/Edit)
- Name (TextField)
- Description (TextField)
- Icon (TextField with default emoji)

### Product Form (Create/Edit)
- Name (TextField)
- Description (TextField)
- Image (TextField with default emoji)
- Price (TextField - numeric)
- Category (ComboBox with categories)

### Cart Form (Create/Edit)
- User (ComboBox with users) - Create only
- Status (ComboBox: Active, Completed, Cancelled)

## Auto-increment IDs

Each model class has a static counter:
```java
private static int idCounter = 1;
```

When a new object is created:
```java
this.id = idCounter++;
```

This ensures each entity has a unique ID without a database.

## Search Functionality

Search filters data by checking if the search term (case-insensitive) is contained in:
- User: Full name, Email, Phone
- Category: Name
- Product: Name
- Cart: User's full name

## Relationships

```
User ──(1:M)─→ Cart
Category ──(1:M)─→ Product
Cart ──(1:M)─→ CartItem
Product ──(1:M)─→ CartItem
```

- A User can have multiple Carts
- A Category can have multiple Products
- A Cart can have multiple CartItems
- A Product can be in multiple CartItems

## Color Scheme

| Element | Color Code | Usage |
|---------|-----------|-------|
| Primary Blue | #4FB3E8 | Header, Sidebar |
| Light Gray | #f5f5f5 | Background |
| White | #FFFFFF | Cards, Tables |
| Green | #4CAF50 | Create buttons |
| Black | #000000 | Text |

## Styling Approach

- Inline CSS using `.setStyle()` method
- No external CSS files
- Consistent padding and margins
- Rounded corners (8px radius)
- Drop shadows for cards
- Hover effects on buttons

## Extension Points

To add a new entity type:

1. Create a Model class (e.g., `Order.java`)
2. Add ArrayList to `DataStore.java`
3. Add CRUD methods to `DataStore.java`
4. Create table view method in `DashboardApp.java`
5. Create dialog methods (create, view, edit)
6. Add sidebar button and link to management method
7. Add to dashboard statistics if needed

## Best Practices Used

1. **Singleton Pattern**: DataStore ensures single data source
2. **Separation of Concerns**: Models separate from UI logic
3. **Consistent Naming**: Methods follow naming conventions
4. **Code Reusability**: Helper methods for common tasks
5. **User Feedback**: Dialogs for confirmations and inputs
6. **Responsive Design**: ScrollPane for content overflow
7. **Search Functionality**: Real-time filtering
8. **Error Prevention**: Null checks and validations

## Running the Application

### Method 1: Using run.sh
```bash
chmod +x run.sh
./run.sh
```

### Method 2: Using Maven
```bash
mvn clean javafx:run
```

### Method 3: Manual Compilation
```bash
javac src/main/java/com/adminpanel/*.java
java -cp src/main/java com.adminpanel.DashboardApp
```

## Dependencies

- **JavaFX**: For GUI components
- **Java 11+**: For modern Java features
- **No external libraries**: Pure JavaFX implementation

## Testing Checklist

- [ ] Application launches successfully
- [ ] Dashboard displays correct statistics
- [ ] Navigation between screens works
- [ ] User CRUD operations work
- [ ] Category CRUD operations work
- [ ] Product CRUD operations work
- [ ] Cart CRUD operations work
- [ ] Search functionality filters correctly
- [ ] Dialogs open and close properly
- [ ] Data persists during session
- [ ] Delete operations work correctly
- [ ] Edit operations update data correctly
