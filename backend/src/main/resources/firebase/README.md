# Firebase Configuration Directory

This directory contains Firebase service account credentials.

## Setup Instructions

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project or create a new one
3. Navigate to **Project Settings** (⚙️) > **Service Accounts**
4. Click **Generate new private key**
5. Save the downloaded JSON file as `firebase-service-account.json` in this directory

## Security

⚠️ **IMPORTANT**: Never commit the service account JSON file to version control!

The `.gitignore` file in this directory ensures that all JSON files are excluded from Git.

## File Structure

```
firebase/
├── .gitignore
├── README.md
└── firebase-service-account.json  (You need to add this file)
```
