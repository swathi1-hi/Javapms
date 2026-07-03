# 💊 Pharmacy Management System

A Java Swing based Pharmacy Management System developed using Java and CSV file storage. The application helps pharmacy staff manage medicines, monitor stock, search medicines, generate bills, and automatically update inventory after every sale.

---

# Features

## Medicine Management
- Add new medicines
- Update existing medicines
- Delete medicines
- View all medicines in a table

## Billing System
- Create customer bills
- Auto-complete medicine search
- Add multiple medicines to one bill
- Automatic total calculation
- Save bill as a text file
- Automatically reduce stock after billing

## Inventory Management
- CSV based data storage
- Low stock highlighting
- Automatic inventory update
- Dashboard showing:
  - Total Medicines
  - Low Stock Count
  - Total Categories

## Search & Filter
- Live medicine search
- Search by ID
- Search by Name
- Category filter
- Reset search option

## Dashboard
- Total medicines available
- Number of low stock medicines
- Number of medicine categories

---

# Technologies Used

- Java
- Java Swing
- AWT
- Collections Framework
- File Handling
- CSV Storage
- IntelliJ IDEA / VS Code

---

# Project Structure

```
Pharmacy Management System
│
├── PharmacyGUIFileCSV.java
├── BillingWindow.java
├── Medicine.java
├── medicines.csv
├── Bill_xxxxxxxxx.txt
└── README.md
```

---

# File Description

## PharmacyGUIFileCSV.java

Main application window.

Responsibilities:
- Dashboard
- Medicine Management
- Search
- Filter
- Inventory
- Table display

---

## BillingWindow.java

Billing module.

Responsibilities:
- Auto-complete medicine search
- Bill generation
- Stock update
- Save bill

---

## Medicine.java

Medicine model class.

Stores:

- Medicine ID
- Name
- Price
- Quantity
- Category

---

## medicines.csv

Stores all medicine records.

Example:

```
101,Paracetamol,25.5,38,Tablet
102,Amoxicillin,120.0,8,Tablet
103,Cough Syrup,75.0,13,Syrup
```

---

# Requirements

- Java JDK 17 or above
- VS Code
- Extension Pack for Java

---

# How to Run

## Step 1

Open the project folder in VS Code.

---

## Step 2

Ensure the following files are inside the project folder:

```
BillingWindow.java
Medicine.java
PharmacyGUIFileCSV.java
medicines.csv
```

---

## Step 3

Compile the project

```
javac *.java
```

---

## Step 4

Run the application

```
java PharmacyGUIFileCSV
```

---

# Application Workflow

```
Start Application
        │
        ▼
Load medicines.csv
        │
        ▼
Display Dashboard
        │
        ▼
Manage Medicines
(Add / Update / Delete)
        │
        ▼
Search or Filter Medicines
        │
        ▼
Open Billing Window
        │
        ▼
Generate Bill
        │
        ▼
Reduce Stock Automatically
        │
        ▼
Save Updated medicines.csv
        │
        ▼
Save Bill.txt
```

---

# Dashboard

The dashboard displays:

- Total Medicines
- Low Stock Medicines
- Number of Categories

Low stock medicines are highlighted automatically.

---

# Billing Module

Features:

- Auto-complete medicine search
- Quantity validation
- Stock checking
- Automatic grand total
- Bill generation
- Bill saved as text file
- Inventory updated automatically

---

# Search System

Supports:

- Live Search
- Search by Medicine ID
- Search by Medicine Name
- Category Filtering

---

# CSV Storage

Medicine data is stored in CSV format.

Example:

```
ID,Name,Price,Quantity,Category
101,Paracetamol,25.5,38,Tablet
102,Amoxicillin,120.0,8,Tablet
```

Advantages:

- Lightweight
- Easy to edit
- No database required

---

# Future Enhancements

- Login Authentication
- Customer Management
- Supplier Management
- Barcode Scanner
- QR Code Support
- PDF Bill Generation
- GST Calculation
- Expiry Date Management
- Purchase History
- Sales Reports
- Charts and Analytics
- MySQL Database Integration
- User Roles (Admin/Staff)
- Backup and Restore

---

# Sample Output

Dashboard:

```
Total Medicines : 51
Low Stock : 12
Categories : 10
```

Billing:

```
Medicine        Qty     Price     Total
----------------------------------------
Paracetamol      2      25.50      51.00
Vitamin C        1      40.00      40.00
----------------------------------------
Grand Total : ₹91.00
```

---

# Advantages

- Easy to use
- User-friendly interface
- Fast billing
- Automatic stock updates
- No external database required
- Lightweight application
- Suitable for small pharmacies

---

# Developed Using

- Java
- Swing
- AWT
- CSV File Storage
- Object-Oriented Programming

---

# Author

**Swathi Shri V**

Java Desktop Application Project

```
Pharmacy Management System
Version 1.0
```