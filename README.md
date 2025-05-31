# Parkinson's Disease Detection System

A comprehensive mobile and web-based system for detecting Parkinson's Disease through movement analysis using machine learning.

## Overview

This project consists of two main components:
1. A Spring Boot backend service
2. An Android mobile application that collects sensor data and provides a user interface

## Features

### Mobile Application
- User authentication and account management
- Real-time sensor data collection
- Movement recording and analysis
- Machine learning model for Parkinson's detection
- Historical data viewing
- Profile management
- Secure data transmission to backend

### Backend Service
- RESTful API endpoints
- User authentication and authorization
- Data storage and management
- Real-time data processing
- Email notifications
- Rate limiting and security features

## Technical Stack

### Backend
- Java 17
- Spring Boot 3.4.5
- PostgreSQL Database
- DeepLearning4J for ML
- JWT Authentication
- Flyway for database migrations
- Docker support

### Mobile App
- Android (min SDK 24, target SDK 35)
- Room Database for local storage
- Retrofit for API communication
- ViewBinding for UI
- Material Design components
- Sensor data collection services

## Prerequisites

### Backend
- Java 17 or higher
- PostgreSQL 12 or higher
- Maven
- Docker (optional)

### Mobile App
- Android Studio
- Android SDK 24 or higher
- JDK 11 or higher

## Setup and Installation

### Backend Setup

1. Clone the repository
2. Configure PostgreSQL database
3. Update `application.properties` with your database credentials
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Mobile App Setup

1. Open the project in Android Studio
2. Update the API base URL in `ApiClient.java`
3. Build and run the application

## API Documentation

The backend API is available at `http://localhost:8080/api/v1` with the following main endpoints:

- Authentication endpoints
- User management endpoints
- Sensor data endpoints
- Analysis endpoints

## Security Features

- JWT-based authentication
- Password encryption
- Rate limiting
- Secure data transmission
- Input validation
- Exception handling

## Database Schema

The system uses two databases:
1. PostgreSQL (Backend)
   - User data
   - Analysis results
   - System configuration

2. Room Database (Mobile)
   - Local user data
   - Offline recordings
   - Cache

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
