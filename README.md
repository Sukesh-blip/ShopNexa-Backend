# 🛒 ShopNexa – Full Stack Cloud-Native E-Commerce Platform

Live Demo: https://salmon-mushroom-072f9aa00.2.azurestaticapps.net/

---

## 📌 Overview

ShopNexa is a production-grade full-stack e-commerce platform built using **Spring Boot, React (Vite), and Microsoft Azure Cloud**.

The system demonstrates real-world cloud deployment practices including:

- Docker containerization
- Azure App Service deployment
- Azure Container Registry (ACR)
- Azure MySQL Flexible Server
- GitHub Actions CI/CD pipeline
- JWT-based authentication with RBAC
- Razorpay payment integration

This project was designed and deployed with a cloud-first architecture approach.

---

## 🏗 Cloud Architecture

Frontend (React + Vite)
→ Azure Static Web Apps

Backend (Spring Boot – Dockerized)
→ Azure App Service (Linux Container)

Container Registry
→ Azure Container Registry (ACR)

Database
→ Azure MySQL Flexible Server

CI/CD
→ GitHub Actions

Payments
→ Razorpay (Test Mode)

Authentication
→ Spring Security + JWT + RBAC

---

## 🔐 Security Features

- Stateless JWT authentication
- Role-Based Access Control (ADMIN / USER)
- Custom authentication filter
- Protected cart, order, and admin endpoints
- Swagger secured with Bearer token authorization
- Production CORS configuration

---

## 💳 Payment Integration

Integrated Razorpay payment gateway with:

- Backend order creation
- Secure signature verification
- Transaction validation
- User-specific order tracking

---

## ⚙️ Backend Tech Stack

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- Docker

---

## 🎨 Frontend Tech Stack

- React (Vite)
- Axios
- JWT storage handling
- Responsive UI
- Protected routes

---

## ☁️ DevOps & Deployment

- Dockerized Spring Boot backend
- Image pushed to Azure Container Registry
- Backend deployed via Azure App Service (Container-based)
- Frontend deployed via Azure Static Web Apps
- GitHub Actions automated CI/CD pipeline
- Environment variable management at build time
- Production debugging via Azure log stream

---

## 🧠 Production Engineering Challenges Solved

- Fixed CORS conflicts between Azure domains
- Resolved JWT authentication filter conflicts
- Debugged environment variable injection at build time
- Fixed Swagger security configuration issues
- Resolved Hibernate–MySQL schema mismatches
- Handled container cold-start behavior

---

## 📸 Deployment Evidence

Complete Azure deployment screenshots available here:

👉 [View Deployment Evidence](./deployment-evidence)

![image alt](https://github.com/Sukesh-blip/ShopNexa-Backend/blob/ac1c5c097cc863ec74cf071eae173a35e0177c76/ShopNexa-App%20Service_backend.png)
![image alt](https://github.com/Sukesh-blip/ShopNexa-Backend/blob/ac1c5c097cc863ec74cf071eae173a35e0177c76/ShopNexa-Azure_MySQL.png)
![image alt](https://github.com/Sukesh-blip/ShopNexa-Backend/blob/ac1c5c097cc863ec74cf071eae173a35e0177c76/ShopNexa-Conatainer%20Registry.png)
![image alt](https://github.com/Sukesh-blip/ShopNexa-Backend/blob/ac1c5c097cc863ec74cf071eae173a35e0177c76/ShopNexa_GitHub%20Actions.png)
![image alt](https://github.com/Sukesh-blip/ShopNexa-Backend/blob/ac1c5c097cc863ec74cf071eae173a35e0177c76/ShopNexa-WebApp_Frontend.png)


## 🧪 API Documentation

Swagger UI available at:

![image alt](https://github.com/Sukesh-blip/ShopNexa-Backend/blob/ac1c5c097cc863ec74cf071eae173a35e0177c76/ShopNexa-Swagger.png)

Includes secured endpoints for:

- Authentication
- Products
- Cart
- Orders
- Payments

---

## 📂 Key Features

- User Registration & Login
- Role-Based Admin Panel
- Product Management
- Add to Cart
- Order Placement
- Secure Online Payment
- Order History Tracking

---

## 🏆 Certification Alignment

This project demonstrates practical implementation of concepts aligned with:

- Microsoft Azure Fundamentals (AZ-900)
- Cloud Architecture & Governance
- Secure Cloud Deployment
- DevOps CI/CD Practices

---

## 👨‍💻 Author

Sukesh Biradar  
Software Engineer | Azure Certified (AZ-900)  
LinkedIn: https://linkedin.com/in/sukeshbiradar  
GitHub: https://github.com/Sukesh-blip

---

## 📄 License

This project is built for educational and portfolio demonstration purposes.


