# UserType Removal - Changes Summary

## Overview

Removed the UserType system and simplified user registration to make all users "author" by default.

## Changes Made

### 1. **UserEntity.java**

-   ❌ Removed `UserType` enum (user, author)
-   ❌ Removed `type` field and related annotations (`@ElementCollection`, `@CollectionTable`)
-   ❌ Removed `type` parameter from constructor
-   ❌ Removed `getType()` and `setType()` methods
-   ✅ Updated `getAuthorities()` to always return "author" role
-   ✅ Updated `toString()` to remove type field
-   ✅ Cleaned up unused imports

### 2. **RegisterUserDTO.java**

-   ❌ Removed `type` parameter from record
-   ❌ Removed `UserType` import

### 3. **AuthController.java**

-   ❌ Removed `newUser.setType(registeredUser.type())` from register method
-   ✅ All new users are automatically "author" type

### 4. **UserServiceImp.java**

-   ❌ Removed type manipulation logic in `registerUser()` method
-   ✅ Simplified to just save the user (type is handled by entity defaults)
-   ✅ Cleaned up unused imports

## API Changes

### Registration Request Format

**Before:**

```json
{
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "userImg": "profile.jpg",
    "type": ["author"]
}
```

**After:**

```json
{
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "userImg": "profile.jpg"
}
```

### Database Schema Impact

-   The `user_types` table is no longer needed
-   All existing users will need to be migrated or the `type` column can be dropped

## Benefits

1. **Simplified Registration**: No need to specify user type
2. **Consistent Permissions**: All users have author privileges
3. **Reduced Complexity**: Less code to maintain
4. **Better UX**: Streamlined registration process

## Security

-   All registered users now have "author" authority by default
-   Can create, read, update posts
-   JWT tokens will contain "author" role
-   Existing security rules remain unchanged since they already used "author" authority

## Testing

✅ Application compiles successfully  
✅ No breaking changes to existing endpoints  
✅ Registration endpoint updated to new format
