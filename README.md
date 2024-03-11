# Web Application & Service Development with Spring Framework v5 Course Project
## Online-Video-Learning-Platform
Project is using Spring Mvc, Spring Data JPA, Spring Security and Thymeleaf.



## Project Summary

Nowadays we all work and learn online. The Online Video Learning Platform (OVLP) provides ability for moderators and admins to create online lessons for users, which can search, watch and comment them. It allows anonymous users to sign up and admins can manage all the users and their roles. The admins and moderators can upload, update and delete lessons. There will be also a video streaming using Spring Content. Every user has its own profile and can manage his information like profile picture, username and password. The system is web application using Thymeleaf server side templates. Each page have a distinct URL, and the routing between pages will be done server side using SpringMVC. T The main user roles (actors in UML) are:
 - User – Can watch and comment lessons. 
 - Moderator – Can watch and comment lessons and also can create and modify his/her own lessons. 
 - Administrator – Can watch, comment, upload, update and delete lessons, also manage (access, edit and delete user data) all Registered Users.


## Main Use Cases / Scenarios
### Basic system functionalities
 - By default there is one admin user in the system with username ‘admin’.
 - Anonymous user can register himself in the system by providing a username and password. By default, all new registered users have User role. 
 - All users can browse information views (Home,  My Profile).
### User info management
 - User can update his/her username and password, also can upload and remove his/her profile picture.
 - Administrator can view and edit User Data of all Users, assign them Roles and delete their accounts.
 - Administrator can browse and filter user by username containing a keyword(case insensitive).
### Lesson related functionalities
 - Every user can search and watch lessons based on the lesson’s title.
 - Moderators and Administrators can upload new lessons.
 - Moderator can update or delete his lessons.
 - Administrators can update or delete all lessons.
 - Every user can comment on every lesson.
 - Moderator can delete comments on his lessons.
 - Administrator can delete comments on all lessons.
