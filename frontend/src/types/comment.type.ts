// Minimal representation of a comment data for API calls

export interface Comment {
  id: number;
  content: string;
  createdDate: Date;
  commenter: { id: number; name: string; initials: string };
}
