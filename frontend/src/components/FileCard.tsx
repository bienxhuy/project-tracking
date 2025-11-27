import { Button } from "@/components/ui/button";
import { FileText, X, Download } from "lucide-react";

interface FileCardProps {
  // File information
  fileName: string;
  fileSize: number;
  
  // Optional download URL (for existing files)
  downloadUrl?: string;
  
  // Optional callback for remove action
  onRemove?: () => void;
  
  // Optional custom styling
  variant?: "default" | "new" | "existing";
}

export const FileCard = ({
  fileName,
  fileSize,
  downloadUrl,
  onRemove,
  variant = "default",
}: FileCardProps) => {
  // Format file size for display
  const formatFileSize = (bytes: number): string => {
    if (bytes < 1024) return bytes + " B";
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + " KB";
    return (bytes / (1024 * 1024)).toFixed(1) + " MB";
  };

  // Handle download if URL is provided
  const handleDownload = () => {
    if (downloadUrl) {
      window.open(downloadUrl, "_blank");
    }
  };

  // Determine background class based on variant
  const getBackgroundClass = () => {
    switch (variant) {
      case "new":
        return "bg-primary/5";
      case "existing":
        return "bg-secondary/50 border border-border";
      default:
        return "bg-muted";
    }
  };

  return (
    <div className={`flex items-center justify-between p-2 rounded-md ${getBackgroundClass()}`}>
      <div className="flex items-center gap-2 flex-1 min-w-0">
        <FileText className="w-4 h-4 text-muted-foreground flex-shrink-0" />
        <span className="text-sm text-foreground truncate">{fileName}</span>
        <span className="text-xs text-muted-foreground flex-shrink-0">
          ({formatFileSize(fileSize)})
        </span>
      </div>
      
      <div className="flex items-center gap-1 flex-shrink-0">
        {/* Download button - only show if downloadUrl is provided */}
        {downloadUrl && (
          <Button
            type="button"
            size="icon"
            variant="ghost"
            className="h-7 w-7"
            onClick={handleDownload}
            title="Tải xuống"
          >
            <Download className="w-3 h-3" />
          </Button>
        )}
        
        {/* Remove button - only show if onRemove is provided */}
        {onRemove && (
          <Button
            type="button"
            variant="ghost"
            size="sm"
            onClick={onRemove}
            title="Xóa tệp"
          >
            <X className="w-4 h-4" />
          </Button>
        )}
      </div>
    </div>
  );
};
