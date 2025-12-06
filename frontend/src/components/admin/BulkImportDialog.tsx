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
import { Upload, FileSpreadsheet, AlertCircle, CheckCircle, Download, Minimize2, Loader2 } from "lucide-react";
import { BulkImportResult, CreateUserDto } from "@/types/user.type";
import * as XLSX from 'xlsx';
import { useToast } from "@/components/ui/toast";
import { userService } from "@/services/user.service";
import { getErrorMessage } from "@/api/axios.customize";

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
  const [progress, setProgress] = useState<{ current: number; total: number } | null>(null);
  const [result, setResult] = useState<BulkImportResult | null>(null);
  const [dragActive, setDragActive] = useState(false);
  const [isMinimized, setIsMinimized] = useState(false);
  const [isCancelling, setIsCancelling] = useState(false);
  const [currentTaskId, setCurrentTaskId] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const abortControllerRef = useRef<AbortController | null>(null);
  const { addToast } = useToast();

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
    const validExtensions = ['.csv', '.xls', '.xlsx'];
    const fileName = selectedFile.name.toLowerCase();
    const isValidFile = validExtensions.some(ext => fileName.endsWith(ext));

    if (!isValidFile) {
      alert("Please upload a CSV, XLS, or XLSX file");
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

  const parseFile = async (file: File): Promise<CreateUserDto[]> => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      
      reader.onload = (e) => {
        try {
          const data = e.target?.result;
          let workbook: XLSX.WorkBook;

          // Parse based on file type
          const fileName = file.name.toLowerCase();
          if (fileName.endsWith('.csv')) {
            // Parse CSV
            workbook = XLSX.read(data, { type: 'binary' });
          } else {
            // Parse XLS/XLSX
            workbook = XLSX.read(data, { type: 'array' });
          }

          // Get first sheet
          const firstSheetName = workbook.SheetNames[0];
          const worksheet = workbook.Sheets[firstSheetName];
          
          // Convert to JSON
          const jsonData = XLSX.utils.sheet_to_json(worksheet) as any[];

          if (jsonData.length === 0) {
            reject(new Error("File is empty or has no valid data"));
            return;
          }

          // Transform to CreateUserDto
          const users: CreateUserDto[] = jsonData.map((row, index) => {
            const displayName = row.displayName || row.name || row.DisplayName || row.Name || "";
            const email = row.email || row.Email || "";
            const role = (row.role || row.Role || "STUDENT").toUpperCase();
            // Convert studentId to string (handles numbers from Excel) and handle null/undefined
            const rawStudentId = row.studentId || row.student_id || row.StudentId || row.Student_ID;
            const studentId = rawStudentId != null ? String(rawStudentId).trim() : "";
            
            // Generate username from email
            const username = email.split("@")[0].toLowerCase().replace(/[^a-z0-9]/g, "") || 
                           displayName.toLowerCase().replace(/[^a-z0-9]/g, "");
            
            // Validate required fields
            if (!displayName || !email) {
              throw new Error(`Row ${index + 2}: Missing required fields: displayName or email`);
            }
            
            // Validate studentId for STUDENT role
            if (role === "STUDENT" && !studentId) {
              throw new Error(`Row ${index + 2}: Student ID is required for STUDENT role`);
            }

            return {
              username: username,
              password: generatePassword(),
              email: email,
              displayName: displayName,
              studentId: role === "STUDENT" && studentId ? studentId : undefined,
              role: role as any
            };
          });

          resolve(users);
        } catch (error) {
          reject(error);
        }
      };
      
      reader.onerror = reject;
      
      // Read file based on type
      const fileName = file.name.toLowerCase();
      if (fileName.endsWith('.csv')) {
        reader.readAsBinaryString(file);
      } else {
        reader.readAsArrayBuffer(file);
      }
    });
  };

  const handleImport = async () => {
    if (!file) return;

    setLoading(true);
    setIsCancelling(false);
    setProgress({ current: 0, total: 0 });
    
    // Create abort controller
    abortControllerRef.current = new AbortController();
    
    try {
      const users = await parseFile(file);
      setProgress({ current: 0, total: users.length });
      
      // Check if cancelled during parsing
      if (abortControllerRef.current?.signal.aborted) {
        throw new Error("Import cancelled by user");
      }
      
      // Show toast that import is running in background
      addToast({
        title: "Import Started 🚀",
        description: `Processing ${users.length} users. You can minimize this dialog and continue working.`,
        variant: "default"
      });
      
      const importResult = await onSubmit(users);
      
      // Check if cancelled after submit
      if (abortControllerRef.current?.signal.aborted) {
        throw new Error("Import cancelled by user");
      }
      
      setResult(importResult);
      setProgress(null);
      
      // Store taskId for cancellation
      if (importResult.taskId) {
        setCurrentTaskId(importResult.taskId);
      }
      
      // Show completion toast
      if (importResult.success > 0) {
        addToast({
          title: "Import Completed! ✅",
          description: `Successfully created ${importResult.success} out of ${importResult.total} users.`,
          variant: "success"
        });
      }
      
      if (importResult.failed > 0) {
        addToast({
          title: "Import Completed with Errors ⚠️",
          description: `${importResult.failed} users failed to import. Check the details in the dialog.`,
          variant: "destructive"
        });
      }
    } catch (error) {
      console.error("Import failed:", error);
      setProgress(null);
      
      // Use helper function to extract user-friendly error message
      const errorMessage = getErrorMessage(error);
      
      // Determine error type and customize title/description
      let errorTitle = "Import Failed ❌";
      let errorDescription = errorMessage;
      
      if (errorMessage.includes("cancelled by user")) {
        errorTitle = "Request Cancelled ⏹️";
        errorDescription = "Import is processing in background. Users and emails will be created. Refresh the page to see results.";
      } else if (errorMessage.includes("timeout") || errorMessage.includes("Request timeout")) {
        errorTitle = "Request Timeout ⏱️";
        errorDescription = "Import is taking longer than expected. Please try with a smaller file or contact support.";
      } else if (errorMessage.includes("Network") || errorMessage.includes("internet connection")) {
        errorTitle = "Connection Error 🔌";
      } else if (errorMessage.includes("session") || errorMessage.includes("log in again")) {
        errorTitle = "Session Expired 🔒";
      } else if (errorMessage.includes("permission")) {
        errorTitle = "Access Denied ⛔";
      } else if (errorMessage.includes("Server error")) {
        errorTitle = "Server Error 🔧";
      }
      
      addToast({
        title: errorTitle,
        description: errorDescription,
        variant: errorTitle.includes("Cancelled") ? "default" : "destructive"
      });
    } finally {
      setLoading(false);
      setIsCancelling(false);
      abortControllerRef.current = null;
    }
  };

  const handleClose = () => {
    // Don't allow closing if import is in progress
    if (loading) {
      setIsMinimized(true);
      return;
    }
    
    setFile(null);
    setResult(null);
    setProgress(null);
    setIsMinimized(false);
    setCurrentTaskId(null);
    onOpenChange(false);
  };
  
  const handleMinimize = () => {
    setIsMinimized(true);
  };
  
  const handleRestore = () => {
    setIsMinimized(false);
  };
  
  const handleStopImport = async () => {
    if (abortControllerRef.current) {
      setIsCancelling(true);
      abortControllerRef.current.abort();
      
      // Cancel email sending on backend if taskId exists
      if (currentTaskId) {
        try {
          await userService.cancelBulkEmailSending(currentTaskId);
          addToast({
            title: "Import Stopped ⏹️",
            description: "Users have been created, but email sending has been stopped.",
            variant: "default"
          });
        } catch (error) {
          console.error("Failed to cancel email sending:", error);
          addToast({
            title: "Partial Stop",
            description: "Some emails may still be sent. Users have been created.",
            variant: "default"
          });
        }
      } else {
        addToast({
          title: "Request Cancelled",
          description: "You stopped waiting for response. Users are being created in background.",
          variant: "default"
        });
      }
    }
  };

  const downloadTemplate = (format: "csv" | "xlsx" = "csv") => {
    const sampleData = [
      { displayName: "Nguyễn Văn An", email: "nguyen.van.an@ute.edu.vn", role: "STUDENT", studentId: "22000001" },
      { displayName: "Trần Thị Bích", email: "tran.thi.bich@ute.edu.vn", role: "STUDENT", studentId: "22000002" },
      { displayName: "Lê Văn Cường", email: "le.van.cuong@ute.edu.vn", role: "STUDENT", studentId: "22000003" },
      { displayName: "Phạm Văn Đức", email: "pham.van.duc@ute.edu.vn", role: "INSTRUCTOR" }
    ];
    
    if (format === "xlsx") {
      // Create Excel file
      const ws = XLSX.utils.json_to_sheet(sampleData);
      const wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, ws, "Users");
      XLSX.writeFile(wb, `student_import_template.xlsx`);
    } else {
      // Create CSV file
      const ws = XLSX.utils.json_to_sheet(sampleData);
      const csv = XLSX.utils.sheet_to_csv(ws);
      const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `student_import_template.csv`;
      a.click();
      window.URL.revokeObjectURL(url);
    }
  };

  return (
    <>
      {/* Minimized indicator */}
      {isMinimized && loading && (
        <div 
          className="fixed bottom-4 right-4 z-50 bg-white border-2 border-primary rounded-lg shadow-lg p-4 cursor-pointer hover:shadow-xl transition-shadow"
          onClick={handleRestore}
        >
          <div className="flex items-center gap-3">
            <Loader2 className="h-5 w-5 animate-spin text-primary" />
            <div>
              <p className="font-semibold text-sm">Importing Users...</p>
              {progress && (
                <p className="text-xs text-muted-foreground">
                  Processing {progress.total} users
                </p>
              )}
            </div>
          </div>
          <p className="text-xs text-blue-600 mt-2">Click to view details</p>
        </div>
      )}
      
      <Dialog open={open && !isMinimized} onOpenChange={handleClose}>
        <DialogContent className="sm:max-w-[600px] max-h-[90vh] flex flex-col">
          <DialogHeader className="flex flex-row items-center justify-between pb-3">
            <div>
              <DialogTitle>Bulk Import Users</DialogTitle>
              <DialogDescription className="text-xs mt-1">
                Upload a CSV, XLS, or XLSX file to create multiple user accounts at once. 
                <span className="block mt-1 text-blue-600 font-medium">
                  ⚠️ Note: Student ID (8 digits, format: 22xxxxxx) is required for STUDENT role.
                </span>
              </DialogDescription>
            </div>
            {loading && (
              <Button
                variant="ghost"
                size="icon"
                onClick={handleMinimize}
                className="h-8 w-8"
                title="Minimize (continue in background)"
              >
                <Minimize2 className="h-4 w-4" />
              </Button>
            )}
          </DialogHeader>

        <div className="space-y-3 overflow-y-auto flex-1 pr-2">
          {/* Progress indicator */}
          {loading && progress && (
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-3">
              <div className="flex items-center gap-3 mb-2">
                <Loader2 className="h-5 w-5 animate-spin text-blue-600" />
                <div>
                  <p className="font-semibold text-sm">Importing users...</p>
                  <p className="text-xs text-muted-foreground">
                    Processing {progress.total} users. This may take a few minutes.
                  </p>
                </div>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2 mt-2">
                <div className="bg-blue-600 h-2 rounded-full transition-all duration-300 animate-pulse w-full"></div>
              </div>
              <p className="text-xs text-blue-600 mt-2">
                💡 You can click "Stop Import" to cancel waiting (users will still be created in background)
              </p>
            </div>
          )}
          
          {!result ? (
            <>
              {/* File Upload Area */}
              <div
                className={`border-2 border-dashed rounded-lg p-6 text-center transition-colors ${
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
                    <p className="text-sm text-muted-foreground">CSV, XLS, or XLSX file</p>
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
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-3">
                <h4 className="font-semibold text-xs mb-2 flex items-center gap-2">
                  <Download className="h-4 w-4" />
                  Download Templates
                </h4>
                <p className="text-xs text-muted-foreground mb-2">
                  Templates include sample data with <span className="font-medium text-blue-600">studentId</span> column for STUDENT role.
                </p>
                <div className="flex gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => downloadTemplate("csv")}
                    className="flex-1"
                  >
                    Download CSV Template
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => downloadTemplate("xlsx")}
                    className="flex-1"
                  >
                    Download XLSX Template
                  </Button>
                </div>
              </div>

              {/* Required Fields Info */}
              <div className="bg-amber-50 border border-amber-200 rounded-lg p-3">
                <p className="font-semibold text-xs mb-2 text-amber-900">📋 Required Fields:</p>
                <ul className="text-xs space-y-1.5 text-amber-800">
                  <li className="flex items-start gap-2">
                    <span className="font-medium">• displayName:</span>
                    <span>Full name of the user</span>
                  </li>
                  <li className="flex items-start gap-2">
                    <span className="font-medium">• email:</span>
                    <span>Must be unique, will be used for login</span>
                  </li>
                  <li className="flex items-start gap-2">
                    <span className="font-medium">• role:</span>
                    <span>STUDENT or INSTRUCTOR</span>
                  </li>
                  <li className="flex items-start gap-2">
                    <span className="font-medium text-red-600">• studentId:</span>
                    <span>
                      <span className="font-semibold text-red-600">REQUIRED for STUDENT role only</span>
                      <span className="block mt-0.5">Format: 8 digits (e.g., 22000001, 22000002)</span>
                      <span className="block mt-0.5 text-amber-700">Leave empty for INSTRUCTOR role</span>
                    </span>
                  </li>
                </ul>
                <div className="mt-2 pt-2 border-t border-amber-300">
                  <p className="text-xs text-amber-700">
                    💡 <span className="font-medium">Auto-generated:</span> Username and password will be created automatically for each user.
                  </p>
                </div>
              </div>
            </>
          ) : (
            /* Import Result */
            <div className="space-y-3">
              <div className="grid grid-cols-3 gap-3">
                <div className="border rounded-lg p-3 text-center">
                  <div className="text-2xl font-bold">{result.total}</div>
                  <div className="text-xs text-muted-foreground mt-1">Total</div>
                </div>
                <div className="border rounded-lg p-3 text-center bg-green-50">
                  <div className="text-2xl font-bold text-green-600">{result.success}</div>
                  <div className="text-xs text-muted-foreground mt-1">Success</div>
                </div>
                <div className="border rounded-lg p-3 text-center bg-red-50">
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
                      <div className="max-h-32 overflow-y-auto space-y-1">
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
              {loading ? (
                <>
                  <Button 
                    variant="outline" 
                    onClick={handleMinimize}
                  >
                    Minimize
                  </Button>
                  <Button 
                    variant="destructive" 
                    onClick={handleStopImport}
                    disabled={isCancelling}
                  >
                    {isCancelling ? "Cancelling..." : "Stop Waiting"}
                  </Button>
                </>
              ) : (
                <>
                  <Button 
                    variant="outline" 
                    onClick={handleClose}
                  >
                    Cancel
                  </Button>
                  <Button onClick={handleImport} disabled={!file}>
                    Import Users
                  </Button>
                </>
              )}
            </>
          ) : (
            <Button onClick={handleClose}>
              Close
            </Button>
          )}
        </DialogFooter>
      </DialogContent>
    </Dialog>
    </>
  );
}

