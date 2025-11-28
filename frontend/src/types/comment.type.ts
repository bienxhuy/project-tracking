// Minimal representation of a comment data for API calls

import { BaseUser } from "./user.type";

export interface Comment {
  id: number;
  content: string;
  createdDate: Date;
  commenter: BaseUser;
}
