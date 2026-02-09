# Quick Start Guide - JavaFX Admin Panel

## 🚀 Get Started in 3 Steps

### Step 1: Requirements
Make sure you have installed:
- **Java JDK 11 or higher** - [Download from Oracle](https://www.oracle.com/java/technologies/downloads/)
- **JavaFX SDK** - [Download from Gluon](https://gluonhq.com/products/javafx/)

### Step 2: Setup

#### Option A: Using IDE (Recommended for Beginners)

**IntelliJ IDEA:**
1. Open IntelliJ IDEA
2. Click `File` → `New` → `Project from Existing Sources`
3. Select the `javafx-admin-panel` folder
4. Choose "Import project from external model" → "Maven"
5. Click Finish
6. Right-click `DashboardApp.java` → `Run`

**Eclipse:**
1. Open Eclipse
2. `File` → `Import` → `Maven` → `Existing Maven Projects`
3. Select the `javafx-admin-panel` folder
4. Click Finish
5. Right-click `DashboardApp.java` → `Run As` → `Java Application`

#### Option B: Using Maven (Easiest)

```bash
cd javafx-admin-panel
mvn clean javafx:run
```

#### Option C: Using Command Line

```bash
cd javafx-admin-panel
chmod +x run.sh
./run.sh
```

### Step 3: Explore the Application

The application will open with the **Dashboard** displayed.

## 📱 Application Navigation

### Sidebar Menu
- **Dashboard** - Overview with statistics
- **User** - Manage users
- **Category** - Manage product categories  
- **Product** - Manage products
- **User cart** - Manage shopping carts
- **Setting** - Application settings

## 🎯 Common Tasks

### Adding a New User
1. Click **User** in the sidebar
2. Click the green **Create ➕** button
3. Fill in the form:
   - First Name: John
   - Last Name: Doe
   - Email: john@example.com
   - Password: password123
   - Position: Customer
   - Address: Phnom Penh
   - Phone: 012345678
   - Image: 👤
4. Click **Create**

### Searching for Data
1. Navigate to any management screen (User, Category, Product, Cart)
2. Type in the search box at the top
3. Results filter automatically as you type

### Editing a Record
1. Find the record in the table
2. Click the **✏️ (Edit)** icon in the Action column
3. Update the fields
4. Click **Save**

### Viewing Details
1. Find the record in the table
2. Click the **👁 (View)** icon in the Action column
3. A popup shows all details

### Deleting a Record
1. Find the record in the table
2. Click the **🗑 (Delete)** icon in the Action column
3. The record is removed immediately

## 📊 Sample Data Included

The application comes with pre-loaded data:

**Users (5)**
- Jonh Vist (Admin)
- John Doe (Manager)
- Jane Smith (Customer)
- Bob Johnson (Customer)
- Alice Williams (Customer)

**Categories (5)**
- Electronics 💻
- Clothing 👕
- Food & Beverage 🍔
- Books 📚
- Home & Garden 🏠

**Products (8)**
- Laptop, Smartphone, T-Shirt, Jeans, Coffee, Pizza, Novel, Magazine

**Shopping Carts (3)**
- With various items and different statuses

## 🎨 UI Elements Explained

### Dashboard Cards
Shows total counts for:
- User amount
- Category count
- Product count
- User Cart count

### Recent Users Table
Displays the 5 most recent users with:
- ID, Full name, Phone, Position, Image

### Data Tables
All management screens have tables with:
- **Sortable columns** - Click headers to sort
- **Action buttons** - View, Edit, Delete
- **Search bar** - Filter results instantly

### Forms
Create and Edit dialogs include:
- **Text fields** - For entering data
- **Dropdowns** - For selecting categories, users, status
- **Buttons** - Create/Save and Cancel

## 🔧 Troubleshooting

### Issue: Application won't start

**Solution 1**: Check Java version
```bash
java -version
```
Should show Java 11 or higher.

**Solution 2**: Verify JavaFX is installed
- IntelliJ: `File` → `Project Structure` → `Libraries` → Add JavaFX
- Maven: Ensure `pom.xml` has JavaFX dependencies

### Issue: "Module not found" error

**Solution**: Add VM arguments
```
--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls
```

In IntelliJ: `Run` → `Edit Configurations` → `VM options`

### Issue: Blank screen on launch

**Solution**: Check console for errors. Usually means JavaFX is not properly configured.

## 💡 Tips & Tricks

1. **Quick Navigation**: Use sidebar buttons to jump between sections
2. **Fast Search**: Start typing immediately after clicking on a management screen
3. **Batch Testing**: Use sample data to test all features
4. **Data Persistence**: Data only persists during the current session
5. **Multiple Actions**: You can create, edit, and delete without leaving the screen

## 🎓 Learning Path

### Beginner
1. ✅ Run the application
2. ✅ Navigate between screens
3. ✅ View existing data
4. ✅ Search for records

### Intermediate
1. ✅ Create new users
2. ✅ Edit existing records
3. ✅ Delete records
4. ✅ Add categories and products

### Advanced
1. ✅ Understand the code structure
2. ✅ Modify the UI colors and styles
3. ✅ Add new fields to models
4. ✅ Create new entity types

## 📚 Next Steps

### Explore the Code
1. Open `DashboardApp.java` - Main application logic
2. Check `DataStore.java` - Data management
3. Review model classes - User, Category, Product, Cart, CartItem

### Customize the Application
1. Change colors in `createHeader()` and `createSidebar()`
2. Add new fields to model classes
3. Modify table columns
4. Add new statistics to dashboard

### Extend Functionality
1. Add export to CSV feature
2. Implement data validation
3. Add user authentication
4. Create reports and analytics
5. Integrate with a real database

## 📞 Resources

- **Full Documentation**: See `README.md`
- **Project Structure**: See `PROJECT_STRUCTURE.md`
- **JavaFX Docs**: [openjfx.io](https://openjfx.io/)
- **Java Tutorials**: [Oracle Java Tutorials](https://docs.oracle.com/javase/tutorial/)

## ✨ Features Highlight

✅ No database required - Uses in-memory ArrayLists
✅ Full CRUD operations for all entities
✅ Real-time search functionality
✅ Clean and modern UI design
✅ Responsive layout
✅ Easy to understand code structure
✅ Ready-to-use sample data
✅ Modular and extensible

## 🎉 You're Ready!

Start exploring the application and have fun managing your data!

**Happy Coding! 🚀**
