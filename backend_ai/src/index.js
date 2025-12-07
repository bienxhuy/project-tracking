import dotenv from "dotenv";
import express from "express";
import cors from "cors";
import { generateProject } from "./services/gemini.service.js";

// Load env FIRST before importing services
dotenv.config();

const app = express();
const PORT = 3030;

// Enable CORS for frontend
// app.use(cors({
//   origin: ['http://localhost:3000'],
// }));
app.use(cors());
// Middleware to parse JSON bodies
app.use(express.json());

app.get("/", (req, res) => {
  res.send("Hello!");
});

app.post("/api/generate-project", async (req, res) => {
  try {
    const { projectTitle, projectDescription, startDate, endDate } = req.body;

    // Validate required fields
    if (!projectTitle || !projectDescription || !startDate || !endDate) {
      return res.status(400).json({
        error: "Missing required fields: projectTitle, projectDescription, startDate, endDate",
      });
    }

    // Generate project using Gemini AI
    const project = await generateProject(
      projectTitle,
      projectDescription,
      startDate,
      endDate
    );

    res.json(project);
  } catch (error) {
    console.error("Error generating project:", error);
    res.status(500).json({
      error: "Failed to generate project",
      message: error.message,
    });
  }
});

app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
