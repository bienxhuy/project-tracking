// Minimal representation of a attachment data for API calls

export interface Attachment {
  id: number;
  fileName: string; // Maps to fileName from BE
  fileSize: number;
  fileType: string;
  url: string; // Maps to url from BE (download URL)
}
