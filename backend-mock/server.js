const express = require('express');
const app = express();
const port = 8080;

app.use(express.json());

// Dummy Database
const users = []; // { username, password, fullName, email }
const products = [
    { id: "P001", name: "Điện thoại Samsung S24", description: "Điện thoại thông minh 256GB", price: 24000000 },
    { id: "P002", name: "Laptop Dell XPS 15", description: "Laptop doanh nhân cao cấp", price: 35000000 },
    { id: "P003", name: "Tai nghe Sony WH-1000XM5", description: "Tai nghe chống ồn tốt nhất", price: 8500000 },
    { id: "P004", name: "Bàn phím cơ Keychron Q1", description: "Bàn phím cơ custom vỏ nhôm", price: 4200000 },
];
const orders = [];

// Middleware mô phỏng Xác thực JWT tại API Gateway
const authMiddleware = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    if (authHeader && authHeader.startsWith('Bearer ')) {
        const token = authHeader.substring(7);
        if (token === 'mock-jwt-token-123') {
            return next(); // Token hợp lệ, cho qua
        }
    }
    return res.status(401).json({ message: "Unauthorized (Token Invalid)" });
};

// ──────────────── AUTH SERVICE ────────────────

// POST /api/v1/auth/register
app.post('/api/v1/auth/register', (req, res) => {
    const { fullName, username, email, password } = req.body;
    
    const exists = users.find(u => u.username === username);
    if (exists) {
        return res.status(400).json({ message: "Username already exists" });
    }
    
    users.push({ fullName, username, email, password });
    return res.status(201).json({ message: "Register Success" });
});

// POST /api/v1/auth/login
app.post('/api/v1/auth/login', (req, res) => {
    const { username, password } = req.body;
    
    const user = users.find(u => u.username === username && u.password === password);
    if (user || (username === 'admin' && password === '123456')) {
        // Trả về mock token
        return res.status(200).json({ 
            token: "mock-jwt-token-123",
            user: {
                id: "U01",
                username: username,
                fullName: user ? user.fullName : "Admin Demo"
            }
        });
    }
    
    return res.status(401).json({ message: "Invalid credentials" });
});

// ──────────────── PRODUCT SERVICE ────────────────

// GET /api/v1/products (Phải có token)
app.get('/api/v1/products', authMiddleware, (req, res) => {
    return res.status(200).json(products);
});

// ──────────────── ORDER SERVICE ────────────────

// POST /api/v1/orders (Phải có token)
app.post('/api/v1/orders', authMiddleware, (req, res) => {
    const { items, deliveryAddress } = req.body;
    
    if (!items || items.length === 0) {
        return res.status(400).json({ message: "Cart is empty" });
    }
    
    // Giả lập check tồn kho và lưu DB
    const orderId = "ORD-" + Math.floor(Math.random() * 1000000);
    orders.push({
        orderId,
        items,
        deliveryAddress,
        status: "CREATED",
        timestamp: new Date()
    });
    
    return res.status(201).json({
        orderId: orderId,
        status: "CREATED",
        message: "Order placed successfully"
    });
});

app.listen(port, () => {
    console.log(`Mock API Gateway listening at http://localhost:${port}`);
    console.log(`- Login with admin/123456 or create a new account`);
});
