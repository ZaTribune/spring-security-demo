## Overview

This demo project demonstrates how to use spring-security 
to offer a secure way to manage account & payroll information 
for employees via a corporate API, leveraging Java and the SpringBoot Framework.

<p align="center">
<img height="100" src="docs/spring-security.svg" alt="overview"/>
<img height="100" src="docs/account-management.svg" alt="overview"/>
</p>

## account-service

Companies send out payrolls to employees using corporate mail.  
This solution has certain disadvantages related to security and usability.   
In this project, put on a robe of such an employee. As you're familiar with Java and Spring Framework, 
you've suggested an idea of sending payrolls to the employee's account on the corporate website.   
The management has approved your idea, but it will be you who will implement this project. 
You've decided to start by developing the API structure, then define the role model, implement the business logic, 
and, of course, ensure the security of the service.

## Features

### Role Model
The application defines the following roles:

- `USER`: Can view their own payroll information.
- `ACCOUNTANT`: Can view, upload and update payroll information for all employees.
- `ADMINISTRATOR`: Has all permissions.
- `AUDITOR`: Can view security records for audit purposes.

### Role-Based Access Control
Access to endpoints is restricted based on roles. For example:

- Users can only access their information and own payroll data.
- Accountants can access all payroll data.
- Administrators have full access to all resources and management capabilities.
- Auditors can access security records for auditing purposes.
### Business Logic
The core business logic includes:

- Allow new users to sign up for the platform.
- Control users' access and authorities.
- Upload or Fetch payroll data from the database.
- Processing and formatting payroll information for display.
- Ensuring that only authorized users can access payroll information.
### Auditing
All security events will be logged into the database.
## Security Implementation
### Authentication
**Basic Authentication**: Used for securing API endpoints. Users provide their username and password for each request.
### Authorization
**Role-Based Access Control**: Implemented using Spring Security annotations and configuration.
### Password Encryption
Passwords are stored securely using `BCrypt` hashing.


## Technologies
- Java 17
- Maven
- Spring data JPA
- Spring Security
- H2 Database

## Notes
- This project uses an embedded "H2" Database.
- Provided with a [postman.collection](docs/account-service.postman_collection.json) file.


## Authors
[![Linkedin](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white&label=Muhammad%20Ali)](https://linkedin.com/in/zatribune)
