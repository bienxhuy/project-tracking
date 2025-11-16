#!/usr/bin/env node

/**
 * Generate firebase-messaging-sw.js with environment variables
 * Run: node scripts/generate-sw.js
 */

import { writeFileSync, readFileSync } from 'fs';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';
import dotenv from 'dotenv';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
const rootDir = join(__dirname, '..');

// Load .env file
dotenv.config({ path: join(rootDir, '.env') });

// Read template
const template = readFileSync(join(__dirname, 'firebase-messaging-sw.template.js'), 'utf-8');

// Replace placeholders with actual values
const content = template
  .replace('{{VITE_FIREBASE_API_KEY}}', process.env.VITE_FIREBASE_API_KEY || '')
  .replace('{{VITE_FIREBASE_AUTH_DOMAIN}}', process.env.VITE_FIREBASE_AUTH_DOMAIN || '')
  .replace('{{VITE_FIREBASE_PROJECT_ID}}', process.env.VITE_FIREBASE_PROJECT_ID || '')
  .replace('{{VITE_FIREBASE_STORAGE_BUCKET}}', process.env.VITE_FIREBASE_STORAGE_BUCKET || '')
  .replace('{{VITE_FIREBASE_MESSAGING_SENDER_ID}}', process.env.VITE_FIREBASE_MESSAGING_SENDER_ID || '')
  .replace('{{VITE_FIREBASE_APP_ID}}', process.env.VITE_FIREBASE_APP_ID || '');

// Write to public folder
writeFileSync(join(rootDir, 'public', 'firebase-messaging-sw.js'), content);

console.log('✅ Generated firebase-messaging-sw.js with environment variables');
