import { useState, useRef } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Upload, FileSpreadsheet, AlertCircle, CheckCircle, Download } from "lucide-react";
import { BulkImportResult, CreateUserDto } from "@/types/user.type";

interface BulkImportDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (users: CreateUserDto[]) => Promise<BulkImportResult>;
}

export function BulkImportDialog({
  open,
  onOpenChange,
  onSubmit
}: BulkImportDialogProps) {
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<BulkImportResult | null>(null);
  const [dragActive, setDragActive] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleDrag = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);

    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFile(e.dataTransfer.files[0]);
    }
  };

  const handleFileInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      handleFile(e.target.files[0]);
    }
  };

  const handleFile = (selectedFile: File) => {
    const validTypes = [
      "text/csv",
      "application/vnd.ms-excel",
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    ];

    if (!validTypes.includes(selectedFile.type)) {
      alert("Please upload a CSV or Excel file");
      return;
    }

    setFile(selectedFile);
    setResult(null);
  };

  const generatePassword = () => {
    const length = 12;
    const charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*";
    let password = "";
    for (let i = 0; i < length; i++) {
      password += charset.charAt(Math.floor(Math.random() * charset.length));
    }
    return password;
  };

  const parseCSV = async (file: File): Promise<CreateUserDto[]> => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e) => {
        try {
          const text = e.target?.result as string;
          const lines = text.split("\n").filter(line => line.trim());
          const headers = lines[0].split(",").map(h => h.trim());

          const users: CreateUserDto[] = [];
          
          for (let i = 1; i < lines.length; i++) {
            const values = lines[i].split(",").map(v => v.trim());
            const rowData: any = {};
            
            headers.forEach((header, index) => {
              rowData[header] = values[index];
            });

            // Transform CSV data to CreateUserDto
            const displayName = rowData.displayName || rowData.name || "";
            const email = rowData.email || "";
            const role = rowData.role || "STUDENT";
            
            // Generate username from email
            const username = email.split("@")[0].toLowerCase().replace(/[^a-z0-9]/g, "");
            
            // Map role: STUDENT -> STUDENT, INSTRUCTOR -> INSTRUCTOR
            const backendRole = role; // Direct mapping

            users.push({
              username: username,
              password: generatePassword(), // Auto-generate password
              email: email,
              displayName: displayName,
              role: backendRole as any,
              loginType: "LOCAL" as any
            });
          }

          resolve(users);
        } catch (error) {
          reject(error);
        }
      };
      reader.onerror = reject;
      reader.readAsText(file);
    });
  };

  const handleImport = async () => {
    if (!file) return;

    setLoading(true);
    try {
      const users = await parseCSV(file);
      const importResult = await onSubmit(users);
      setResult(importResult);
    } catch (error) {
      console.error("Import failed:", error);
      alert("Failed to import users. Please check your file format.");
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setFile(null);
    setResult(null);
    onOpenChange(false);
  };

  const downloadTemplate = (type: "student" | "instructor") => {
    const headers = ["displayName", "email", "role"];
    const sampleData = type === "student" 
      ? ["Nguyễn Văn An,nguyen.van.an@ute.edu.vn,STUDENT"]
      : ["Dr. Trần Văn Bình,tran.van.binh@ute.edu.vn,INSTRUCTOR"];
    
    const csv = [headers.join(","), ...sampleData].join("\n");
    const blob = new Blob([csv], { type: "text/csv" });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `${type}_template.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
  };

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-[600px]">
        <DialogHeader>
          <DialogTitle>Bulk Import Users</DialogTitle>
          <DialogDescription>
            Upload a CSV or Excel file to create multiple user accounts at once.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4 py-4">
          {!result ? (
            <>
              {/* File Upload Area */}
              <div
                className={`border-2 border-dashed rounded-lg p-8 text-center transition-colors ${
                  dragActive ? "border-primary bg-primary/5" : "border-gray-300"
                }`}
                onDragEnter={handleDrag}
                onDragLeave={handleDrag}
                onDragOver={handleDrag}
                onDrop={handleDrop}
              >
                <input
                  ref={fileInputRef}
                  type="file"
                  accept=".csv,.xls,.xlsx"
                  onChange={handleFileInput}
                  className="hidden"
                  aria-label="Upload CSV or Excel file"
                />
                
                {file ? (
                  <div className="space-y-2">
                    <FileSpreadsheet className="h-12 w-12 mx-auto text-green-600" />
                    <p className="font-medium">{file.name}</p>
                    <p className="text-sm text-muted-foreground">
                      {(file.size / 1024).toFixed(2)} KB
                    </p>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => fileInputRef.current?.click()}
                    >
                      Choose Different File
                    </Button>
                  </div>
                ) : (
                  <div className="space-y-2">
                    <Upload className="h-12 w-12 mx-auto text-muted-foreground" />
                    <p className="font-medium">Click to upload or drag and drop</p>
                    <p className="text-sm text-muted-foreground">CSV or Excel file</p>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => fileInputRef.current?.click()}
                    >
                      Select File
                    </Button>
                  </div>
                )}
              </div>

              {/* Template Downloads */}
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                <h4 className="font-semibold text-sm mb-2 flex items-center gap-2">
                  <Download className="h-4 w-4" />
                  Download Templates
                </h4>
                <div className="flex gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => downloadTemplate("student")}
                  >
                    Student Template
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => downloadTemplate("instructor")}
                  >
                    Instructor Template
                  </Button>
                </div>
              </div>

              {/* Required Fields Info */}
              <div className="text-sm text-muted-foreground">
                <p className="font-medium mb-1">Required fields:</p>
                <ul className="list-disc list-inside space-y-1">
                  <li>displayName (full name)</li>
                  <li>email (must be unique)</li>
                  <li>role (STUDENT or INSTRUCTOR)</li>
                </ul>
                <p className="text-xs mt-2 text-blue-600">
                  Note: Username and password will be auto-generated for each user.
                </p>
              </div>
            </>
          ) : (
            /* Import Result */
            <div className="space-y-4">
              <div className="grid grid-cols-3 gap-4">
                <div className="border rounded-lg p-4 text-center">
                  <div className="text-2xl font-bold">{result.total}</div>
                  <div className="text-xs text-muted-foreground mt-1">Total</div>
                </div>
                <div className="border rounded-lg p-4 text-center bg-green-50">
                  <div className="text-2xl font-bold text-green-600">{result.success}</div>
                  <div className="text-xs text-muted-foreground mt-1">Success</div>
                </div>
                <div className="border rounded-lg p-4 text-center bg-red-50">
                  <div className="text-2xl font-bold text-red-600">{result.failed}</div>
                  <div className="text-xs text-muted-foreground mt-1">Failed</div>
                </div>
              </div>

              {result.success > 0 && (
                <div className="flex items-start gap-2 p-3 bg-green-50 border border-green-200 rounded-lg">
                  <CheckCircle className="h-5 w-5 text-green-600 flex-shrink-0 mt-0.5" />
                  <p className="text-sm text-green-800">
                    Successfully created {result.success} user account(s).
                  </p>
                </div>
              )}

              {result.errors.length > 0 && (
                <div className="space-y-2">
                  <div className="flex items-start gap-2 p-3 bg-red-50 border border-red-200 rounded-lg">
                    <AlertCircle className="h-5 w-5 text-red-600 flex-shrink-0 mt-0.5" />
                    <div className="text-sm text-red-800">
                      <p className="font-medium mb-1">Failed to create {result.failed} user(s):</p>
                      <div className="max-h-40 overflow-y-auto space-y-1">
                        {result.errors.map((error, index) => (
                          <div key={index} className="text-xs">
                            <span className="font-medium">Row {error.row} (@{error.username}, {error.email}):</span>{" "}
                            {error.errors.join(", ")}
                          </div>
                        ))}
                      </div>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}
        </div>

        <DialogFooter>
          {!result ? (
            <>
              <Button variant="outline" onClick={handleClose}>
                Cancel
              </Button>
              <Button onClick={handleImport} disabled={!file || loading}>
                {loading ? "Importing..." : "Import Users"}
              </Button>
            </>
          ) : (
            <Button onClick={handleClose}>
              Close
            </Button>
          )}
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}


