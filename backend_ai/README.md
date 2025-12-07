# Backend AI Service

An AI-powered microservice that generates structured project breakdowns with milestones and tasks using Google's Gemini AI. This service integrates with the Project Tracking system to automatically create detailed project plans based on instructor-provided descriptions.

## 🌟 Features

- **AI-Powered Project Generation**: Leverages Google Gemini 2.5 Flash to generate comprehensive project structures
- **Multi-Language Support**: Automatically responds in the same language as the input (Vietnamese, English, etc.)
- **Structured Output**: Uses Zod schemas to ensure type-safe, validated JSON responses
- **Hierarchical Planning**: Generates projects with nested milestones and tasks
- **Date Validation**: Ensures all dates are logical and fall within the project timeline
- **RESTful API**: Simple HTTP API for integration with frontend applications

## 📋 Table of Contents

- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Schema Definitions](#schema-definitions)
- [Usage Examples](#usage-examples)
- [Development](#development)
- [Troubleshooting](#troubleshooting)

## 🏗️ Architecture

The service follows a clean architecture pattern:

```
Request → Express Router → Gemini Service → Zod Schema Validation → Response
```

### Technology Stack

- **Runtime**: Node.js (ES Modules)
- **Framework**: Express.js 5.x
- **AI Provider**: Google Gemini AI (@google/genai)
- **Validation**: Zod + zod-to-json-schema
- **Environment Management**: dotenv
- **CORS**: Enabled for cross-origin requests

## 📦 Prerequisites

- Node.js 18.x or higher
- npm or yarn package manager
- Google Gemini API key (obtain from [Google AI Studio](https://aistudio.google.com/))

## 🚀 Installation

1. **Clone the repository** (if not already done):
   ```bash
   git clone <repository-url>
   cd project-tracking/backend_ai
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Create environment file**:
   ```bash
   cp .env.example .env
   # or manually create .env file
   ```

4. **Configure environment variables** (see [Configuration](#configuration))

## ⚙️ Configuration

Create a `.env` file in the root of the `backend_ai` directory:

```env
# Google Gemini API Configuration
GOOGLE_GENAI_API_KEY=your_gemini_api_key_here

# Server Configuration (optional)
PORT=3030
```

### Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `GOOGLE_GENAI_API_KEY` | Yes | - | Your Google Gemini API key |
| `PORT` | No | 3030 | Port number for the Express server |

### Obtaining a Gemini API Key

1. Visit [Google AI Studio](https://aistudio.google.com/)
2. Sign in with your Google account
3. Navigate to "Get API Key"
4. Create a new API key
5. Copy the key to your `.env` file

## 📚 API Documentation

### Base URL

```
http://localhost:3030
```

### Endpoints

#### 1. Health Check

```http
GET /
```

**Response:**
```
Hello!
```

#### 2. Generate Project

```http
POST /api/generate-project
```

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "projectTitle": "E-commerce Website Development",
  "projectDescription": "Build a full-stack e-commerce platform with user authentication, product catalog, shopping cart, and payment integration",
  "startDate": "2024-01-01",
  "endDate": "2024-06-30"
}
```

**Success Response (200):**
```json
{
  "content": "A comprehensive e-commerce platform that enables online shopping...",
  "objectives": "1. Implement secure user authentication\n2. Create product catalog...",
  "milestones": [
    {
      "id": 1,
      "title": "Backend API Development",
      "description": "Develop RESTful APIs for all core functionalities...",
      "startDate": "2024-01-01T00:00:00.000Z",
      "endDate": "2024-02-28T00:00:00.000Z",
      "tasks": [
        {
          "id": 1,
          "title": "Setup project structure",
          "description": "Initialize Node.js project with Express...",
          "startDate": "2024-01-01T00:00:00.000Z",
          "endDate": "2024-01-07T00:00:00.000Z"
        }
      ]
    }
  ]
}
```

**Error Response (400):**
```json
{
  "error": "Missing required fields: projectTitle, projectDescription, startDate, endDate"
}
```

**Error Response (500):**
```json
{
  "error": "Failed to generate project",
  "message": "Detailed error message"
}
```

### Request Validation

All requests are validated for:
- Required fields presence
- Date format validity
- Logical date ranges (end date after start date)

## 📁 Project Structure

```
backend_ai/
├── src/
│   ├── index.js                 # Express server entry point
│   ├── services/
│   │   └── gemini.service.js    # Gemini AI integration logic
│   └── schemas/
│       ├── project.schema.js    # Project structure schema
│       ├── milestone.schema.js  # Milestone structure schema
│       └── task.schema.js       # Task structure schema
├── .env                         # Environment variables (not in git)
├── package.json                 # Project dependencies
└── README.md                    # This file
```

## 🔍 Schema Definitions

### ProjectTreeSchema

```javascript
{
  content: string,        // Project scope and overview
  objectives: string,     // Goals and outcomes
  milestones: Milestone[] // Array of milestones
}
```

### MilestoneSchema

```javascript
{
  id: number,            // Unique identifier
  title: string,         // Milestone name
  description: string,   // Detailed description
  startDate: Date,       // Start date (within project range)
  endDate: Date,         // End date (within project range)
  tasks: Task[]          // Array of tasks
}
```

### TaskSchema

```javascript
{
  id: number,            // Unique identifier
  title: string,         // Task name
  description: string,   // Detailed description
  startDate: Date,       // Start date (within milestone range)
  endDate: Date          // End date (within milestone range)
}
```

### Validation Rules

- **Project**: End date must be after start date
- **Milestone**: 
  - End date must be after or equal to start date
  - All dates must fall within project date range
  - All task dates must fall within milestone date range
- **Task**: End date must be after or equal to start date

## 💡 Usage Examples

### Example 1: Generate a Software Project

```bash
curl -X POST http://localhost:3030/api/generate-project \
  -H "Content-Type: application/json" \
  -d '{
    "projectTitle": "Mobile Banking App",
    "projectDescription": "Develop a secure mobile banking application with features like balance checking, money transfers, bill payments, and transaction history",
    "startDate": "2024-03-01",
    "endDate": "2024-08-31"
  }'
```

### Example 2: Vietnamese Language Project

```bash
curl -X POST http://localhost:3030/api/generate-project \
  -H "Content-Type: application/json" \
  -d '{
    "projectTitle": "Hệ thống quản lý thư viện",
    "projectDescription": "Xây dựng hệ thống quản lý thư viện với các tính năng mượn/trả sách, quản lý độc giả, tìm kiếm sách, và thống kê báo cáo",
    "startDate": "2024-01-15",
    "endDate": "2024-05-15"
  }'
```

### Example 3: Frontend Integration (JavaScript/TypeScript)

```javascript
async function generateProject(projectData) {
  try {
    const response = await fetch('http://localhost:3030/api/generate-project', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(projectData),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const project = await response.json();
    console.log('Generated project:', project);
    return project;
  } catch (error) {
    console.error('Error generating project:', error);
    throw error;
  }
}

// Usage
generateProject({
  projectTitle: "Portfolio Website",
  projectDescription: "Create a personal portfolio website with project showcase, blog, and contact form",
  startDate: "2024-02-01",
  endDate: "2024-03-31"
});
```

## 🛠️ Development

### Running the Service

**Development mode:**
```bash
npm start
```

The server will start on `http://localhost:3030` (or the port specified in `.env`)

### Testing the API

**Using curl:**
```bash
# Health check
curl http://localhost:3030

# Generate project
curl -X POST http://localhost:3030/api/generate-project \
  -H "Content-Type: application/json" \
  -d @test-project.json
```

**Using Postman or Thunder Client:**
1. Create a new POST request to `http://localhost:3030/api/generate-project`
2. Set header: `Content-Type: application/json`
3. Add request body with required fields
4. Send request

### Adding New Schemas

1. Create a new schema file in `src/schemas/`
2. Define schema using Zod
3. Export the schema
4. Import in service or other schemas as needed

Example:
```javascript
// src/schemas/subtask.schema.js
import { z } from 'zod';

export const SubtaskSchema = z.object({
  id: z.number(),
  title: z.string(),
  completed: z.boolean()
});
```

### Extending the Gemini Service

To add new AI-powered features:

1. Add new function in `src/services/gemini.service.js`
2. Define appropriate Zod schema for response structure
3. Create prompt with clear instructions
4. Use `zodToJsonSchema` for structured output
5. Add new API endpoint in `src/index.js`

## 🐛 Troubleshooting

### Common Issues

#### 1. API Key Not Found

**Error:**
```
Error: GOOGLE_GENAI_API_KEY not found in environment variables
```

**Solution:**
- Ensure `.env` file exists in the `backend_ai` directory
- Verify `GOOGLE_GENAI_API_KEY` is set in `.env`
- Restart the server after adding environment variables

#### 2. Port Already in Use

**Error:**
```
Error: listen EADDRINUSE: address already in use :::3030
```

**Solution:**
- Change the port in `.env`: `PORT=3031`
- Or kill the process using port 3030:
  ```bash
  # Find process
  netstat -ano | findstr :3030
  # Kill process
  taskkill /PID <process_id> /F
  ```

#### 3. CORS Issues

**Error:**
```
Access to fetch at 'http://localhost:3030/api/generate-project' from origin 'http://localhost:3000' has been blocked by CORS policy
```

**Solution:**
- CORS is already enabled for all origins in `src/index.js`
- For production, update CORS configuration to whitelist specific origins:
  ```javascript
  app.use(cors({
    origin: ['https://your-frontend-domain.com']
  }));
  ```

#### 4. Invalid JSON Schema Response

**Error:**
```
ZodError: Invalid input
```

**Solution:**
- The AI sometimes returns slightly different structures
- Check the prompt in `gemini.service.js` for clarity
- Verify schema definitions match expected AI output
- Review Gemini API response logs

#### 5. Rate Limiting

**Error:**
```
Error: 429 Too Many Requests
```

**Solution:**
- Gemini API has rate limits on free tier
- Implement request throttling or caching
- Consider upgrading API plan for higher limits
- Add retry logic with exponential backoff

### Debug Mode

Enable detailed logging:

```javascript
// Add to src/index.js
app.use((req, res, next) => {
  console.log(`${req.method} ${req.path}`, req.body);
  next();
});
```

### Checking Logs

Monitor server logs for detailed error messages:
```bash
npm start
# Watch terminal output for errors
```

## 🔗 Integration with Main Backend

This AI service is designed to integrate with the Java Spring Boot backend:

1. **Frontend** calls this service to generate project structure
2. **Frontend** receives the generated data
3. **Frontend** sends the structured data to the **Java backend** for persistence
4. **Java backend** saves projects, milestones, and tasks to database

### Integration Flow

```
User Input → Frontend → AI Service (Generate) → Frontend → Java Backend (Save) → Database
```

## 📝 Notes

- **Language Detection**: The service automatically detects and responds in the same language as the project description
- **ID Assignment**: The IDs generated by the AI are temporary; the Java backend assigns permanent database IDs
- **Date Ranges**: All dates are validated to ensure logical ordering and range compliance
- **Response Format**: Always returns structured JSON matching the defined schemas
- **Error Handling**: Comprehensive error messages for debugging

## 🤝 Contributing

When contributing to this service:

1. Maintain ES Module syntax (`import`/`export`)
2. Use Zod for all data validation
3. Follow existing code structure and naming conventions
4. Test AI prompts thoroughly for different inputs
5. Update schemas when changing data structures
6. Document new endpoints in this README

## 📄 License

This project is part of the Project Tracking system. Refer to the main repository for license information.

## 📞 Support

For issues related to:
- **AI responses**: Check Gemini API documentation
- **Schema validation**: Review Zod documentation
- **Integration**: Refer to main backend documentation
- **General issues**: Check troubleshooting section above

---

**Last Updated**: December 2024  
**Version**: 1.0.0  
**Node Version**: 18.x+
