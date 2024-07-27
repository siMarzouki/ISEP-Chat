# ISEP-Chat 

## 1. Description of the Application

### 1.1. What’s the type of application?
ISEP-Chat is a real-time messaging mobile application designed for Android devices. It provides a platform for users within the ISEP community to communicate seamlessly and instantly. The application supports text messaging, media sharing, and location sharing, making it a comprehensive tool for social interaction and information exchange.

### 1.2. What’s the goal of the application?
The primary goal of ISEP-Chat is to enhance communication and collaboration among students, faculty, and staff within the ISEP community. The application aims to create a user-friendly environment where users can connect, share information, and foster a sense of community.

### 1.3. What’s the scope of the application?
ISEP-Chat focuses on:
- Enabling users to exchange text messages, photos, videos, and current location.
- Connecting the entire school community through posts and interactions.
- Creating a safe digital space for interaction and information sharing for the ISEP community.

## 2. Technical Description

### 2.1. What application features did we develop?
- **Log-in and Registration Module**: User authentication and registration.
- **Customizable Profiles**: Users can personalize their profiles.
- **Real-time Messaging**: Instant text communication between users.
- **Sending Photos and Videos**: Share multimedia content within conversations.
- **Reacting with Like Emoji**: Users can react to messages with emojis.
- **Sharing Location**: Users can share their current location in messages.
- **Posts for Community**: Users can create and view posts visible to the whole community.
- **Liking Posts**: Engage with posts by liking them.
- **Notifications**: Receive real-time notifications for messages and posts.
- **Tracking Seen Messages**: Indicates whether messages have been read.

### 2.2. What APIs did we use?
- **Firestore Authentication**: Used for user log-in, registration, and identification.
- **Firebase Cloud Messaging**: Provides real-time notifications, even when the app is not open.
- **Firebase Database**: Handles sending and receiving messages, photos, and videos.

### 2.3. What skills did we use?
- **Animations**: For enhancing user experience with dynamic visual elements.
- **Databases**: Managing and storing user data and messages.
- **Lazy Lists**: Efficiently displaying large sets of data.
- **Navigation Drawer**: Implementing side navigation for better app organization.
- **Notifications**: Real-time updates and alerts for users.
- **Permissions**: Handling user permissions for media access and location.
- **Video Handling**: Managing and displaying video content.

## 3. Final Appearance of the Application

### 3.1. Splash Screen
The splash screen appears for 3 seconds, serving as a loading screen. It checks user authentication status and redirects to the Home Page if the user is already logged in. If not, it directs the user to the log-in page.

### 3.2. Authentication Module
- **Log-in Page**: Simple and intuitive, validates email and password, and uses `signInWithEmailAndPassword()` with Firestore Authentication API. Successful log-in redirects to Home Page; otherwise, an error message is displayed.
- **Registration Page**: Users enter email, password, confirm password, and name. Passwords are checked for consistency, and `createUserWithEmailAndPassword()` is used. Successful registration creates a user profile in Firestore and navigates to Home Page; errors trigger appropriate messages.

### 3.3. Discussions
- **Main Page**: Displays a comprehensive view of all discussions, including user photo, name, last message, and timestamp.
- **Starting New Conversations**: Users can start new conversations by selecting from a list or searching for users.
- **Chat Activity**: Displays messages, allows sending new messages, photos, GIFs, or location. Users can navigate back to the list of conversations.

### 3.4. Community
- **Posts Feed**: Displays posts with name, profile picture, timestamp, and like button. Users can view and like posts.
- **Creating Posts**: A pop-up window allows users to enter and post new messages.

### 3.5. Settings
- **Profile Editing**: Users can change their display name and email. Changes are saved to the user profile, or discarded if cancelled.
- **Log-out**: Users can log out using `auth.signOut()`.

## 4. Features We Are Most Proud Of and Problems We Encountered Implementing Them

### 4.1. Keeping Track of Seen Messages
**Feature**: Indicates which messages have been read, enhancing user engagement and communication clarity.
**Problem**: Implementing this feature required efficient tracking and updating of message states, which was crucial for improving user experience.

### 4.2. Media and Location Sharing
**Feature**: Allows users to share images, GIFs, and location, adding functionality and versatility to the messaging platform.
**Problem**: Integrating media sharing with Firebase required careful handling of file storage and retrieval, along with adapting the layout to accommodate different media types. Ensuring proper synchronization and display of media content was challenging but rewarding.

### 4.3. Notifications
**Feature**: Real-time notifications for messages and posts keep users engaged and informed.
**Problem**: Implementing notifications with Firebase Cloud Messaging (FCM) presented challenges due to recent updates and documentation changes. Despite difficulties, we successfully integrated notifications for both messages and posts, ensuring users receive timely updates.

## Conclusion
ISEP-Chat is a comprehensive messaging application designed to foster communication within the ISEP community. The application features real-time messaging, media sharing, and community posts, with robust support for notifications and seen message tracking. Despite challenges in implementing notifications, media sharing, and tracking features, the project successfully delivers a valuable tool for enhancing community interaction and engagement.
