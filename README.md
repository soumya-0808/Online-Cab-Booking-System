# C.V. Raman Global University - Online Cab Booking System

## Overview

C.V. Raman Global University student Launcher is a comprehensive cab booking system implemented as a Java console application. It provides an efficient platform for cab bookings, managing drivers, customers, and administrative operations. The application is designed using object-oriented principles and follows clean code practices.

## Features

### User Roles

1. **Admin**
   - Approve/deactivate drivers and customers
   - Manage system settings
   - View booking statistics

2. **Driver**
   - Profile management (registration, login, password recovery)
   - View and accept ride requests
   - View earnings and trip history
   - Rate customers
   - Receive bonuses based on ratings

3. **Customer**
   - Profile management (registration, login, password recovery)
   - Book cabs with preferences (location, time, driver gender)
   - Cancel bookings
   - View upcoming and past trips
   - Rate drivers
   - Receive cashback based on ratings

### Core Functionality

- **Cab Selection**: Select cabs based on location, traffic conditions, and driver preferences
- **Price Calculation**: Dynamic pricing based on distance, traffic, time of day
- **Booking Management**: Create, view, and cancel bookings
- **Rating System**: Two-way rating system between drivers and customers
- **Incentive System**: Bonuses for drivers and cashback for customers based on ratings
- **Score System**: User scoring based on ratings and behavior (cancellations, punctuality)

## Technical Architecture

- **Programming Language**: Java 8
- **Database**: MySQL
- **Build Tool**: Maven
- **Design Patterns**: Singleton, Factory, Strategy
- **Architecture**: Layered architecture (Presentation, Business, Data Access)

## Database Schema

The application uses the following key tables:
- `cabby_admin`: Admin user information
- `driver`: Driver details including approval status
- `customer`: Customer details including approval status
- `bookings`: Booking information
- `trips`: Completed trip details
- `cabs`: Available cabs with their properties
- `driver_ratings` & `customer_ratings`: Rating information
- `driver_score` & `customer_score`: Score tracking
- `price_calculation`: Data for price calculation
- `user_points`: Points earned by users
- `coupons`: Available coupon information

## Setup Instructions

### Prerequisites

- Java 8 or higher
- Maven 3.6 or higher
- MySQL 5.7 or higher (XAMPP can be used)

### Database Setup

1. Install and start MySQL server (or XAMPP)
2. Create a database named 'cabby'
3. Import the SQL schema from `src/main/sql/db.sql`

```bash
mysql -u root -p < src/main/sql/db.sql
```

Or import using phpMyAdmin if using XAMPP.

### Application Setup

1. Clone the repository
```bash
git clone https://github.com/yourusername/Cab-Booking-Application.git
cd Cab-Booking-Application
```

2. Build the application
```bash
mvn clean package
```

3. Run the application
```bash
java -cp target/classes com.dal.cabby.Application
```

Or use the provided script:
```bash
./start.sh
```

## Usage Guide

1. When starting the application, you'll be prompted to select your role:
   ```
   ***** CBRaman Soumya Launcher: A one stop app for your cab booking *****
   
   Are you: 
   1: Admin
   2: Driver
   3: Customer
   ```

2. After selecting a role, you'll be presented with login/registration options:
   ```
   Enter input: 
   1: Login
   2: Registration
   3: Forgot password?
   4. Exit
   ```

3. Based on your role, you'll see different menus:
   - **Admin**: Manage drivers and customers
   - **Driver**: Manage rides, view ratings, earnings
   - **Customer**: Book rides, rate drivers, view trips

### Demo Credentials

- Admin:
  - Username: admin1
  - Password: admin1@123

- Driver:
  - Username: devraj
  - Password: devraj@123

- Customer:
  - Username: cust1
  - Password: cust1@123

## Testing

The application includes JUnit tests for core functionality. To run tests:

```bash
mvn test
```

## Project Structure

- `src/main/java/com/dal/cabby`: Main source code
  - `admin`: Admin functionality
  - `booking`: Booking operations
  - `cabPrice`: Price calculation
  - `cabSelection`: Cab selection algorithms
  - `customer`: Customer functionality
  - `dbHelper`: Database connection and operations
  - `driver`: Driver functionality
  - `incentives`: Bonus and cashback systems
  - `io`: Input/output handling
  - `money`: Payment processing
  - `pojo`: Plain old Java objects
  - `prelogin`: Initial login screens
  - `profileManagement`: User profile operations
  - `rating`: Rating system
  - `rides`: Ride management
  - `score`: User scoring system
  - `util`: Utility functions

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributors

- Soumya Ranjan - CB Raman College

## Acknowledgments

- CB Raman College
- Faculty of Computer Science

---

© 2023 CBRaman Soumya Launcher - All Rights Reserved 
