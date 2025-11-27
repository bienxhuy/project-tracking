// API Service to fetch user data

import { dummyStudents } from "@/data/dummy/users.dummy";
import { User } from "@/types/user.type";

export function fetchAllStudents(): User[] {
  // TODO: Replace with real API call
  return dummyStudents;
}

export function searchStudents(query: string): User[] {
  // TODO: Replace with real API call
  if (!query) return dummyStudents;
  
  const lowerQuery = query.toLowerCase();
  return dummyStudents.filter(
    student => 
      student.full_name.toLowerCase().includes(lowerQuery) ||
      student.email.toLowerCase().includes(lowerQuery)
  );
}
