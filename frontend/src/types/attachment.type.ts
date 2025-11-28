// Minimal representation of a attachment data for API calls

export interface Attachment {
  id: number;
  originalFilename: string;
  storedFilename: string;
  fileSize: number;
  fileType: string;
  storageUrl: string;
}
