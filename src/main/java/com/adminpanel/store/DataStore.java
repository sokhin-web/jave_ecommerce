package com.adminpanel.store;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.adminpanel.model.*;

public class DataStore {
    private static DataStore instance;
    
    private ObservableList<User> users;
    private ObservableList<Category> categories;
    private ObservableList<Product> products;
    private ObservableList<Cart> carts;
    private ObservableList<CartItem> cartItems;

    private DataStore() {
        users = FXCollections.observableArrayList();
        categories = FXCollections.observableArrayList();
        products = FXCollections.observableArrayList();
        carts = FXCollections.observableArrayList();
        cartItems = FXCollections.observableArrayList();
        initializeSampleData();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    private void initializeSampleData() {
        // Add sample users
        users.add(new User("Jonh", "Vist", "example56@gamil.com", "password123", 
                          "Admin", "Phnom Penh", "03334444", "https://cdn-icons-png.flaticon.com/512/149/149071.png"));
        users.add(new User("John", "Doe", "john.doe@gmail.com", "password123", 
                          "Manager", "Phnom Penh", "012345678", "https://cdn-icons-png.flaticon.com/512/149/149071.png"));
        users.add(new User("Jane", "Smith", "jane.smith@gmail.com", "password123", 
                          "Customer", "Siem Reap", "098765432", "https://cdn-icons-png.flaticon.com/512/149/149071.png"));
        users.add(new User("Bob", "Johnson", "bob.j@gmail.com", "password123", 
                          "Customer", "Battambang", "011223344", "https://cdn-icons-png.flaticon.com/512/149/149071.png"));
        users.add(new User("Alice", "Williams", "alice.w@gmail.com", "password123", 
                          "Customer", "Phnom Penh", "099887766", "https://cdn-icons-png.flaticon.com/512/149/149071.png"));

        // Add sample categories
        categories.add(new Category("Electronics", "Electronic devices and accessories", "💻"));
        categories.add(new Category("Clothing", "Fashion and apparel", "👕"));
        categories.add(new Category("Food & Beverage", "Food and drinks", "🍔"));
        categories.add(new Category("Books", "Books and magazines", "📚"));
        categories.add(new Category("Home & Garden", "Home improvement and gardening", "🏠"));

        // Add sample products
        products.add(new Product("Laptop", "High-performance laptop", "💻", 999.99, 1));
        products.add(new Product("Smartphone", "Latest smartphone model", "📱", 699.99, 1));
        products.add(new Product("T-Shirt", "Cotton t-shirt", "👕", 19.99, 2));
        products.add(new Product("Jeans", "Blue denim jeans", "👖", 49.99, 2));
        products.add(new Product("Coffee", "Premium coffee beans", "☕", 12.99, 3));
        products.add(new Product("Pizza", "Large pepperoni pizza", "🍕", 15.99, 3));
        products.add(new Product("Novel", "Bestselling fiction book", "📖", 14.99, 4));
        products.add(new Product("Magazine", "Monthly tech magazine", "📰", 5.99, 4));

        // Add sample carts
        carts.add(new Cart(3, "Active"));
        carts.add(new Cart(4, "Completed"));
        carts.add(new Cart(5, "Active"));

        // Add sample cart items
        cartItems.add(new CartItem(1, 1, 999.99, 1));
        cartItems.add(new CartItem(1, 3, 19.99, 2));
        cartItems.add(new CartItem(2, 5, 12.99, 3));
        cartItems.add(new CartItem(3, 2, 699.99, 1));

        // Update cart total prices
        updateCartTotalPrice(1);
        updateCartTotalPrice(2);
        updateCartTotalPrice(3);
    }

    // User methods
    public List<User> getUsers() { return new ArrayList<>(users); }
    public ObservableList<User> getUsersObservable() { return users; }
    public void addUser(User user) { users.add(user); }
    public void deleteUser(User user) { users.remove(user); }
    public User getUserById(int id) {
        for (User user : users) {
            if (user.getId() == id) return user;
        }
        return null;
    }

    // Category methods
    public List<Category> getCategories() { return new ArrayList<>(categories); }
    public ObservableList<Category> getCategoriesObservable() { return categories; }
    public void addCategory(Category category) { categories.add(category); }
    public void deleteCategory(Category category) { categories.remove(category); }
    public Category getCategoryById(int id) {
        for (Category category : categories) {
            if (category.getId() == id) return category;
        }
        return null;
    }

    // Product methods
    public List<Product> getProducts() { return new ArrayList<>(products); }
    public ObservableList<Product> getProductsObservable() { return products; }
    public void addProduct(Product product) { products.add(product); }
    public void deleteProduct(Product product) { products.remove(product); }
    public Product getProductById(int id) {
        for (Product product : products) {
            if (product.getId() == id) return product;
        }
        return null;
    }

    // Cart methods
    public List<Cart> getCarts() { return new ArrayList<>(carts); }
    public ObservableList<Cart> getCartsObservable() { return carts; }
    public void addCart(Cart cart) { carts.add(cart); }
    public void deleteCart(Cart cart) { carts.remove(cart); }
    public Cart getCartById(int id) {
        for (Cart cart : carts) {
            if (cart.getId() == id) return cart;
        }
        return null;
    }

    // CartItem methods
    public List<CartItem> getCartItems() { return new ArrayList<>(cartItems); }
    public ObservableList<CartItem> getCartItemsObservable() { return cartItems; }
    public void addCartItem(CartItem cartItem) { 
        cartItems.add(cartItem);
        updateCartTotalPrice(cartItem.getCartId());
    }
    public void deleteCartItem(CartItem cartItem) { 
        cartItems.remove(cartItem);
        updateCartTotalPrice(cartItem.getCartId());
    }
    public List<CartItem> getCartItemsByCartId(int cartId) {
        List<CartItem> items = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.getCartId() == cartId) {
                items.add(item);
            }
        }
        return items;
    }

    public void updateCartTotalPrice(int cartId) {
        Cart cart = getCartById(cartId);
        if (cart != null) {
            double total = 0.0;
            for (CartItem item : getCartItemsByCartId(cartId)) {
                total += item.getTotal();
            }
            cart.setTotalPrice(total);
            // Trigger ObservableList update by removing and re-adding
            int index = carts.indexOf(cart);
            if (index >= 0) {
                carts.set(index, cart);
            }
        }
    }
}
