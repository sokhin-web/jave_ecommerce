# JavaFX Admin Panel Application

A desktop application built with JavaFX for managing users, categories, products, and shopping carts.

## Features

### 1. Dashboard
- Display summary statistics (User count, Category count, Product count, Cart count)
- Show recently added users in a table
- Quick overview of the system

### 2. User Management
- Create, Read, Update, Delete (CRUD) operations for users
- Search functionality to filter users
- View detailed user information
- Fields: First Name, Last Name, Email, Password, Position, Address, Phone, Image

### 3. Category Management
- CRUD operations for product categories
- Search functionality
- Fields: Name, Description, Icon

### 4. Product Management
- CRUD operations for products
- Search functionality
- Link products to categories
- Fields: Name, Description, Image, Price, Category

### 5. User Cart Management
- CRUD operations for shopping carts
- View cart items and total price
- Track cart status (Active, Completed, Cancelled)
- Link carts to users

## Project Structure

```
javafx-admin-panel/
└── src/
    └── main/
        └── java/
            └── com/
                └── adminpanel/
                    ├── DashboardApp.java    (Main application)
                    ├── User.java            (User model)
                    ├── Category.java        (Category model)
                    ├── Product.java         (Product model)
                    ├── Cart.java            (Cart model)
                    ├── CartItem.java        (CartItem model)
                    └── DataStore.java       (In-memory data storage)
```

## Data Storage

This application uses **ArrayList** for in-memory data storage. No database is required. Data is stored in the `DataStore` class which implements a singleton pattern.

### Sample Data Included:
- 5 Users (Admins and Customers)
- 5 Categories (Electronics, Clothing, Food & Beverage, Books, Home & Garden)
- 8 Products across different categories
- 3 Shopping carts with items

## How to Run

### Prerequisites
- Java Development Kit (JDK) 11 or higher
- JavaFX SDK (if not bundled with JDK)

### Option 1: Using Command Line

1. Navigate to the project directory:
```bash
cd javafx-admin-panel
```

2. Compile the Java files:
```bash
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml \
  src/main/java/com/adminpanel/*.java
```

3. Run the application:
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml \
  -cp src/main/java com.adminpanel.DashboardApp
```

### Option 2: Using IDE (IntelliJ IDEA / Eclipse / NetBeans)

1. Import the project as a Java project
2. Add JavaFX library to the project build path
3. Set VM arguments (if required):
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls
   ```
4. Run `DashboardApp.java`

### Option 3: Using Maven (Create pom.xml)

If you prefer Maven, create a `pom.xml` file with JavaFX dependencies:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.adminpanel</groupId>
    <artifactId>javafx-admin-panel</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <javafx.version>17.0.2</javafx.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <configuration>
                    <mainClass>com.adminpanel.DashboardApp</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

Then run:
```bash
mvn clean javafx:run
```

## UI Features

### Color Scheme
- Primary Color: Light Blue (#4FB3E8)
- Background: Light Gray (#f5f5f5)
- Cards: White with subtle shadow
- Success Button: Green (#4CAF50)

### Navigation
- Sidebar menu with buttons for each section
- Hover effects on buttons
- Active section highlighted

### Tables
- Search functionality for filtering data
- Action buttons (View, Edit, Delete) for each row
- Responsive column widths

### Dialogs
- Modal dialogs for Create and Edit operations
- Form validation
- Confirmation dialogs for delete operations

## Usage Tips

1. **Dashboard**: Click on any menu item in the sidebar to navigate
2. **Create New Record**: Click the green "Create" button in each management section
3. **Search**: Use the search bar to filter records by name, email, phone, etc.
4. **Edit**: Click the pencil (✏️) icon to edit a record
5. **View**: Click the eye (👁) icon to view details
6. **Delete**: Click the trash (🗑) icon to delete a record

## Customization

### Adding More Sample Data
Edit the `initializeSampleData()` method in `DataStore.java`

### Changing Colors
Update the style strings in `DashboardApp.java`:
- Header color: `-fx-background-color: #4FB3E8;`
- Button colors: Modify the `createSidebarButton()` method
- Card styles: Modify the `createStatCard()` method

### Adding New Features
1. Create model classes (if needed)
2. Add methods to `DataStore.java`
3. Create UI components in `DashboardApp.java`
4. Add navigation button in sidebar

## Database Schema Reference

The application follows this database schema structure:

**Users**
- id (PK), first_name, last_name, image, email, password, position, address, phone, create_at

**Categories**
- id (PK), name, description, icon

**Products**
- id (PK), name, description, image, price, category_id (FK), create_at

**Carts**
- id (PK), user_id (FK), status, total_price

**Cart_items**
- id (PK), cart_id (FK), product_id (FK), price, quantity

## Future Enhancements

- Export data to CSV/Excel
- Import data from files
- Print reports
- Charts and analytics
- Database integration (MySQL, PostgreSQL, SQLite)
- User authentication
- Role-based access control
- Image upload functionality
- Advanced search filters
- Pagination for large datasets

## License

This is a learning project created for educational purposes.

## Author

Created as a JavaFX desktop application example.
